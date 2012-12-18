package org.dsol.engine;

import org.dsol.ConcreteAction;
import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Plan;
import org.dsol.planner.api.PlanResult;

public abstract class ActionBinder {
	
	protected PlanResult planResult;
	protected Plan plan;
	protected ConcreteActionsFactory concreteActionsFactory;
	
	public ActionBinder(PlanResult planResult, ConcreteActionsFactory concreteActionsFactory){
		this.planResult = planResult;
		this.plan = planResult.getPlan();
		this.concreteActionsFactory = concreteActionsFactory;
	}

	public abstract ConcreteAction getBinding(AbstractAction abstractAction);
	
	public abstract void bindActions();
	public abstract void markAsExecuted(AbstractAction abstractAction);

}
