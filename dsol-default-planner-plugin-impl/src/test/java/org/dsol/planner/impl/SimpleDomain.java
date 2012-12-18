package org.dsol.planner.impl;
import java.io.File;
import java.io.FileInputStream;

import junit.framework.Assert;

import org.dsol.planner.qos.QoS;
import org.dsol.planner.qos.QosBound;
import org.junit.Test;


public class SimpleDomain {
	@Test
	public void plan() throws Exception{

		File initial_state = new File(this.getClass().getResource("/simple/initial_state.txt").getFile());
		File goal = new File(this.getClass().getResource("/simple/goal.txt").getFile());
		File actions = new File(this.getClass().getResource("/simple/actions.txt").getFile());

		DefaultPlanner planner = new DefaultPlanner();
		planner.initialize(new FileInputStream(actions),
						   new FileInputStream(initial_state),
						   new FileInputStream(goal));
		

		QosBound val = planner.getCurrentGoal().getDesiredQoSValue(QoS.RESPONSE_TIME.value());
		
		Assert.assertEquals(1000.0, val.getValue());
		
	}
}
