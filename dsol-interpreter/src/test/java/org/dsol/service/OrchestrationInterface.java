package org.dsol.service;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.dsol.annotation.DestroyInstance;
import org.dsol.annotation.Instance;
import org.dsol.annotation.StoreInstance;

@WebService
public interface OrchestrationInterface {
	
	
	@WebResult(name="message")
	@StoreInstance(id={"clientName"})
	public String helloWorld(@WebParam(name="name") String clientName,
							 @WebParam(name="last_name") String clientLastName);

	
	@WebResult(name="translated_message")
	@Instance(id={"clientName"})
	@DestroyInstance
	public String translate(@WebParam(name="name") String clientName);
	
}
