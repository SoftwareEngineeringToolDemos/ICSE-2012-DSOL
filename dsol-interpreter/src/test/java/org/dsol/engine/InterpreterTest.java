package org.dsol.engine;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.dsol.AbstractActionMock;
import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Plan;
import org.junit.Test;


public class InterpreterTest {
	
	@Test
	public void newPlanAdjustmentWithoutCompensation(){
		
		Plan oldPlan = getPlan1();
		
		oldPlan.getLevel(0).get(0).markAsExecuted();
		oldPlan.getLevel(1).get(0).markAsExecuted();
		
		Interpreter engine = new Interpreter();
		
		Plan newPlan = getPlan2();
		engine.adjustPlans(newPlan, oldPlan, 2);
		
		Assert.assertTrue(newPlan.getLevel(0).get(0).isExecuted());
		Assert.assertTrue(!oldPlan.getLevel(0).get(0).isMarkedForCompensation());
		Assert.assertTrue(newPlan.getLevel(1).get(0).isExecuted());
		Assert.assertTrue(!oldPlan.getLevel(1).get(0).isMarkedForCompensation());

		Assert.assertTrue(!newPlan.getLevel(2).get(0).isExecuted());
		Assert.assertTrue(!oldPlan.getLevel(2).get(0).isMarkedForCompensation());
		
		
	}
	
	
	@Test
	public void newPlanAdjustmentWithCompensation(){
		Plan oldPlan = getPlan1();
		
		Interpreter engine = new Interpreter();

		oldPlan.getLevel(0).get(0).markAsExecuted();
		oldPlan.getLevel(1).get(0).markAsExecuted();

		oldPlan.getLevel(2).get(1).markAsExecuted();

		
		Plan newPlan = getPlan2();
		engine.adjustPlans(newPlan, oldPlan, 2);
		
		Assert.assertTrue(oldPlan.getLevel(2).get(1).isMarkedForCompensation());
		
	}
	
	public Plan getPlan1(){
		
		
		List<AbstractAction> level0 = new ArrayList<AbstractAction>();
		level0.add(new AbstractActionMock("step1(p1,p2)",0));
		
		List<AbstractAction> level1 = new ArrayList<AbstractAction>();
		level1.add(new AbstractActionMock("step2(p1,p2,p3)",1));
		
		List<AbstractAction> level2 = new ArrayList<AbstractAction>();
		level2.add(new AbstractActionMock("step3(p1)",2));
		level2.add(new AbstractActionMock("step4(p4)",2));
		
		List<AbstractAction> level3 = new ArrayList<AbstractAction>();
		level3.add(new AbstractActionMock("step5(p2)",3));
		level3.add(new AbstractActionMock("step6(p5)",3));

		List<List<AbstractAction>> levels = new ArrayList<List<AbstractAction>>();
		levels.add(level0);
		levels.add(level1);
		levels.add(level2);
		levels.add(level3);
		
		return new Plan(levels);
	}
	
	public Plan getPlan2(){
		
		List<AbstractAction> level0 = new ArrayList<AbstractAction>();
		level0.add(new AbstractActionMock("step1(p1,p2)",0));
		
		List<AbstractAction> level1 = new ArrayList<AbstractAction>();
		level1.add(new AbstractActionMock("step2(p1,p2,p3)",1));
		
		List<AbstractAction> level2 = new ArrayList<AbstractAction>();
		level2.add(new AbstractActionMock("step31(p1)",2));
		level2.add(new AbstractActionMock("step41(p4)",2));
		
		List<AbstractAction> level3 = new ArrayList<AbstractAction>();
		level3.add(new AbstractActionMock("step5(p2)",3));
		level3.add(new AbstractActionMock("step6(p5)",3));

		List<List<AbstractAction>> levels = new ArrayList<List<AbstractAction>>();
		levels.add(level0);
		levels.add(level1);
		levels.add(level2);
		levels.add(level3);		
		return new Plan(levels);
	}
}