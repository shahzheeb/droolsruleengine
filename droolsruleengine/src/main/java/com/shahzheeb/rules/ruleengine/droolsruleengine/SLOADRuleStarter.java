package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.util.Iterator;
import java.util.List;

import org.kie.internal.io.ResourceChangeScannerConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLOADRuleStarter implements RuleStarter{
	
	// default is 1 hour
	private int scanInterval = 3600;  
	private List<RuleSetFactory> ruleSets;
	
	private static final Logger logger = LoggerFactory.getLogger(SLOADRuleStarter.class);
	
	/**
	 * @return the scanInterval
	 */
	public int getScanInterval() {
		return scanInterval;
	}

	/**
	 * @param scanInterval the scanInterval to set
	 */
	public void setScanInterval(int scanInterval) {
		this.scanInterval = scanInterval;
	}

	/**
	 * @return the ruleSets
	 */
	public List<RuleSetFactory> getRuleSets() {
		return ruleSets;
	}

	/**
	 * @param ruleSets the ruleSets to set
	 */
	public void setRuleSets(List<RuleSetFactory> ruleSets) {
		this.ruleSets = ruleSets;
	}
	
	/**
	 * 
	 * @param start
	 */
	public void startKnowledgeAgent() {
		
		if (this.ruleSets != null && this.ruleSets.size() > 0) {
			for (Iterator<RuleSetFactory> iterator = this.ruleSets.iterator(); iterator.hasNext();) {
				
				startKnowledgeAgent(iterator.next());
				
			}
		}
		
		
		// start the notification service
		ResourceFactory.getResourceChangeNotifierService().start();
			
		if (this.scanInterval > 0) {
			// start the scanner service
			ResourceChangeScannerConfiguration scannerInterval = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
			scannerInterval.setProperty( "drools.resource.scanner.interval", Integer.toString(this.scanInterval) );
			ResourceFactory.getResourceChangeScannerService().configure(scannerInterval);
		}
		
		ResourceFactory.getResourceChangeScannerService().start();
		
	}
	
	
	
	/**
	 * 
	 * @param ruleSet
	 */
	private void startKnowledgeAgent(RuleSetFactory ruleSet) {
		
		try {
			
			ruleSet.startKnowledgeAgent();
			
		} catch (RuntimeException e) {
			logger.error("Error Updating ruleset " + ruleSet.getRuleKey() + " with newer ruleset", e);
		}
		
	}
}
