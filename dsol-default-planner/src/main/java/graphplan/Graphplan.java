/*
 * ---------------------------------------------------------------------------
 * Copyright (C) 2010  Felipe Meneguzzi
 * JavaGP is distributed under LGPL. See file LGPL.txt in this directory.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * To contact the author:
 * http://www.meneguzzi.eu/felipe/contact.html
 * ---------------------------------------------------------------------------
 */
package graphplan;

import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.PlanningGraph;
import graphplan.graph.PlanningGraphException;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.MultipleSolutionsExtractionVisitor;
import graphplan.graph.algorithm.SolutionExtractionVisitor;
import graphplan.graph.algorithm.impl.ActionLevelGeneratorImpl;
import graphplan.graph.algorithm.impl.PropositionLevelGeneratorImpl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main class and accessor for the Graphplan algorithm
 * 
 * @author Felipe Meneguzzi
 * 
 */
public class Graphplan {
	private static final Logger logger = Logger.getLogger(Graphplan.class.getName());
	public static final String LOGGER_FILE = "logging.properties";

	protected int maxLevels = 1000;
	private PlanningGraph planningGraph;
	private SolutionExtractionVisitor solutionExtraction;

	public static void setupLogger() {
		try {
			if (new File(LOGGER_FILE).exists()) {

				LogManager.getLogManager().readConfiguration(
						new FileInputStream(new File(LOGGER_FILE)));

			} else {
				LogManager.getLogManager().readConfiguration(
						Graphplan.class.getResourceAsStream("/" + LOGGER_FILE));
			}
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("Error setting up logger:" + e);
		}
	}

	/**
	 * Empty constructor for testing.
	 */
	public Graphplan() {

	}

	/**
	 * Sets the maximum number of levels to be searched for in creating the
	 * graph.
	 * 
	 * @param maxLevels
	 */
	public void setMaxLevels(int maxLevels) {
		this.maxLevels = maxLevels;
	}

	public PlanResult plan(DomainDescription domainDescription)
			throws PlanningGraphException, OperatorFactoryException {
		List<PlanResult> planResult =  plan(domainDescription, null, false);
		return planResult.get(0);
	}
	
	public List<PlanResult> plan(DomainDescription domainDescription, boolean returnMultiplePlans)
			throws PlanningGraphException, OperatorFactoryException {
		return plan(domainDescription, null, returnMultiplePlans);
	}
	
	/**
	 * Return all available plan in the given domain description
	 * @param domainDescription
	 * @return
	 * @throws PlanningGraphException
	 * @throws OperatorFactoryException
	 */
	public List<PlanResult> multiplan(DomainDescription domainDescription)
			throws PlanningGraphException, OperatorFactoryException {
		return plan(domainDescription, null, true);
	}

	/**
	 * 
	 * @param domainDescription
	 * @return
	 * @throws PlanningGraphException
	 * @throws OperatorFactoryException
	 */
	public List<PlanResult> plan( 	DomainDescription domainDescription, 
									List<Proposition> currentState, 
									boolean returnMultiplePlans) throws PlanningGraphException, OperatorFactoryException {

		PropositionLevel initialLevel = new PropositionLevel();
		if (currentState != null) {
			initialLevel.addPropositions(currentState);
		} else {
			initialLevel.addPropositions(domainDescription.getInitialState());
		}

		solutionExtraction = returnMultiplePlans ? 	new MultipleSolutionsExtractionVisitor(domainDescription) : 
													new SolutionExtractionVisitor(domainDescription);

		planningGraph = new PlanningGraph(initialLevel, new ActionLevelGeneratorImpl(), new PropositionLevelGeneratorImpl());

		OperatorFactory.getInstance().resetOperatorTemplates();
		
		for (Operator operator : domainDescription.getOperators()) {
			if (operator.isEnabled()) {
				OperatorFactory.getInstance().addOperatorTemplate(operator);
			}
		}

		boolean planFound = false;
		long init = System.currentTimeMillis();
		
		while ((returnMultiplePlans ||!planFound) && (planningGraph.size() <= maxLevels)) {
			try {
				planningGraph.expandGraph();
			} catch (PlanningGraphException e) {
				// If we have a problem with the planning graph
				// Issue the error and quit
				System.err.println(e);
				return Arrays.asList(new PlanResult(false));
			}

			if (planningGraph.goalsPossible(domainDescription.getGoalState(), planningGraph.size() - 1)) {
				// extract solution
				// logger.info("Extracting solution");
				planFound = planningGraph.accept(solutionExtraction);
				
				if (!planFound && !planPossible()) {
					if (currentState != null) {
						planningGraph = restartFromInitialState(domainDescription);
						currentState = null;
					} else {
							throw new PlanningGraphException(
									"Graph has levelled off, plan is not possible.",
									planningGraph.levelOffIndex());
					}
				}				
			} else {
				// If the goals are not possible, and the graph has levelled
				// off,
				// then this problem has no possible plan
				if (planningGraph.levelledOff()) {
					if (currentState != null) {
						planningGraph = restartFromInitialState(domainDescription);
						currentState = null;
					} else {
						throw new PlanningGraphException(
								"Goals are not possible and graph has levelled off, plan is not possible.",
								planningGraph.levelOffIndex());
					}
				}
			}
			
			if(	planFound && 
					returnMultiplePlans && 
						solutionExtraction.levelledOff(planningGraph.levelOffIndex())){
				break;
			}
		}
		
		long end = System.currentTimeMillis();
		List<PlanResult> planResults = solutionExtraction.getPlanResult();
		for(PlanResult result:planResults){
			result.setPlanningTime((int)( end - init ));
		}
		
		return solutionExtraction.getPlanResult();
	}

	private PlanningGraph restartFromInitialState(
			DomainDescription domainDescription) {
		PropositionLevel initialLevel = new PropositionLevel();
		initialLevel.addPropositions(domainDescription.getInitialState());
		planningGraph = new PlanningGraph(initialLevel,
				new ActionLevelGeneratorImpl(),
				new PropositionLevelGeneratorImpl());
		return planningGraph;
	}

	/**
	 * Returns whether or not a plan is possible according to both the
	 * memoization table criterion and the graph level size criterion.
	 * 
	 * @return
	 */
	public boolean planPossible() {
		if (!planningGraph.levelledOff()) {
			return true;
		} else {
			return solutionExtraction.levelledOff(planningGraph.levelOffIndex());
		}
	}

	/**
	 * @return the solutionExtraction
	 */
	public SolutionExtractionVisitor getSolutionExtraction() {
		return solutionExtraction;
	}

	/**
	 * @param solutionExtraction
	 *            the solutionExtraction to set
	 */
	public void setSolutionExtraction(
			SolutionExtractionVisitor solutionExtraction) {
		this.solutionExtraction = solutionExtraction;
	}

	/*
	 * ********************* ADDED CODE
	 * ************************************************
	 */

	public static List<Proposition> applyEffects(List<Proposition> props,
			List<Proposition> effects) {
		List<Proposition> retVal = new ArrayList<Proposition>(props);
		for (Proposition prop : effects) {
			Proposition clone = (Proposition) prop.clone();
			// change to mutex value
			clone.setNegated(!prop.negated());
			if (retVal.contains(clone)) {
				retVal.remove(clone);
			}

			if (!retVal.contains(prop)) {
				retVal.add(prop);
			}
		}
		return retVal;
	}

}
