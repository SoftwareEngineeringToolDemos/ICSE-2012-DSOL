package org.dsol.service;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import junit.framework.Assert;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServiceTest {

	public static final String ADDRESS = "http://localhost:8086/hello";
	public static final String WSDL = ADDRESS + "?wsdl";
	public static final String PORT_TYPE = "Hello";
	public static final String OPERATION = "hi";
	private static Endpoint endpoint;

	@BeforeClass
	public static void start() {
		Object serviceImpl = new Hello();

		System.out.println("Starting Server");
		endpoint = Endpoint.publish(ADDRESS, serviceImpl);
	}

	@Test
	public void testService() throws Exception {
		DynamicClientFactory dcf = DynamicClientFactory.newInstance();
		Client client = dcf.createClient("http://localhost:8086/hello?wsdl");

		Object[] res = client.invoke("hi", "Leandro", "Sales");
		Assert.assertEquals("Hi, Leandro SALES", res[0]);
	}

	@Test
	public void testService2() throws Exception {
		DynamicClientFactory dcf = DynamicClientFactory.newInstance();

		Client client = dcf.createClient("http://localhost:8086/hello?wsdl",
				new QName("http://service.dsol.org/", "HelloService"),
				new QName("http://service.dsol.org/", "HelloPort"));

		Object[] res = client.invoke("hi", "Leandro", "Sales");
		Assert.assertEquals("Hi, Leandro SALES", res[0]);
	}

	@AfterClass
	public static void stop() {
		endpoint.stop();
	}
}
