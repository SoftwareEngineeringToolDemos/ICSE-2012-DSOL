package org.dsol.planner.api;

import java.util.ArrayList;
import java.util.List;

public class PlanResult {

	private State initialState;
	private Goal goal;
	private List<State> states;
	private int planningTime;
	
	private Plan plan;

	public PlanResult() {
	}

	public PlanResult(State initialState, Goal goal) {
		this(new Plan(), initialState, goal);
	}

	public PlanResult(Plan plan, State initialState, Goal goal) {
		this.plan = plan;
		this.initialState = initialState;
		this.goal = goal;
		this.states = new ArrayList<State>();
	}

	public boolean planFound() {
		return plan != null;
	}

	public Plan getPlan() {
		return plan;
	}

	public void addStep(int level, AbstractAction action) {
		plan.addStep(level, action);
	}

	public void addState(State state) {
		this.states.add(state);
	}
	
	public Goal getGoal() {
		return goal;
	}

	public State getFinalState() {
		if (!states.isEmpty()) {
			return states.get(states.size() - 1);
		}
		return null;
	}

	public boolean isEmpty(){
		return plan == null || plan.getSteps() == null || plan.getSteps().isEmpty();
	}
	
	public void setPlanningTime(int planningTime) {
		this.planningTime = planningTime;
	}
	
	public int getPlanningTime() {
		return planningTime;
	}
}
