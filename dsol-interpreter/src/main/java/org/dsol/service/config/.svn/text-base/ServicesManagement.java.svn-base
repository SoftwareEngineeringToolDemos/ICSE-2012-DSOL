package org.dsol.service.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path("/services")
@Produces("application/json")
public interface ServicesManagement {

	@GET
	@Path("/")
	public Response getServices();
	
	@GET
	@Path("/{name}")
	public Response getServicesByName(@PathParam("name")String servicesName);
	
	@POST
	@Path("/{name}")
	@Consumes("application/json")
	public Response addService(@PathParam("name")String servicesName, Service service);
	
	@GET
	@Path("/{name}/{id}")
	public Response getServiceById(@PathParam("name")String servicesName, @PathParam("id")String serviceId);
	
	@PUT
	@Path("/{name}/{id}")
	@Consumes("application/json")
	public Response updateService(@PathParam("name")String servicesName, @PathParam("id")String servicesId, Service service);

	
	@DELETE
	@Path("/{name}/{id}")
	public Response deleteServiceById(@PathParam("name")String servicesName, @PathParam("id")String serviceId);
	

}
