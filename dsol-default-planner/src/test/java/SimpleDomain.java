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


public class SimpleDomain {
	@Test
	public void plan() throws ParseException, PlanningGraphException, OperatorFactoryException{
		PlannerParser parser = new PlannerParser();
		DomainDescription domain = parser.parseProblem(	this.getClass().getResourceAsStream("/actionsSimple.txt"), 
														this.getClass().getResourceAsStream("/factsSimple.txt"));

		Graphplan graphplan = new Graphplan();
		List<PlanResult> plans = graphplan.plan(domain,true);
		
		Assert.assertEquals(3, plans.size());
		
		
	}
}
