package org.dsol.service;

import java.util.Map;

public class Response {
	
	private Map<String, String> headers;
	private Object returnValue;
	private Class expectedReturnValueClass;
	
	public Response() {}
	
	public Response(Class expectedReturnValueClass) {
		this.expectedReturnValueClass = expectedReturnValueClass;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	public Object getReturnValue() {
		return returnValue;
	}
	
	public void setHeaders(Map<String,String> headers){
		this.headers = headers;
	}
	
	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public Object getHeader(String header) {
		if(headers == null){
			return null;
		}
		return headers.get(header);
	}

	public Class getExpectedReturnValueClass() {
		return expectedReturnValueClass;
	}
}
