package org.dsol.monitoring;

import java.util.List;

public interface MonitorCounter {

	public void addSuccessRequest(String abstractAction);
	public void addFaultyRequest(String abstractAction);
	public List<State> getStates();

}
