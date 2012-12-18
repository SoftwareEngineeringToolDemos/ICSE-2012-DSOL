package org.dsol.planner.impl;

import graphplan.domain.Proposition;
import graphplan.domain.jason.OperatorImpl;
import graphplan.flyweight.OperatorFactory;

import java.util.ArrayList;
import java.util.List;

import org.dsol.planner.api.Fact;
import org.dsol.planner.api.GenericAbstractAction;

public class Util {

	public static List<Fact> getPropositionsAsFacts(List<Proposition> propositions){
		List<Fact> propositionsAsFacts = new ArrayList<Fact>();
		for(Proposition proposition:propositions){
			propositionsAsFacts.add(new Fact(getPropositionAsString(proposition)));
		}
		return propositionsAsFacts;
	}
	
	public static String getPropositionAsString(Proposition proposition){
		String fact = null;
		if(proposition.getTerms().isEmpty()){
			fact = proposition.getFunctor();	
		}
		else{
			fact = proposition.getSignature();
		}
		if(proposition.negated()){
			fact = "~"+fact;
		}
		return fact;
	}
	
	public static OperatorImpl createOperatorFromGenericAbstractAction(GenericAbstractAction abstractAction){
		OperatorFactory operatorFactory = OperatorFactory.getInstance(true);
		StringBuilder signature = new StringBuilder(abstractAction.getName());
		List<String> params = abstractAction.getParams();
		if(!params.isEmpty()){
			signature.append("(");
			for(int i = 0;i<params.size();i++){
				if(i != 0){
					signature.append(",");		
				}
				signature.append(params.get(i));
			}
			signature.append(")");
		}
		
		OperatorImpl op = (OperatorImpl) operatorFactory.createOperatorTemplate(signature.toString(), abstractAction.getPre().toArray(new String[]{}), abstractAction.getPost().toArray(new String[]{}));
		op.setSeam(abstractAction.isSeam());
		if(!abstractAction.isEnabled()){
			op.disable();
		}
		
		return op;
	}
}
