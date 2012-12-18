package org.dsol.planner.impl;

import graphplan.Graphplan;
import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.domain.jason.OperatorImpl;
import graphplan.domain.jason.PropositionImpl;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.PlanningGraphException;
import graphplan.parser.ParseException;
import graphplan.parser.PlannerParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Fact;
import org.dsol.planner.api.GenericAbstractAction;
import org.dsol.planner.api.Goal;
import org.dsol.planner.api.PlanResult;
import org.dsol.planner.api.Planner;
import org.dsol.planner.api.State;
import org.dsol.planner.api.util.ProblemParserUtil;

/**
 * Default implementation of DSOL planner.
 */
public class DefaultPlanner implements Planner {
	
	private DomainDescription domain;
	Graphplan graphplan;
	private List<Goal> goals;
	
	private int currentGoalIndex = 0;
	private Goal currentGoal = null;
	Goal currentGoalState;
	boolean isMultiThread=true;
	
	
	/**
	 * Constructor used only for testing purposes
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public DefaultPlanner(){
		graphplan = new Graphplan();
	}

	@Override
	public void initialize(	InputStream actions, 
							InputStream initialState, 
							InputStream goal) throws Exception{
		
		if(isMultiThread){
			OperatorFactory.getInstance(true);			
		}
		else{
			OperatorFactory.getInstance().resetInvalidOperations();			
		}
		
		PlannerParser parser = new PlannerParser();
		
		this.goals = ProblemParserUtil.parseGoals(goal);
		domain = parser.parseProblem(actions, createProblem(initialState, new ByteArrayInputStream(Planner.EMPTY_GOAL.getBytes())));
		setGoal(0);
	}
	
	public void setIsMultiThread(boolean isMultiThread){
		this.isMultiThread = isMultiThread;
	}
	
	public List<PlanResult> plan(){
		setGoal(currentGoalIndex);
		List<PlanResult> plans = new ArrayList<PlanResult>();
		try {
			List<graphplan.PlanResult> planResults = graphplan.plan(domain, currentGoal.isQoSEnabled());
			for(graphplan.PlanResult planResult:planResults){
				if(planResult.isTrue()){
					List<Fact> initialState = Util.getPropositionsAsFacts(domain.getInitialState());
					State currentState = new StateImpl(initialState);

					PlanResult dsolPlanResult = new PlanResult(currentState.clone(),currentGoalState);
					dsolPlanResult.setPlanningTime(planResult.getPlanningTime());
					
					List<List<Operator>> stepsDividedByLevel = planResult.getSteps();
					int levelIndex = 0;
					for(List<Operator> level:stepsDividedByLevel){
						for (Operator operator : level) {
							AbstractAction action = new AbstractActionImpl(operator);
							action.setLevel(levelIndex);
							dsolPlanResult.addStep(levelIndex, action);
							
							currentState.apply(Util.getPropositionsAsFacts(operator.getEffects()));
							dsolPlanResult.addState(currentState.clone());
						}	
						levelIndex++;
					}
					plans.add(dsolPlanResult);
				}
			}
		} catch (PlanningGraphException e) {
			e.printStackTrace();
		} catch (OperatorFactoryException e) {
			e.printStackTrace();
		}
		if(plans.isEmpty()){
			plans.add(new PlanResult());
		}
		
		return plans;
	}
	
	public State getInitialState() {
		return new StateImpl(Util.getPropositionsAsFacts(domain.getInitialState()));
	}
	
	public void reset(){
		OperatorFactory.getInstance().resetInvalidOperations();
	}
	
	@Override
	public Goal getCurrentGoal() {
		return currentGoal;
	}
		
	protected void setGoal(int index) {
		currentGoalIndex = index;
		currentGoal = goals.get(index);
		List<Proposition> currentGoalState = domain.getGoalState();
		currentGoalState.clear();
		
		for(Fact fact:currentGoal){
			currentGoalState.add(new PropositionImpl(fact.get()));
		}
	}
	
	public boolean tryNextGoal() {
		currentGoalIndex++;
		return currentGoalIndex < goals.size();
	}

	public void removeOperation(AbstractAction action) {
		OperatorFactory operatorFactory = OperatorFactory.getInstance();
		if(action instanceof AbstractActionImpl){
			operatorFactory.addInvalidOperatorInstantiation(((AbstractActionImpl) action).getOperator().toString());	
		}
		else{
			operatorFactory.addInvalidOperatorInstantiation(action.toString());
		}
		
	}

	@Override
	public void addToInitialState(List<Fact> newFacts) {
		List<Proposition> initialState = domain.getInitialState();
		List<Proposition> newInitialState = new ArrayList<Proposition>();
		newInitialState.addAll(initialState);
		for(Fact fact:newFacts){
			newInitialState.add(new PropositionImpl(fact.get()));	
		}
		initialState.clear();
		initialState.addAll(newInitialState);
	}

	@Override
	public void setGoal(Goal goal) {
		goals.clear();
		goals.add(goal);
		setGoal(0);
	}

	@Override
	public void update(Planner plannerToBeUPdated) {
		DefaultPlanner dsolPlanner = (DefaultPlanner)plannerToBeUPdated;
		List<Operator> operators = dsolPlanner.domain.getOperators();
		operators.clear();
		for(Operator operator : domain.getOperators()){
			operators.add(operator);
		}
	}

	@Override
	public void updateActions(List<GenericAbstractAction> newAbstractActions) {
		List<Operator> operators = domain.getOperators();
		operators.clear();
		for(GenericAbstractAction abstractAction:newAbstractActions){
			OperatorImpl op = Util.createOperatorFromGenericAbstractAction(abstractAction);			
			operators.add(op);
		}
	}

	@Override
	public List<AbstractAction> getAbstractActions() {
		List<Operator> operators = domain.getOperators();
		List<AbstractAction> actions = new ArrayList<AbstractAction>();
		
		for(Operator operator:operators){
			actions.add(new AbstractActionImpl(operator));
		}
		
		return actions;
	}

	@Override
	public void disableAction(String action) {
		List<Operator> operators = domain.getOperators();
		
		for(Operator operator:operators){
			if(operator.getFunctor().equals(action)){
				operator.disable();
			}
		}		
	}

	@Override
	public void loadActions(InputStream actions) {
		PlannerParser plannerParser = new PlannerParser();
		try {
			domain.setOperators(plannerParser.parseOperators(actions));
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void setInitialState(State initialState) {

		try {
			
			PlannerParser plannerParser = new PlannerParser();
			
			DomainDescription domain = plannerParser.parseDomain(createProblem(createInitialStateStream(initialState), new ByteArrayInputStream(Planner.EMPTY_GOAL.getBytes())));
			
			this.domain.getInitialState().clear();
			for(Proposition fact:domain.getInitialState()){
				this.domain.getInitialState().add(fact);
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}	
	}
	
	private InputStream createInitialStateStream(State initialState){
		StringBuffer initialStateBuffer = new StringBuffer("start(");
		for(Fact fact:initialState.getFacts()){
			initialStateBuffer.append(fact.get());
			initialStateBuffer.append(",");
		}
		initialStateBuffer.deleteCharAt(initialStateBuffer.length() - 1);
		initialStateBuffer.append(")");
		return new ByteArrayInputStream(initialStateBuffer.toString().getBytes());
	}
	
	@Override
	public void setInitialStateAndGoal(InputStream initialState, InputStream goal) {

		try {
			setInitialStateAndGoal(initialState, ProblemParserUtil.parseGoals(goal));			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}	
	}
	
	public void setInitialStateAndGoal(InputStream initialState, List<Goal> goal) {

		try {
			
			PlannerParser plannerParser = new PlannerParser();
			this.goals = goal;			
			DomainDescription domain = plannerParser.parseDomain(createProblem(initialState, new ByteArrayInputStream(Planner.EMPTY_GOAL.getBytes())));
			
			this.domain.getInitialState().clear();
			for(Proposition fact:domain.getInitialState()){
				this.domain.getInitialState().add(fact);
			}
			
			setGoal(0);
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}	
	}
	
	private InputStream createProblem(InputStream initialState, InputStream goal) throws IOException{
		ByteArrayOutputStream problem = new ByteArrayOutputStream();
		byte[] bytes = new byte[10240];
		int bytesRead = 0;
		
		while((bytesRead=initialState.read(bytes)) != -1){
			problem.write(bytes,0,bytesRead);
		}
		problem.write("\n".getBytes());
		
		while((bytesRead=goal.read(bytes)) != -1){
			problem.write(bytes,0,bytesRead);
		}
		return new ByteArrayInputStream(problem.toByteArray());
	}	
}