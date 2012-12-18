package org.dsol.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

public interface Resource {
	
	@GET
	@Path("/res/html/{name}")
	@Produces(value={"text/html"})	
	public String getHtml(@PathParam("name") String name);
	
	@GET
	@Path("/res/js/{name}")
	@Produces(value={"text/javascript"})	
	public String getScript(@PathParam("name") String name);

	@GET
	@Path("/res/css/{name:.*}")
	@Produces(value={"text/css"})	
	public String getStyle(@PathParam("name") String name);
	
	@GET
	@Path("/res/css/ui-lightness/images/{name:.*}")
	@Produces(value={"image/png"})	
	public byte[] getImage(@PathParam("name") String name);
}
