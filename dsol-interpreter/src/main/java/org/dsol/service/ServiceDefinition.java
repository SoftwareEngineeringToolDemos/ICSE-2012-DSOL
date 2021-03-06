package org.dsol.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.dsol.service.config.AdditionalParameter;
import org.dsol.service.config.Service;

public abstract class ServiceDefinition{

	protected Service serviceData;
	private ServiceType serviceType;
	private String[] requestHeaders;
	private String[] responseHeaders;
	private Map<String,Object> varsValues;
	
	public ServiceDefinition(Service serviceMetaData){
		super();
		this.serviceData = serviceMetaData;
		this.serviceType = ServiceType.fromString(serviceMetaData.getType());
		setRequestHeaders(serviceMetaData.getRequestHeaders());
		setResponseHeaders(serviceMetaData.getResponseHeaders());
	}
	
	public abstract ServiceDefinition clone();
	public abstract Response invoke(Request request) throws Exception;
	
	public String getId() {
		return serviceData.getId();
	}
	
	public String getName() {
		return serviceData.getName();
	}
		
	public Object[] getParameters(Object[] args) {
		if(serviceData.getAdditionalParameters().isEmpty()){
			return args;
		}
		Object[] newArgs = new Object[serviceData.getAdditionalParameters().size() + args.length];
		for(AdditionalParameter additionalParameter:serviceData.getAdditionalParameters()){
			newArgs[additionalParameter.getIndex()] = parseVars(additionalParameter.getValue()); 
		}
		
		//fill newArgs in empty indexes
		int readIndex = 0;
		for(int i = 0; i<newArgs.length ; i++){
			if(newArgs[i] == null){
				newArgs[i] = args[readIndex];
				readIndex++;
			}
		}
		return newArgs;
	}
	
	public String[] getRequestHeaders() {
		return requestHeaders;
	}

	public String[] getResponseHeaders() {
		return responseHeaders;
	}

	public Service getService() {
		return serviceData;
	}
	
	private String getUrl() {
		switch (serviceType) {
		case SOAP:
			return serviceData.getWsdl();
		case REST:
			return serviceData.getUrl();
		default:
			return "";
		}
	}
	
	public List<String> getVars(){
		
		List<String> vars = new ArrayList<String>();
		vars.addAll(getVars(getUrl()));
		for(AdditionalParameter additionalParameter:serviceData.getAdditionalParameters()){
			vars.addAll(getVars(additionalParameter.getValue()));
		}
		
		return vars;
	}
	
	private List<String> getVars(String mutableString){
		String patternStr = "#\\{[^\\}]*\\}";
		
		// Compile and use regular expression
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(mutableString);
		
		List<String> vars = new ArrayList<String>();
		while(matcher.find()){
			String var = matcher.group();
			vars.add(var.substring(2,var.length() -1 ));//remove #{ from beginning and } from 
		}
		
		return vars;
	}
		
	public void parseUrl(){
		String url = getUrl();
		setUrl(parseVars(url));
	}
	
	private String setUrl(String url) {
		switch (serviceType) {
		case SOAP:
			serviceData.setWsdl(url);
		case REST:
			serviceData.setUrl(url);
		default:
			return "";
		}
	}
	
	private String parseVars(String mutableString){
		
		for(String var:getVars(mutableString)){
			try {
				
				Object varValue = PropertyUtils.getNestedProperty(varsValues, var);
				String varStr = varValue ==null?"":URLEncoder.encode(varValue.toString(),"UTF-8");
				mutableString = mutableString.replaceAll("#\\{"+var+"\\}", varStr);
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return mutableString;
	}

	private void setRequestHeaders(String requestHeaders){
		if(requestHeaders == null){
			this.requestHeaders = new String[0];
		}
		else{
			this.requestHeaders = requestHeaders.split(",");
			for(int i = 0;i < this.requestHeaders.length;i++){
				this.requestHeaders[i] = this.requestHeaders[i].trim(); 
			}			
		}
	}
	
	private void setResponseHeaders(String responseHeaders) {
		if(responseHeaders == null){
			this.responseHeaders = new String[0];
		}
		else{
			this.responseHeaders = responseHeaders.split(",");
			for(int i = 0;i < this.responseHeaders.length;i++){
				this.responseHeaders[i] = this.responseHeaders[i].trim(); 
			}			
		}
	}

	public void setVarsValues(Map<String, Object> varsValues) {
		this.varsValues = varsValues;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ServiceDefinition)){
			return false;
		}
		ServiceDefinition that = (ServiceDefinition)obj;
		return this.serviceData.equals(that.serviceData);
	}

}
