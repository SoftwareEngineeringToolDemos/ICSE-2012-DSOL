package org.dsol.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.dsol.service.config.Service;
import org.junit.Test;

public class ServiceDefinitionTest {

	@Test
	public void testGetUrlObjects(){
		
		String url = "http://#{var-1}/#{var2}?name=#{var3}";
		
		Service serviceMetadata = Service.createRestService("id", "name", url, "method", "mediaType", "requestHeaders", "responseHeaders", null);
		
		
		ServiceDefinition serviceDef = new ServiceDefinition(serviceMetadata) {
			public Response invoke(Request request) throws Exception {return null;}
			public ServiceDefinition clone() {return null;}
		};
		
		Assert.assertEquals(Arrays.asList(new String[]{"var-1","var2","var3"}), serviceDef.getVars());
		
		String expectedUrl = "http://a/b?name=c";
		Map<String,Object> values = new HashMap<String, Object>();
		values.put("var-1", "a");
		values.put("var2", "b");
		values.put("var3", "c");
		
		serviceDef.setVarsValues(values);
		serviceDef.parseUrl();
		
		Assert.assertEquals(expectedUrl, serviceMetadata.getUrl());
		
	}
	
}
