package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

public class RunTest {

	private static final Logger logger = LoggerFactory.getLogger(RunTest.class);
	public static void main(String[] args) {
		
		try {
			
			ApplicationContext context = null;
			
			if (args.length > 0) {
				context = ApplicationContextLoader.getApplicationContext(args[0]);
			} else {
				context = ApplicationContextLoader.getApplicationContext("classpath:drools.xml");
			}
			
			
			System.out.println("DecisionTable Rules Container is ready!");
			
			// wait for a few seconds
			Thread.sleep(5000);
			
			// run the test
			Document doc = XmlUtils.getDocumentFromFile("C:/SLOADRulesContainer/rulescontainer/Rules/REP5_Request.xml");
			RuleEngine re = (RuleEngine) context.getBean("SLOADValidationRuleEngine");
			
			InputValidationFact output = re.runRules(doc);			
			System.out.println(output.getValidations());
			if(output.getValidations().size() > 0){
				List<ValidationOutput> validationErrorList = output.getValidations();
				//ServicingRequestDocument srd = ServicingRequestDocument.Factory.parse(output.getXml());
				//WorkingTransactionDocument wtd = WorkingTransactionDocument.Factory.newInstance();
				//MessageListType messageList = MessageListType.Factory.newInstance();
				
/*				for (ValidationOutput validationOutput : validationErrorList) {
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
				
				logger.info("SRD :"+srd.xmlText());*/
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
