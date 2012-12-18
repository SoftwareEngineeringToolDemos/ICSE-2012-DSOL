package org.dsol;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.dsol.annotation.Action;
import org.dsol.service.Request;
import org.dsol.service.Response;
import org.dsol.service.ServiceDefinition;
import org.dsol.service.ServiceSelector;

import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;


public class ClassWithServiceAction implements MethodInterceptor {

	private Logger logger = Logger.getLogger(ClassWithServiceAction.class.getName());
	
	private ServiceSelector serviceSelector;
	private Instance instance;
	
	public ClassWithServiceAction(ServiceSelector serviceSelector, Instance instance) {
		this.serviceSelector = serviceSelector;
		this.instance = instance;
	}
	
	public static Object newInstance (	Class clazz, 
										ServiceSelector serviceSelector,
										Instance instance) {
		
		ClassWithServiceAction serviceAction = new ClassWithServiceAction(serviceSelector, instance);
		
		Enhancer e = new Enhancer();
		e.setSuperclass(clazz);	
		e.setCallback(serviceAction);
		Object bean = e.create();
		return bean;
	}
	
	public Object intercept(Object obj, 
							Method method, 
							Object[] args,
							MethodProxy proxy) throws Throwable {
		
		
		if(Modifier.isAbstract(method.getModifiers())){
			Action action = method.getAnnotation(Action.class);
			String service = action.service();
			if(!Action.N_A.equals(service)){
					List<ServiceDefinition> services = serviceSelector.getServiceList(service);
					if(services == null || services.isEmpty()){
						throw new RuntimeException("Service not specified! "+service);
					}
					for(ServiceDefinition serviceDef:services){
						try {

							List<String> urlVars = serviceDef.getVars();
							Map<String,Object> values = new HashMap<String, Object>();
							for(String var:urlVars){
								values.put(var, instance.get(var,true));
							}
							
							Paranamer paranamer = new CachingParanamer();
							String[] parameterNames = paranamer.lookupParameterNames(method, false); // will
																										// return
																										// null
																										// if
																										// not
																										// found
							if(parameterNames != null){
								for (int i = 0; i < parameterNames.length; i++) {
									values.put(parameterNames[i], args[i]);									
								}
							}
							
							serviceDef.setVarsValues(values);
							serviceDef.parseUrl();
							
							Object[] allArgs = serviceDef.getParameters(args);
							
							Request request = new Request(allArgs, method.getReturnType());
							for(String header:serviceDef.getRequestHeaders()){
								Object value = instance.get(header);
								request.addHeader(header, value==null?"":value.toString());
							}
							
							Response response = serviceDef.invoke(request);
							
							for(String header:serviceDef.getResponseHeaders()){
								instance.put(header, response.getHeader(header));
							}
							
							Object returnValue = response.getReturnValue();
							
							//If the method expects a list, but the service returns an array (maybe cxf problem), convert to list
							if (List.class.isAssignableFrom(method.getReturnType()) && 
									returnValue.getClass().isArray()) {
								returnValue = Arrays.asList((Object[]) returnValue);
								response.setReturnValue(returnValue);
							}
							
							return returnValue;
						} catch (Exception e) {
							logger.info("\n=======================\n\nService unavailable: \n" +serviceDef.toString()+"\n=======================\n");
							e.printStackTrace();
						}
					}
					
					throw new RuntimeException("All services defined for " + service + " are faulty!");
			}
			else{
				throw new RuntimeException("In an abstract method, the service must be informed!");
			}
		}
		return proxy.invokeSuper(obj, args);
	}	
}
