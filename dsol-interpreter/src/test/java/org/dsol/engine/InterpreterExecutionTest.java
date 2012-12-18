package org.dsol.engine;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import junit.framework.Assert;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.dsol.service.Hello;
import org.dsol.service.HelloReverse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InterpreterExecutionTest {
	
	public static final String ADDRESS = "http://localhost:8086/hello";
	public static final String WSDL = ADDRESS + "?wsdl";

	private static Endpoint endpointHello;
	private static Endpoint endpointHelloReverse;
	
	@Before
	public void setUpTest(){
		Object hello = new Hello();
		endpointHello = Endpoint.publish("http://localhost:8086/hello", hello);
		Object helloReverse = new HelloReverse();
		endpointHelloReverse = Endpoint.publish("http://localhost:8087/helloReverse", helloReverse);
		
		DSOLServer.start(null);
		
		//while(!(DSOLServer.isPublished())){}
		
	}
	
	@After
	public void tearDownTest(){
		if(endpointHello.isPublished()){
			endpointHello.stop();	
		}
		if(endpointHelloReverse.isPublished()){
			endpointHelloReverse.stop();
		}
		DSOLServer.stop();
	}
	
	@Test
	public void execution1() throws Exception{
		DynamicClientFactory dcf = DynamicClientFactory.newInstance();

		Client client = dcf.createClient("http://localhost:8085/dsol_test/?wsdl",
										new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLService"),
										new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLPort"));

		Object[] res = client.invoke("helloWorld", "Leandro", "Sales");
		Assert.assertEquals("Hi, Leandro SALES", res[0]);
		
	}
	
	/**
	 * In this test, the first "hi" service is shutdown. THe second one should respond
	 * @throws Exception
	 */
	@Test
	public void execution2() throws Exception{
		endpointHello.stop();//shutdown the first "hi" service
		
		DynamicClientFactory dcf = DynamicClientFactory.newInstance();

		Client client = dcf.createClient("http://localhost:8085/dsol_test/?wsdl",
											new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLService"),
											new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLPort"));

		Object[] res = client.invoke("helloWorld", "Leandro", "Sales");
		Assert.assertEquals("Hi, SALES Leandro", res[0]);
	}
	
	/**
	 * In this test, the second "hi" service is shutdown. NO more services are available. A GenericAction is
	 * available when non of the services respond.
	 * @throws Exception
	 */
	@Test
	public void execution3() throws Exception{
		endpointHello.stop();//shutdown the first "hi" service
		endpointHelloReverse.stop();//shutdown the second "hi" service
		
		DynamicClientFactory dcf = DynamicClientFactory.newInstance();

		Client client = dcf.createClient("http://localhost:8085/dsol_test/?wsdl",
											new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLService"),
											new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLPort"));

		Object[] res = client.invoke("helloWorld", "Leandro", "Sales");
		Assert.assertEquals("Generic, Leandro Sales", res[0]);
	}
	
	/**
	 * The GenericAction available for the hello abstract action fails when the name is Pedro. In this case, all the plan should
	 * fail. Another plan will be built wing the hello2 action, that is implemented by ine GenericAction 
	 * 
	 * @throws Exception
	 */
	@Test
	public void execution4() throws Exception{
		endpointHello.stop();//shutdown the first "hi" service
		endpointHelloReverse.stop();//shutdown the second "hi" service

		DynamicClientFactory dcf = DynamicClientFactory.newInstance();

		Client client = dcf.createClient("http://localhost:8085/dsol_test/?wsdl",
											new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLService"),
											new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLPort"));

		Object[] res = client.invoke("helloWorld", "Pedro", "Arraes");
		Assert.assertEquals("Generic2, Arraes[c]", res[0]);
	}
	
	@Test
	public void testInstance() throws Exception{

		DynamicClientFactory dcf = DynamicClientFactory.newInstance();

		Client client = dcf.createClient("http://localhost:8085/dsol_test/?wsdl",
											new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLService"),
											new QName("http://service.dsol.org/", "OrchestrationInterfaceDSOLPort"));

		Object[] res = client.invoke("helloWorld", "Leandro", "Sales");
		Assert.assertEquals("Hi, Leandro SALES", res[0]);
		
		res = client.invoke("translate", "Leandro");
		Assert.assertEquals("Hi, Leandro SALES [translated]", res[0]);
		
		try{
			res = client.invoke("translate", "Leandro");
			Assert.fail();
		}
		catch (Exception e) {
			Assert.assertEquals("Instance with key clientName=Leandro not found",e.getMessage());
		}
	}
	
	
}
