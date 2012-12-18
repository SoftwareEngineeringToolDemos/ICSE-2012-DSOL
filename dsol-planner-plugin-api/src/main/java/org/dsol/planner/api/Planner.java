package org.dsol.planner.api;

import java.io.InputStream;
import java.util.List;

public interface Planner {
	
	public static final String DEFAULT_ACTIONS = "classpath:abstract_actions.dsol";
	public static final String EMPTY_INITIAL_STATE = "start(true)";
	public static final String EMPTY_GOAL = "goal(true)";

	// QoS Related part of the Domain
	public static final String DESIRED_QOS = "desired";
	public static final String MAX_QOS = "max";
	public static final String MIN_QOS = "min";
	public static final String UPPER_BOUND = "UpperBound";
	public static final String LOWER_BOUND = "LowerBound";

	
	public void initialize(InputStream actions, InputStream initialState, InputStream goal) throws Exception;

	/**
	 * This method is used to add new facts to the initial state
	 * 
	 * @param newFacts
	 */
	void addToInitialState(List<Fact> newFacts);
	
	/**
	 * Used to set the goal of the planner
	 * @param goal
	 */
	void setGoal(Goal goal);
	
	Goal getCurrentGoal();
	
	void setInitialStateAndGoal(InputStream initialState, InputStream goal);
	
	List<PlanResult> plan();

	void removeOperation(AbstractAction faultyaction);

	boolean tryNextGoal();

	//public JsonObject toJSON();

	void update(Planner plannerToBeUpdated);
	
	void updateActions(List<GenericAbstractAction> newAbstractActions);
	
	public List<AbstractAction> getAbstractActions();

	public void disableAction(String action);
	
	public void loadActions(InputStream inputStream);

	
	
}
