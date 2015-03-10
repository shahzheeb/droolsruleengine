package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;

/**
 * The Rule Engine Interface
 * @author bhahn
 *
 */
public interface RuleEngine {
	
	/**
	 * run the rule for a single line of data 
	 * @param xml
	 * @return
	 */
	InputValidationFact runRules(String xml);
	
	/**
	 * run the rule for a specific ruleset collection key and data 
	 * @param xml
	 * @return
	 */
	InputValidationFact runRules(XmlObject xml);
	
	/**
	 * run the rule for a specific ruleset collection key and data 
	 * @param xml
	 * @return
	 */
	InputValidationFact runRules(Document xml);
	
	
	
	/**
	 * run the rule for a single line of data and specified rule tasks
	 * @param xml
	 * @param tasks
	 * @return
	 */
	InputValidationFact runRules(String xml, List<RuleTask> tasks);
	
	/**
	 * run the rule for a specific ruleset collection key, data line and specified rule tasks
	 * @param xml
	 * @param tasks
	 * @return
	 */
	InputValidationFact runRules(XmlObject xml, List<RuleTask> tasks);
	
	/**
	 * run the rule for a specific ruleset collection key, data line and specified rule tasks
	 * @param xml
	 * @param tasks
	 * @return
	 */
	InputValidationFact runRules(Document xml, List<RuleTask> tasks);
	
	
}