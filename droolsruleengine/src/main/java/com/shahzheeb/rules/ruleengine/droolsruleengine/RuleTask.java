package com.shahzheeb.rules.ruleengine.droolsruleengine;

/**
 * rule pre and post initialization tasks
 * @author bhahn
 *
 */
public interface RuleTask {
	
	/**
	 * the pre initialization task to run
	 * @param fact
	 */
	void preRunTask(Object fact);
	
	/**
	 * the post initialization task to run
	 * @param fact
	 */
	void postRunTask(Object fact);
	

}
