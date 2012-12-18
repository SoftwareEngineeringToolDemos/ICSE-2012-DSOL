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
import graphplan.graph.GraphElementVisitor;
import graphplan.graph.GraphLevel;
import graphplan.graph.PlanningGraph;
import graphplan.graph.PropositionLevel;
import graphplan.graph.memo.MemoizationTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * A visitor class that implements the Graphplan solution extraction
 * algorithm.
 * 
 * TODO implement solution extraction using this pattern
 * @author Felipe Meneguzzi
 *
 */
public class SolutionExtractionVisitor implements GraphElementVisitor {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SolutionExtractionVisitor.class.getName());
	
	protected final List<Proposition> goals;
	
	protected final MemoizationTable memoizationTable ;
	
	protected Stack<Set<Proposition>> subGoalStack;
	protected Stack<Set<Operator>> supportActionStack;
	
	protected PlanResult planResult = null;
	protected DomainDescription domainDescription;
	
	private SolutionExtractionVisitor(List<Proposition> goals) {
		this.goals = goals;
		//By default the plan result will be false, unless changed during a
		//round of solution extraction
		planResult = new PlanResult(false);
		subGoalStack = new Stack<Set<Proposition>>();
		supportActionStack = new Stack<Set<Operator>>();		
		memoizationTable = new MemoizationTable();
	}
	
	public SolutionExtractionVisitor(DomainDescription domainDescription) {
		this(domainDescription.getGoalState());
		this.domainDescription = domainDescription;
	}

	public boolean visitElement(GraphElement element) {
		if(element instanceof PlanningGraph) {
			PlanningGraph planningGraph = (PlanningGraph) element;
			if(planningGraph.getLastGraphLevel().isPropositionLevel()) {
				subGoalStack.clear();
				supportActionStack.clear();
				subGoalStack.push(new TreeSet<Proposition>(goals));
				
				//Whenever we try to iterate in the graph, we need to expand
				//the no goods table to match the size of the graph
				memoizationTable.ensureCapacity(planningGraph.size()/2);
				
				if(planningGraph.getLastGraphLevel().accept(this)) {
					planResult = new PlanResult(this.supportActionStack);
				} else {
					planResult = new PlanResult(false);
				}
				
				//logger.info("Table size is: "+noGoodTableSize());
				//logger.info("Hits         : "+hits);
				//logger.info("Misses       : "+misses);
				
				return planResult.isTrue();
			}
		}
		return false;
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
				//We hit the first level, return
				return true;
			}
			PropositionLevel propositionLevel = (PropositionLevel) graphLevel;
			Set<Proposition> subGoals = new TreeSet<Proposition>(subGoalStack.peek());
			
			//First, check the no goods table
			if(memoizationTable.isNoGood(subGoalStack.peek(), propositionLevel.getIndex())) {
				//And not even creep if the goals are no good
				return false;
			}
			
			//Then check if the goals are conceptually possible
			//If the goals are possible in this level
			if(propositionLevel.goalsPossible(subGoals)){				
				boolean planFound = this.visitPropositionLevel(propositionLevel, subGoals);
				/*if(!planFound) {
					this.supportActionStack.pop();
				}*/
				return planFound;
			} else {
				//When memoization is in, check this
				return false;
			}
		}

		//return false;
	}
	
	protected boolean visitPropositionLevel(PropositionLevel propositionLevel, 
										  Set<Proposition> subGoals) {
		//If we have reached the first proposition level
		//We have found a plan
		//TODO check this for redundancy
		if(propositionLevel.getPrevLevel() == null) {
			return true;
		}
		
		if(memoizationTable.isNoGood(subGoals, propositionLevel.getIndex())) {
			return false;
		}
		
		final ActionLevel actionLevel = (ActionLevel) propositionLevel.getPrevLevel();
		
		//For each possible set of actions
		for(ActionSetIterator iterator = new ActionSetIterator(subGoals, actionLevel);
			iterator.hasNext(); ) {
			Set<Operator> selectedOperators = iterator.next();
			if(selectedOperators != null) {
				supportActionStack.push(selectedOperators);
				Set<Proposition> newSubGoals = determineSubgoals(selectedOperators);
				this.subGoalStack.push(newSubGoals);
				
				boolean planFound = propositionLevel.getPrevLevel().accept(this);
				
 				if(!planFound) {
					this.memoizationTable.addNoGood(newSubGoals, propositionLevel.getIndex()-2);
					this.subGoalStack.pop();
					this.supportActionStack.pop();
					//this.memoizationTable.addNoGood(newSubGoals, propositionLevel.getIndex()-2);
 				} else {
					return true;
 				}
			}
		}
		return false;
	}
	
	/**
	 * Tries to determine early one if a set of actions will not be minimal;
	 * @param proposition
	 * @param actions
	 * @return
	 */
	protected final boolean alreadySatisfied(Proposition proposition, Set<Operator> actions) {
		for(Operator operator: actions) {
			if(operator.getEffects().contains(proposition))
				return true;
		}
		return false;
	}
	
	/**
	 * An iterator over the set of all possible combinations of operators
	 * needed to satisfy the specified set of subgoals in the specified
	 * action level.
	 * @author Felipe Meneguzzi
	 *
	 */
	public class ActionSetIterator implements Iterator<Set<Operator>> {
		//The current actions selected by this iterator
		private final List<Operator> actionSet;
		//The subgoals for which action sets are trying to satisfy
		private final Proposition[] subGoals;
		//A reference to the action level used in this iterator
		private final ActionLevel actionLevel;
		//The iterators over the possible operators for each proposition
		private final Iterator<Operator>[] iterators;
		//A cache of the required operators list
		private final List<Operator>[] requiredOperators;
		//The operators selected so far
		private final Operator[] selectedOperators;
		//Previous propositional level
		PropositionLevel propositionLevel = null;
		
		//Temp variable
		protected final Set<Proposition> achievableGoals;
		
		public ActionSetIterator(Set<Proposition> subGoals, ActionLevel actionLevel) {
			this.actionSet = new ArrayList<Operator>(subGoals.size());
			this.achievableGoals = new HashSet<Proposition>();
			this.subGoals = subGoals.toArray(new Proposition[subGoals.size()]);
			this.iterators = new Iterator[subGoals.size()];
			this.actionLevel = actionLevel;
			this.requiredOperators = new List[subGoals.size()];
			this.selectedOperators = new Operator[subGoals.size()];
			if(actionLevel.getPrevLevel() != null && actionLevel.getPrevLevel().isPropositionLevel()){
					propositionLevel = (PropositionLevel) actionLevel.getPrevLevel();
			}
						
			for(int i=0; i< this.subGoals.length; i++) {
				List<Operator> ops = actionLevel.getGeneratingActions(this.subGoals[i]);
				this.requiredOperators[i] = ops;
				this.iterators[i] = ops.iterator();
				if(i>0) {
					selectedOperators[i] = iterators[i].next();
				}
			}
		}

		public boolean hasNext() {
			for(Iterator<Operator> iterator : iterators) {
				if(iterator.hasNext()) {
					return true;
				}
			}
			return false;
		}

		public Set<Operator> next() {
			boolean advanceNext = true;
			
			int i=0;
			//We only advance the next iterator if we had
			//to reset the current one
			while(advanceNext) {
				if(iterators[i].hasNext()) {
					advanceNext = false;
					selectedOperators[i] = iterators[i].next();
				} else {
					iterators[i] = requiredOperators[i].iterator();
					selectedOperators[i] = iterators[i].next();
					i++;
				}
			}
			
			achievableGoals.clear();
			actionSet.clear();
			
			for(i=0; i<selectedOperators.length; i++) {
				//Maybe we don't need to do all these checks
				//Make sure we did not select the same operator twice
				if(actionSet.contains(selectedOperators[i])) {
					continue;
				}
				
				//If the selected operator is not already in the set
				//And the proposition it is supposed to achieve
				//has already been achieved through some other operator
				//Then this set is not minimal
				if(achievableGoals.contains(subGoals[i])) {
					return null;
				}
				//Make sure this operator is not inconsistent with the 
				//ones selected
				for(Operator operator:actionSet) {
					if(actionLevel.isMutex(selectedOperators[i], operator)) {
						return null;
					}
				}

				actionSet.add(selectedOperators[i]);
				Proposition[] subGoalsCopy = new Proposition[i+1];
				System.arraycopy(subGoals, 0, subGoalsCopy, 0, subGoalsCopy.length);
				
				int sizeBeforeReduction = actionSet.size();
				removeUnnecessarySteps(Arrays.asList(subGoalsCopy));
				int sizeAfterReduction = actionSet.size();
				
				if(sizeAfterReduction == sizeBeforeReduction){
					achievableGoals.addAll(selectedOperators[i].getEffects());
				}
				else{
					achievableGoals.clear();
					for(Operator operator:actionSet) {
						achievableGoals.addAll(operator.getEffects());						
					}
				}
			}
			return new LinkedHashSet<Operator>(actionSet);
		}

		public void remove() {
			// TODO Auto-generated method stub
		}
		
		private void removeUnnecessarySteps(List<Proposition> partialGoal){
			
			boolean simplify = true;
			while(simplify){
				simplify = false;
				int size = actionSet.size();
				for(int i = 0; i < size;i++){
					Operator removedOperator = actionSet.remove(i);
					boolean goalStillReacheable = goalStillReacheable(partialGoal);
					if(goalStillReacheable){
						simplify = true;
						break;
					}
					actionSet.add(i, removedOperator);
				}				
			}
		}
		
		private boolean goalStillReacheable(List<Proposition> partialGoal){
			//For all subgoals, there is at least one actions that has it as effect
			boolean goalPartStillReacheable = false;
			for(Proposition proposition:partialGoal){
				goalPartStillReacheable = false;
				for(Operator operator:actionSet){
					if(operator.getEffects().contains(proposition)){
						goalPartStillReacheable = true;
					}
				}
				if(!goalPartStillReacheable){//if this proposition is not provided anymore,
											 //we are sure that the goal is not reachable
					return false;
				}
			}
			
			return true;
		}
		
	}
	
	/**
	 * Gets the preconditions for the operators 
	 * given as parameter
	 * @param operators
	 * @return
	 */
	protected Set<Proposition> determineSubgoals(Set<Operator> operators) {
		final TreeSet<Proposition> subGoals = new TreeSet<Proposition>();
		
		for (Operator operator : operators) {
			for(Proposition proposition : operator.getPreconds()) {
				subGoals.add(proposition);
			}
		}
		
		return subGoals;
	}
	
	/**
	 * Returns whether or not the memoization table has levelled off. This just
	 * forwards the call to the memoization table.
	 * 
	 * XXX Review this method, I suspect it may be wrong according to Blum and
	 * Furst's paper.
	 * 
	 * @param graphLevel The last graph level
	 * @return
	 */
	public boolean levelledOff(int graphLevel) {
		return memoizationTable.levelledOff(graphLevel);
	}

	/**
	 * Returns the plan resulting from this solution extraction cycle.
	 * @return
	 */
	public List<PlanResult> getPlanResult() {
		return Arrays.asList(planResult);
	}
}
