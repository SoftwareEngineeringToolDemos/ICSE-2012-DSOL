package org.dsol.util;

import org.dsol.InstanceSession;
import org.junit.Assert;
import org.junit.Test;

public class ExprInterpreterTest {
    
	@Test
	public void isTrue(){
		String expString = "PaymentMethod.equals(\"BankTransfer\")";
		String expString2 = "PaymentMethod.equals(\"CreditCard\")";
		
		InstanceSession instanceSession = new InstanceSession();
		instanceSession.put("PaymentMethod", "BankTransfer");
		
		ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter(instanceSession);
		Assert.assertTrue(expressionInterpreter.isTrue(expString));
		Assert.assertFalse(expressionInterpreter.isTrue(expString2));
	}
	
	@Test
	public void isTrue2(){
		String expString = "PaymentMethod.equals(\"CreditCard\")";
		String expString2 = "PaymentMethod.equals(\"BankTransfer\")";
		
		InstanceSession instanceSession = new InstanceSession();
		instanceSession.put("PaymentMethod", "CreditCard");
		
		ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter(instanceSession);
		
		
		Assert.assertTrue(expressionInterpreter.isTrue(expString));
		Assert.assertFalse(expressionInterpreter.isTrue(expString2));
	}
	
	@Test
	public void getObject(){
		String expString = "TransportationValue + AccommodationValue";
		
		InstanceSession instanceSession = new InstanceSession();
		
		instanceSession.put("TransportationValue", 1000d);
		instanceSession.put("AccommodationValue", 1000d);
		
		
		ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter(instanceSession);
		
		Assert.assertEquals(2000d,(Double)expressionInterpreter.getObject(expString),0.1);
	}
	
}
