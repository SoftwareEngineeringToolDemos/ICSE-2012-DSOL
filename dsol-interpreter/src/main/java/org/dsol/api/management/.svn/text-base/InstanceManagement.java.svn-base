package org.dsol.api.management;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.dsol.api.Resource;
import org.dsol.config.MethodsInfo;
import org.dsol.management.Actions;
import org.dsol.management.PlannerInfo;

@Path("/management")
@Produces("application/json")
public interface InstanceManagement extends Resource {
	
	@GET
	@Path("/instances")
	public String getInstances();

	@GET
	@Path("/instances/{refid}")
	public String getInstance(@PathParam("refid") String refId);	
	
	@POST
	@Path("/instances/{refid}")
	@Consumes("application/json")
	public void updateInstance(	@PathParam("refid") String refId, 	
								@QueryParam("all") boolean applyForAllInstances, 
								Actions actions);
	

	@POST
	@Path("/instances/{refid}/test_plan")
	@Consumes("application/json")
	public String testPlan(	@PathParam("refid") String refId,
							PlannerInfo plannerInfo);
	
	@POST
	@Path("/instances/{refid}/methods")
	@Consumes("application/json")
	public Response changeMethodsInfo(	@PathParam("refid") String refId,
										@QueryParam("all") boolean applyForAllInstances,
										MethodsInfo methodsInfo);



}
