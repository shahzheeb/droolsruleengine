package com.shahzheeb.rules.ruleengine.droolsruleengine;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;



/**
 * @author ben.hahn
 *
 * ApplicationContextLoader - This is the Spring context loader which will either be through a servlet context
 * listener if on a web app, deployer or manually set 
 */
public class ApplicationContextLoader implements ApplicationContextAware {
	
	private static final String APPLICATION_CONTEXT_XML = "classpath:rules-container.xml";
	private static final String PARENT_CONTEXT_LOCATOR = "classpath*:parent-ref.xml";
	private static final String PARENT_CONTEXT_KEY = "SLOADGlobal";

	
	private static final Logger log = LoggerFactory.getLogger(ApplicationContextLoader.class);
	private static ApplicationContext context = null;
	private static int errorTries;
	
	
	/**
	 * returns the current application context
	 * @return - the current application context
	 */
	public static ApplicationContext getApplicationContext() {
		return getApplicationContext(null);
	}
	
	/**
	 * returns the current application context
	 * @param contextResource
	 * @return - the current application context
	 */
	public static ApplicationContext getApplicationContext(String contextResource) {
		
		if (context == null) {
			synchronized (ApplicationContextLoader.class) {
				if (context == null && errorTries < 5) {
					try {
						createContextFromProperties(contextResource);
					} catch (ContextInitializationException ce) {
						if (errorTries >= 5){
							log.error("Tried " + errorTries + " times to get an application context. Not retrying", ce);
						}else{
							errorTries++;
						}
					}
				}
			} 
		}
		return context;
	}
	
	/**
	 * creates application context manually from XML config
	 */
	protected static void createContextFromProperties(String contextResource) throws ContextInitializationException {
		
		String contextLocation = APPLICATION_CONTEXT_XML;
		if (StringUtils.isNotBlank(contextResource)){
			contextLocation = contextResource;
		}
		
		context = createContext(contextLocation);
		
	}
	
	/**
	 * creates application context manually from XML config
	 */
	public static ApplicationContext createContext(String contextResource) throws ContextInitializationException {
		
		GenericApplicationContext ctx = null;
		
		if (StringUtils.isNotBlank(contextResource)){
			String contextLocation = contextResource;
		
			try {
				Resource source = getSpringResource(contextLocation);
				if (source != null) {
					log.debug("Got Application Context Resource from: " + contextLocation);
					
					ctx = new GenericApplicationContext(); 
					//new ClassPathXmlApplicationContext(new String[] {contextLocation}, parent);
					
					XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ctx);
					reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
					reader.loadBeanDefinitions(source);
					ctx.refresh();
									}
			} catch (Exception e){
				e.printStackTrace();
				log.info(e.getMessage());
				log.error("Could not create Application Context.The error was " + e.getMessage(), e);
				throw new ContextInitializationException(contextLocation, e);
			}
			
		}
		
		return ctx;
		
	}
	
	/**
	 * this attempts to create or retrieve a parent context based on a predefined key and locator Xml
	 * @return - the parent context or null if the BeanFactoryLocator throws an error  
	 */
	protected static ApplicationContext createParentContext() {
		
		String resourceKey = PARENT_CONTEXT_LOCATOR;
		String parentKey = PARENT_CONTEXT_KEY;
		ApplicationContext parent = null;
		
		if (parentKey != null) {
			try {
				// get the parent context first to conform to standard context methodology
				BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(resourceKey);
				BeanFactoryReference ref = locator.useBeanFactory(parentKey);
				parent = (ApplicationContext) ref.getFactory();
			} catch (BeansException be) {
				log.warn("No available or undefined parent context", be);
			}
		}
		
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		context = ctx;
	}
	
	public Object getBean(String name) {
		Object bean = null;
		if (name != null && context != null) {
			bean = context.getBean(name);
		}
		return bean;
	}
	
   /**
    * Returns a Spring Resource for the given resource specified as a resource string
    * The resource string can be any valid Spring URL resource including wildcards.
    * @param url - the resuorce URL
    * @return - InputStream for the resource
    */
   public static Resource getSpringResource(String url) {

   	Resource is = null;
       
       if (url != null) {
       	
       	try {
	
       		ResourceLoader loader = new PathMatchingResourcePatternResolver();
	            is = loader.getResource(url);
	
	        } catch (Exception e) {
	
	            log.error("Exception encountered opening resource", e);
	            throw new RuntimeException("Exception encountered opening resource", e);
	
	        }
	        
       }

       return is;

   }

}
