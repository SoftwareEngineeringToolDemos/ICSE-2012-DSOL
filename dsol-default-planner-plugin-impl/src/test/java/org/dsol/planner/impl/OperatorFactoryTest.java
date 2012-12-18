package org.dsol.planner.impl;

import graphplan.domain.Operator;
import graphplan.flyweight.OperatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import junit.framework.Assert;

import org.dsol.planner.api.PlanResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class OperatorFactoryTest {
	
	@Before
	@After
	public void setUp(){
		OperatorFactory.getInstance();
		OperatorFactory.reset();
	}
	
	@Test
	public void addInvalidInstantiation() throws Exception{
		
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

		
		Assert.assertEquals(getPlanBookFlight(),planResult.getPlan().toString());
		
		int i = 0;
		//the third step must be bookFlight(from,event.city,event.date,event.dayAfter)
		Operator bookFilghtOp = null;
		
		bookFilghtOp = ((AbstractActionImpl)planResult.getPlan().getLevel(2).get(0)).getOperator();		
//		
		OperatorFactory opFactory = OperatorFactory.getInstance();
		opFactory.addInvalidOperatorInstantiation(bookFilghtOp.toString());
		
		planResults = planner.plan();
		Assert.assertEquals(1, planResults.size());
		
		planResult = planResults.get(0);

		
		Assert.assertEquals(getPlanBookFlightLowCost(),planResult.getPlan().toString());
		
	}
	
	private String getPlanBookFlight(){
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
	
	private String getPlanBookFlightLowCost(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("[0] getEvent(eventId)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[1] buyTicket(event)");
		builder.append(System.getProperty("line.separator"));
		builder.append("[2] bookFlightLowCost(from,event.city,event.date,event.dayAfter)");
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
	

}
