package org.dsol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.gson.JsonObject;

public class InstanceSession {

	private Map<String, Object> session;
	private Map<String, Object> userSpecificProperties;
	
	public InstanceSession() {
		this(new HashMap<String, Object>());
		
	}
	
	public InstanceSession(Map<String, Object> initialValues) {
		session = new HashMap<String, Object>(initialValues);
		userSpecificProperties = new HashMap<String, Object>();
	}
	
	public Object get(String key) {
		return get(key, false);
}
	
    public Object get(String key, boolean returnNull) {
    		try {
				return PropertyUtils.getNestedProperty(session, key);
			} catch (Exception e) {
				if(returnNull){
					return null;
				}
				throw new RuntimeException("Error retrieving object "+key);
			}
    }
    
    public void putAllUserSpecificProperties(Map<String, Object> values) {
        this.userSpecificProperties = values;
    	session.putAll(values);
    }
    
    public Object put(String key, Object value) {
        return session.put(key,value);
    }
    
    public Object remove(String key) {
        return session.remove(key);
    }

    public InstanceSession copy(){
    	synchronized (session) {
        	return new InstanceSession(session);			
		}
    }
    
    public Map<String, Object> get(){
    	return session;
    }

	public void populate(InstanceSession instanceSessionToPopulate) {
		Iterator<String> keys = session.keySet().iterator();
		
		while(keys.hasNext()){
			String key = keys.next();
			Object value = session.get(key);
			if(value instanceof Cloneable){
				try{
					value = value.getClass().getMethod("clone").invoke(value);
				}
				catch(Exception ex){
					//just ignore... Probably clone is not visible. In such clase, the value will not be cloned
				}
			}
			if(!instanceSessionToPopulate.contains(key)){
				instanceSessionToPopulate.put(key, value);	
			}
		}		
	}

	public JsonObject toJSON() {
		JsonObject jsonObject = new JsonObject();
		Set<String> keys = session.keySet();
		
		for(Iterator<String> it=keys.iterator();it.hasNext();){
			String key = it.next();
			if(!userSpecificProperties.containsKey(key)){// it is not an user pre-defined property
				Object value = session.get(key);
				if(value != null){
					jsonObject.addProperty(key, value.toString());	
				}	
			}			
		}
		return jsonObject;
	}

	public boolean contains(String key) {
		return session.containsKey(key);
	}
    
}
