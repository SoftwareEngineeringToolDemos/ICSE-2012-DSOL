package org.dsol.api.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ws.rs.WebApplicationException;

import org.dsol.service.config.Service;
import org.junit.Assert;
import org.junit.Test;

public class ServiceInfoMessageBodyReaderTest {

	@Test
	public void testIsReadable() {
		ServiceMessageBodyReader actionsMessageBodyReader = new ServiceMessageBodyReader();
		Assert.assertTrue(actionsMessageBodyReader.isReadable(Service.class, null,null,null));
	}
	
	@Test
	public void testReadFrom() throws WebApplicationException, IOException {
		ServiceMessageBodyReader serviceInfoMessageBodyReader = new ServiceMessageBodyReader();
		
		
		String data = "{\"id\":\"id\",\"name\":\"name\",\"type\":\"soap\", \"additional_parameters\":[{\"name\":\"test\", \"value\":\"10\", \"index\":\"0\"},{\"name\":\"test2\", \"value\":\"2\", \"index\":\"1\"}]}";
		ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
		
		Service serviceInfo = serviceInfoMessageBodyReader.readFrom(null,null,null,null,null, bis);
		
		Assert.assertNotNull(serviceInfo);
		Assert.assertEquals("id",serviceInfo.getId());
		Assert.assertEquals("name",serviceInfo.getName());
		Assert.assertEquals("soap",serviceInfo.getType());
		Assert.assertEquals(2, serviceInfo.getAdditionalParameters().size());
		Assert.assertEquals("test", serviceInfo.getAdditionalParameters().get(0).getName());
		Assert.assertEquals("10", serviceInfo.getAdditionalParameters().get(0).getValue());
		Assert.assertEquals(new Integer(0), serviceInfo.getAdditionalParameters().get(0).getIndex());

		Assert.assertEquals("test2", serviceInfo.getAdditionalParameters().get(1).getName());
		Assert.assertEquals("2", serviceInfo.getAdditionalParameters().get(1).getValue());
		Assert.assertEquals(new Integer(1), serviceInfo.getAdditionalParameters().get(1).getIndex());
		

	}

}
