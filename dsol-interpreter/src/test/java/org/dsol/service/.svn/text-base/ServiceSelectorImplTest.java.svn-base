package org.dsol.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Endpoint;

import junit.framework.Assert;

import org.dsol.ClassWithServiceAction;
import org.dsol.annotation.Action;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class ServiceSelectorImplTest {

	static ServiceSelector selector;
	static Endpoint endpointHello;
	static Endpoint endpointHelloReverse;
	
	@Before
	public void setUpTest() throws ParserConfigurationException, SAXException, IOException{
		Object hello = new Hello();
		endpointHello = Endpoint.publish("http://localhost:8086/hello", hello);
		Object helloReverse = new HelloReverse();
		endpointHelloReverse = Endpoint.publish("http://localhost:8087/helloReverse", helloReverse);
		selector = new ServiceSelectorImpl();
	}
	
	@After
	public void tearDownTest(){
		if(endpointHello.isPublished()){
			endpointHello.stop();	
		}
		if(endpointHelloReverse.isPublished()){
			endpointHelloReverse.stop();
		}
	}
	
	@Test
	public void getService() throws FileNotFoundException {
		
		
		List<ServiceDefinition> services = selector.getServiceList("hi");
		Assert.assertEquals(2, services.size());

		ServiceDefinition service = services.get(0);
		Assert.assertEquals("hi1", service.getId());

		service = services.get(1);
		Assert.assertEquals("hi_reverse", service.getId());
	}

	@Test
	public void testInvocation1() {


		HelloTest action = (HelloTest) ClassWithServiceAction.newInstance(HelloTest.class, selector, null);
		String returnValue = action.hi("Leandro", "Sales");

		Assert.assertEquals("Hi, Leandro SALES", returnValue);

		action = (HelloTest) ClassWithServiceAction.newInstance(HelloTest.class, selector, null);

		returnValue = action.bye("Leandro", "Sales");

		Assert.assertEquals("Bye bye, Leandro SALES", returnValue);

	}

	@Test
	public void testInvocation2() {
		
		//">>> In this test, first should appear an exception because the first service \"hi\" is unavailable! This is exactly what we want to test");
		endpointHello.stop();

		HelloTest action = (HelloTest) ClassWithServiceAction.newInstance(HelloTest.class, selector, null);
		String returnValue = action.hi("Leandro", "Sales");

		Assert.assertEquals("Hi, SALES Leandro", returnValue);

	}
}

abstract class HelloTest {
	@Action(service = "hi")
	public abstract String hi(String name, String surname);

	@Action(service = "compensate_hi", compensation = true)
	public abstract String bye(String name, String surname);
}
