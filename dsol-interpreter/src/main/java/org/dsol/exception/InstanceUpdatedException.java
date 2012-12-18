package org.dsol.exception;


public class InstanceUpdatedException extends Exception {

	/**
	 * Represents the point in which the plan was stoped
	 */
	private int level;
	
	public InstanceUpdatedException(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

}
