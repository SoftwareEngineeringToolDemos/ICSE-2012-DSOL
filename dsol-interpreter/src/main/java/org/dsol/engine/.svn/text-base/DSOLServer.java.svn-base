package org.dsol.engine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Endpoint;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.dsol.api.util.ActionsMessageBodyReader;
import org.dsol.api.util.ListMessageBodyWriter;
import org.dsol.api.util.MethodsInfoMessageBodyReader;
import org.dsol.api.util.PlannerInfoMessageBodyReader;
import org.dsol.api.util.ServiceMessageBodyReader;
import org.dsol.api.util.ServicesRelatedMessageBodyWriter;
import org.dsol.config.DSOLConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sun.istack.logging.Logger;


public class DSOLServer {
	
	private static Endpoint endpoint;
	private static Server apiServer;
	private static Logger logger = Logger.getLogger(DSOLServer.class);
	
	public static void main(String[] args) throws IOException{

		String configFile = null;
		if(args.length > 0){
			configFile = args[0];
		}
		start(configFile);

	}
	
	public static void start(String configFile){
		
		if(configFile == null){
			configFile = "classpath:META-INF/dsol/dsol-default-config.xml";
		}
		
		logger.info("Starting Server...");
		
		ApplicationContext context = new ClassPathXmlApplicationContext(configFile);
		
		DSOLConfig config = (DSOLConfig) context.getBean("dsolConfig");
		
		Object serviceImpl = context.getBean("dsol-endpoint");
		
		String address = getAddress(config);
		endpoint = Endpoint.publish(address, serviceImpl);
		
		logger.info("Web service available at: "+address+"/?wsdl");		

		try {
		
			JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
			List<Object> beans = new ArrayList<Object>();
			List<Object> providers = new ArrayList<Object>();
			
			beans.add(context.getBean("dsol-index"));
			beans.add(context.getBean("dsol-application"));
			beans.add(context.getBean("dsol-instance-management"));
			beans.add(context.getBean("dsol-services-management"));
			
			sf.setServiceBeans(beans);
			
			String managementAddress = getApiAddress(config);
			sf.setAddress(managementAddress);
			
			//MessageBodyReaders
			providers.add(new ActionsMessageBodyReader());
			providers.add(new PlannerInfoMessageBodyReader());
			providers.add(new ServiceMessageBodyReader());
			providers.add(new MethodsInfoMessageBodyReader());
			
			//MessageBodyWriters
			providers.add(new ServicesRelatedMessageBodyWriter());
			providers.add(new ListMessageBodyWriter());
			
			sf.setProviders(providers);
			
			apiServer = sf.create();
			logger.info("DSOL Web Application available at: "+managementAddress);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static String getAddress(DSOLConfig config){
		return "http://localhost:"+config.getPort()+"/"+config.getContext();
	}
	
	public static String getApiAddress(DSOLConfig config) throws MalformedURLException{
		return getAddress(config)+"/api";
	}
		
	public static boolean isPublished(){
		return endpoint.isPublished();
	}
	
	public static void stop(){
		if(endpoint.isPublished()){
			endpoint.stop();
			if(apiServer != null){
				apiServer.stop();	
			}
		}
		
	}
}
