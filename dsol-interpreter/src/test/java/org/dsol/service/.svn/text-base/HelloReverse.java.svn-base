package org.dsol.service;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public class HelloReverse {

	public String hi(	@WebParam(name = "name") String name,
						@WebParam(name = "surname") String surname) {
		return "Hi, " + surname.toUpperCase() + " " +name;
	}

}
