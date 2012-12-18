package org.dsol.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

@Path("/app")
@Produces("application/json")
public interface Application extends Resource {

	@GET
	@Path("/")
	@Produces("text/html")	
	public String index();

	@GET
	@Path("/instances/{refid}")
	@Produces("text/html")	
	public String instancePage(@PathParam("refid") String refId);
	
	@GET
	@Path("/instances/{refid}/orchestration_interface/")
	@Produces("text/html")	
	public String orchestrationInterfacePage(@PathParam("refid") String refId);
	
	@GET
	@Path("/services")
	@Produces("text/html")	
	public String servicesPage();
	
	@POST
	@Path("/instances/{refid}/upload-actions")
	@Produces("text/html")
	@Consumes("multipart/form-data")
	String uploadActions(@PathParam("refid") String refId, MultipartBody body);

	@POST
	@Path("/instances/{refid}/upload-jar")
	@Produces("text/html")
	@Consumes("multipart/form-data")
	public String uploadJar(@PathParam("refid") String refId, MultipartBody body);


}
