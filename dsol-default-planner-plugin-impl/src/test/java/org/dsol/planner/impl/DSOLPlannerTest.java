package org.dsol.planner.impl;

import graphplan.flyweight.OperatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import junit.framework.Assert;

import org.dsol.planner.api.PlanResult;
import org.junit.BeforeClass;
import org.junit.Test;

public class DSOLPlannerTest {

	@BeforeClass
	public static void setUp() {
		OperatorFactory.getInstance();
		OperatorFactory.reset();
	}

	@Test
	public void plan() throws Exception{
		
		File initial_state = new File(this.getClass().getResource("/problem1/initial_state.txt").getFile());
		File goal = new File(this.getClass().getResource("/problem1/goal.txt").getFile());
		File actions = new File(this.getClass().getResource("/problem1/actions.txt").getFile());

		DefaultPlanner planner = new DefaultPlanner();
		planner.initialize(new FileInputStream(actions),
						   new FileInputStream(initial_state),
						   new FileInputStream(goal));

		List<PlanResult> planResults = planner.plan();
		Assert.assertEquals(1, planResults.size());
		
		PlanResult planResult = planResults.get(0);
		
		Assert.assertTrue(planResult.planFound());
		Assert.assertEquals(getPlan1(), planResult.getPlan().toString());
		
		Assert.assertTrue(planner.tryNextGoal());
		
		planResults = planner.plan();
		Assert.assertEquals(1, planResults.size());
		
		planResult = planResults.get(0);
		
		Assert.assertTrue(planResult.planFound());
		Assert.assertEquals(getPlan2(), planResult.getPlan().toString());
		
		Assert.assertFalse(planner.tryNextGoal());

	}


	private String getPlan1() {
		StringBuilder builder = new StringBuilder();

		builder.append("[0] getEvent(eventId)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[1] buyTicket(event)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[2] bookFlight(from,event.city,event.date,event.dayAfter)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[3] bookHotel(event.city,event.date,event.dayAfter)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[3] payWithCreditCard(transportation)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[4] bookAndPayTransportation(from,event.city,event.date,event.dayAfter)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[4] payWithCreditCard(accommodation)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[5] bookAndPayAccommodation(event.city,event.date,event.dayAfter)");
		builder.append(System.getProperty("line.separator"));

		return builder.toString();

	}

	private String getPlan2() {
		StringBuilder builder = new StringBuilder();

		builder.append("[0] getEvent(eventId)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[1] buyTicket(event)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[2] bookFlight(from,event.city,event.dayBefore,event.dayAfter)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[3] bookHotel(event.city,event.dayBefore,event.dayAfter)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[3] payWithCreditCard(transportation)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[4] bookAndPayTransportation(from,event.city,event.dayBefore,event.dayAfter)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[4] payWithCreditCard(accommodation)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[5] bookAndPayAccommodation(event.city,event.dayBefore,event.dayAfter)");
		builder.append(System.getProperty("line.separator"));
		return builder.toString();

	}

}
