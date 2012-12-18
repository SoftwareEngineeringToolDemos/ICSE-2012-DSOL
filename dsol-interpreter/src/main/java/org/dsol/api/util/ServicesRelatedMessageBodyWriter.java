package org.dsol.api.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.dsol.service.config.Service;
import org.dsol.service.config.Services;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
@Produces("application/json")
public class ServicesRelatedMessageBodyWriter implements MessageBodyWriter {

	private Gson gson;
	
	public ServicesRelatedMessageBodyWriter() {
		 gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	@Override
	public long getSize(Object object, Class arg1, Type arg2, Annotation[] arg3,
			MediaType arg4) {
		if(object instanceof Services){
			return gson.toJson((Services)object).getBytes().length;
		}
		if(object instanceof Service){
			return gson.toJson((Service)object).getBytes().length;
		}
		
		return 0;
	}

	@Override
	public void writeTo(Object object, Class arg1, Type arg2, Annotation[] arg3,
			MediaType arg4, MultivaluedMap arg5, OutputStream outputStream)
			throws IOException, WebApplicationException {
		if(object instanceof Services){
			outputStream.write(gson.toJson((Services)object).getBytes());
		}
		else if(object instanceof Service){
			outputStream.write(gson.toJson((Service)object).getBytes());
		}
	}

	@Override
	public boolean isWriteable(Class type, Type arg1, Annotation[] arg2,
			MediaType arg3) {

		return Services.class.isAssignableFrom(type) || 
				Service.class.isAssignableFrom(type);
		
				
	}
}