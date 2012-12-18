package org.dsol.service;

import org.dsol.service.config.Service;

public enum ServiceType {
	SOAP {
		@Override
		public String toString() {
			return "soap";
		}
	},REST {
		@Override
		public String toString() {
			return "rest";
		}
	};

	public static ServiceType fromString(String type) {
		if (type.equalsIgnoreCase(REST.toString()) || "http".equalsIgnoreCase(type)) {
			return REST;
		}
		if (type.equalsIgnoreCase(SOAP.toString())) {
			return SOAP;
		}
		return null;
	}
	
	public abstract String toString();

	public static ServiceDefinition createServiceDefinition(Service service){
		switch (ServiceType.fromString(service.getType())) {
		case SOAP:
			return new SOAPService(service);
		case REST:	
			return new RESTService(service);
		default:
			return null;
		}
	}
	
}
