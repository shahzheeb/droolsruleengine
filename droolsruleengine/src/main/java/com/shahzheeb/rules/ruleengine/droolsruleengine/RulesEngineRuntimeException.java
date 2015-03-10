package com.shahzheeb.rules.ruleengine.droolsruleengine;

public class RulesEngineRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5429818663037069930L;

	/**
	 * 
	 */
	public RulesEngineRuntimeException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RulesEngineRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public RulesEngineRuntimeException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public RulesEngineRuntimeException(Throwable arg0) {
		super(arg0);
	}
	
	

}
