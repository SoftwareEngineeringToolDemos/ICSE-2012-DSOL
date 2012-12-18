package org.dsol.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.dsol.planner.api.Planner;
import org.dsol.service.ServiceSelector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration("dsolConfig")
public class DSOLConfig implements ApplicationContextAware{

	private static final String PROPERTY_Service_Port = "Service_Port";
	private static final String PROPERTY_Service_Context = "Service_Context";
	private static final String PROPERTY_Orchestration_Interface = "Orchestration_Interface";
	private static final String PROPERTY_Classes = "Classes";
	private static final String PROPERTY_Classpath_Folder = "Classpath_Folder";
	private static final String PROPERTY_Abstract_Actions = "Abstract_Actions";
	private static final String PROPERTY_Services = "Services";
	
	Logger logger = Logger.getLogger(DSOLConfig.class.getName());
	private String DEFAULT_PORT = "8088";
	private String DEFAULT_CONTEXT = "dsol-service";
	private ApplicationContext applicationContext;
	private Map<String, Object> userSpecificProperties = null;
	
	
	/**
	 * All the classes in which the engine should look for Actions (methods
	 * annotated with @Action)
	 */
	private List<String> concreteActionClasses;

	private String classpathFolder;
	
	private Class<?> orchestrationInterface;
	
	private String abstractActionsFile;
	private String composedServicesFile;
	
	private Integer port;
	
	private String context;
	
	private Properties properties;
	
	public boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}
	
	public String getAbstractActionsFile() {
		return abstractActionsFile;
	}

	public Object getBean(String bean) {
		return applicationContext.getBean(bean);
	}

	public String getClasspathFolder() {
		return classpathFolder;
	}

	public String getComposedServicesFile() {
		return composedServicesFile;
	}

	public List<String> getConcreteActionClasses() {
		return concreteActionClasses;
	}
	
	public String getContext() {
		return context;
	}
	
	public Class getOrchestrationInterface() {
		return orchestrationInterface;
	}
	
	public Integer getPort() {
		return port;
	}

	public String getProperty(String key){
		return properties.getProperty(key, "");
	}

	public String getProperty(String key, String defaultValue){
		return properties.getProperty(key, defaultValue);
	}

	public Map<String, Object> getUserSpecificProperties() {
		if(userSpecificProperties == null){
			userSpecificProperties = new HashMap<String, Object>();
			Set<String> keys = properties.stringPropertyNames();
			
			for(Iterator<String> it=keys.iterator();it.hasNext();){
				String key = it.next();
				if(!isInternalProperty(key)){
					userSpecificProperties.put(key, properties.getProperty(key));
				}
			}
		}
		return userSpecificProperties;
	}

	private boolean isInternalProperty(String key) {
		return key.contains("initial_state") ||
			   key.contains("goal") ||
			   key.equals(PROPERTY_Classes) ||
			   key.equals(PROPERTY_Classpath_Folder) ||
			   key.equals(PROPERTY_Orchestration_Interface) ||
			   key.equals(PROPERTY_Service_Context) ||
			   key.equals(PROPERTY_Service_Port);
	}

	public void setAbstractActionsFile(String abstractActionsFile) {
		this.abstractActionsFile = abstractActionsFile.trim();
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;		
	}
	
	private void setClasses(String classes){
		if(classes == null || classes.isEmpty()){
			concreteActionClasses = new ArrayList<String>();
		}
		else{
			concreteActionClasses = Arrays.asList(classes.split(","));
			for(int i = 0;i<concreteActionClasses.size();i++){
				concreteActionClasses.set(i, concreteActionClasses.get(i).trim());
			}
		}
	}
	
	private void setClasspathFolder(String classpathFolder){
		this.classpathFolder = classpathFolder.trim();
	}
	
	public void setComposedServicesFile(String composedServicesFile) {
		this.composedServicesFile = composedServicesFile;
	}

	private void setContext(String context) {
		this.context = context.trim();
	}
	
	private void setOrchestrationInterface(String orchestrationInterface){
		try {
			this.orchestrationInterface = Class.forName(orchestrationInterface.trim());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(orchestrationInterface+ " not found!");
		}
	}

	private void setPort(Integer port) {
		this.port = port;
	}

	@Value("#{dsolProperties}")
	public void setProperties(Properties properties){
		logger.info("\n\nReading properties file.\n\n"+properties.toString() + "\n\n");
		
		String orchestrationInterface = properties.getProperty(PROPERTY_Orchestration_Interface, "");
		if(!orchestrationInterface.trim().isEmpty()){
			setOrchestrationInterface(orchestrationInterface);
		}
		else{
			logger.severe("Property Orchestration_Interface not specified.");
			throw new RuntimeException("Orchestration Interface not specified!");
		}

		setAbstractActionsFile(properties.getProperty(PROPERTY_Abstract_Actions, Planner.DEFAULT_ACTIONS));
		logger.info("Abstract Actions file: "+abstractActionsFile);
		
		setComposedServicesFile(properties.getProperty(PROPERTY_Services, ServiceSelector.DEFAULT_SERVICES));
		logger.info("Composed services file: "+composedServicesFile);

		
		String classes = properties.getProperty(PROPERTY_Classes, "");
		if(!classes.isEmpty()){
			setClasses(classes);
		}
		else{
			logger.info("Property Classes not specified.");
		}
		
		String classpathFolder = properties.getProperty(PROPERTY_Classpath_Folder, "");
		setClasspathFolder(classpathFolder);
		if(classpathFolder.isEmpty()){
			logger.info("Property Classpath_Folder not specified.");
		}
		
		String port = properties.getProperty(PROPERTY_Service_Port, "");
		if(port.isEmpty()){
			logger.info("Property Service_Port not specified. Using default:"+DEFAULT_PORT);
			port = DEFAULT_PORT;
		}
		setPort(new Integer(port));
		
		String context = properties.getProperty(PROPERTY_Service_Context, "");
		if(context.isEmpty()){
			logger.info("Property Service_Context not specified. Using default: "+DEFAULT_CONTEXT);
			context = DEFAULT_CONTEXT;
		}
		setContext(context);
		
		this.properties = properties;
		
	}
}
