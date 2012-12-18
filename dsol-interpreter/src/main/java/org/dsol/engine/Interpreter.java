package org.dsol.engine;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.dsol.ConcreteAction;
import org.dsol.Instance;
import org.dsol.exception.ActionException;
import org.dsol.exception.InstanceUpdatedException;
import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Plan;
import org.dsol.planner.api.PlanResult;

public class Interpreter{
	
	private Logger logger = Logger.getLogger(Interpreter.class.getName());
	private Map<AbstractAction, ConcreteAction> executedActions;
	
	private final Instance instance;

	public Interpreter() {
		instance = null;
	}
	
	public Interpreter(	Instance instance){
		this.instance = instance;
	}	
	
	public boolean executeService() throws Throwable{
		
		executedActions = new HashMap<AbstractAction, ConcreteAction>();
		
        boolean success = false;
        
        Plan plan = null;
        Plan lastPlan = null;
        
        boolean containsNextGoal = true;
        
        //This variable indicates that the plan execution has thrown an excetion
        //that is also thrown by the entry point method. This exception is than
        //propagated directly to the client, without re-planning
        boolean hasThrownAKnownException = false;
        Throwable exceptionThrown=null;
        
        int lastExecutedLevel = -1;
		while (!success && containsNextGoal && !hasThrownAKnownException) {
			List<PlanResult> plansFound = instance.plan();
			PlanResult planResult = plansFound.get(0);
			if (planResult.planFound()) {
				plan = planResult.getPlan();		
				
				if (lastPlan != null) {
					adjustPlans(plan, lastPlan, lastExecutedLevel);
					compensate(lastPlan, lastExecutedLevel);
				}

				try {
					printPlan(plan);
					executePlan(plan);
					success = true;
				} 
				catch (InstanceUpdatedException e) {
					//save last plan and go to re-planning phase!
					lastPlan = plan;
					lastExecutedLevel = e.getLevel();
					instance.updateRead();
				}
				catch (ActionException actionException) {
					
					actionException.printStackTrace();
					AbstractAction lastTriedStep = actionException.getAbstractAction();
					lastExecutedLevel = lastTriedStep.getLevel();
					lastPlan = plan;
					instance.removeOperation(lastTriedStep);		
				
					if(instance.isThrownException(actionException.getCause().getClass())){
						hasThrownAKnownException = true;
						exceptionThrown = actionException.getCause();
					}
				}
			}
			else{
				containsNextGoal = instance.tryNextGoal();	
			}			
		}
		
		if (!success) {
			if(plan != null){
				compensateAllExecutedActions(plan);
			}
		}
		if(hasThrownAKnownException){
			throw exceptionThrown;
		}
		return success;
	}
	
	protected void adjustPlans(	Plan newPlan, 
								Plan oldPlan, 
								int lastExecutedLevel){
		
		for (int i = 0; i <= lastExecutedLevel; i++) {
			List<AbstractAction> level = oldPlan.getLevel(i);
			
			//get all actions in this level in the old plan 
			for(AbstractAction action:level){
				//if the action was executed in the old plan
				if(action.isExecuted()){
					//check if the action is present also in the new plan
					AbstractAction newPlanAction = newPlan.getAction(i, action);
					
					//if the action is not found in the new plan, the action
					//is marked for compensation
					if(newPlanAction == null){
						action.markForCompensation();
					}
					else{
						
						//the action in the new plan will also be marked as executed
						//if it was executed in the old plan, and all actions in the previous level
						//that are pre-requisite to this action are also marked as executed
						boolean markAsExecuted = true;
						if(i > 0){
							List<AbstractAction> previousLevelActions = newPlan.getLevel(i - 1);
							for(AbstractAction previousLevelAction:previousLevelActions){
								if(newPlanAction.isTriggeredBy(previousLevelAction)){
									markAsExecuted = markAsExecuted && previousLevelAction.isExecuted();
								}
							}
						}
						else{
							markAsExecuted = true;
						}
						
						if(markAsExecuted){
							newPlanAction.markAsExecuted();	
						}
						else{
							action.markForCompensation();
						}
					}
				}
			}
		}
	}
	
	private void compensateAllExecutedActions(Plan plan){
		plan.markForCompensation();
		compensate(plan, plan.size() - 1);
	}
	
	/**
	 * This method is used to compensate an executed plan, from the point that
	 * the new plan diverge from this plan until the faulty operation
	 * @param plan
	 * @param from
	 * @param faultyOperation
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void compensate(final Plan plan, int startLevel){
		for (int i = startLevel; i >= 0; i--) {
			List<AbstractAction> level = plan.getLevel(i);
			for(AbstractAction action:level){
				if (action.isMarkedForCompensation()) {
					try {
						compensateExecutedActionOfThePlan(action);
					} catch (Throwable ex) {
						// TODO: What to do when errors occurs while compensating
						// actions?????
						ex.printStackTrace();
						System.out.println("Error while compensating operation " + action);
					}
				}				
			}
		}
	}
	
	protected void executePlan(Plan plan) throws ActionException, Throwable {
		int levelIndex=0;
		for(List<AbstractAction> level : plan.getSteps()){
			executeLevel(level);
			if(instance.wasUpdated()){
				throw new InstanceUpdatedException(levelIndex);
			}
			levelIndex++;
		}
	}
	
	ReentrantLock lock = new ReentrantLock();
	private ActionThread lastFinishedThread;
	int runningThreads=0;
	
	private void executeLevel(List<AbstractAction> level) throws ActionException, InterruptedException {
		
		Condition condition = lock.newCondition();
		
		List<ActionThread> threads = new ArrayList<Interpreter.ActionThread>();
		
		for (AbstractAction action : level) {
			
			//If the current step represents a seam action or
			//if the step was already executed in a previous plan (this will happen in the case when 
			//the plan of execution was re-planned and the new plan contain actions in common with
			//the previous one) this action can be "skipped".
			if(action.isSeam() || action.isExecuted()) continue;
		
			ActionThread actionThread = new ActionThread(action, condition);
			threads.add(actionThread);
		}
		
		runningThreads = threads.size();

		for(ActionThread thread:threads){
			thread.start();
		}
		
		try{
			lock.lock();
			while(runningThreads > 0){
				condition.await();
				if(lastFinishedThread.hasFailed()){
					for(ActionThread thread:threads){
						thread.stopAction();
					}
					throw new ActionException(lastFinishedThread.action, lastFinishedThread.getFault());
				}
			}	
		}
		finally{
			lock.unlock();
		}
		
		
	}

	private boolean executeAction(	AbstractAction action ) throws Throwable{
		List<String> params = action.getParamList();
		List<ConcreteAction> concreteActions = instance.getConcreteActions(action, params);
		
		boolean executedSuccessfully = false;
		for(ConcreteAction concreteAction:concreteActions){
			executedSuccessfully = concreteAction.execute(instance, params);
			if (executedSuccessfully) {
				action.markAsExecuted();
				executedActions.put(action, concreteAction);
				break;
			}		
		}		
		
		if(action.hasTerminated()){
			return false;
		}
		if(executedSuccessfully){
			return true;
		}
		throw new RuntimeException(
				"All concrete action found for the execution of step " + action + " are faulty!");
	}
	
	private void compensateExecutedActionOfThePlan(AbstractAction action) throws Throwable {
		
		ConcreteAction executedConcreteAction = executedActions.remove(action);
		executeCompensationAction(action, instance.getCompensationActions(executedConcreteAction));
	
	}
	
	
	
	private void executeCompensationAction(	AbstractAction actionToCompensate,
											List<ConcreteAction> concreteActions) throws Throwable {

		if(concreteActions.isEmpty()){
			logger.info("Compensation was not defined to "+actionToCompensate);
			return;
		}
		
		List<String> params = actionToCompensate.getParamList();
		boolean executedSuccessfully = false;
		for (ConcreteAction concreteAction : concreteActions) {
			// logger.info("Executing compensation action: " + concreteAction);
			executedSuccessfully = concreteAction.execute(instance,params);
			if (executedSuccessfully) {
				return;
			}
		}
		throw new RuntimeException(
				"All concrete action found for the execution of step "
						+ actionToCompensate + " are faulty!");
	}
	
    private void printPlan(Plan plan){
    	StringBuffer buffer = new StringBuffer();
        buffer.append("\n/*************** PLAN ***************/\n");
        
		for (List<AbstractAction> level : plan.getSteps()) {
			for (AbstractAction action : level) {
				buffer.append(action).append("   ");
			}
			buffer.append("\n");
		}
        
        buffer.append("/***********************************/\n");
        
        logger.info(buffer.toString());
    }
    
    class ActionThread extends Thread{
    	
    	private AbstractAction action;
    	private Condition condition;
    	private boolean running;
    	private Throwable fault;
    	
    	public ActionThread(AbstractAction action, Condition condition) {
			this.action = action;
			this.condition = condition;
		}
    	
    	public boolean isRunning(){
    		return running;
    	}
    	
    	public Throwable getFault() {
			return fault;
		}
    	
    	public boolean hasFailed(){
    		return fault != null;
    	}
    	
    	public void stopAction() {
    		try{
				lock.lock();
				if(isRunning()){
					running = false;
					action.terminate();					
				}
			}
			finally{
				lock.unlock();
			}
		}
    	
    	@Override
    	public void run() {
			try {
				running = true;
				if(executeAction(action)){
					action.markAsExecuted();					
				}
			} catch (Throwable e) {
				this.fault = e;
			}
			finally{
				try{
					lock.lock();
					if(isRunning()){
						running = false;
						runningThreads--;
						lastFinishedThread = this;
						condition.signal();					
					}
				}
				finally{
					lock.unlock();
				}
			}
    	}
    }
}
