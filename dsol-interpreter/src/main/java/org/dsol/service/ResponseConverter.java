package org.dsol.service;


public abstract interface ResponseConverter<ResponseConvertedType> {
	
	public abstract ResponseConvertedType convert(Response response);
	
}
