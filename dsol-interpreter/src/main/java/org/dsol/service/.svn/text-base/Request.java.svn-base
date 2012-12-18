package org.dsol.service;

import java.util.HashMap;
import java.util.Map;

public class Request {
	
	private Object[] params;
	private Map<String, String> headers;
	private Response response;
	
	public Request(Object[] params, Class expectedReturnValueClass) {
		super();
		this.params = params;
		this.headers = new HashMap<String, String>();
		this.response = new Response(expectedReturnValueClass);
	}
	
	public Object[] getParams() {
		return params;
	}
		
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public void addHeader(String key, String value){
		headers.put(key, value);
	}
	
	public Response getResponse() {
		return response;
	}
}
