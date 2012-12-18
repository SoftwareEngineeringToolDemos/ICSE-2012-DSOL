import graphplan.Graphplan;
import graphplan.domain.DomainDescription;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.PlanningGraphException;
import graphplan.parser.ParseException;
import graphplan.parser.PlannerParser;

import org.junit.Test;

public class Blockworld {

	@Test
	public void plan() throws ParseException, PlanningGraphException,
			OperatorFactoryException {
		PlannerParser parser = new PlannerParser();
		DomainDescription domain = parser.parseProblem(this.getClass()
				.getResourceAsStream("/blocksActions.txt"), this.getClass()
				.getResourceAsStream("/factsBlocks.txt"));

		Graphplan graphplan = new Graphplan();
		//OperatorFactory.getInstance().addInvalidOperatorInstantiation("pickUp(block2,table,rightHand)");

		System.out.println(graphplan.plan(domain));

	}
}
