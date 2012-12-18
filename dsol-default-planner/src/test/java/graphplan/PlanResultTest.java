package graphplan;

import graphplan.PlanResult;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.domain.jason.PropositionImpl;
import graphplan.parser.ParseException;
import graphplan.parser.PlannerParser;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import junit.framework.Assert;

import org.junit.Test;


public class PlanResultTest {
	
	
	@Test
	public void testReorderLevel() throws ParseException {


		List<Operator> operators = getOperators();
		
		Operator a1 = operators.get(0);
		Operator a2 = operators.get(1);
		Operator a3 = operators.get(2);
		
		Stack<Set<Operator>> plan1Actions = new Stack<Set<Operator>>();
		Set level1 = new HashSet<Operator>();
		level1.add(a1);
		Set level2 = new HashSet<Operator>();
		level2.add(a2);
		Set level3 = new HashSet<Operator>();
		level3.add(a3);
		
		
		plan1Actions.add(level3);
		plan1Actions.add(level2);
		plan1Actions.add(level1);
		
		PlanResult plan1 = new PlanResult(plan1Actions);
		
		Proposition a = new PropositionImpl("a");
		List<Proposition> previousState = Arrays.asList(a);
	
		int levelIndex = 1;
		plan1.reorderLevel(levelIndex, previousState);
	
		Assert.assertEquals(Arrays.asList(a1,a2), plan1.getSteps().get(0));
	}
	
	@Test
	public void buildStatesList() throws ParseException {


		List<Operator> operators = getOperators();
		
		Operator a1 = operators.get(0);
		Operator a2 = operators.get(1);
		Operator a3 = operators.get(2);
		Operator a4 = operators.get(3);
		
		Stack<Set<Operator>> plan1Actions = new Stack<Set<Operator>>();
		Set level1 = new HashSet<Operator>();
		level1.add(a1);
		Set level2 = new HashSet<Operator>();
		level2.add(a2);
		Set level3 = new HashSet<Operator>();
		level3.add(a3);
		Set level4 = new HashSet<Operator>();
		level4.add(a4);
		
		
		plan1Actions.add(level4);
		plan1Actions.add(level3);
		plan1Actions.add(level2);
		plan1Actions.add(level1);
		
		
		PlanResult plan1 = new PlanResult(plan1Actions);

		Proposition a = new PropositionImpl("a");
		Proposition b = new PropositionImpl("b");
		Proposition c = new PropositionImpl("c");
		Proposition d = new PropositionImpl("d");
		Proposition d2 = new PropositionImpl("d2");
		List<Proposition> initialState = Arrays.asList(a);
		
		List<List<Proposition>> statesList = plan1.buildStatesList(initialState);
		

		List<List<Proposition>> expectedStatesList = new ArrayList<List<Proposition>>();
		
		expectedStatesList.add(initialState);
		expectedStatesList.add(Arrays.asList(a,b));//level1
		expectedStatesList.add(Arrays.asList(a,b,c));////level2
		expectedStatesList.add(Arrays.asList(a,b,c,d));////level3
		expectedStatesList.add(Arrays.asList(a,b,c,d,d2));////level4
	
		Assert.assertEquals(expectedStatesList, statesList);

	}
	
	@Test
	public void testReorder() throws ParseException {


		List<Operator> operators = getOperators();
		
		Operator a1 = operators.get(0);
		Operator a2 = operators.get(1);
		Operator a3 = operators.get(2);
		Operator a4 = operators.get(3);
		Operator a5 = operators.get(4);
		
		Stack<Set<Operator>> plan1Actions = new Stack<Set<Operator>>();
		Set level1 = new HashSet<Operator>();
		level1.add(a1);
		Set level2 = new HashSet<Operator>();
		level2.add(a2);
		Set level3 = new HashSet<Operator>();
		level3.add(a3);
		Set level4 = new HashSet<Operator>();
		level4.add(a4);
		level4.add(a5);
		
		
		plan1Actions.add(level4);
		plan1Actions.add(level3);
		plan1Actions.add(level2);
		plan1Actions.add(level1);
		
		Stack<Set<Operator>> plan2Actions = new Stack<Set<Operator>>();
		level1 = new HashSet<Operator>();
		level1.add(a1);
		level1.add(a2);
		level2 = new HashSet<Operator>();
		level2.add(a3);
		level2.add(a4);
		
		plan2Actions.add(level2);
		plan2Actions.add(level1);
		
		PlanResult plan1 = new PlanResult(plan1Actions);
		
		PlanResult plan2 = new PlanResult(plan2Actions);
		
		Proposition a = new PropositionImpl("a");
		List<Proposition> initialState = Arrays.asList(a);
	
		plan1.reorder(initialState);
	
		Assert.assertEquals(plan2.steps.size(), plan1.steps.size());
		Assert.assertEquals(plan2, plan1);
	}

	private List<Operator> getOperators() throws ParseException {
		StringBuilder builder = new StringBuilder();
		builder.append("action a1");
		builder.append(System.getProperty("line.separator"));
		builder.append("pre:a");
		builder.append(System.getProperty("line.separator"));
		builder.append("post: b");
		builder.append(System.getProperty("line.separator"));
		builder.append(System.getProperty("line.separator"));
		
		builder.append("action a2");
		builder.append(System.getProperty("line.separator"));
		builder.append("pre:a");
		builder.append(System.getProperty("line.separator"));
		builder.append("post: c");
		builder.append(System.getProperty("line.separator"));
		builder.append(System.getProperty("line.separator"));

		builder.append("action a3");
		builder.append(System.getProperty("line.separator"));
		builder.append("pre:b");
		builder.append(System.getProperty("line.separator"));
		builder.append("post: d");
		builder.append(System.getProperty("line.separator"));
		builder.append(System.getProperty("line.separator"));
		
		builder.append("action a4");
		builder.append(System.getProperty("line.separator"));
		builder.append("pre:b");
		builder.append(System.getProperty("line.separator"));
		builder.append("post: d2");
		builder.append(System.getProperty("line.separator"));
		builder.append(System.getProperty("line.separator"));
		
		builder.append("action a5");
		builder.append(System.getProperty("line.separator"));
		builder.append("pre:true");
		builder.append(System.getProperty("line.separator"));
		builder.append("post: a");
		builder.append(System.getProperty("line.separator"));
		builder.append(System.getProperty("line.separator"));
		
		List<Operator> operators = new PlannerParser().parseOperators(new ByteArrayInputStream(builder.toString().getBytes()));
		
		Operator a1 = operators.get(0);
		Operator a2 = operators.get(1);
		Operator a3 = operators.get(2);
		Operator a4 = operators.get(3);
		Operator a5 = operators.get(4);
		
		Assert.assertEquals("a1", a1.getSignature());
		Assert.assertEquals("a2", a2.getSignature());
		Assert.assertEquals("a3", a3.getSignature());
		Assert.assertEquals("a4", a4.getSignature());
		Assert.assertEquals("a5", a5.getSignature());
		
		return operators;

	}

}



