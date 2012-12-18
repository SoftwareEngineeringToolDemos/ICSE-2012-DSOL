package org.dsol.management;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.dsol.planner.api.GenericAbstractAction;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "planner", propOrder = { "actions", "initial_state", "goal","removed_actions" })
public class PlannerInfo {

	private List<GenericAbstractAction> actions;
	private String initial_state;
	private String goal;
	private List<GenericAbstractAction> removed_actions;
	
	public List<GenericAbstractAction> getActions() {
		return actions;
	}

	public void setActions(List<GenericAbstractAction> actions) {
		this.actions = actions;
	}

	public String getInitial_state() {
		return initial_state;
	}

	public void setInitial_state(String initial_state) {
		this.initial_state = initial_state;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}
	
	public void setRemoved_actions(List<GenericAbstractAction> removed_actions) {
		this.removed_actions = removed_actions;
	}
	
	public List<GenericAbstractAction> getRemoved_actions() {
		return removed_actions;
	}

}
