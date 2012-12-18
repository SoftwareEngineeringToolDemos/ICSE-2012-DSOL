package org.dsol.monitoring;

import java.util.concurrent.atomic.AtomicLong;

import org.dsol.planner.api.AbstractAction;

public class State {

	private AbstractAction abstractAction;
	private AtomicLong numberOfSuccessRequests;
	private AtomicLong numberOfFaultyRequests;

	public State(AbstractAction abstractAction) {
		numberOfSuccessRequests = new AtomicLong(0);
		numberOfFaultyRequests = new AtomicLong(0);
		this.abstractAction= abstractAction;
	}

	public void addSuccessRequest() {
		numberOfSuccessRequests.incrementAndGet();
	}

	public void addFaultyRequest() {
		numberOfFaultyRequests.incrementAndGet();
	}

	public AbstractAction getAbstractAction() {
		return abstractAction;
	}

	public Long getNumberOfSuccessRequests() {
		return numberOfSuccessRequests.get();
	}

	public Long getNumberOfFaultyRequests() {
		return numberOfFaultyRequests.get();
	}
}
