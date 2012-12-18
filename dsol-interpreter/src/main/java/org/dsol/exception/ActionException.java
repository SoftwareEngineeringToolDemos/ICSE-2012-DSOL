package org.dsol.exception;


import org.dsol.planner.api.AbstractAction;

public class ActionException extends Exception {

	private AbstractAction abstractAction;
	private Throwable why;

	public ActionException(AbstractAction abstractAction, Throwable why) {
		this.abstractAction = abstractAction;
		this.why = why;
	}

	public AbstractAction getAbstractAction() {
		return abstractAction;
	}
	
	@Override
	public Throwable getCause() {
		return why;
	}

	@Override
	public void printStackTrace() {
		why.printStackTrace();
	}
}
