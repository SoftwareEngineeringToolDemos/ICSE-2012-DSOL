package org.dsol.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Planner;

public class MonitorCounterImpl implements MonitorCounter {
	
	private Map<String, State> states = null;
	private List<AbstractAction> actions;
	
	public MonitorCounterImpl(Planner planner) {
		states = new HashMap<String, State>();
		actions = planner.getAbstractActions();
		for(AbstractAction action:actions){
			String actionName = action.getName(); 
			states.put(actionName, new State(action));
		}
	}
	
	public List<State> getStates(){
		List<State> states = new ArrayList<State>();
		for(AbstractAction action:actions){
			String actionName = action.getName(); 
			states.add(this.states.get(actionName));
		}
		return states;
	}

	@Override
	public void addSuccessRequest(String abstractAction) {
		State state = states.get(abstractAction);
		state.addSuccessRequest();
	}

	@Override
	public void addFaultyRequest(String abstractAction) {
		State state = states.get(abstractAction);
		state.addFaultyRequest();
	}
}
