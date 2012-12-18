package org.dsol.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class Index {
	
	@GET
	@Produces(value={"text/html"})
	public String redirect(){
		return "<html><head><meta http-equiv=\"Refresh\" content=\"0;url=api/app\" /></head></html>";
	}	
}
