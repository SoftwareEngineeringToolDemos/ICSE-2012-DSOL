package org.dsol.planner.api;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

public class GenericAbstractActionTest {

	@Test
	public void testToString() {
		GenericAbstractAction action = new GenericAbstractAction();
		action.setName("action1");
		action.setParams(Arrays.asList("p1","p2"));
		
		Assert.assertEquals("action1(p1,p2)", action.toString());
		
		action = new GenericAbstractAction();
		action.setLevel(0);
		action.setName("action1");
		
		Assert.assertEquals("[0] action1", action.toString());
	}

}
