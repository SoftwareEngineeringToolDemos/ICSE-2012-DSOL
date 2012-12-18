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
package graphplan.graph.algorithm;

import graphplan.PlanResult;
import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.graph.ActionLevel;
import graphplan.graph.GraphElement;
import graphplan.graph.GraphLevel;
import graphplan.graph.PlanningGraph;
import graphplan.graph.PropositionLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A visitor class that implements the Graphplan solution extraction
 * algorithm.
 * 
 * TODO implement solution extraction using this pattern
 * @author Felipe Meneguzzi
 *
 */
public class MultipleSolutionsExtractionVisitor extends SolutionExtractionVisitor {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(MultipleSolutionsExtractionVisitor.class.getName());
	
	protected DomainDescription domainDescription;
	List<PlanResult> possiblePlans = new ArrayList<PlanResult>();
	
	private int MAX_VISITS_WITHOUT_NEW_PLAN = 1;
	private int visitsWithoutNewPlan = 0;
	private int lastPossiblePlansSize = 0;
	
	public MultipleSolutionsExtractionVisitor(DomainDescription domainDescription) {
		super(domainDescription);	
		this.domainDescription = domainDescription;
	}
			
	public boolean visitGraphLevel(GraphLevel graphLevel) {
		if(graphLevel.isActionLevel()) {
			//For every action level we visit, we add a list
			//of actions to the support action stack to be
			//used in the iteration of the proposition level
			boolean planFound = graphLevel.getPrevLevel().accept(this);
			return planFound;
		} else {
			if(graphLevel.getPrevLevel() == null) {
				PlanResult result = new PlanResult(this.supportActionStack, domainDescription.getInitialState());
				//result.reorder(domainDescription.getInitialState());
				if(!possiblePlans.contains(result)){
					System.out.println("Plan found.");
					System.out.println(result);
					possiblePlans.add(result);
					return true;
				}
				else {
					//System.out.println("Plan found but already exists.");
					return false;
				}
			}
			return super.visitGraphLevel(graphLevel);
		}

		//return false;
	}
	
	@Override
	public boolean visitElement(GraphElement element) {
		if(element instanceof PlanningGraph) {
			System.out.println("visit");
			if(visitsWithoutNewPlan > MAX_VISITS_WITHOUT_NEW_PLAN){
				return false;
			}
			if(possiblePlans.size() == lastPossiblePlansSize){
				visitsWithoutNewPlan++;
			}
			else{
				lastPossiblePlansSize = possiblePlans.size();
				visitsWithoutNewPlan = 0;
			}
		}
		
		return super.visitElement(element);
	}
	
	protected boolean visitPropositionLevel(PropositionLevel propositionLevel, 
										    Set<Proposition> subGoals) {
		//If we have reached the first proposition level
		// We have found a plan
		// TODO check this for redundancy
		if (propositionLevel.getPrevLevel() == null) {
			return true;
		}

		if (memoizationTable.isNoGood(subGoals, propositionLevel.getIndex())) {
			return false;
		}
		
		final ActionLevel actionLevel = (ActionLevel) propositionLevel.getPrevLevel();

		//For each possible set of actions
		for(ActionSetIterator iterator = new ActionSetIterator(subGoals, actionLevel); iterator.hasNext(); ) {
			Set<Operator> selectedOperators = iterator.next();
			if(selectedOperators != null) {
				supportActionStack.push(selectedOperators);
				Set<Proposition> newSubGoals = determineSubgoals(selectedOperators);
				this.subGoalStack.push(newSubGoals);				
				propositionLevel.getPrevLevel().accept(this);
				this.subGoalStack.pop();
				this.supportActionStack.pop();
			}
		}
		return !possiblePlans.isEmpty();
	}		
	
	
	@Override
	public List<PlanResult> getPlanResult() {
		if(possiblePlans.isEmpty()){
			Arrays.asList(new PlanResult(false));
		}
		return possiblePlans;
	}
	
	
	public final boolean levelledOff(int graphLevel) {
		if(visitsWithoutNewPlan > MAX_VISITS_WITHOUT_NEW_PLAN){
			return true;
		}
		return super.levelledOff(graphLevel);
	}

}
