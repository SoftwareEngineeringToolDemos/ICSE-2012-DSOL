package org.dsol.util;

import java.util.Iterator;
import java.util.Map;

import org.dsol.InstanceSession;

import bsh.EvalError;

public class ExpressionInterpreter {
	
	private bsh.Interpreter beanShellinterpreter;
	
	public ExpressionInterpreter(InstanceSession instanceSession) {
		beanShellinterpreter = new bsh.Interpreter();  // Construct an interpreter
		
		Map<String, Object> variables = instanceSession.get();

		
		Iterator<String> keys = variables.keySet().iterator();
		
		while(keys.hasNext()){
			String key = keys.next();
			
			try {
				beanShellinterpreter.set(key, variables.get(key) );
			} catch (EvalError e) {
				System.out.println("Error adding value of "+key+ " on When Interpreter");
			}
		}
	}
	
	
	public boolean isTrue(String expression){
		// Eval a statement and get the result
		try {
			beanShellinterpreter.eval("result = "+expression);
			return (Boolean)beanShellinterpreter.get("result");
		} catch (EvalError e) {
			System.out.println("Invalid when expression "+expression);
			e.printStackTrace();
		}             
		
		return false;
		
	}
	
	public Object getObject(String expression){
		// Eval a statement and get the result
		try {
			beanShellinterpreter.eval("result = "+expression);
			return beanShellinterpreter.get("result");
		} catch (EvalError e) {
			System.out.println("Invalid when expression "+expression);
			e.printStackTrace();
		}             
		
		return null;
		
	}
	

}
