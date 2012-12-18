package org.dsol.management;

import java.util.List;

import org.dsol.Instance;
import org.dsol.config.MethodsInfo;


public interface ManagementCallback {

	public List<Instance> getInstances();
	public Instance getInstance(String refId);
	public void updateModel(String refId, Boolean applyToRunningInstances, Actions actions);
	public void updateOrchestrationInterface(String instanceRefId, Boolean applyForAllInstances, MethodsInfo methodsInfo);
	public void disableAction(String action);
	

}
