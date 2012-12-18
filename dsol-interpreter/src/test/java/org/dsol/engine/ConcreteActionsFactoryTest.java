package org.dsol.engine;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.dsol.AbstractActionMock;
import org.dsol.ConcreteAction;
import org.dsol.Instance;
import org.dsol.InstanceSession;
import org.dsol.annotation.Action;
import org.dsol.annotation.When;
import org.dsol.service.ConcreteActions1;
import org.dsol.service.ConcreteActions2;
import org.junit.Test;

public class ConcreteActionsFactoryTest {

	@Test
	public void testGetActionMethods() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException {

		// List<String> concreteActions =
		// Arrays.asList("org.dsol.service.ConcreteAction1","org.dsol.service.ConcreteAction2");
		ConcreteActionsFactory concreteActionsFactory = new ConcreteActionsFactory();

		Class clazz = Class.forName("org.dsol.service.ConcreteActions1");
		List<Method> methods = concreteActionsFactory.getActionMethods(clazz);

		Assert.assertEquals(2, methods.size());

		Assert.assertTrue(methods.contains(clazz.getMethod("hi", String.class,
				String.class)));
		Assert.assertTrue(methods.contains(clazz.getMethod("getStarted")));

	}

	@Test
	public void testGetActionName() throws ClassNotFoundException,
			NoSuchMethodException, SecurityException {
		ConcreteActionsFactory concreteActionsFactory = new ConcreteActionsFactory();

		Class clazz = Class.forName("org.dsol.service.ConcreteActions1");

		String name = concreteActionsFactory.getActionName(clazz.getMethod(
				"hi", String.class, String.class));
		Assert.assertEquals("hello", name);

		name = concreteActionsFactory.getActionName(clazz
				.getMethod("getStarted"));
		Assert.assertEquals("getStarted", name);

		name = concreteActionsFactory.getActionName(clazz.getMethod(
				"compensate_getStarted", String.class));
		Assert.assertEquals("org.dsol.service.ConcreteActions1.getStarted",
				name);

		clazz = Class.forName("org.dsol.service.ConcreteActions2");

		name = concreteActionsFactory.getActionName(clazz.getMethod(
				"translate_message_method", String.class));
		Assert.assertEquals("translate_message", name);

		name = concreteActionsFactory.getActionName(clazz
				.getMethod("compensation_method_2"));
		Assert.assertEquals(
				"org.dsol.service.ConcreteActions2.translate_message_method",
				name);

		name = concreteActionsFactory.getActionName(clazz
				.getMethod("compensation_method"));
		Assert.assertEquals("", name);

	}

	@Test
	public void testFilter() throws NoSuchMethodException, SecurityException {
		
		InstanceSession instanceSession = new InstanceSession();
		instanceSession.put("a", 1);

		Instance instance = new Instance(null, instanceSession, null, null,null,null);
		ConcreteActionsFactory concreteActionsFactory = instance.getConcreteActionsFactory();

		ConcreteAction concreteAction = new ConcreteAction(TestFilter.class.getMethod("aGreaterThanOne"), null);
		ConcreteAction concreteAction2 = new ConcreteAction(TestFilter.class.getMethod("aLessThanEqualOne"), null);

		List<ConcreteAction> actionsFiltered = concreteActionsFactory
				.filter(Arrays.asList(concreteAction, concreteAction2), null);
		Assert.assertEquals(1, actionsFiltered.size());

		Method expectedMethod = TestFilter.class.getMethod("aLessThanEqualOne");
		String methodName = expectedMethod.getDeclaringClass().getName() + "."
				+ expectedMethod.getName();

		Assert.assertEquals(methodName, actionsFiltered.get(0)
				.getMethodNameIncludingDeclaringClass());

	}

	@Test
	public void testCreateMaps() throws NoSuchMethodException,
			SecurityException {

		List<String> classes = Arrays.asList(
				"org.dsol.service.ConcreteActions1",
				"org.dsol.service.ConcreteActions2");

		// The constructor automatically call createMaps
		ConcreteActionsFactory concreteActionsFactory = new Instance(null,
				new InstanceSession(), null, null,classes,null).getConcreteActionsFactory();
		

		List<ConcreteAction> concreteActions = concreteActionsFactory
				.getConcreteActions(new AbstractActionMock("hello",0), null);
		Assert.assertEquals(2, concreteActions.size());

		List<ConcreteAction> compensationAction = concreteActionsFactory
				.getCompensationActions(new ConcreteAction(ConcreteActions2.class.getMethod(
								"translate_message_method", String.class), null));
		Assert.assertEquals(1, compensationAction.size());

		List<ConcreteAction> compensationAction2 = concreteActionsFactory
				.getCompensationActions(new ConcreteAction(ConcreteActions1.class.getMethod("hi", String.class,String.class),
						null));
		Assert.assertEquals(1, compensationAction2.size());
		
		List<ConcreteAction> compensationAction3 = concreteActionsFactory
				.getCompensationActions(new ConcreteAction(ConcreteActions2.class.getMethod("hi", String.class,String.class),
						null));
		Assert.assertTrue(compensationAction3.isEmpty());
	}

	@Test
	public void testToJSON(){
		
		List<String> classes = Arrays.asList(
				"org.dsol.service.ConcreteActions1",
				"org.dsol.service.ConcreteActions2");
		
		ConcreteActionsFactory concreteActionsFactory = new ConcreteActionsFactory(null,ConcreteActionsFactory.class.getClassLoader(), classes, null);
		
		Assert.assertEquals("{\"classes\":[\"org.dsol.service.ConcreteActions1\",\"org.dsol.service.ConcreteActions2\"],\"actions\":{\"hello\":[\"org.dsol.service.ConcreteActions1.hi\",\"org.dsol.service.ConcreteActions2.hi\"],\"getStarted\":[\"org.dsol.service.ConcreteActions1.getStarted\"],\"hello2\":[\"org.dsol.service.ConcreteActions2.hi2\"],\"translate_message\":[\"org.dsol.service.ConcreteActions2.translate_message_method\"]}}", 
							concreteActionsFactory.toJSON().toString());
	}

	public class TestFilter {

		public TestFilter() {
		}

		@Action("a")
		@When("a > 1")
		public void aGreaterThanOne() {

		}

		@Action("a")
		@When("a <= 1")
		public void aLessThanEqualOne() {

		}

	}
}

