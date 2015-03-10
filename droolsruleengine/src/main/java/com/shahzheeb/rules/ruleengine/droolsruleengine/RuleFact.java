package com.shahzheeb.rules.ruleengine.droolsruleengine;

/**
 * 
 * @author Ben Hahn
 *
 * RuleFact - assertable object with input and output 
 * parameters
 */
public interface RuleFact<T> {

	/**
	 * should create a new instance for non thread-safe impl
	 * @param input
	 * @return
	 */
	RuleFact<T> newInstance(T input);
	
	/**
	 * 
	 * @param input
	 * @param output
	 * @return
	 */
	RuleFact<T> newInstance(T input, T output);
	
	/**
	 * gets the input object
	 * @return - the input Object
	 */
	T getInput();
	
	/**
	 * sets the input Object
	 * @param obj - the input Object to set
	 */
	void setInput(T obj);
	
	/**
	 * gets the output Object
	 * @return - the output XmlObject
	 */
	T getOutput();
	
	/**
	 * sets the output Object
	 * @param obj - the outpuyt Object to set
	 */
	void setOutput(T obj);
}

