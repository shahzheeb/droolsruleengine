package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.awt.TrayIcon.MessageType;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("rulesExecutorDecisionTable")
@Scope("prototype")
public class RulesExecutorDecisionTable implements RulesExecutorInf {

	private static final Logger logger = LoggerFactory.getLogger(RulesExecutorDecisionTable.class);
	@Autowired
	@Qualifier("SLOADValidationRuleEngine")
	private RuleEngine re;
	
	@Override
	public String executeRules(String xml, String rulesTargetName)
			throws Exception {
		//ServicingRequestDocument srd = ServicingRequestDocument.Factory.parse(xml);
		InputValidationFact output = re.runRules(xml);			
		System.out.println(output.getValidations());
		if(output.getValidations().size() > 0){
			List<ValidationOutput> validationErrorList = output.getValidations();			
	/*		WorkingTransactionDocument wtd = WorkingTransactionDocument.Factory.newInstance();
			MessageListType messageList = MessageListType.Factory.newInstance();
			
			for (ValidationOutput validationOutput : validationErrorList) {
				//MessageType message = MessageType.Factory.newInstance();
				MessageType message = messageList.addNewMessage();
				message.setId(Integer.valueOf(validationOutput.getErrorCode()));
				message.setText(validationOutput.getErrorMessage());
				ParameterValueListType parameterValueListType = ParameterValueListType.Factory.newInstance();
				ParameterValueType parameterValueType = ParameterValueType.Factory.newInstance();
				parameterValueType.setParameterId("First Parameter");
				int index = validationOutput.getErrorMessage().indexOf(':');
				parameterValueType.setActualValue(validationOutput.getErrorMessage().substring(0, index));
				ParameterValueType parameterValueTypeArray[] = new ParameterValueType[1]; 
				parameterValueTypeArray[0] = parameterValueType;
				parameterValueListType.setParameterValueArray(parameterValueTypeArray);
				message.setParameterValueList(parameterValueListType);
				//messageTypeArray[0] = message;
				
			}
			
			//messageList.setMessageArray(messageTypeArray);
			wtd.addNewWorkingTransaction().setMessageList(messageList);
			
			srd.getServicingRequest().getTransactionDetail().setWorkingTransaction(wtd.getWorkingTransaction());
			
			// Adding service flow code
			srd.getServicingRequest().getBusinessRuleTransientElements().setServiceFlowCode("Post Rules Processing");

			
			logger.info("SRD :"+srd.xmlText());		
*/	}
		//return srd.xmlText();
		return null;

}

	public RuleEngine getRe() {
		return re;
	}

	public void setRe(RuleEngine re) {
		this.re = re;
	}
}
