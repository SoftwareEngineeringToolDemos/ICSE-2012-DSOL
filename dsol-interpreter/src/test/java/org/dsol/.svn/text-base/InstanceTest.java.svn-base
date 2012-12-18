package org.dsol;

import junit.framework.Assert;

import org.junit.Test;

public class InstanceTest {

	@Test
	public void testSetId() {
		
		InstanceSession instanceSession = new InstanceSession();
		
		instanceSession.put("id1", "1");
		instanceSession.put("id2", "2");
		instanceSession.put("id3", 3);
		Instance instance = new Instance(null, instanceSession,null, null,null,null);
		
		instance.setId(new String[]{"id1","id2","id3"});
		
		Assert.assertEquals("id1=1,id2=2,id3=3", instance.getId());
		
	}

}
