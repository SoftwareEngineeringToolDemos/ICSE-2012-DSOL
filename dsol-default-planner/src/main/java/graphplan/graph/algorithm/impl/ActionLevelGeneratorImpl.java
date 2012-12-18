package graphplan.graph.algorithm.impl;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.ActionLevel;
import graphplan.graph.PlanningGraphException;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.ActionLevelGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ActionLevelGeneratorImpl implements ActionLevelGenerator {
	


	public ActionLevel createNextActionLevel(PropositionLevel propositionLevel) throws PlanningGraphException {
		final ActionLevel actionLevel = new ActionLevel();
		
		OperatorFactory opFactory = OperatorFactory.getInstance();
		LinkedHashSet<Operator> opTemplateSet = new LinkedHashSet<Operator>();
		Set<Operator> opSet = new LinkedHashSet<Operator>();
		Set<Proposition> preconds = new HashSet<Proposition>(); 
				
		//TODO Change this to scan by operator rather than by proposition
		//operatorTemplate
		opTemplateSet.addAll(opFactory.getRequiringOperatorTemplates(propositionLevel));

		// For every proposition
		for (Proposition proposition : propositionLevel) {
			//Add all noops
			opSet.add(opFactory.getNoop(proposition));
			//And prepare the list of preconditons for later
			preconds.add(proposition);
		}
		//Get operators with empty preconditions
		opTemplateSet.addAll(opFactory.getRequiringOperatorTemplates((Proposition)null));

		//Piece of crap algorithm used before has been replaced by this call
		try {
			opSet.addAll(opFactory.getAllPossibleInstantiations(new ArrayList<Operator>(opTemplateSet), preconds));
		} catch (OperatorFactoryException e) {
			throw new PlanningGraphException(e.getMessage(),propositionLevel.getIndex()+1);
		}
		for (Operator operator : opSet) {
			actionLevel.addAction(operator);
		}
		// TODO discover how to properly instantiate operator templates
		// TODO optimize this algorithm
		return actionLevel;
	}

}
