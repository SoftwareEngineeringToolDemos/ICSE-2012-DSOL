package org.dsol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dsol.planner.api.AbstractAction;
import org.dsol.planner.api.Fact;


public class AbstractActionMock extends AbstractAction{

	private String name;
	private List<String> paramList;
	
	public AbstractActionMock(String step, int level) {
		this.level = level;
		int paramIndex = step.indexOf("(");
		if(paramIndex >=0){
			name = step.substring(0,paramIndex);	
			paramList = Arrays.asList(step.substring(paramIndex + 1,step.indexOf(")")).split(","));
		}
		else{
			name = step;
			paramList = new ArrayList<String>();
		}
		
	}
	
	public String getName() {
		return name;
	}

	public List<String> getParamList() {
		return paramList;
	}
	
	@Override
	public boolean equals(Object obj) {
		AbstractActionMock that = (AbstractActionMock)obj;
		return name.equals(that.getName()) && level == (that.level) ;
	}

	@Override
	public boolean isSeam() {
		return false;
	}

	@Override
	public boolean isTriggeredBy(AbstractAction action) {
		return true;
	}

	@Override
	public List<Fact> getPostConditions() {
		return null;
	}

	@Override
	public List<Fact> getPreConditions() {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
