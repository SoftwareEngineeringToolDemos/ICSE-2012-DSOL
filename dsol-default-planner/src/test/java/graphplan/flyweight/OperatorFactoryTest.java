package graphplan.flyweight;

import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.graph.ActionLevel;
import graphplan.graph.PlanningGraphException;
import graphplan.graph.PropositionLevel;
import graphplan.parser.ParseException;
import graphplan.parser.PlannerParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class OperatorFactoryTest {

	@Test
	public void test() throws OperatorFactoryException, ParseException, PlanningGraphException {

		PlannerParser parser = new PlannerParser();
		DomainDescription domain = parser.parseProblem(this.getClass()
				.getResourceAsStream("/complex_actions.txt"), this.getClass()
				.getResourceAsStream("/factsComplex.txt"));
		PropositionLevel propositionLevel = new PropositionLevel();
		propositionLevel.addPropositions(domain.getInitialState());
		OperatorFactory.getInstance().resetOperatorTemplates();
		for (Operator operator : domain.getOperators()) {
			OperatorFactory.getInstance().addOperatorTemplate(operator);
		}
		
		long init = System.currentTimeMillis();
		System.out.println(propositionLevel);
		final ActionLevel actionLevel = new ActionLevel();
		
		OperatorFactory opFactory = OperatorFactory.getInstance();
		LinkedHashSet<Operator> opTemplateSet = new LinkedHashSet<Operator>();
		Set<Operator> opSet = new LinkedHashSet<Operator>();
		Set<Proposition> preconds = new HashSet<Proposition>(); 
				
		//TODO Change this to scan by operator rather than by proposition
		//operatorTemplate
		opTemplateSet.addAll(opFactory.getRequiringOperatorTemplates(propositionLevel));

		// For every proposition
		for (Proposition proposition : propositionLevel) {
			//Add all noops
			opSet.add(opFactory.getNoop(proposition));
			//And prepare the list of preconditons for later
			preconds.add(proposition);
		}
		
		//Get operators with empty preconditions
		opTemplateSet.addAll(opFactory.getRequiringOperatorTemplates((Proposition)null));


		//Piece of crap algorithm used before has been replaced by this call
		try {
			long init2 = System.currentTimeMillis();
			opSet.addAll(opFactory.getAllPossibleInstantiations(new ArrayList<Operator>(opTemplateSet), preconds));
			System.out.println(opSet);
			long end = System.currentTimeMillis();
			System.out.println((end-init2));
			System.out.println((end-init));
			
			List<String> expectedpossibleInst = Arrays.asList(new String[]{"noop_initial1(a)", "noop_initial2(b)", "noop_initial3(c)", "noop_initial0(d)", "noop_initial1(e)", "noop_initial2(f)", "noop_initial3(g)", "noop_initial0(h)", "action0(a,f,c,h)", "action0(e,f,c,h)", "action0(a,b,c,h)", "action0(e,b,c,h)", "action0(a,f,g,h)", "action0(e,f,g,h)", "action0(a,b,g,h)", "action0(e,b,g,h)", "action0(a,f,c,d)", "action0(e,f,c,d)", "action0(a,b,c,d)", "action0(e,b,c,d)", "action0(a,f,g,d)", "action0(e,f,g,d)", "action0(a,b,g,d)", "action0(e,b,g,d)"});
			String[] returned = new String[opSet.size()];
			
			Assert.assertEquals(expectedpossibleInst.size(), returned.length);
			
			int i = 0;
			for(Operator op:opSet){
				Assert.assertTrue(expectedpossibleInst.contains(op.toString()));
			}
			
			
			
			
			//Assert.assertEquals(, opSet.toString());
		} catch (OperatorFactoryException e) {
			throw new PlanningGraphException(e.getMessage(),propositionLevel.getIndex()+1);
		}
		for (Operator operator : opSet) {
			actionLevel.addAction(operator);
		}

	}

}
