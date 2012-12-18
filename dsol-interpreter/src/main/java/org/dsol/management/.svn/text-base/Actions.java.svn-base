package org.dsol.management;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.dsol.planner.api.GenericAbstractAction;

@XmlType(name = "actions", propOrder = { "actions","concrete_action_classes" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Actions {
	
	@XmlElement(name="actions")
	private List<GenericAbstractAction> actions;

	private List<String> concrete_action_classes;

	public List<GenericAbstractAction> getActions() {
		return actions;
	}
	
	public void setActions(List<GenericAbstractAction> actions) {
		this.actions = actions;
	}
	
	public List<String> getConcrete_action_classes() {
		return concrete_action_classes;
	}
	
	public void setConcrete_action_classes(List<String> concrete_action_classes) {
		this.concrete_action_classes = concrete_action_classes;
	}
}
