package org.dsol.service;
import javax.xml.ws.Endpoint;


public class HelloServiceApp {
	
	public static void main(String... args){	
		Object serviceImpl = new Hello();

		System.out.println("Starting Server");
		Endpoint.publish("http://localhost:8080/hello", serviceImpl);
	}

}
