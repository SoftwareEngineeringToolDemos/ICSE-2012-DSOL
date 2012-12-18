package org.dsol.service;

import java.util.Collection;
import java.util.List;


public interface ServiceSelector {

	public static final String DEFAULT_SERVICES = "classpath:dsol-composed-services.xml";
	
	public Collection<String> getServicesName();
	
	public List<ServiceDefinition> getServiceList(String service);

	public void addService(ServiceDefinition serviceDef);
	
	public ServiceDefinition getService(String id);
	
	public void deleteService(String id);

}
