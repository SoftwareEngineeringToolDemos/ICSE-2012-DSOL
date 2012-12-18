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

import org.dsol.management.PlannerInfo;

import com.google.gson.Gson;

@Consumes("application/json")
@Provider
public class PlannerInfoMessageBodyReader implements MessageBodyReader<PlannerInfo> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] arg2,
			MediaType arg3) {
		return PlannerInfo.class.isAssignableFrom(type);
	}

	@Override
	public PlannerInfo readFrom(Class<PlannerInfo> arg0, Type arg1, Annotation[] arg2,
							MediaType arg3, MultivaluedMap<String, String> arg4,
							InputStream is) throws IOException, WebApplicationException {
				
		return new Gson().fromJson(new InputStreamReader(is), PlannerInfo.class);			
	}
}
