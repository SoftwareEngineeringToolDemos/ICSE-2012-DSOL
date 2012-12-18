package org.dsol.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.dsol.planner.api.Planner;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

@XmlType(name = "method_info", propOrder = { "name","initial_state","goal"})
@XmlAccessorType(XmlAccessType.FIELD)
public class MethodInfo {

	private Logger logger = Logger.getLogger(MethodInfo.class.getName());
	
	private Method method;
	private String name;
	private String returnType;
	private String returnObject;
	
	
	List<String> parameterTypes;
	List<String> parameterFormalNames;
	List<String> parameterDefinedNames;
	
	private String initialState;
	private String goal;
	
	public MethodInfo(){}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setInitialState(String initialState) {
		if(initialState == null || initialState.trim().isEmpty()){
			logger.info("Additional initial state not defined for method "+this.name+".");
			initialState = Planner.EMPTY_INITIAL_STATE;
		}
		else{
			String prefix = "start(";
			if(!initialState.trim().startsWith(prefix)){
				initialState = prefix + initialState + ")";
			}						
		}

		this.initialState = initialState;
	}

	public void setGoal(String goal) {
		if(goal == null || goal.trim().isEmpty()){
			logger.info("\n\nATTENTION >> Goal not defined for method "+this.name+" declared in the Orchestration Interface. You must specify it in order to allow a plan to be built for this method.\n\n");
			goal = Planner.EMPTY_GOAL;
		}
		String prefix = "goal(";
		if (!goal.trim().startsWith(prefix)) {
			goal = prefix + goal + ")";
		}
		this.goal = goal;
	}

	public MethodInfo(Method method, String initialState, String goal){
		this.method = method;
		this.name = method.getName();
		
		setInitialState(initialState);
		setGoal(goal);
		
		if(!Void.TYPE.equals(method.getReturnType())){
			this.returnType = method.getReturnType().getName();
			if(method.isAnnotationPresent(WebResult.class)){
				this.returnObject = method.getAnnotation(WebResult.class).name();			 
			}
		}

		parameterTypes = new ArrayList<String>();
		parameterFormalNames = new ArrayList<String>();
		parameterDefinedNames = new ArrayList<String>();
		
		for(Class parameterType:method.getParameterTypes()){
			parameterTypes.add(parameterType.getName());
		}
		
		Paranamer paranamer = new CachingParanamer();
		String[] parameterFormalNames = paranamer.lookupParameterNames(method, false);
		if(!Paranamer.EMPTY_NAMES.equals(parameterFormalNames)){
			this.parameterFormalNames = Arrays.asList(parameterFormalNames);
		}
		
		Annotation[][] annotations = method.getParameterAnnotations();
		
		for(Annotation[] paramAnnotations:annotations){
			boolean nameDefined = false;
			for(Annotation annotation:paramAnnotations){
				if (annotation instanceof WebParam) {
					WebParam webParam = (WebParam)annotation;
					parameterDefinedNames.add(webParam.name());
					nameDefined = true;
				}
			}
			if(!nameDefined){
				parameterDefinedNames.add("");
			}
		}

	}
	
	public Method getMethod() {
		return method;
	}
	
	public String getInitialState() {
		return initialState;
	}
	
	public String getGoal() {
		return goal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodInfo other = (MethodInfo) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		return true;
	}

	public JsonElement toJSON() {
		Gson gson = new Gson();
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", name);
		jsonObject.addProperty("return_type", returnType);
		jsonObject.addProperty("return_obj", returnObject);
		if(initialState.equals(Planner.EMPTY_INITIAL_STATE)){
			jsonObject.addProperty("initial_state", "");
		}
		else{
			jsonObject.addProperty("initial_state", initialState.substring(6,initialState.length() -1));
		}
		
		jsonObject.addProperty("goal", goal.substring(5,goal.length() - 1));//remove goal(
		jsonObject.add("parameter_types", gson.toJsonTree(parameterTypes));
		jsonObject.add("parameter_formal_names", gson.toJsonTree(parameterFormalNames));
		jsonObject.add("parameter_defined_names", gson.toJsonTree(parameterDefinedNames));
		
		return jsonObject;
	}

}
