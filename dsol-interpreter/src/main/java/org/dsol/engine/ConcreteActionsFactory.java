package org.dsol.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.dsol.ClassWithServiceAction;
import org.dsol.ConcreteAction;
import org.dsol.Instance;
import org.dsol.InstanceSession;
import org.dsol.InstanceSessionAware;
import org.dsol.annotation.Action;
import org.dsol.annotation.ReturnValue;
import org.dsol.planner.api.AbstractAction;
import org.dsol.service.ServiceSelector;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConcreteActionsFactory {
	
	private Logger logger = Logger.getLogger(ConcreteActionsFactory.class.getName()); 
	
	private Map<String, List<ConcreteAction>> actions = null;
	private Map<String, List<ConcreteAction>> orderedActions = null;
	private Map<String, List<ConcreteAction>> compensationActions = null;
	private Map<String, List<ConcreteAction>> returnValueActions = null;
	
	//Map with a instance of each concrete actions class. The name of the class is the key.
	private Map<String, Object> concreteActionObjectInstances = null;
	
	private final ServiceSelector serviceSelector;
	private Instance instance;
	private ClassLoader classLoader;
	private List<String> classesWithActions;

	private List<Class> classes;

	protected ConcreteActionsFactory() {
		serviceSelector = null;
	}
	
	public ConcreteActionsFactory(	ServiceSelector serviceSelector, 
									ClassLoader classLoader, 
									List<String> classesWithActions, 
									Instance instance){
		
		this.serviceSelector = serviceSelector;
		this.instance = instance;
		this.orderedActions = new HashMap<String, List<ConcreteAction>>();
		
		load(classLoader, classesWithActions);
	
	}
	
	public void load(ClassLoader classLoader, List<String> classesWithActions){
		
		if(classesWithActions == null){
			classesWithActions = new ArrayList<String>();
		}
				
		this.classesWithActions = classesWithActions;
		this.classLoader = classLoader;
		
		try {
			loadClasses(classesWithActions);
			
			createActionMap();
			createCompensationActionMap();
			createReturnValueMap();
		
		} catch (Exception e) {
			logger.severe("!!!!!!!ERROR LOADING CONCRETE ACTIONS CLASSES!!!!!!!");
			e.printStackTrace();
		}		
	}
	
	protected void createActionMap() throws Exception{

		actions = new HashMap<String, List<ConcreteAction>>();
		
		for (Class classWithAction : classes) {
			Object objectInstance = concreteActionObjectInstances.get(classWithAction.getName());
			List<Method> methods = getActionMethods(classWithAction);

			for (Method method : methods) {
				String actionName = getActionName(method);

				if(!actions.containsKey(actionName)){
					actions.put(actionName, new ArrayList<ConcreteAction>());
				}
				List<ConcreteAction> methodsForAction = actions.get(actionName);
				methodsForAction.add(new ConcreteAction(method,objectInstance));
			}
		}
	}
	
	protected void createCompensationActionMap() throws Exception{
		compensationActions = new HashMap<String, List<ConcreteAction>>();
		
		for (Class classWithAction : classes) {
			Object objectInstance = concreteActionObjectInstances.get(classWithAction.getName());
			List<Method> methods = getCompensationActionMethods(classWithAction);
			for (Method method : methods) {
				String actionName = getActionName(method);
 
				if(!compensationActions.containsKey(actionName)){
					compensationActions.put(actionName, new ArrayList<ConcreteAction>());
				}
				List<ConcreteAction> methodsForAction = compensationActions.get(actionName);
				methodsForAction.add(new ConcreteAction(method,objectInstance));
			}
		}
	}
	
	protected void createReturnValueMap() throws Exception{

		returnValueActions = new HashMap<String, List<ConcreteAction>>();
		
		for (Class classWithAction : classes) {
			Object objectInstance = concreteActionObjectInstances.get(classWithAction.getName());
			List<Method> methods = getMethodsWithReturnValue(classWithAction);
			for (Method method : methods) {
				ReturnValue returnValueAnnotation = method.getAnnotation(ReturnValue.class);
				String[] returnValues = returnValueAnnotation.value();
				for(String returnValue:returnValues){
					if(!returnValueActions.containsKey(returnValue)){
						returnValueActions.put(returnValue, new ArrayList<ConcreteAction>());
					}
					List<ConcreteAction> methodsForReturnValue = returnValueActions.get(returnValue);
					methodsForReturnValue.add(new ConcreteAction(method,objectInstance));	
				}
			}
		}
	}
	
	protected String getActionName(Method method) {
		Action action = method.getAnnotation(Action.class);
		String actionName = action.name();
		if(actionName.isEmpty()){
			actionName = action.value();
			if(actionName.isEmpty()){
				if(!action.compensation()){
					actionName = method.getName();	
				}
			}
		}
		/*
		 * Compensation actions are meant to compensate the execution of an *concrete action*. For this reason,
		 * one compensation action must be associated with the name of one method, and this name must be complete, i.e.,
		 * include the name of the class. THe developer has the option to do not include the name of the class, but, in such
		 * case, is a DSOL convention that the method is in the same class. The following line guarantee that behaviour.
		 * 
		 * 
		 */
		if(action.compensation()){
			if(actionName.isEmpty()){
					logger.severe("ACTION NOT INCLUDE! >>> Compensation actions must be linked to an Action through the 'name' attribute. In method ["+method+"]." );
			}
			else if(!actionName.contains(".")){
				actionName = method.getDeclaringClass().getName()+"."+actionName;
			}
		}
		return actionName;
	}

	protected List<Method> getActionMethods(Class _class){
		List<Method> methods = new ArrayList<Method>();
		for (Method method : _class.getMethods()) {
			if (method.isAnnotationPresent(Action.class)) {
				Action action = method.getAnnotation(Action.class);
				if(!action.compensation()){
					methods.add(method);	
				}
			}
		}
		return methods;
	}

	protected List<Method> getCompensationActionMethods(Class _class){
		List<Method> methods = new ArrayList<Method>();
		for (Method method : _class.getMethods()) {
			if (method.isAnnotationPresent(Action.class)) {
				Action action = method.getAnnotation(Action.class);
				if(action.compensation()){
					methods.add(method);	
				}
			}
		}
		return methods;
	}

	
	protected List<Method> getMethodsWithReturnValue(Class _class){
		List<Method> methods = new ArrayList<Method>();
		for (Method method : _class.getMethods()) {
			if (method.isAnnotationPresent(ReturnValue.class)) {
				methods.add(method);
			}
		}
		return methods;
	}

	
	public List<String> getClassesWithActions() {
		return classesWithActions;
	}
	
	/**
	 * Return all the valid methods to execute this step of the plan
	 * @param stepToCompensate
	 * @return
	 */	
	public List<ConcreteAction> getCompensationActions(ConcreteAction executedConcreteAction){

		String methodName = executedConcreteAction.getMethodNameIncludingDeclaringClass();
		List<ConcreteAction> compensationActions = this.compensationActions.get(methodName);
		if(compensationActions == null){
			return new ArrayList<ConcreteAction>();
		}
		return filter(compensationActions, null);
	}

	public List<ConcreteAction> getConcreteActions(AbstractAction action){
		return getConcreteActions(action, null);
	}
	
	public List<ConcreteAction> getConcreteActions(AbstractAction action, List<String> params){
		String actionName = action.getName();
		List<ConcreteAction> concreteActions = null;

		concreteActions = actions.get(actionName);
		if(concreteActions == null){
			return new ArrayList<ConcreteAction>();
		}
		return filter(concreteActions, params);
	}
	
	protected List<ConcreteAction> filter(List<ConcreteAction> concreteActions, List<String> params){
		List<ConcreteAction> validConcreteActions = new ArrayList<ConcreteAction>();
		InstanceSession instanceSessionCopy = instance.getInstanceSession().copy();
		for(ConcreteAction concreteAction:concreteActions){
			if(concreteAction.holdWhenExpression(instanceSessionCopy,params)){
				validConcreteActions.add(concreteAction);
			}
		}
		return validConcreteActions;
	}

	public JsonObject toJSON() {
		Gson gson = new Gson();
		JsonObject jsonConcreteActionsFactory = new JsonObject();
		
		jsonConcreteActionsFactory.add("classes", gson.toJsonTree(classesWithActions));
		
		Set<String> actionsNames = this.actions.keySet();
		JsonObject jsonActions = new JsonObject();
		for(Iterator<String> it = actionsNames.iterator();it.hasNext();){
			String actionName = it.next();
			List<ConcreteAction> actions = this.actions.get(actionName);
			List<String> methodsNames = new ArrayList<String>();
			for(ConcreteAction concreteAction:actions){
				methodsNames.add(concreteAction.getMethodNameIncludingDeclaringClass());
			}
			jsonActions.add(actionName, gson.toJsonTree(methodsNames));
		}
		jsonConcreteActionsFactory.add("actions", jsonActions);
		return jsonConcreteActionsFactory;
	}	

	public List<ConcreteAction> getConcreteActionByReturnValue(String objectName) {
		List<ConcreteAction> actions = returnValueActions.get(objectName);
		if(actions == null) {
			return new ArrayList<ConcreteAction>();
		}
		return actions;
	}

	
	/*************************** AUXILIAR METHODS *************************************/
	 
	 /** @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException */
	private void loadClasses(List<String> classesNames) throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		classes = new ArrayList<Class>();
		concreteActionObjectInstances = new HashMap<String, Object>();
		for(String className:classesNames){
			try {
				Class _class = classLoader.loadClass(className);
				classes.add(_class);
				concreteActionObjectInstances.put(className, createObject(_class));
			} catch (ClassNotFoundException e) {
				logger.severe("Impossible to load "+className);
			}
		}
	}

	private Object createObject(Class _class) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Object object = null;
		if(_class.isInterface() || Modifier.isAbstract(_class.getModifiers())){
			object = ClassWithServiceAction.newInstance(_class,serviceSelector, instance);
		}
		else{
			Constructor constructor = _class.getConstructor();
			object = constructor.newInstance();		
		}
		
		if(InstanceSessionAware.class.isAssignableFrom(object.getClass())){
			InstanceSessionAware instanceSessionAware = (InstanceSessionAware)object;
			if(instance.getInstanceSession() != null){
				instanceSessionAware.setInstanceSession(instance.getInstanceSession());	
			}
		}

		return object;
	}

	public List<ConcreteAction> getConcreteActionsOrderedByResponseTime(
			AbstractAction abstractAction) {
		
		String actionName = abstractAction.getName();
		List<ConcreteAction> orderedConcreteActions = orderedActions.get(actionName);
		if(orderedConcreteActions != null){
			return orderedConcreteActions;
		}
		
		orderedConcreteActions = new ArrayList<ConcreteAction>();
		List<ConcreteAction> concreteActions = actions.get(actionName);
		
		if(concreteActions != null){
			orderedConcreteActions.addAll(concreteActions);
		}
		
		Collections.sort(orderedConcreteActions, new Comparator<ConcreteAction>() {
			@Override
			public int compare(ConcreteAction concreteAction1, ConcreteAction concreteAction2) {
				return concreteAction1.getResponseTime() - concreteAction2.getResponseTime();
			}
		});
		orderedActions.put(actionName, orderedConcreteActions);
		return orderedConcreteActions;
	}
}
