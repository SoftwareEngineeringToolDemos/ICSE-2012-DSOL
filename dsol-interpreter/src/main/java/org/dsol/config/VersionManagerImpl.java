package org.dsol.config;

public class VersionManagerImpl implements VersionManager {

	private String currentVersion;
	
	public VersionManagerImpl() {
		currentVersion = "1";
	}
	
	@Override
	public String getCurrentVersion() {
		return "1";
	}

	@Override
	public String updateVersion() {
		Integer currentVersionAsInt = new Integer(currentVersion);
		currentVersionAsInt++;
		currentVersion = currentVersionAsInt.toString();
		return currentVersion;
	}

}
