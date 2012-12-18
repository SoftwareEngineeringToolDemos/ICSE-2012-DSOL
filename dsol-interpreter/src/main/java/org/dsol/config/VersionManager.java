package org.dsol.config;

public interface VersionManager {

	/**
	 * Return the current version of the model
	 * @return
	 */
	public String getCurrentVersion();
	
	/**
	 * updates the current version number and returns the new version
	 * @return
	 */
	public String updateVersion();
	
}
