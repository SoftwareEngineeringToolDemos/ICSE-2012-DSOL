package org.dsol.api.services;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.dsol.service.ServiceDefinition;
import org.dsol.service.ServiceSelector;
import org.dsol.service.ServiceType;
import org.dsol.service.config.Service;
import org.dsol.service.config.Services;

public class ServicesManagementImpl implements ServicesManagement {
	
	private ServiceSelector serviceSelector;
	
	public ServicesManagementImpl(ServiceSelector serviceSelector){
		this.serviceSelector = serviceSelector;
	}

	@Override
	public Response getServicesByName(String servicesName) {
		Services services = getServicesInfoByName(servicesName);
		if(services == null){
			return	Response.status(Status.NOT_FOUND).build();
		}
		
		return Response.ok(services).build();
	}

	@Override
	public Response addService(String servicesName, Service service) {
		if(service == null ||
		   (service.getId() == null || service.getId().isEmpty()) ||
		   (service.getName() == null || service.getName().isEmpty()) ||
		   (service.getType() == null || service.getType().isEmpty()) ||
		   (!service.getName().equalsIgnoreCase(servicesName))){
			return Response.status(Status.BAD_REQUEST).build();
		}
		ServiceDefinition serviceDefinition = serviceSelector.getService(service.getId());
		if(serviceDefinition != null){
			return Response.status(Status.CONFLICT).build();
		}		
		serviceSelector.addService(ServiceType.createServiceDefinition(service));
		return Response.ok().build();
	}

	@Override
	public Response getServiceById(String servicesName, String serviceId) {
		ServiceDefinition serviceDef = serviceSelector.getService(serviceId);
		if(serviceDef == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		
		return Response.ok(serviceDef.getService()).build();
	}

	@Override
	public Response deleteServiceById(String servicesName, String serviceId) {
		ServiceDefinition serviceDef = serviceSelector.getService(serviceId);
		if(serviceDef == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		serviceSelector.deleteService(serviceId);
		return Response.ok().build();
	}

	@Override
	public Response getServices() {
		
		Collection<String> names = serviceSelector.getServicesName();
		Services services = new Services();
		
		for(Iterator<String> it = names.iterator();it.hasNext();){
			String serviceName = it.next();
			services.addServices(getServicesInfoByName(serviceName));
		}
		
		return Response.ok(services).build();
	}

	
	private Services getServicesInfoByName(String servicesName){
		List<ServiceDefinition> servicesDefinitions =  serviceSelector.getServiceList(servicesName);
		if(servicesDefinitions == null){
			return new Services();
		}
		
		Services services = new Services();
		for(ServiceDefinition serviceDefinition:servicesDefinitions){
			services.addService(serviceDefinition.getService());			
		}
		return services;
	}

	@Override
	public Response updateService(String serviceName,String serviceId, Service service) {
		ServiceDefinition serviceDef = serviceSelector.getService(serviceId);
		if(serviceDef == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		if(serviceDef.getName().equals(serviceName)){
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		return addService(serviceName, service);
	}
	
}
