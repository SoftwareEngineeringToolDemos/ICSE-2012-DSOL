package org.dsol.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.xml.bind.JAXB;

import org.apache.cxf.helpers.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.dsol.service.config.Service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;

public class RESTService extends ServiceDefinition{
	
	private final String JSON_CAMEL_CASE_PROP = "CAMEL_CASE";
	
	private final static Logger logger = Logger.getLogger(RESTService.class.getName());
	
	private Request request;
	private Gson gson;//Used to seralize/deserialize json services
	JsonPath jsonPath;
	
	
	public RESTService(Service serviceMetadata) {
		super(serviceMetadata);

		String mediaType = serviceData.getMediaType();
		if(mediaType != null && mediaType.toLowerCase().startsWith(MediaType.APPLICATION_JSON.toLowerCase())){
			if(mediaType.contains(JSON_CAMEL_CASE_PROP)){
				gson = new GsonBuilder().create();	
			}
			else{
				gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
			}
			String path = serviceData.getPath();
			if(path != null && !path.isEmpty()) {
				if(path.startsWith("/")){
					path = path.replaceFirst("/", Matcher.quoteReplacement("$."));
					path = path.replaceAll("/", ".");
				}
				jsonPath = JsonPath.compile(path);
			}
		}
		
		
	}
	
	@Override
	public ServiceDefinition clone(){
		RESTService clone = new RESTService(serviceData.clone());		
		return clone;
	}
	
	public Response invoke(Request request) throws Exception {
		this.request = request;
        HttpClient httpclient = new DefaultHttpClient();      
		try {
			HttpRequestBase requestBase = createHttpRequest();
			HttpResponse httpResponse = httpclient.execute(requestBase);
			return createResponse(httpResponse);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	private HttpRequestBase createHttpRequest() throws IOException{
		String method = serviceData.getMethod();
		String mediaType = serviceData.getMediaType();
		String url = serviceData.getUrl();
		
        logger.info("Sending HTTP "+ method.toUpperCase()+ " request to "+url);
		
        HttpRequestBase requestBase = null;
		if (HttpGet.METHOD_NAME.equalsIgnoreCase(method)) {
			requestBase = new HttpGet(url);
		} else if (HttpPost.METHOD_NAME.equalsIgnoreCase(method)) {
			
			requestBase = createHttpPostRequest();
			
		} else if (HttpDelete.METHOD_NAME.equalsIgnoreCase(method)) {
			requestBase = new HttpDelete(url);
		}
		if(mediaType != null){
			requestBase.addHeader("Accept", mediaType);	
		}
		
		addHeaders(requestBase);
		return requestBase;
	}
	
	private HttpPost createHttpPostRequest() throws IOException{

		String mediaType = serviceData.getMediaType();		
		
		HttpPost post = new HttpPost(serviceData.getUrl());
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Object[] params = request.getParams();
		if(MediaType.APPLICATION_XML.equalsIgnoreCase(mediaType)){
			for(Object param:params){
				JAXB.marshal(param, bos);
			}				
		}
		else if(MediaType.APPLICATION_JSON.equalsIgnoreCase(mediaType)){
			for(Object param:params){
				bos.write(gson.toJson(param, param.getClass()).getBytes());
			}				
		}
		
		byte[] requestData = bos.toByteArray();
		post.setEntity(new InputStreamEntity(new ByteArrayInputStream(requestData),requestData.length));
		
		return post;
	}
	
	private void addHeaders(HttpRequestBase requestBase){
		if(this.request.getHeaders() == null){
			return;
		}
		Map<String,String> headers = this.request.getHeaders();
		for(String header:headers.keySet()){
			requestBase.addHeader(header, headers.get(header));
		}
	}	
	
	private Response createResponse(HttpResponse httpResponse) throws IOException, JSONException {
		
		Status status = Status.fromStatusCode(httpResponse.getStatusLine().getStatusCode());
		Family statusFamily = status.getFamily();
		
		if (!statusFamily.equals(Family.SUCCESSFUL)) {
			throw new RuntimeException("Error invoking service:"+serviceData.getName()+" Result: "+httpResponse);
		}
		
		Response response = request.getResponse();
		response.setHeaders(createHeadersMap(httpResponse.getAllHeaders()));

		HttpEntity entity = httpResponse.getEntity();
		Class expectedReturnValueClass = response.getExpectedReturnValueClass();
	
		String mediaType = serviceData.getMediaType();
		
		if(expectedReturnValueClass != null && !Void.TYPE.equals(expectedReturnValueClass) && entity != null && entity.getContent() != null){
			String responseBody = IOUtils.readStringFromStream(entity.getContent());
			if(responseBody.length() > 0) {
				if(MediaType.APPLICATION_XML.equalsIgnoreCase(mediaType)){
					response.setReturnValue(JAXB.unmarshal(new ByteArrayInputStream(responseBody.getBytes()), expectedReturnValueClass));			
				}
				else if(mediaType != null && mediaType.toLowerCase().startsWith(MediaType.APPLICATION_JSON.toLowerCase())){
					if(jsonPath != null){
						Object object;
						try {
							object = jsonPath.read(responseBody);
						} catch (ParseException e) {
							throw new IOException("Error reading json object from path ");
						}
						responseBody = gson.toJson(object);
					}
					response.setReturnValue(gson.fromJson(responseBody, expectedReturnValueClass));
				}
				else{
					response.setReturnValue(responseBody.getBytes());			
				}
			}
		}

		return response;
	}
	
	private Map<String,String> createHeadersMap(Header[] headers){
		Map<String,String> headersMap = new HashMap<String,String>();
		for(Header header:headers){
			headersMap.put(header.getName(), header.getValue());
		}
		return headersMap;
	}
	
	@Override
	public String toString() {
		return  "RESTFUL SERVICE\n"+
				"id: "+serviceData.getId()+ " \n"+
				"name: "+serviceData.getName()+ " \n"+
				"url: "+serviceData.getUrl()+ " \n"+
				"method: "+serviceData.getMethod()+ " \n"+	
				"mediaType: "+serviceData.getMediaType()+ " \n";
	}
}
