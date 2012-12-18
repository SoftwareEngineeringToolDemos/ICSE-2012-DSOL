package org.dsol.service.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.dsol.service.ServiceType;

@XmlType(name = "service", propOrder = { "id", "name", "type", "wsdl",
		"operation", "operationNamespaceUri", "url", "method", "mediaType",
		"requestHeaders", "responseHeaders","additionalParameters","path" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Service {

	@XmlAttribute
	private String id;
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String type;
	@XmlAttribute
	private String requestHeaders;
	@XmlAttribute
	private String responseHeaders;
	@XmlAttribute
	private String path;

	
	// SOAP
	@XmlAttribute
	private String wsdl;
	@XmlAttribute
	private String operation;
	@XmlAttribute
	private String operationNamespaceUri;

	// REST
	@XmlAttribute
	private String url;
	@XmlAttribute
	private String method;
	@XmlAttribute
	private String mediaType;
	
	
	@XmlElement(name="additional-parameter")
	List<AdditionalParameter> additionalParameters;

	private Service() {
		additionalParameters = new ArrayList<AdditionalParameter>();
	}
	
	public static Service createRestService(String id, String name, String url,
			String method, String mediaType, String requestHeaders,
			String responseHeaders, String path) {

		Service service = new Service();

		service.id = id;
		service.name = name;
		service.type = ServiceType.REST.toString();
		service.url = url;
		service.method = method;
		service.mediaType = mediaType;
		service.requestHeaders = requestHeaders;
		service.responseHeaders = responseHeaders;
		service.path = path;
		
		return service;
	}

	public static Service createSoapService(String id, String name,
			String wsdl, String operation, String operationNamespaceUri,
			String requestHeaders, String responseHeaders, String path) {

		Service service = new Service();

		service.id = id;
		service.name = name;
		service.type = ServiceType.SOAP.toString();
		service.wsdl = wsdl;
		service.operation = operation;
		service.operationNamespaceUri = operationNamespaceUri;
		service.requestHeaders = requestHeaders;
		service.responseHeaders = responseHeaders;
		service.path = path;
		
		return service;
	}

	public String getId() {
		if(id == null){
			return name;
		}
		return id;
	}

	public String getMediaType() {
		return mediaType;
	}

	public String getMethod() {
		return method;
	}

	public String getName() {
		return name;
	}

	public String getOperation() {
		return operation;
	}

	public String getOperationNamespaceUri() {
		return operationNamespaceUri;
	}
	
	public String getPath() {
		return path;
	}

	public String getRequestHeaders() {
		return requestHeaders;
	}

	public String getResponseHeaders() {
		return responseHeaders;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getWsdl() {
		return wsdl;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public void setOperationNamespaceUri(String operationNamespaceURI) {
		this.operationNamespaceUri = operationNamespaceURI;
	}

	public void setRequestHeaders(String requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public void setResponseHeaders(String responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setWsdl(String wsdl) {
		this.wsdl = wsdl;
	}

	public void addAditionalParameter(AdditionalParameter additionalParameter){
		additionalParameters.add(additionalParameter);
	}
	
	public List<AdditionalParameter> getAdditionalParameters() {
		return additionalParameters;
	}
	
	public Service clone(){
		Service service = null;
		ServiceType serviceType = ServiceType.fromString(type);
		if(ServiceType.SOAP.equals(serviceType)){
			service = createSoapService(id, name, wsdl, operation, operationNamespaceUri, requestHeaders, responseHeaders, path);
		}
		else if(ServiceType.REST.equals(serviceType)){
			service = createRestService(id, name, url, method, mediaType, requestHeaders, responseHeaders, path);
		}
		service.additionalParameters = additionalParameters;
		return service;		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Service other = (Service) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
