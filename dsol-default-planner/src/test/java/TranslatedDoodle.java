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


public class TranslatedDoodle {
	
	@Test
	public void plan() throws ParseException, PlanningGraphException, OperatorFactoryException{
		PlannerParser parser = new PlannerParser();
		DomainDescription domain = parser.parseProblem(	this.getClass().getResourceAsStream("/poll-translator/abstract_actions.dsol"), 
														this.getClass().getResourceAsStream("/poll-translator/initial_state_and_goal.txt") );

		Graphplan graphplan = new Graphplan();
		List<PlanResult> plans = graphplan.multiplan(domain);

		Assert.assertEquals(2, plans.size());
		
		
	}

}
