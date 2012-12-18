package org.dsol.planner.api;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Represents a plan that can be executed to accomplish a given goal.
 * A plan is a list of ordered abstract actions.   
 * 
 * 	@author Leandro Sales Pinto (leandro.shp@gmail.com)
 *	@since 0.1
 *
 *
 */
public class Plan{

	private List<List<AbstractAction>> levels;

	public Plan() {
		this.levels = new ArrayList<List<AbstractAction>>();
	}
	
	public Plan(final List<List<AbstractAction>> steps) {
		this.levels = steps;
	}

	public final List<AbstractAction> getLevel(final int levelIndex) {
		return levels.get(levelIndex);
	}
	
	/**
	 * Returns the action object in this plan that represents the same action passed as
	 * parameter
	 * 
	 *   returns null if this plan does not contain this action in this level
	 */
	public final AbstractAction getAction(final int level, AbstractAction actionToFind) {
		for(AbstractAction action:levels.get(level)){
			if(action.equals(actionToFind)){
				return action;
			}
		}
		return null;
	}
	
	public List<List<AbstractAction>> getSteps() {
		return levels;
	}
	
	public void addStep(int level, AbstractAction step){
		step.setLevel(level);
		if(level == size()){
			createNewLevel();
		}
		this.levels.get(level).add(step);
	}
	
	private void createNewLevel() {
		this.levels.add(new ArrayList<AbstractAction>());
	}

	public final int size() {
		return levels.size();
	}

	
	@Override
	public final String toString() {
		StringBuilder builder = new StringBuilder();

		for(List<AbstractAction> level:levels){
			for (AbstractAction action : level) {
				builder.append(action);
	        	if(action.isExecuted()){
	        		builder.append("                     <= [EXECUTED]");
	        	}
				builder.append(System.getProperty("line.separator"));
			}			
		}

		return builder.toString();
	}

	public void markForCompensation() {
		for(int i = 0;i < levels.size(); i++){
			markLevelForCompensation(i);
		}
	}
	
	private void markLevelForCompensation(int levelIndex){
		List<AbstractAction> level = levels.get(levelIndex);
		for(AbstractAction action:level){
			if(action.isExecuted()){
				action.markForCompensation();
			}
		}
	}

}