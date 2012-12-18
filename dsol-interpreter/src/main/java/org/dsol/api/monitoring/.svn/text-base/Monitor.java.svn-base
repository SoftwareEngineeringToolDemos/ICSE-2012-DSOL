package org.dsol.api.monitoring;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.dsol.api.Resource;


@Path("/monitoring")
@Produces("application/json")
public interface Monitor extends Resource{
	
	
	@GET
	@Path("/app")
	@Produces("text/html")	
	public String index();
	
	@GET
	@Path("/actions/")
	public String getActions();
	
	
	

}
