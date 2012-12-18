package org.dsol.planner.api;

import java.util.List;

/**
 * Interface that represent an abstract action.
 * An abstract action represents the actions that can be used to build an specific plan. When instantiated,
 * represents a step of the plan.
 * 
 * 	@author Leandro Sales Pinto (leandro.shp@gmail.com)
 *	@since 0.1
 *
 */
public abstract class AbstractAction {

	private boolean compensate = false;
	private boolean executed = false;
	protected int level = -1; //the level in which the action is in the plan
	private boolean terminated = false;
	
	private boolean equals(AbstractAction that){
		return this.toString().equals(that.toString());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(!(obj instanceof AbstractAction)){
			return false;
		}

		return this.equals((AbstractAction) obj);
	}
	
	public int getLevel() {
		return level;
	}
	public abstract String getName();
	public abstract List<String> getParamList();
	public abstract List<Fact> getPostConditions();
	public abstract List<Fact> getPreConditions();
	public abstract boolean isEnabled();
	
	public boolean hasTerminated(){
		return this.terminated;
	}
	
	
	
	public boolean isExecuted() {
		return executed;
	}
	
	
	public boolean isMarkedForCompensation() {
		return compensate;
	}
	public abstract boolean isSeam();
	
	/**
	 * returns true if this actions depends on the 
	 * action parameter to be executed
	 * @param action
	 * @return
	 */
	public abstract boolean isTriggeredBy(AbstractAction action);
	
	public void markAsExecuted(){
		executed = true;
	}
	
	public void markForCompensation() {
		compensate = true;
	}
	
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void terminate(){
		this.terminated = true;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(level>=0){
			sb.append("[").append(level).append("] ");			
		}
		sb.append(getName());
		if(!getParamList().isEmpty()){
			sb.append("(");
			
			int i =0;
			for(String param:getParamList()){
				if(i > 0){
					sb.append(",");	
				}
				sb.append(param);
				i++;
			}
			sb.append(")");			
		}
		return sb.toString();
	}
}
