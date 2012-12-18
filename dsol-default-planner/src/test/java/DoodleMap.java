import java.util.List;

import junit.framework.Assert;
import graphplan.Graphplan;
import graphplan.PlanResult;
import graphplan.domain.DomainDescription;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.PlanningGraphException;
import graphplan.parser.ParseException;
import graphplan.parser.PlannerParser;

import org.junit.Test;


public class DoodleMap {
	
	@Test
	public void plan() throws ParseException, PlanningGraphException, OperatorFactoryException{
		PlannerParser parser = new PlannerParser();
		
		DomainDescription domain = parser.parseProblem(	this.getClass().getResourceAsStream("/doodle-map/actions.txt"), 
														this.getClass().getResourceAsStream("/doodle-map/initial_state_and_goal.txt"));

		
		Graphplan graphplan = new Graphplan();
		List<PlanResult> results = graphplan.multiplan(domain);
		Assert.assertEquals(6, results.size());	
	}

}
