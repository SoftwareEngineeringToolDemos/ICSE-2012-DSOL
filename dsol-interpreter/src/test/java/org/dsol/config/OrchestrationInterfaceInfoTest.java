package org.dsol.config;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.dsol.planner.api.Planner;
import org.dsol.service.OrchestrationInterface;
import org.junit.Assert;
import org.junit.Test;

public class OrchestrationInterfaceInfoTest {

	@Test
	public void testGetMethods() throws NoSuchMethodException, SecurityException, IOException{
		DSOLConfig dsolConfig = new DSOLConfig();
		Properties properties = new Properties();
		properties.put("Orchestration_Interface", OrchestrationInterface.class.getName());
		properties.put("helloWorld.goal", "classpath:helloWorld_goal.dsol");
		properties.put("translate.goal", "classpath:translate_goal.dsol");
		
		dsolConfig.setProperties(properties);
		
		OrchestrationInterfaceInfo orchestrationInterfaceInfo = new OrchestrationInterfaceInfo(dsolConfig);
		
		MethodInfo method1 = new MethodInfo(OrchestrationInterface.class.getMethod("helloWorld", String.class, String.class), Planner.EMPTY_INITIAL_STATE,Planner.EMPTY_GOAL);
		MethodInfo method2 = new MethodInfo(OrchestrationInterface.class.getMethod("translate", String.class),Planner.EMPTY_INITIAL_STATE,Planner.EMPTY_GOAL);
		
		List<MethodInfo> methods = orchestrationInterfaceInfo.getMethods();
		
		Assert.assertEquals(2, methods.size());
		Assert.assertTrue(methods.contains(method1));
		Assert.assertTrue(methods.contains(method2));
		
	}
	
}
