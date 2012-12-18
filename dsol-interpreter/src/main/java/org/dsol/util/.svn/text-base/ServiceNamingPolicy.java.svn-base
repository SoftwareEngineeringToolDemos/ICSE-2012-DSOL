package org.dsol.util;

import javax.jws.WebService;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;

public class ServiceNamingPolicy implements NamingPolicy {

	private String targetNamespace;
	
	public ServiceNamingPolicy(Class serviceInterface){		
		if(serviceInterface.isAnnotationPresent(WebService.class)){
			WebService ws = (WebService) serviceInterface.getAnnotation(WebService.class);
			String targetNamespace = ws.targetNamespace();
			if(targetNamespace != null && !targetNamespace.isEmpty()){
				this.targetNamespace = targetNamespace;
			}
		}
	}
	
	public String getClassName(String prefix, String source, Object key,
			Predicate names) {
		StringBuffer sb = new StringBuffer();
		String interfaceName = prefix;
		if(targetNamespace != null){
			interfaceName = prefix.substring(prefix.lastIndexOf('.') + 1);
			sb.append(targetNamespace);
			sb.append(".");
		}
		sb.append(interfaceName);
		
		
		//sb.append((prefix != null) ? (prefix.startsWith("java") ? "$" + prefix
		//		: prefix) : "net.sf.cglib.empty.Object");
		//sb.append("$$");
		//sb.append(source.substring(source.lastIndexOf('.') + 1));
		//sb.append("ByCGLIB$$");
		//sb.append("X");
		sb.append("DSOL");
		String base = sb.toString();
		String attempt = base;
		int index = 2;
		while (names.evaluate(attempt)) {
			attempt = base + "-" + index++;
		}

		return attempt;
	}
}
