package org.dsol;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.dsol.planner.api.Planner;
import org.junit.Test;

public class GenericWebServiceTest {
	
	WebServiceProxy genericWebService = new WebServiceProxy(){
		@Override
		protected Planner getPlanner() {
			return null;
		}
	};
	
	@Test
	public void createArgumentsMap() throws NoSuchMethodException, SecurityException{
		
		String[] params = new String[]{"Leandro","Sales Pinto"};
		Map<String,Object> argumentsMap = genericWebService.createArgumentsMap(HelpGenericWebServiceTest.class.getMethod("help", String.class,String.class), params);
		
		Assert.assertEquals("Leandro", argumentsMap.get("arg1FormalName"));
		Assert.assertEquals("Sales Pinto", argumentsMap.get("arg2FormalName"));
		
	}
	
	@Test
	public void getInstanceKey(){
		
		Map<String,Object> argumentsMap = new HashMap<String, Object>();
		argumentsMap.put("id1", "1");
		argumentsMap.put("id2", "2");
		argumentsMap.put("id3", 3);
		
		
		
		String key = genericWebService.getInstanceKey(new String[]{"id1","id2","id3"}, argumentsMap);
		
		Assert.assertEquals("id1=1,id2=2,id3=3", key);
	}

}


