/*
 * ---------------------------------------------------------------------------
 * Copyright (C) 2010  Felipe Meneguzzi
 * JavaGP is distributed under LGPL. See file LGPL.txt in this directory.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * To contact the author:
 * http://www.meneguzzi.eu/felipe/contact.html
 * ---------------------------------------------------------------------------
 */
package graphplan;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * A class encapsulating the result of planning. This class allows direct 
 * comparison with a boolean value determining whether or not the plan was 
 * successful, as well as iteration through the plan steps.
 * @author Felipe Meneguzzi
 *
 */
public class PlanResult{
	
	protected boolean value = false;
	private int planningTime;
	//The steps are a list of list, since we can have multiple operators at any given step
	protected List<List<Operator>> steps;
	
	public PlanResult(boolean value) {
		this.value = value;
		if(value) {
			this.steps = new ArrayList<List<Operator>>();
		}
	}
	
	public PlanResult(Stack<Set<Operator>> stack) {
		this(stack, Collections.EMPTY_LIST);
	}
	
	/**
	 * Creates a new PlanResult from a stack of operators in 
	 * reverse order.
	 */
	public PlanResult(Stack<Set<Operator>> stack, List<Proposition> initialState) {
		this(true);
		List<Proposition> currState = new ArrayList<Proposition>();
		currState.addAll(initialState);
		
		for(int i = stack.size() - 1 ; i>=0; i--) {
			Collection<Operator> stepsInStack = stack.get(i);
			List<Operator> mySteps = new ArrayList<Operator>(stepsInStack.size());
			for(Operator step : stepsInStack) {
				if(!step.isNoop() && !currState.containsAll(step.getEffects())) {
					mySteps.add(step);			
					currState = Graphplan.applyEffects(currState, step.getEffects());
				}
			}
			
			if(!mySteps.isEmpty()){
				Collections.sort(mySteps,new OperatorComparator());
				this.steps.add(mySteps);
			}			
		}
	}
	
	/**
	 * Creates a new plan with the specified steps. This assumes that planning 
	 * was successful
	 * @param steps
	 */
	public PlanResult(List<Operator> steps) {
		this(true);
		this.steps = new ArrayList<List<Operator>>(steps.size());
		this.addSteps(steps);
	}
	
	/**
	 * Adds a step to this plan.
	 * @param step
	 */
	public void addStep(Operator step) {
		this.steps.add(new ArrayList<Operator>());
		this.steps.get(this.steps.size() - 1).add(step);
	}
	
	/**
	 * Adds a step in the specified time
	 * @param time
	 * @param step
	 */
	public void addStep(int time, Operator step) {
		if(this.steps.size() <= time) {
			this.steps.add(time, new ArrayList<Operator>());
		}
		this.steps.get(time).add(step);
	}
	
	/**
	 * Adds all the steps in the list of operators to this plan, one for
	 * each unit of time.
	 * @param list
	 */
	public void addSteps(List<Operator> list) {
		for(Operator step : list) {
			this.addStep(step);
		}
	}
	
	public List<List<Operator>> getSteps() {
		return steps;
	}
	
	public boolean isTrue() {
		return value;
	}
	
	@Override
	public String toString() {
		if(getSteps() == null || getSteps().isEmpty()){
			return "[empty plan or plan not found]";
		}
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(List<Operator> level:getSteps()){
			for (Operator step : level) {
				builder.append("[").append(i).append("] ");
				builder.append(step);
				builder.append(System.getProperty("line.separator"));
			}
			i++;
		}
		
		
		return builder.toString();
	}
	
	public int getPlanningTime() {
		return planningTime;
	}
	
	public void setPlanningTime(int planningTime) {
		this.planningTime = planningTime;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof PlanResult)){
			return false;
		}
		PlanResult that = (PlanResult)obj;
		return this.steps.equals(that.steps);
	}
	
	class OperatorComparator implements Comparator<Operator>{

		@Override
		public int compare(Operator x, Operator y) {
			return x.toString().compareTo(y.toString());
		}
		
	}

	public void reorder(List<Proposition> originalInitialState) {		
		List<List<Proposition>> statesList = buildStatesList(originalInitialState);
		for(int i = steps.size() - 1;i >= 1;i--){
			reorderLevel(i, statesList.get(i-1));
			//As levels have probably changed, we need to rebuild the states list
			statesList = buildStatesList(originalInitialState);
		}		
		simplifyPlan(originalInitialState);		
	}

	protected List<List<Proposition>> buildStatesList(List<Proposition> originalInitialState){
		List<List<Proposition>> states = new ArrayList<List<Proposition>>();
		states.add(originalInitialState);
		
		List<Proposition> currentState = new ArrayList<Proposition>();
		currentState.addAll(originalInitialState);
		
		for(int i = 0;i<steps.size();i++){
			for(Operator operator:steps.get(i)){
				currentState = Graphplan.applyEffects(currentState, operator.getEffects());
			}
			List<Proposition> previousState = new ArrayList<Proposition>();
			previousState.addAll(currentState);
			states.add(previousState);
		}
		
		return states;
	}

	
	private void simplifyPlan(List<Proposition> initialState) {
		List<Proposition> currState = new ArrayList<Proposition>();
		currState.addAll(initialState);
		List<List<Operator>> newSteps = new ArrayList<List<Operator>>();
		for(List<Operator> step:steps){
			List<Operator> newStep = new ArrayList<Operator>();
			for(Operator operator:step){
				if(!currState.containsAll(operator.getEffects())){
					newStep.add(operator);
					currState = Graphplan.applyEffects(currState, operator.getEffects());
				}
			}
			if(!newStep.isEmpty()){
				newSteps.add(newStep);
			}
		}
		this.steps = newSteps;
	}
	
	protected void reorderLevel(int levelIndex, List<Proposition> previousState){
		List<Operator> operatorsToMoveToPreviousLevel = new ArrayList<Operator>();
		for(Operator operator:steps.get(levelIndex)){
			if(previousState.containsAll(operator.getPreconds())){
				operatorsToMoveToPreviousLevel.add(operator);
			}
		}
		if(!operatorsToMoveToPreviousLevel.isEmpty()){
			steps.get(levelIndex).removeAll(operatorsToMoveToPreviousLevel);
			steps.get(levelIndex - 1).addAll(operatorsToMoveToPreviousLevel);
			Collections.sort(steps.get(levelIndex - 1),new OperatorComparator());
		}	
	}

	public PlanResult getSubPlanContaining(String operatorInstance) {
		Operator targetOperator = null;
		
		int operatorLevel = -1;
		levels:
		for(int i = 0;i<steps.size();i++){
			List<Operator> level = steps.get(i);
			for(Operator operator:level){
				if(operator.toString().equals(operatorInstance)){
					targetOperator = operator;
					operatorLevel = i;
					break levels;
				}
			}
		}
		
		if(targetOperator  != null){
			Stack<Set<Operator>> subPlan = new Stack<Set<Operator>>();
			Set levelOperator = new HashSet<Operator>();			
			levelOperator.add(targetOperator);
			subPlan.add(levelOperator);

			List<Proposition> initialState = new ArrayList<Proposition>();
			
			if(operatorLevel > 0){
				List<Proposition> preconds = targetOperator.getPreconds();
				for(int i = operatorLevel - 1;i>=0;i--){
					Set previousLevel = new HashSet<Operator>();
					List<Operator> level = steps.get(i);
					for(Operator operator:level){
						List<Proposition> propositionsToRemoveFromPreconds= new ArrayList<Proposition>();
						for(Proposition precond:preconds){
							if(operator.getEffects().contains(precond)){
								propositionsToRemoveFromPreconds.add(precond);
							}
						}
						if(!propositionsToRemoveFromPreconds.isEmpty()){
							preconds.removeAll(propositionsToRemoveFromPreconds);
							previousLevel.add(operator);
						}
					}
					if(!previousLevel.isEmpty()){
						subPlan.add(previousLevel);
					}
				}
			}
			else{
				initialState.addAll(targetOperator.getPreconds());
			}
			
			return new PlanResult(subPlan, initialState);
		}
		
		return new PlanResult(false);
	}
}
