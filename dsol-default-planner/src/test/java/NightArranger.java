import graphplan.Graphplan;
import graphplan.PlanResult;
import graphplan.domain.DomainDescription;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.PlanningGraphException;
import graphplan.parser.ParseException;
import graphplan.parser.PlannerParser;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


public class NightArranger {
	
	@Test
	public void plan() throws ParseException, PlanningGraphException, OperatorFactoryException{
		PlannerParser parser = new PlannerParser();
		
		DomainDescription domain = parser.parseProblem(	this.getClass().getResourceAsStream("/night-arranger/actions.txt"), 
														this.getClass().getResourceAsStream("/night-arranger/initial_state_and_goal.txt"));

		
		Graphplan graphplan = new Graphplan();
		PlanResult planResult = graphplan.plan(domain);
				
		PlanResult subPlan = planResult.getSubPlanContaining("getMap(restaurants)");
		
		Assert.assertEquals("[0] searchRestaurants(userDefinedLocation)\n[1] getMap(restaurants)\n", subPlan.toString());
		
		subPlan = planResult.getSubPlanContaining("sendText(participants,selectedVenue,selectedTheater,selectedMovie)");
	
		System.out.println(subPlan);
		
	}
	
	@Test
	public void multiplan() throws ParseException, PlanningGraphException, OperatorFactoryException{
		
		System.out.println("---------HERE--------");
		
		PlannerParser parser = new PlannerParser();
		
		DomainDescription domain = parser.parseProblem(	this.getClass().getResourceAsStream("/night-arranger/actions.txt"), 
														this.getClass().getResourceAsStream("/night-arranger/initial_state_and_goal.txt"));

		
		Graphplan graphplan = new Graphplan();
		List<PlanResult> planResult = graphplan.plan(domain,true);
	
		System.out.println(planResult);
		
	}
}
