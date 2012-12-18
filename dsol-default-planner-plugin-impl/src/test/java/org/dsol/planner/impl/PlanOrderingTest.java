package org.dsol.planner.impl;

import graphplan.flyweight.OperatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.dsol.planner.api.Fact;
import org.dsol.planner.api.PlanResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlanOrderingTest {
	
	@Before
	@After
	public void setUp(){
		OperatorFactory.getInstance();
		OperatorFactory.reset();
	}
	
	@Test
	public void test1() throws Exception{
		
		File initial_state = new File(this.getClass().getResource("/problem2/initial_state.txt").getFile());
		File goal = new File(this.getClass().getResource("/problem2/goal.txt").getFile());
		File actions = new File(this.getClass().getResource("/problem2/abstractactions.txt").getFile());

		DefaultPlanner planner = new DefaultPlanner();
		planner.initialize(new FileInputStream(actions),
						   new FileInputStream(initial_state),
						   new FileInputStream(goal));
		
		List<Fact> initials = new ArrayList<Fact>();
		initials.add(new Fact("language(portuguese)"));
		initials.add(new Fact("name(name)"));
		
		planner.addToInitialState(initials);
				
		List<PlanResult> planResults = planner.plan();
		Assert.assertEquals(1, planResults.size());
		
		PlanResult planResult = planResults.get(0);
	
		
		Assert.assertEquals(getPlan1(), planResult.getPlan().toString());
		
	}
	
	private String getPlan1(){
		StringBuilder builder = new StringBuilder();

		builder.append("[0] getHour");
		builder.append(System.getProperty("line.separator"));		
		builder.append("[1] defineGreetingInPortuguese");
		builder.append(System.getProperty("line.separator"));
		builder.append("[2] sendGreeting(greetingInPortuguese,name)");
		builder.append(System.getProperty("line.separator"));
		
		return builder.toString();

	}
	
	public static void main(String[] args) throws Exception {
		PlanOrderingTest planOrderingTest = new PlanOrderingTest();
		planOrderingTest.test1();
	}
	

}
