package org.dsol.planner.api.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.dsol.planner.api.Fact;
import org.dsol.planner.api.Goal;
import org.junit.Test;

public class GoalParserUtilTest {
	
	@Test
	public void extractGoalAsString() throws IOException{
		String goal = "goal(\n"+
				"(	ticketBought(event),\n"+ 
						"transportationBookedAndPaid(from,event.city,event.date,event.dayAfter),\n"+
						"accommodationBookedAndPaid(event.city,event.date,event.dayAfter)) \n"+
						"or\n"+
					"(	ticketBought(event),\n"+ 
						"transportationBookedAndPaid(from,event.city,event.dayBefore,event.dayAfter),\n"+
						"accommodationBookedAndPaid(event.city,event.dayBefore,event.dayAfter))\n"+
					")";
		
		String goalParsed = ProblemParserUtil.extractGoalAsString(new ByteArrayInputStream(goal.getBytes()));
		
		String expectedGoal = "goal( "
				+ "(	ticketBought(event), "
				+ "transportationBookedAndPaid(from,event.city,event.date,event.dayAfter), "
				+ "accommodationBookedAndPaid(event.city,event.date,event.dayAfter)) "
				+ "or "
				+ "(	ticketBought(event), "
				+ "transportationBookedAndPaid(from,event.city,event.dayBefore,event.dayAfter), "
				+ "accommodationBookedAndPaid(event.city,event.dayBefore,event.dayAfter)) "
				+ ")"; 
	
		Assert.assertEquals(expectedGoal, goalParsed);
	}
	
	@Test
	public void getGoals() throws IOException, ParseException {

		File problem = new File(this.getClass().getResource("/problem1/problem.txt").getFile());

		Goal goal1 = new Goal();
		goal1.add(new Fact("ticketBought(event)"));
		goal1.add(new Fact(
				"transportationBookedAndPaid(from,event.city,event.date,event.dayAfter)"));
		goal1.add(new Fact(
				"accommodationBookedAndPaid(event.city,event.date,event.dayAfter)"));

		Goal goal2 = new Goal();
		goal2.add(new Fact("ticketBought(event)"));
		goal2.add(new Fact(
				"transportationBookedAndPaid(from,event.city,event.dayBefore,event.dayAfter)"));
		goal2.add(new Fact(
				"accommodationBookedAndPaid(event.city,event.dayBefore,event.dayAfter)"));

		List<Goal> goals = ProblemParserUtil.parseGoals(new FileInputStream(problem));
		Assert.assertEquals(goal1, goals.get(0));
		Assert.assertEquals(goal2, goals.get(1));
	}
	
	@Test
	public void getGoals2(){
		
		List<Goal> goals = ProblemParserUtil.parseGoals("goal(rentalCarOrdered,towTruckOrdered)");
		Assert.assertEquals(1, goals.size());
		Goal goal = goals.get(0);
		
		Fact fact1 = new Fact("rentalCarOrdered");
		Fact fact2= new Fact("towTruckOrdered");
		
		Assert.assertEquals(2,goal.size());
		
		Assert.assertEquals(fact1.get(),goal.get(0).get());
		Assert.assertEquals(fact2.get(),goal.get(1).get());
		
	}
	
	@Test
	public void getGoals3(){
		
		List<Goal> goals = ProblemParserUtil.parseGoals("goal(rentalCarOrdered,towTruckOrdered, test(test))");
		Assert.assertEquals(1, goals.size());
		Goal goal = goals.get(0);
		
		Fact fact1 = new Fact("rentalCarOrdered");
		Fact fact2= new Fact("towTruckOrdered");
		Fact fact3= new Fact("test(test)");
		
		Assert.assertEquals(3,goal.size());
		
		Assert.assertEquals(fact1.get(),goal.get(0).get());
		Assert.assertEquals(fact2.get(),goal.get(1).get());
		Assert.assertEquals(fact3.get(),goal.get(2).get());
		
	}
	
	
	@Test
	public void getGoals4(){
		
		List<Goal> goals = ProblemParserUtil.parseGoals("goal(rentalCarOrdered,test(test),towTruckOrdered)");
		Assert.assertEquals(1, goals.size());
		Goal goal = goals.get(0);
		
		Fact fact1 = new Fact("rentalCarOrdered");
		Fact fact2= new Fact("test(test)");
		Fact fact3= new Fact("towTruckOrdered");
		
		Assert.assertEquals(3,goal.size());
		
		Assert.assertEquals(fact1.get(),goal.get(0).get());
		Assert.assertEquals(fact2.get(),goal.get(1).get());
		Assert.assertEquals(fact3.get(),goal.get(2).get());
		
	}
	
	@Test
	public void getGoals5(){
		
		List<Goal> goals = ProblemParserUtil.parseGoals("goal(rentalCarOrdered)");
		Assert.assertEquals(1, goals.size());
		Goal goal = goals.get(0);
		
		Fact fact1 = new Fact("rentalCarOrdered");
		
		Assert.assertEquals(1,goal.size());
		
		Assert.assertEquals(fact1.get(),goal.get(0).get());
		
	}

	@Test
	public void getGoals6(){
		
		List<Goal> goals = ProblemParserUtil.parseGoals("goal(teste(teste))");
		Assert.assertEquals(1, goals.size());
		Goal goal = goals.get(0);
		
		Fact fact1 = new Fact("teste(teste)");
		
		Assert.assertEquals(1,goal.size());
		
		Assert.assertEquals(fact1.get(),goal.get(0).get());
		
	}
	
	@Test
	public void parseGoal() throws IOException{
		Goal goal = ProblemParserUtil.parseGoal("ticketBought(event),flightBooked(from,event.city,event.date,event.dayAfter),paid(transportation),hotelBooked(event.city,event.date,event.dayAfter),paid(accommodation)");

		Goal expectedGoal = new Goal();
		expectedGoal.add(new Fact("ticketBought(event)"));
		expectedGoal.add(new Fact("flightBooked(from,event.city,event.date,event.dayAfter)"));
		expectedGoal.add(new Fact("paid(transportation)"));
		expectedGoal.add(new Fact("hotelBooked(event.city,event.date,event.dayAfter)"));
		expectedGoal.add(new Fact("paid(accommodation)"));

		Assert.assertEquals(expectedGoal, goal);
	}
	
	
	
}
