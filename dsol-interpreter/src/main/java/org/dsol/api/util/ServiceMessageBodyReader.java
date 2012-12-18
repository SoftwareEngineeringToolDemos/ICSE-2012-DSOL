package org.dsol.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.dsol.service.config.Service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Consumes("application/json")
@Provider
public class ServiceMessageBodyReader implements MessageBodyReader<Service> {

	private Gson gson = null;
	
	public ServiceMessageBodyReader() {
		gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}
			
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] arg2,
			MediaType arg3) {
		return Service.class.isAssignableFrom(type);
	}

	@Override
	public Service readFrom(Class<Service> arg0, Type arg1, Annotation[] arg2,
							MediaType arg3, MultivaluedMap<String, String> arg4,
							InputStream is) throws IOException, WebApplicationException {
				
		return gson.fromJson(new InputStreamReader(is), Service.class);
			
	}
}
