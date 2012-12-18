package org.dsol.api.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;


@Provider
@Produces("application/json")
public class ListMessageBodyWriter implements MessageBodyWriter<List> {

	private Gson gson = new Gson();
	
	@Override
	public long getSize(List list, Class<?> arg1, Type arg2,
			Annotation[] arg3, MediaType arg4) {
		return toJson(list).getBytes().length;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return List.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(List list, Class<?> arg1, Type type,
						Annotation[] arg3, MediaType arg4,
						MultivaluedMap<String, Object> arg5, OutputStream outputStream) throws IOException, WebApplicationException {
		
		outputStream.write(toJson(list).getBytes());
				
	}
	
	private String toJson(List list){
		List<String> listAsString = new ArrayList<String>();
		
		for(Object o:list){
			listAsString.add(o.toString());
		}
		
		return gson.toJson(listAsString);
	}
}
