package org.dsol.planner.impl;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;

import java.util.ArrayList;
import java.util.List;

import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Fact;

public class AbstractActionImpl extends AbstractAction {

	private Operator operatorInstance;

	public AbstractActionImpl(Operator operator) {
		this.operatorInstance = operator;
	}

	public String getName() {
		return operatorInstance.getFunctor();
	}

	public Operator getOperator() {
		return operatorInstance;
	}

	public List<String> getParamList() {

		List terms = operatorInstance.getTerms();
		List<String> termsStr = new ArrayList<String>();
		for (Object term : terms) {
			termsStr.add(term.toString());
		}
		return termsStr;
	}

	@Override
	public List<Fact> getPostConditions() {
		return Util.getPropositionsAsFacts(operatorInstance.getEffects());
	}

	@Override
	public List<Fact> getPreConditions() {
		return Util.getPropositionsAsFacts(operatorInstance.getPreconds());
	}

	@Override
	public boolean isEnabled() {
		return operatorInstance.isEnabled();
	}
	
	@Override
	public boolean isSeam() {
		return operatorInstance.isSeam();
	}

	@Override
	public boolean isTriggeredBy(AbstractAction action) {
		AbstractActionImpl thatAction = (AbstractActionImpl)action;
		
		List<Proposition> effects = thatAction.operatorInstance.getEffects();
		List<Proposition> preConds = this.operatorInstance.getPreconds();
		
		for(Proposition preCond:preConds){
			if(effects.contains(preCond)){
				return true;
			}
		}
		
		return false;
	}
}
