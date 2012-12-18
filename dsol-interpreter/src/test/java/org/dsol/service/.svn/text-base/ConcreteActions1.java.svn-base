package org.dsol.service;

import org.dsol.annotation.Action;
import org.dsol.annotation.ObjectName;
import org.dsol.annotation.ReturnValue;

public abstract class ConcreteActions1 {
	
	@Action(name="hello",service = "hi")
	@ReturnValue("message")
	public abstract String hi(String name, String surname);
	
	@Action(name="hi",service = "compensate_hi", compensation = true)
	public abstract String bye(String name, String surname);

	@Action
	public void getStarted(){
		System.out.println("********* get started executed **********");
	}
	
	@Action(name="getStarted",compensation=true)
	@ReturnValue("clientLastName")
	public String compensate_getStarted(@ObjectName("clientLastName") String clientLastName){
		System.out.println("********* compensating get started **********");
		return clientLastName + "[c]";
	}
}
