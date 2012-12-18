package org.dsol.api.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.dsol.config.MethodInfo;
import org.dsol.config.MethodsInfo;
import org.junit.Assert;
import org.junit.Test;

public class MethodsInfoMessageBodyReaderTest {

	@Test
	public void testIsReadable() {
		MethodsInfoMessageBodyReader methodsInfoMessageBodyReader = new MethodsInfoMessageBodyReader();
		Assert.assertTrue(methodsInfoMessageBodyReader.isReadable(MethodsInfo.class, null,null,null));
	}
	
	@Test
	public void testReadFrom() throws WebApplicationException, IOException {
		MethodsInfoMessageBodyReader methodsInfoMessageBodyReader = new MethodsInfoMessageBodyReader();
		
		String data = "{\"methods_info\":[{\"name\":\"name\",\"initial_state\":\"is\",\"goal\":\"goal\"}]}";
		ByteArrayInputStream bis = new ByteArrayInputStream(data.getBytes());
		
		MethodsInfo methodsInfo = methodsInfoMessageBodyReader.readFrom(null,null,null,null,null, bis);
		
		Assert.assertNotNull(methodsInfo);
		Assert.assertNotNull(methodsInfo.getMethodsInfo());
		Assert.assertEquals(1, methodsInfo.getMethodsInfo().size());
		
		List<MethodInfo> list = new ArrayList<MethodInfo>();
		list.addAll(methodsInfo.getMethodsInfo());
		
		Assert.assertEquals("name", list.get(0).getName());
		Assert.assertEquals("is", list.get(0).getInitialState());
		Assert.assertEquals("goal", list.get(0).getGoal());
		
	}

}
