package org.dsol.engine.qos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dsol.ConcreteAction;
import org.dsol.engine.ActionBinder;
import org.dsol.engine.ConcreteActionsFactory;
import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Goal;
import org.dsol.planner.api.Plan;
import org.dsol.planner.api.PlanResult;
import org.dsol.planner.qos.QoS;
import org.jampl.Constant;
import org.jampl.EqualExpression;
import org.jampl.Expression;
import org.jampl.GreaterThanOrEqualExpression;
import org.jampl.LessThanOrEqualExpression;
import org.jampl.Log;
import org.jampl.Max;
import org.jampl.Model;
import org.jampl.Objective;
import org.jampl.Operand;
import org.jampl.SimpleOperand;
import org.jampl.Variable;
import org.jampl.exception.InfeasibleProblemException;
import org.jampl.exception.InvalidModelException;

public class AMPLBinderOptimizer extends ActionBinder {

	/*
	 * Map containing all the concrete actions, separated by abstract actions,
	 * ordered by response time
	 */
	private Map<AbstractAction, List<ConcreteAction>> orderedActions;
	

	private boolean minimizeResponseTime = false;
	private boolean maximizeReliability = true;
	private Constant DESIRED_RESPONSE_TIME = null;
	private Constant DESIRED_RELIABILITY = null;

	private PlanBinding planBinding;
	private boolean adaptativeRebindingOn = false;
	private double aggregatedReliability;

	public AMPLBinderOptimizer(PlanResult plan, ConcreteActionsFactory concreteActionsFactory) {
		super(plan, concreteActionsFactory);
		
		Goal goal = planResult.getGoal();
		
		if(goal.isDesiredValueDefinedForQoSMetric(QoS.RESPONSE_TIME.value())){
			DESIRED_RESPONSE_TIME = new Constant(goal.getDesiredQoSValue(QoS.RESPONSE_TIME.value()).getValue());			
		}
		if(goal.isDesiredValueDefinedForQoSMetric(QoS.RELIABILITY.value())){
			DESIRED_RELIABILITY = new Constant(goal.getDesiredQoSValue(QoS.RELIABILITY.value()).getValue());			
		}

		setOrderedActions();
	}

	private void setOrderedActions() {
		orderedActions = new HashMap<AbstractAction, List<ConcreteAction>>();
		for (int i = 0; i < plan.size(); i++) {
			List<AbstractAction> abstractActions = plan.getLevel(i);
			for (int j = 0; j < abstractActions.size(); j++) {
				AbstractAction abstractAction = abstractActions.get(j);
				orderedActions.put(abstractAction, concreteActionsFactory.getConcreteActionsOrderedByResponseTime(abstractAction));
			}
		}
	}

	public void markAsExecuted(AbstractAction abstractAction) {
		adaptativeRebindingOn = true;
		synchronized (planBinding) {
			planBinding.markAsExecuted(abstractAction);
			aggregatedReliability = aggregatedReliability
					/ planBinding.getBinding(abstractAction).getReliability();
		}
	}

	@Override
	public ConcreteAction getBinding(AbstractAction abstractAction) {
		synchronized (planBinding) {
			ConcreteAction currentBinding = planBinding.getBinding(abstractAction);
			if (adaptativeRebindingOn) {
				List<ConcreteAction> orderedConcreteActions = this.orderedActions
						.get(abstractAction);
				double localAggregatedReliability = aggregatedReliability
						/ currentBinding.getReliability();
				for (ConcreteAction concreteAction : orderedConcreteActions) {
					double reliability = concreteAction.getReliability();
					localAggregatedReliability = localAggregatedReliability
							* reliability;
					if (localAggregatedReliability >= DESIRED_RELIABILITY
							.getValue().doubleValue()) {
						planBinding.setBinding(abstractAction, concreteAction);
						aggregatedReliability = aggregatedReliability
								* reliability;
						break;
					} else {
						localAggregatedReliability = localAggregatedReliability
								/ reliability;
					}
				}
				return planBinding.getBinding(abstractAction);
			}
			return currentBinding;
		}
	}

	@Override
	public void bindActions() {
		Model model = new Model();

		Objective objective = new Objective("opt");

		List<AbstractAction> abstractActions = null;
		Constant ONE = new Constant(1);
		
		Expression responseTimeExpression = new Expression();
		Expression reliabilityExpression = new Expression();

		//Variables used inside the loop
		Expression abstractActionResponseTimeExpression = null;
		Expression abstractActionReliabilityExpression = null;
		Variable variable = null;
		List<ConcreteAction> concreteActions = null;
		Expression oneBinding = null;
		ConcreteAction concreteAction = null;
		List<Operand> paralellResponseTimes = null;
		
		int oneBindingIndex = 0;
		AbstractAction abstractAction = null;
		
		Map<String, List<ConcreteAction>> concreteActionsMap = new HashMap<String, List<ConcreteAction>>();
		
		for (int i = 0; i < plan.size(); i++) {
			abstractActions = plan.getLevel(i);
			paralellResponseTimes = new ArrayList<Operand>();
			
			
			for (int j = 0; j < abstractActions.size(); j++) {
				
				abstractActionResponseTimeExpression = new Expression();
				abstractActionReliabilityExpression = new Expression();
				oneBinding = new Expression();
				abstractAction = abstractActions.get(j);
				
				concreteActions = concreteActionsFactory.getConcreteActions(abstractAction);
				concreteActionsMap.put(abstractAction.getName(), concreteActions);
				
				for (int s = 0; s < concreteActions.size(); s++) {
					variable = new Variable(getVariableName(i, j, s)).asBinary();
					
					model.addVariable(variable);
					
					concreteAction = concreteActions.get(s);

					abstractActionResponseTimeExpression
							.plus(new SimpleOperand(concreteAction
									.getResponseTime(), variable));

					abstractActionReliabilityExpression.plus(new Expression(
							new SimpleOperand(variable)).times(new Log(
							new Constant(concreteAction.getReliability()))));

					oneBinding.plus(variable);
				}
				
				paralellResponseTimes.add(abstractActionResponseTimeExpression);
				reliabilityExpression.plus(abstractActionReliabilityExpression);

				objective.subjectTo("oneB" + (oneBindingIndex++), new EqualExpression(oneBinding).equalTo(ONE));
			}
			
			if (paralellResponseTimes.size() > 1) {
				responseTimeExpression.plus(new Max(paralellResponseTimes));
			} else {
				responseTimeExpression.plus(paralellResponseTimes.get(0));
			}
			
		}
		
		if (minimizeResponseTime) {
			objective.minimize(responseTimeExpression);
		} else if (maximizeReliability) {
			objective.maximize(reliabilityExpression);
		}

		objective.subjectTo("d_rt",
				new LessThanOrEqualExpression(responseTimeExpression)
						.lessThanOrEqualTo(DESIRED_RESPONSE_TIME));
		objective.subjectTo("d_rl",
				new GreaterThanOrEqualExpression(reliabilityExpression)
						.greaterThanOrEqualTo(new Log(DESIRED_RELIABILITY)));

		model.setObjective(objective);

		try {
			
			model.solve();

			planBinding = new PlanBinding();
			for (int i = 0; i < plan.size(); i++) {
				abstractActions = plan.getLevel(i);
				for (int j = 0; j < abstractActions.size(); j++) {
					abstractAction = abstractActions.get(j);
					concreteActions = concreteActionsMap.get(abstractAction.getName());

					concreteAction = null;
					double maxValue = 0;
					for (int s = 0; s < concreteActions.size(); s++) {
						variable = model.getVariable(getVariableName(i, j, s));

						if (variable.getValue() == 1) {
							concreteAction = concreteActions.get(s);
							break;// stop searching
						}

						// Sometimes may happen that none of the variables is
						// null because
						// of the solver... In those cases we take the one with
						// the max value
						if (variable.getValue() > maxValue) {
							concreteAction = concreteActions.get(s);
							maxValue = variable.getValue();
						}
					}
					planBinding.setBinding(abstractAction, concreteAction);
				}
			}
			
			ReliabilityQoSAggregator qoSAggregator = new ReliabilityQoSAggregator(plan, planBinding);
			aggregatedReliability = qoSAggregator.getAggregatedReliability();
			
			
		} catch (InfeasibleProblemException e) {
			e.printStackTrace();
		} catch (InvalidModelException e) {
			e.printStackTrace();
		}
	}

	private String getVariableName(int level, int abstractActionIndex,
			int concreteActionIndex) {
		return "A" + level + "_" + abstractActionIndex + "_"+ concreteActionIndex;
	}

	class PlanBinding {

		private Map<AbstractAction, OptimalBinding> actions;

		public PlanBinding() {
			actions = new HashMap<AbstractAction, AMPLBinderOptimizer.OptimalBinding>();
		}

		public ConcreteAction getBinding(AbstractAction abstractAction) {
			return actions.get(abstractAction).getConcreteAction();
		}

		public void setBinding(AbstractAction abstractAction,
				ConcreteAction concreteAction) {
			actions.put(abstractAction, new OptimalBinding(concreteAction));
		}

		public void markAsExecuted(AbstractAction abstractAction) {
			OptimalBinding optimalBinding = actions.get(abstractAction);
			optimalBinding.markAsExecuted();
		}

		public double getReliability(AbstractAction abstractAction) {
			return actions.get(abstractAction).getReliability();
		}

	}

	class OptimalBinding {
		private ConcreteAction action;
		private int responseTime = -1;
		private double reliability = -1;
		private boolean executed = false;

		public OptimalBinding(ConcreteAction action) {
			this.action = action;
		}

		public ConcreteAction getConcreteAction() {
			return action;
		}

		public double getReliability() {
			if (reliability == -1) {
				return action.getReliability();
			}
			return reliability;
		}

		public int getResponseTime() {
			if (responseTime == -1) {
				return action.getResponseTime();
			}
			return responseTime;
		}

		public void markAsExecuted() {
			responseTime = 0;
			reliability = 1;
			executed = true;
		}

		public boolean wasExecuted() {
			return executed;
		}

	}

	public class ReliabilityQoSAggregator {
		private PlanBinding planBinding;
		private Plan plan;

		public ReliabilityQoSAggregator(Plan plan, PlanBinding planBinding) {
			this.plan = plan;
			this.planBinding = planBinding;
		}

		public Double getAggregatedReliability() {

			Double planAggregatedValue = 1d;
			for (List<AbstractAction> level : plan.getSteps()) {
				planAggregatedValue = planAggregatedValue
						* getLevelValue(level);
			}

			return planAggregatedValue;
		}

		protected double getLevelValue(List<AbstractAction> level) {
			double levelValue = 1;
			for (AbstractAction abstractAction : level) {
				levelValue = levelValue * planBinding.getBinding(abstractAction).getReliability();
			}
			return levelValue;
		}
	}

}
