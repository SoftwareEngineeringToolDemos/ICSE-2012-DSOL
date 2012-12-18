package org.dsol.service;

import org.dsol.annotation.Action;
import org.dsol.annotation.ReturnValue;

public class ConcreteActions2 {
		
	@Action(name="hello")
	@ReturnValue("message")
	public String hi(String name, String surname){
		if(name.equals("Pedro")){
			throw new RuntimeException("I don't like Pedro! Not going to say hi to him!");
		}
		return "Generic, "+name+" "+surname;
	}
	
	@Action(name="hello2")
	@ReturnValue("message")
	public String hi2(String surname){
		return "Generic2, "+surname;
	}
	
	@Action("translate_message")
	@ReturnValue("translated_message")
	public String translate_message_method(String message){
		return message + " [translated]";
	}
	
	//IMPORTANT FOR TESTING
	@Action(compensation=true)
	public void compensation_method(){}
	
	@Action(name="org.dsol.service.ConcreteActions2.translate_message_method",compensation=true)
	public void compensation_method_2(){}

}
