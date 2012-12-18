package org.dsol.service;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public class Hello {

	public String hi(@WebParam(name = "name") String name,
					 @WebParam(name = "surname") String surname) {
		return "Hi, " + name + " " + surname.toUpperCase();
	}
	 
	public String bye(@WebParam(name = "name") String name,
						@WebParam(name = "surname") String surname) {
		return "Bye bye, " + name + " " + surname.toUpperCase();
	}

}
