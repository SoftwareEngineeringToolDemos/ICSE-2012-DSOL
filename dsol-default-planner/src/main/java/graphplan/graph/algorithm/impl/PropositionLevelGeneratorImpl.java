package graphplan.graph.algorithm.impl;

import graphplan.domain.Operator;
import graphplan.graph.ActionLevel;
import graphplan.graph.PlanningGraphException;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.PropositionLevelGenerator;

public class PropositionLevelGeneratorImpl implements PropositionLevelGenerator {

	public PropositionLevel createNextPropositionLevel(ActionLevel actionLevel) throws PlanningGraphException {
		PropositionLevel propositionLevel = new PropositionLevel();
		for (Operator operator : actionLevel) {
			propositionLevel.addPropositions(operator.getEffects());
		}
		return propositionLevel;
	}
}
