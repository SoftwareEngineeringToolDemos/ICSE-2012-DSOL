package org.dsol.planner.api;

import java.util.List;

public abstract class State implements Cloneable {
	
	public abstract List<Fact> getFacts();
	public abstract State apply(List<Fact> effects);
	public abstract State clone();
	
	//public abstract JsonElement toJSON();
}
