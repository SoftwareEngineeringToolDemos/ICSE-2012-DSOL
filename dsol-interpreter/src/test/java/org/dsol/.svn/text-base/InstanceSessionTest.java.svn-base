package org.dsol;

import junit.framework.Assert;

import org.junit.Test;

public class InstanceSessionTest {

	InstanceSession instanceSession = new InstanceSession();
	
	@Test
	public void test() {
		instanceSession.put("number", "1");
		Assert.assertEquals("1", instanceSession.get("number"));
		
		instanceSession.remove("number");
		Assert.assertNull(instanceSession.get("number"));
	}
	
	@Test(expected=RuntimeException.class)
	public void testNotFound() {
		instanceSession.get("number.test");
	}
	
	@Test
	public void populate(){
		InstanceSession instanceSession = new InstanceSession();
		InstanceSession instanceSessionToPopulate = new InstanceSession();
		
		instanceSession.put("name","Leandro");
		instanceSession.put("last_name", "Pinto");
		instanceSession.put("age", 28);

		Description desc = new Description("Test");
		DescriptionNotCloneable desc2 = new DescriptionNotCloneable("Test");
		instanceSession.put("desc", desc);
		instanceSession.put("desc2", desc2);
		
		instanceSession.populate(instanceSessionToPopulate);
		
		desc.description = "new description";
		desc2.description = "new description";
		
		Assert.assertEquals("Leandro", instanceSessionToPopulate.get("name"));
		Assert.assertEquals("Pinto", instanceSessionToPopulate.get("last_name"));
		Assert.assertEquals(28, instanceSessionToPopulate.get("age"));
		Assert.assertEquals("Test", instanceSessionToPopulate.get("desc.description"));//As this property is cloneable, 
																					// the value should not be updated to "new description"
		Assert.assertEquals("new description", instanceSessionToPopulate.get("desc2.description"));//As this property is NOT cloneable, 
																					   // the value should be updated to "new description"
		
		
		
	}

	public class DescriptionNotCloneable{
		String description;
		
		public DescriptionNotCloneable(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	public class Description implements Cloneable{
		String description;
		
		public Description(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException {
			return new Description(description);
		}
	}
}
