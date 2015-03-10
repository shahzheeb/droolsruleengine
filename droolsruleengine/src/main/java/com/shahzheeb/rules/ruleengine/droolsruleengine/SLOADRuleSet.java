package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;


/**
 * Not thread-safe implementation class for firing drools rule base  
 * 
 * @author ben.hahn
 *
 * SLOADRuleSet
 */
public class SLOADRuleSet implements RuleSet {
	
	private Set<AssertObjHolder> assertableObjs;
	private StatefulKnowledgeSession ruleMemory;
	private String ruleName;

	/**
	 * default CTOR 
	 */
	public SLOADRuleSet() {
	}
	
	public SLOADRuleSet(KnowledgeBase rb, String ruleName) {
		this.ruleMemory = rb.newStatefulKnowledgeSession();
		this.ruleName = ruleName;
		this.assertableObjs = new HashSet<AssertObjHolder>();
	}
	
	/**
	 * initialize a new RuleSet 
	 * @param rb
	 * @param ruleName
	 */
	public RuleSet init(KnowledgeBase rb, String ruleName) {
		return new SLOADRuleSet(rb, ruleName);
	}
	
	
	
	/**
	 * retrieves the name for this rule set
	 * @return - the RuleSet's name
	 */
	public String getName() {
		return this.ruleName;
	}
 
	public void assertObject(Object obj) {
		if (obj != null)
			assertableObjs.add(new AssertObjHolder(obj));

	}
	
	public void clearObjects() throws RulesEngineRuntimeException {
		try {
			Iterator<AssertObjHolder> iter = assertableObjs.iterator();
			while (iter.hasNext()) {
				AssertObjHolder holder = iter.next();
				if (holder.handle != null) {
					ruleMemory.retract(holder.handle);
				}
					
			}
			assertableObjs.clear();
		} catch (Exception e) {
			throw new RulesEngineRuntimeException(e);
		}
		
	}

	public void retractObject(Object obj) throws RulesEngineRuntimeException {
		try {
			Iterator iter = assertableObjs.iterator();
			while (iter.hasNext()) {
				AssertObjHolder holder = (AssertObjHolder) iter.next();
				if (holder.equals(obj) && holder.handle != null) {
					ruleMemory.retract(holder.handle);
					break;
				}
					
			}
			assertableObjs.remove(new AssertObjHolder(obj));
		} catch (Exception e) {
			throw new RulesEngineRuntimeException(e);
		}

	}

	public void runRules() throws RulesEngineRuntimeException {
		try {
			Iterator iter = assertableObjs.iterator();
			while (iter.hasNext()) {
				AssertObjHolder holder = (AssertObjHolder) iter.next();
				if (holder.handle == null)
					holder.handle = ruleMemory.insert(holder.assertableObj);
				
			}
			ruleMemory.fireAllRules();
			ruleMemory.dispose();
			
		} catch (Exception e) {
			throw new RulesEngineRuntimeException("Error executing rule " + e.getMessage(), e);
		}
		
		

	}
	
	
	
	
	/**
	 * thin wrapper holder for the set
	 * @author ben.hahn
	 *
	 * AssertObjHolder
	 */
	private static class AssertObjHolder {
		FactHandle handle;
		Object assertableObj;
		
		AssertObjHolder(Object obj) {
			assertableObj = obj;
		}
		
		/** overridden hashCode to return the identityHashcode of the underlying object
         * @return - the system unique hashCode of the underlying object
         */
        public int hashCode() {
            // return the object identity hashCode of the object
            return System.identityHashCode(assertableObj);
        }
        
        /** overridden equals to check for object referential equality
         * @param obj - the object to test for equality
         * @return - true if the objects are equal (referential)
         *
         */
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof AssertObjHolder) {
                if (assertableObj == ((AssertObjHolder) obj).assertableObj) {
                    return true;
                }
            } else {
                if (assertableObj == obj) {
                    return true;
                }
            }
            
            return false;
        }
       
        /**
         * the String representation of this object
         * @return - the String representation of this object
         *
         */
        public String toString() {
            return "AssertObjHolder: " + assertableObj.toString();
        }
		
		
	}

}
