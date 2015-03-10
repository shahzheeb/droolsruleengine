package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.util.HashMap;
import java.util.Map;

import org.kie.internal.KnowledgeBase;
import org.kie.internal.agent.KnowledgeAgent;
import org.kie.internal.agent.KnowledgeAgentConfiguration;
import org.kie.internal.agent.KnowledgeAgentFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a rule set factory for creating RuleSet classes
 * @author ben.hahn
 *
 * RuleSetFactory
 */
public class RuleSetFactory {
	
	private RuleSet ruleSet;
	private RuleFact ruleFact;
	private String ruleKey;
	private String ruleChangeset;
	private KnowledgeAgent agent;

	/*
	 * list of callable RuleSets 
	 */
	private static final Map<String, RuleSetFactory> registry = new HashMap<String, RuleSetFactory>();
	
	private static final Logger logger = LoggerFactory.getLogger(RuleSetFactory.class);
	
	public RuleSetFactory() {
		super();
	}

	/**
	 * @return the ruleSet
	 */
	public String getRuleKey() {
		return ruleKey;
	}

	/**
	 * @param ruleSet the ruleSet to set
	 */
	public void setRuleKey(String ruleKey) {
		this.ruleKey = ruleKey;
	}

	public String getRuleChangeset() {
		return ruleChangeset;
	}

	public void setRuleChangeset(String ruleChangeset) {
		this.ruleChangeset = ruleChangeset;
	}

	
	/**
	 * @return the ruleSet
	 */
	public RuleSet getRuleSet() {
		return ruleSet;
	}

	/**
	 * @param ruleSet the ruleSet to set
	 */
	public void setRuleSet(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}

	/**
	 * @return the ruleFact
	 */
	public RuleFact getRuleFact() {
		return ruleFact;
	}

	/**
	 * @param ruleFact the ruleFact to set
	 */
	public void setRuleFact(RuleFact ruleFact) {
		this.ruleFact = ruleFact;
	}

	
	
	/**
	 * starts the KnowledgeBase agent to scan and build rules
	 * 
	 * @return
	 * @throws ACESESBRuntimeException
	 */
	public synchronized final KnowledgeBase startKnowledgeAgent() {
		
		if (this.agent == null) {
			createKnowledgeAgent();
		}
			
		return this.agent.getKnowledgeBase();
		 
	}
	
	/**
	 * 
	 * @param rule
	 * @param dsl
	 */
	private final void createKnowledgeAgent() {
		
		
		try {
			
			logger.info("Starting Agent and Building Rules for " + this.ruleKey);
			
			KnowledgeAgentConfiguration agentConf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
			agentConf.setProperty("drools.agent.scanDirectories", "false");
				
			KnowledgeAgent agent = KnowledgeAgentFactory.newKnowledgeAgent(this.ruleKey, agentConf);
			//agent.monitorResourceChangeEvents(false);
			//agent.applyChangeSet(ResourceFactory.newUrlResource(this.ruleChangeset));
			//agent.applyChangeSet(ResourceFactory.newFileResource(this.ruleChangeset));
			agent.applyChangeSet(ResourceFactory.newClassPathResource(this.ruleChangeset));
			
			logger.info("Finished Building Rules " + this.ruleKey);
				
			this.agent = agent;
			 
			
		} catch (RuntimeException ree) {
			logger.error("Error Building Rule " + this.ruleKey, ree);
            throw new RulesEngineRuntimeException(ree);
        }
		catch(Exception e) 
		{
			logger.error("Error Building Rule " + this.ruleKey, e);
			throw new RulesEngineRuntimeException("Error building rule: " + this.ruleKey, e);
		}
		
			
		
	}
	
	/**
	 * run the RuleEngine with a expected input and ouput structures
	 * @param input - the input to the rules
	 * @return - true if the rule evaluated to true
	 * @throws RuntimeRuleException - if an error occured while running the rules
	 */
	public static Object evaluateRule(RuleSetFactory ruleSet, Object input) throws RulesEngineRuntimeException {
		return evaluateRule(ruleSet, input, null);
	}
	
	/**
	 * run the RuleEngine with a expected input and ouput structures
	 * @param input - the input to the rules
	 * @param output - the output for the rules
	 * @return - true if the rule evaluated to true
	 * @throws RuntimeRuleException - if an error occured while running the rules
	 */
	public static Object evaluateRule(RuleSetFactory ruleSet, Object input, Object output) 
			throws RulesEngineRuntimeException {
		return evaluateInternal(ruleSet, input, output);
	}
	
	

	/**
	 * run the RuleEngine with a expected input and ouput structures
	 * @param input - the input to the rules
	 * @param output - the output to the rules
	 * @return - true if the rule evaluated to true
	 * @throws RuntimeRuleException - if an error occured while running the rules
	 */
	private static Object evaluateInternal(RuleSetFactory ruleSet, Object input, Object output) 
			throws RulesEngineRuntimeException {
		if (ruleSet.getRuleKey() != null) {
			RuleSet set = ruleSet.getRuleSet().init(ruleSet.startKnowledgeAgent(), ruleSet.getRuleKey());
			
			logger.info("Evaluating Ruleset " + ruleSet.getRuleKey());
			RuleFact ruleFact = null;
			
			if (ruleSet.getRuleFact() != null) {
				if (output == null)
					ruleFact = ruleSet.getRuleFact().newInstance(input);
				else
					ruleFact = ruleSet.getRuleFact().newInstance(input, output);
				
				// assert just the fact
				set.assertObject(ruleFact);
			} else {
				// assert input/output as separate facts
				if (input != null) {
					set.assertObject(input);
				}
				if (output != null) {
					set.assertObject(output);
				}
			}
			
			set.runRules();
			
			logger.info("Finished Evaluating Ruleset " + ruleSet.getRuleKey());
			
			if (ruleFact != null) {
				if (output == null)
					return ruleFact.getInput();
				else
					return ruleFact.getOutput();
			}
			

		}
		if (output == null)
			return input;
		else
			return output;
	}
	
	
	
	
}
