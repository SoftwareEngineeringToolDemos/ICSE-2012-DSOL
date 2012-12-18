package org.dsol.service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.dsol.service.config.Service;

public class SOAPService extends ServiceDefinition{
	
	private static Map<String,Client> clients = new HashMap<String, Client>();
	
	private final static Logger logger = Logger.getLogger(SOAPService.class.getName());
	
	Client client = null;

	public SOAPService(Service serviceMetadata) {
		super(serviceMetadata);
		String wsdl = serviceMetadata.getWsdl();
		client = clients.get(wsdl);
		if(client == null){
			try {
				DynamicClientFactory dcf = DynamicClientFactory.newInstance();
				client = dcf.createClient(wsdl);
				clients.put(wsdl, client);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public SOAPService(	Service serviceMetadata,
						Client client) {

		super(serviceMetadata);
		this.client = client;
	}
	
	
	public Client getClient(){
		return client;
	}
	
	public Response invoke(Request request) throws Exception {
		
		logger.info("Invoking service " + serviceData.getName()
				+ ", operation {" + serviceData.getOperationNamespaceUri()
				+ "}" + serviceData.getOperation() + " at "
				+ serviceData.getWsdl());
		
		Response response = new Response();
		
		Object[] res = client.invoke(new QName(serviceData.getOperationNamespaceUri(),serviceData.getOperation()), request.getParams());
		

		if(res != null && res.length > 0){
			response.setReturnValue(res[0]);
		}
		return response;
	}
	
	@Override
	public String toString() {
		return  "SOAP SERVICE\n"+
				"Service id:"+serviceData.getId()+"\n"+
				"Service name: "+serviceData.getName()+ " \n"+
				"Service wsdl: "+serviceData.getWsdl()+ " \n"+
				"Service operation: "+serviceData.getOperation()+ " \n";
	}
	
	@Override
	public ServiceDefinition clone(){		
		SOAPService clone = new SOAPService(serviceData.clone(), client);
		return clone;
	}


//	@Override
//	public ServiceInfo createServiceInfo() {
//		return ServiceInfo.createSoapService(getId(), getName(), url, operation, operationNamespaceURI, getRequestHeadersAsString(), getResponseHeadersAsString());
//	}
}
