package org.dsol.planner.impl;

import graphplan.Graphplan;
import graphplan.domain.Proposition;
import graphplan.domain.jason.PropositionImpl;
import jason.asSyntax.Structure;

import java.util.ArrayList;
import java.util.List;

import org.dsol.planner.api.Fact;
import org.dsol.planner.api.State;

public class StateImpl extends State {

	private List<Proposition> facts;
	
	private StateImpl(){
		super();
		this.facts = new ArrayList<Proposition>();
	};
	
	public StateImpl(List<Fact> facts) {
		this();
		for(Fact fact:facts){
			this.facts.add(new PropositionImpl(Structure.parse(fact.get())));
		}
	}
	
	@Override
	public State apply(List<Fact> effectsAsFacts) {
		List<Proposition> effects = new ArrayList<Proposition>();
		for(Fact fact:effectsAsFacts){
			effects.add(new PropositionImpl(Structure.parse(fact.get())));
		}
		this.facts = Graphplan.applyEffects(this.facts, effects);
		return this;
	}

	@Override
	public State clone() {
		StateImpl newState = new StateImpl();
		
		for(Proposition prop:facts){
			newState.facts.add(new PropositionImpl(prop));
		}
		
		return newState;
	}

	@Override
	public List<Fact> getFacts() {
		return Util.getPropositionsAsFacts(facts);
	}
	
//	@Override
//	public JsonElement toJSON() {
//		Gson gson = new Gson();
//		List<String> facts = new ArrayList<String>();
//		for (Proposition fact: this.facts) {
//			facts.add(Util.getPropositionAsString(fact));
//		}
//
//		return gson.toJsonTree(facts);
//	}

}
