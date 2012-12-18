package org.dsol;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.dsol.annotation.ObjectName;
import org.dsol.annotation.QoSProfile;
import org.dsol.annotation.ReturnValue;
import org.dsol.annotation.When;
import org.dsol.util.ExpressionInterpreter;

import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;



public class ConcreteAction {
	
	private Logger logger = Logger.getLogger(ConcreteAction.class.getName());
	
	private final Method method;
	private final Object target;
	
	private double reliability = 1.0;
	private double responseTime = 0;
	
	
	public ConcreteAction(Method method, Object target) {
		super();
		this.method = method;
		if(method.isAnnotationPresent(QoSProfile.class)){
			QoSProfile qosProfile = method.getAnnotation(QoSProfile.class);
			String[] metrics = qosProfile.metrics();
			double[] values = qosProfile.values();
			for (int i = 0; i < metrics.length; i++) {
				if(metrics[i].equals("reliability")){
					reliability = values[i];
				}
				else if (metrics[i].equals("response_time")){
					responseTime = values[i];
				}
			}
		}
		this.target = target;
	}
	
	public boolean holdWhenExpression(InstanceSession instanceSession, List<String> params) {
		if (method.isAnnotationPresent(When.class)) {
			
			List<String> addedkeys = new ArrayList<String>(0);
			try {
				if(params != null){
					addedkeys = addParamValuesToInstanceSession(instanceSession, params);					
				}
				When when = method.getAnnotation(When.class);
				String whenExpression = when.value();
				ExpressionInterpreter expressionInterpreter = new ExpressionInterpreter(instanceSession);
				if (!expressionInterpreter.isTrue(whenExpression)) {
					return false;
				}
			} finally{
				for(String key:addedkeys){
					instanceSession.remove(key);//remove parameters
				}
			}				
		}
		return true;
	}
	
	private List<String> addParamValuesToInstanceSession(InstanceSession instanceSession, List<String> params){
		Object[] paramValues = getParamValues(instanceSession, params);
		return addParamValuesToInstanceSession(instanceSession, params, paramValues);
	}
	
	private List<String> addParamValuesToInstanceSession(InstanceSession instanceSession, List<String> params, Object[] paramValues){
		
		Paranamer paranamer = new CachingParanamer();
		String[] parameterFormalNames = paranamer.lookupParameterNames(method, false);
		List<String> addedkeys = new ArrayList<String>();
		
		if(!parameterFormalNames.equals(Paranamer.EMPTY_NAMES)){
			for(int i = 0; i < parameterFormalNames.length;i++){
				if(!instanceSession.contains(parameterFormalNames[i])){
					instanceSession.put(parameterFormalNames[i], paramValues[i]);
					addedkeys.add(parameterFormalNames[i]);
				}
			}
		}
		return addedkeys;
	}
	
	public boolean execute(Instance instance, List<String> params) throws Throwable{
		
		List<String> addedkeys = new ArrayList<String>(0);
		InstanceSession instanceSession = instance.getInstanceSession();
		
		try {
			
			addedkeys = addParamValuesToInstanceSession(instanceSession, params);
			
			Object[] paramValues = getParamValues(instanceSession, params);
		
			Object returnValue =  method.invoke(target, paramValues);
			if(returnValue != null){
				returnValue = addReturnValueToInstanceSession(instanceSession, method, returnValue);			
			}

		} catch (Exception e) {
			Throwable cause = null;
			if (e instanceof InvocationTargetException) {
				cause = e.getCause();
			}
			else{
				cause = e;
			}
			cause.printStackTrace();
			if(instance.isThrownException(cause.getClass())){
				throw cause;
			}
			
			return false;
		}
		finally{
			for(String key:addedkeys){
				instanceSession.remove(key);//remove parameters
			}
		}
		return true;
	}
	
	protected List<String> getParametersObjectsNames(List<String> templateParamList){
		if(templateParamList == null){
			templateParamList = new ArrayList<String>();
		}

		Annotation[][] parametersMetadata = method.getParameterAnnotations();
		List<String> objectsNames = new ArrayList<String>(method.getParameterTypes().length);
		
		int indexTemplate = 0;
		for (int i = 0; i < parametersMetadata.length; i++) {
			Annotation[] metaData = parametersMetadata[i];
			String objectName = getParameterObjectName(metaData);
			if(objectName == null){//does not define an object name. get from templateParamList
				objectsNames.add(templateParamList.get(indexTemplate));
				indexTemplate++;
			}
			else{
				objectsNames.add(objectName);
			}
			
		}
		return objectsNames;
	}
	
	private Object[] getParamValues(InstanceSession instanceSession, 
									List<String> params){
		
		params = getParametersObjectsNames(params);
		//If the list does not contains any parameters, return an empty array
		if(params.isEmpty()){
			return new Object[0];
		}

		Object[] values = new Object[params.size()];
		for (int i = 0; i < values.length; i++) {
			
			String instanceSessionObjectKey = params.get(i);
			Class paramType = method.getParameterTypes()[i];
			
			values[i] = instanceSession.get(instanceSessionObjectKey);
			if(values[i] == null){
				//TODO: REMOVE THIS!!! BAD SMELL
				values[i] = tryToExtractObjectFromParamName(instanceSessionObjectKey, paramType);
			}
			//If the object in the Instance Session is not compatible with the parameter, try to
			//convert it using a suitable constructor
			else if (!paramType.isAssignableFrom(values[i].getClass())){
				values[i] = convertObject(paramType, values[i].getClass(), values[i]);
			}
		}
		return values;
	}
	
	private Object tryToExtractObjectFromParamName(String paramName, Class paramType){
		return convertObject(paramType, String.class, paramName);
	}
	
	private Object convertObject(Class paramType, Class objectType, Object object){
		try {
			return paramType.getConstructor(objectType).newInstance(object);
		} catch (Exception e) {
			//If the constructor throws an exception, the value will remain null
			return null;
		} 
	}
	
	public String getMethodNameIncludingDeclaringClass() {
		return method.getDeclaringClass().getName()+ "." +method.getName();
	}
	
	@Override
	public String toString() {
		return getMethodNameIncludingDeclaringClass();
	}
	
	private static Object addReturnValueToInstanceSession(InstanceSession instanceSession, Method method, Object returnValue){
		if (returnValue instanceof Map) {
			Map<String, Object> returnValueAsMap = (Map) returnValue;
			Iterator<String> keys = returnValueAsMap.keySet().iterator();

			while (keys.hasNext()) {
				String key = keys.next();
				Object value = returnValueAsMap.get(key);
				instanceSession.put(key, value);
			}
		} else {
			String returnValueKey[] = getReturnValueKey(method, returnValue);
			if (returnValueKey != null) {
				if (returnValue.getClass().isArray()
						&& (Array.getLength(returnValue) == returnValueKey.length)) {

					Object[] returnValues = (Object[]) returnValue;
					for (int i = 0; i < returnValues.length; i++) {
						instanceSession.put(returnValueKey[i], returnValues[i]);
					}
				} else {
					instanceSession.put(returnValueKey[0], returnValue);
				}

			}
		}
		return returnValue;
	}

	private static String[] getReturnValueKey(Method method, Object returnValue) {
		String returnValueKey[] = null;
		if (method.isAnnotationPresent(ReturnValue.class)) {
			// The key used is based on the value of the annotation @ReturnValue
			ReturnValue returnValueAnnotation = method
					.getAnnotation(ReturnValue.class);
			returnValueKey = returnValueAnnotation.value();
		}

		return returnValueKey;
	}

	/**
	 * This methos is used to get parameter names description when the parameters
	 * are annotated with @ObjectName. This is usually used for compensation actions 
	 * @return
	 */
	public List<String> getParameterNames(){
		Annotation[][] parametersMetadata = method.getParameterAnnotations();
		List<String> objectsNames = new ArrayList<String>(parametersMetadata.length);
		
		for (int i = 0; i < parametersMetadata.length; i++) {
			Annotation[] metaData = parametersMetadata[i];
			String objectName = getParameterObjectName(metaData);
			if(objectName == null){
				logger.info("Objects Names could not be retrieved for all parameters!");
				return null;
			}
			objectsNames.add(objectName);
		}
		
		return objectsNames;
	}
	
	private String getParameterObjectName(Annotation[] metaData){
		for (int j = 0; j < metaData.length; j++) {
			if(metaData[j] instanceof ObjectName){
				return ((ObjectName)metaData[j]).value();
			}
		}	
		return null;
	}

	public double getReliability(){
		return reliability;
	}
	
	public int getResponseTime() {
		return (int)responseTime;
	}
	
}
