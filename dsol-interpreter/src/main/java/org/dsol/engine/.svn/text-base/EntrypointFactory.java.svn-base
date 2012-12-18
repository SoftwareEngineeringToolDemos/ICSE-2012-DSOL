package org.dsol.engine;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;

import org.dsol.WebServiceProxy;
import org.dsol.WebServiceProxy.InnerManagement;
import org.dsol.config.OrchestrationInterfaceInfo;
import org.dsol.management.ManagementCallback;
import org.dsol.util.ServiceNamingPolicy;


public class EntrypointFactory{
	
	private OrchestrationInterfaceInfo orchestrationInterfaceInfo;
	private WebServiceProxy genericWebService;
	
	public EntrypointFactory(OrchestrationInterfaceInfo orchestrationInterfaceInfo, WebServiceProxy genericWebService) {
		this.orchestrationInterfaceInfo = orchestrationInterfaceInfo;
		this.genericWebService = genericWebService;
	}
	
	public Object createInstance(){
		InnerManagement management = genericWebService.new InnerManagement();

		Enhancer e = new Enhancer();
		e.setInterfaces(new Class[] { orchestrationInterfaceInfo.getOrchestrationInterface(), ManagementCallback.class});
		e.setCallbacks(new Callback[] { genericWebService, management });
		e.setNamingPolicy(new ServiceNamingPolicy(orchestrationInterfaceInfo.getOrchestrationInterface()));

		e.setCallbackFilter(new CallbackFilter() {
			@Override
			public int accept(Method method) {
				if (method.getDeclaringClass().equals(ManagementCallback.class)) {
					return 1;
				}
				return 0;
			}
		});
		
		Object bean = e.create();
		return bean;
	}
}
