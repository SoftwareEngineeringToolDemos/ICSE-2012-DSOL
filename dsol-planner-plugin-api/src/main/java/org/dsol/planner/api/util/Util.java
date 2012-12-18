package org.dsol.planner.api.util;

import java.util.ArrayList;
import java.util.List;

import org.dsol.planner.api.Fact;

public class Util {

	public static List<String> getFactsAsString(List<Fact> facts){
		List<String> factsAsString = new ArrayList<String>();
		for(Fact fact:facts){
			factsAsString.add(fact.get());
		}
		return factsAsString;
	}

	
}
