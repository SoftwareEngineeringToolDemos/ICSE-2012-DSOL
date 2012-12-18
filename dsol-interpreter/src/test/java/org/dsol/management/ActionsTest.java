package org.dsol.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.dsol.planner.api.GenericAbstractAction;
import org.junit.Test;

public class ActionsTest {

	@Test
	public void test() {
		Actions actions = new Actions();
		
		List<GenericAbstractAction> abstractActions = new ArrayList<GenericAbstractAction>();

		GenericAbstractAction abstractAction = new GenericAbstractAction();
		abstractAction.setName("action1");
		abstractAction.setParams(Arrays.asList("Param1","Param2"));
		abstractAction.setPre(Arrays.asList("pre1","pre2"));
		abstractAction.setPost(Arrays.asList("post1","post2"));
		
		abstractActions.add(abstractAction);
		actions.setActions(abstractActions);
		
		Assert.assertEquals(abstractActions, actions.getActions());
		Assert.assertEquals(abstractAction, actions.getActions().get(0));
	}

}
