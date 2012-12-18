package org.dsol.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXB;
import javax.xml.parsers.ParserConfigurationException;

import org.dsol.config.DSOLConfig;
import org.dsol.service.config.Service;
import org.dsol.service.config.Services;
import org.dsol.util.Util;
import org.xml.sax.SAXException;


public class ServiceSelectorImpl implements ServiceSelector {
	
	private final Logger logger = Logger.getLogger(ServiceSelector.class.getName());
	
	Map<String,List<ServiceDefinition>> servicesByName;
	Map<String,ServiceDefinition> servicesById;

	public ServiceSelectorImpl(){
		this(DEFAULT_SERVICES);
	}
	
	public ServiceSelectorImpl(DSOLConfig dsolConfig){
		this(dsolConfig.getComposedServicesFile());
	}
	
	private ServiceSelectorImpl(String file){
		servicesByName = new HashMap<String, List<ServiceDefinition>>();
		servicesById = new HashMap<String, ServiceDefinition>();
		
		InputStream fileInputStream = null;
		try {
			
			logger.info("Loading services from file "+file);
			
			fileInputStream = Util.getInputStream(file);
			if(fileInputStream != null){
				populateMaps(fileInputStream);	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(fileInputStream != null){
				try {
					fileInputStream.close();
				} catch (IOException e) {}
			}
		}
	}
	
	protected void populateMaps(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException{		
		Services services = JAXB.unmarshal(inputStream, Services.class);
		if(services == null || services.getServices() == null){
			return;
		}
		for(Service service:services.getServices()){
			addService(ServiceType.createServiceDefinition(service));	
		}
	}

	@Override
	public void addService(ServiceDefinition serviceDef) {				
		servicesById.put(serviceDef.getId(), serviceDef);
		addToServiceByNameMap(serviceDef);
	}
	
	private void addToServiceByNameMap(ServiceDefinition serviceDefinition){
		List<ServiceDefinition> servicesWithSameName = null;
		String name = serviceDefinition.getName();
		if(servicesByName.containsKey(name)){
			servicesWithSameName = servicesByName.get(name);
		}
		else{
			servicesWithSameName = new ArrayList<ServiceDefinition>();
			servicesByName.put(name, servicesWithSameName);
		}
		servicesWithSameName.add(serviceDefinition);
	}


	public List<ServiceDefinition> getServiceList(String service) {
		List<ServiceDefinition> services = servicesByName.get(service);
		if(services == null){
			return new ArrayList<ServiceDefinition>();
		}
		List<ServiceDefinition> servicesClone = new ArrayList<ServiceDefinition>(services.size());
		for(ServiceDefinition serviceDefinition:services){
			servicesClone.add(serviceDefinition.clone());
		}
		return servicesClone;
	}

	@Override
	public ServiceDefinition getService(String id) {
		return servicesById.get(id);
	}

	@Override
	public void deleteService(String id) {
		ServiceDefinition service = servicesById.remove(id);
		if(service != null){
			List<ServiceDefinition> services = servicesByName.get(service.getName());
			services.remove(service);
			if(services.isEmpty()){
				servicesByName.remove(service.getName());
			}
		}		
	}

	@Override
	public Collection<String> getServicesName() {
		return new ArrayList<String>(servicesByName.keySet());
	}
}
