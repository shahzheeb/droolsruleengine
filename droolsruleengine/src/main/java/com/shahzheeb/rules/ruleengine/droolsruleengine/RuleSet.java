package com.shahzheeb.rules.ruleengine.droolsruleengine;

import org.kie.internal.KnowledgeBase;



/**
 * @author ben.hahn
 *
 * RuleSet
 */
public interface RuleSet {
	
	/**
	 * returns a new instance of the current RuleSet
	 * @return - this RuleSet
	 */
	RuleSet init(KnowledgeBase base, String ruleName);
	
	
	/**
	 * retrieves the name of this rule set
	 * @return - this RuleSet's name
	 */
	String getName();
	
	
	/**
	 * asserts an object for the rule engine
	 * @param obj - the Object to assert
	 */
	void assertObject(Object obj);
	
	/**
	 * fires all the rules defined for this rule engine
	 * @throws RuntimeRuleException - if an error occurred running the rule
	 */
	void runRules() throws RuntimeException;
	
	/**
	 * clears the specified asserted object from rule
	 * @param obj - the object to clear
	 * @throws RuntimeRuleException - if an error occurred clearing the object
	 */
	void retractObject(Object obj) throws RuntimeException;
	
	/**
	 * clears all the asserted objects from the rule
	 * @throws RuntimeRuleException - if an error occurred clearings the objects
	 */
	void clearObjects() throws RuntimeException;
}
