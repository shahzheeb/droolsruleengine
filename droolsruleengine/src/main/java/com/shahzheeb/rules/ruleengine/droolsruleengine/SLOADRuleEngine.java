package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.util.Iterator;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


/**
 * container for RuleSets to evaluate 
 * @author bhahn
 *
 */
public class SLOADRuleEngine implements RuleEngine {

	private List<RuleSetFactory> ruleSets;
	private List<RuleTask> ruleTasks;

	private static final Logger logger = LoggerFactory.getLogger(SLOADRuleEngine.class);
		
	public SLOADRuleEngine() {
		super();
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
	 * @return the ruleTasks
	 */
	public List<RuleTask> getRuleTasks() {
		return ruleTasks;
	}



	/**
	 * @param ruleTasks the ruleTasks to set
	 */
	public void setRuleTasks(List<RuleTask> ruleTasks) {
		this.ruleTasks = ruleTasks;
	}



	@Override
	public InputValidationFact runRules(String xml) {
		return runRules(xml, null);
	}
	
	@Override
	public InputValidationFact runRules(XmlObject xml) {
		if (xml != null)
			return runRules(xml.xmlText());
		else
			return null;
	}
	
	@Override
	public InputValidationFact runRules(Document xml) {
		return runRules(new InputValidationFact(xml), null);
	}

	
	@Override
	public InputValidationFact runRules(String xml, List<RuleTask> tasks) {
		return runRules(new InputValidationFact(xml), tasks);
	}
	
	@Override
	public InputValidationFact runRules(XmlObject xml, List<RuleTask> tasks) {
		if (xml != null) {
			return runRules(xml.xmlText(), tasks);
		} else {
			return null;
		}
	}
	
	@Override
	public InputValidationFact runRules(Document xml, List<RuleTask> tasks) {
		return runRules(new InputValidationFact(xml), tasks);
	}

	/**
	 * run the actual rules
	 * @param fact
	 * @param tasks
	 * @return
	 */
	private InputValidationFact runRules(InputValidationFact fact, List<RuleTask> tasks) {
		
		InputValidationFact input = fact;
		
		
		if (this.ruleSets != null && this.ruleSets.size() > 0) {
			
			// run pre-run tasks
			if (this.ruleTasks != null && this.ruleTasks.size() > 0) {
				for (Iterator<RuleTask> iterator = this.ruleTasks.iterator(); iterator
						.hasNext();) {
					RuleTask task = (RuleTask) iterator.next();
					task.preRunTask(fact);
				}
			}
			
			// run pre-run tasks
			if (tasks != null && tasks.size() > 0) {
				for (Iterator<RuleTask> iterator = tasks.iterator(); iterator
						.hasNext();) {
					RuleTask task = (RuleTask) iterator.next();
					task.preRunTask(fact);
				}
			}
			
			for (Iterator<RuleSetFactory> iterator = this.ruleSets.iterator(); iterator.hasNext();) {
				RuleSetFactory ruleSetFactory = iterator.next();
				try {
					input = (InputValidationFact) RuleSetFactory.evaluateRule(ruleSetFactory, input);
					
				} catch (RuntimeException e) {
					logger.error("Error Evaluating Rule " + ruleSetFactory.getRuleKey(), e);
					throw e;
				}
			}
			
			// run post-run tasks
			if (this.ruleTasks != null && this.ruleTasks.size() > 0) {
				for (Iterator<RuleTask> iterator = this.ruleTasks.iterator(); iterator
						.hasNext();) {
					RuleTask task = (RuleTask) iterator.next();
					task.postRunTask(fact);
				}
			}
			
			// run post-run tasks
			if (tasks != null && tasks.size() > 0) {
				for (Iterator<RuleTask> iterator = tasks.iterator(); iterator
						.hasNext();) {
					RuleTask task = (RuleTask) iterator.next();
					task.postRunTask(fact);
				}
			}
			
		}
		
		return input;
	}
	
	

}
