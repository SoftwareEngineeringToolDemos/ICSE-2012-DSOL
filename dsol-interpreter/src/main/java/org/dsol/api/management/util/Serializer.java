package org.dsol.api.management.util;


import java.util.ArrayList;
import java.util.List;

import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Fact;
import org.dsol.planner.api.Plan;
import org.dsol.planner.api.PlanResult;
import org.dsol.planner.api.Planner;
import org.dsol.planner.api.State;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Serializer {
	
	public static JsonObject toJSON(Planner planner){
		
		JsonObject jsonObject = new JsonObject();

		JsonArray actions = new JsonArray();
		for(AbstractAction action: planner.getAbstractActions()){
			actions.add(toJSON(action));		
		}
		jsonObject.add("actions", actions);

		return jsonObject;
	}
	
	public static JsonObject toJSON(AbstractAction action){
		Gson gson = new Gson();
		
		JsonObject actionAsJSON = new JsonObject();
		actionAsJSON.addProperty("name", action.getName());
		actionAsJSON.addProperty("seam", action.isSeam());
		actionAsJSON.addProperty("enabled", action.isEnabled());
		
		List<String> params = new ArrayList<String>();
		for(String param:action.getParamList()){
			params.add(param);
		}
		actionAsJSON.add("params", gson.toJsonTree(params));
		
		List<String> preconds = new ArrayList<String>();
		for(Fact precond:action.getPreConditions()){
			preconds.add(precond.get());
		}
		actionAsJSON.add("pre", gson.toJsonTree(preconds));

		List<String> postconds = new ArrayList<String>();
		for(Fact postcond:action.getPostConditions()){
			postconds.add(postcond.get());
		}
		actionAsJSON.add("post", gson.toJsonTree(postconds));
		return actionAsJSON;
	}
	
	public static JsonObject toJSON(PlanResult planResult) {
		JsonArray steps = new JsonArray();
		Plan plan = planResult.getPlan();
		
		for (List<AbstractAction> level : plan.getSteps()) {
			JsonArray levelAsJSON = new JsonArray();
			for (AbstractAction step : level) {				
				levelAsJSON.add(toJSON(step));
			}
			steps.add(levelAsJSON);
		}

//		JsonArray states = new JsonArray();
//		for (State state : plan.states) {
//			states.add(state.toJSON());
//		}

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("plan", steps);
		jsonObject.addProperty("planning_time", planResult.getPlanningTime());
		jsonObject.add("final_state", toJSON(planResult.getFinalState()));
		//jsonObject.add("goal", gson.toJsonTree(Util.getFactsAsString(planResult.getGoal())));
		//jsonObject.add("states", states);

		return jsonObject;
	}
	
	public static JsonElement toJSON(State state) {
		Gson gson = new Gson();
		List<String> facts = new ArrayList<String>();
		for (Fact fact : state.getFacts()) {
			facts.add(fact.get());
		}

		return gson.toJsonTree(facts);
	}


}
