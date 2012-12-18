package org.dsol.service.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Services {
	
	public Services() {
		services = null;
		servicesList = null;
	}
	
	//Represents a group of groups of services with different names
    private List<Services> servicesList;

	//Represents a group of services with same name
    private String servicesName; 
    private List<Service> services;
    
    @XmlElement(name="service")
	public List<Service> getServices() {
		return services;
	}
	
	public void setServices(List<Service> services) {
		this.services = services;
	}
	
	public void addService(Service service){
		if(services == null){
			servicesName = service.getName();
			services = new ArrayList<Service>();
		}
		if(!service.getName().equals(servicesName)){
			throw new RuntimeException("Service name does not match with this group");
		}
		services.add(service);
	}


	public void addServices(Services services){
		if(this.servicesList == null){
			this.servicesList = new ArrayList<Services>();
		}
		this.servicesList.add(services);
	}
}
