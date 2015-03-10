/**
 * 
 */
package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author shahzheebkhan
 *
 */
public class InputValidationRule implements RuleFact<InputValidationFact> {
	
	private static final Logger logger = LoggerFactory.getLogger(InputValidationRule.class);

	private static final String STATUS_VALUE = "In Scope";
	
	private static final String XPATH_EXPR = "XPATH=";
	private static final String AND = "AND";
	private static final String GREATER = "GREATER THAN";
	private static final String NOTEQUALS = "NOT EQUALS";
	private static final String GREATER_OR_EQUALS = "GREATER EQUAL THAN";
	private static final String LESS_OR_EQUALS = "LESS EQUAL THAN";
	private static final String LESS = "LESS THAN";
	private static final String EQUALS = "EQUALS";
	private static final String COUNT_SUFFIX = "COUNT"; 
	private static final String COUNT_GREATER = "COUNT GREATER THAN";
	private static final String COUNT_NOTEQUALS = "COUNT NOT EQUALS";
	private static final String COUNT_GREATER_OR_EQUALS = "COUNT GREATER EQUAL THAN";
	private static final String COUNT_LESS_OR_EQUALS = "COUNT LESS EQUAL THAN";
	private static final String COUNT_LESS = "COUNT LESS THAN";
	private static final String COUNT_EQUALS = "COUNT EQUALS";
	private static final String INRANGE = "IN RANGE";
	private static final String NOTINRANGE = "NOT IN RANGE";
	private static final String IN = "IN";
	private static final String NOTIN = "NOT IN";
	private static final String ISEXISTS = "IS EXISTS";
	private static final String ISEMPTY = "IS EMPTY";
	private static final String NOTEMPTY = "NOT EMPTY";
	private static final String SEPARATOR = ",";
	private static final String[] OPERATORS = { GREATER, NOTEQUALS, GREATER_OR_EQUALS, LESS_OR_EQUALS, LESS, EQUALS, INRANGE, NOTINRANGE, IN, NOTIN, ISEXISTS, ISEMPTY, NOTEMPTY,
				COUNT_EQUALS, COUNT_GREATER, COUNT_GREATER_OR_EQUALS, COUNT_LESS, COUNT_LESS_OR_EQUALS, COUNT_NOTEQUALS};
	private static final String NODE_SEPARATOR = "/";
	
	private InputValidationFact input;
	private InputValidationFact output;
	private String status = STATUS_VALUE;
	
	/**
	 * DEFAULT CTOR
	 */
	public InputValidationRule() {
		super();
	}
	
	
	/**
	 * only has 1 input param
	 * @param input
	 */
	public InputValidationRule(InputValidationFact input) {
		this();
		this.input = input;
		this.output = input;
	}
	
	/**
	 * full CTOR
	 * @param input
	 */
	public InputValidationRule(InputValidationFact input, InputValidationFact output) {
		this.input = input;
		this.output = output;
	}
	
	/**
	 * factory creates
	 * @param input
	 * @return
	 */
	public InputValidationRule newInstance(InputValidationFact input) {
		InputValidationRule rule = new InputValidationRule(input);
		return rule;
	}
	
	/**
	 * factory creates
	 * @param input
	 * @return
	 */
	public InputValidationRule newInstance(InputValidationFact input, InputValidationFact output) {
		InputValidationRule rule = new InputValidationRule(input, output);
		return rule;
	}
	
	
	public String getStatus() {
		return status;
	}
	

	/**
	 * the eval expression method
	 * @param expression
	 * @return
	 */
	public final boolean eval(final String expression) throws Exception {
		
		logger.info("Evaluating rule: " + expression);
		
		boolean result = false;
		String xpath = null;
		if (expression != null && expression.startsWith(XPATH_EXPR) ) {
			xpath = expression.substring(XPATH_EXPR.length()).trim();
		} else {
			xpath = buildXPath(expression);
		}
		
		if (StringUtils.isNotBlank(xpath)) {
			logger.info("Xpath is " + xpath);
			result = XmlUtils.evaluateXPath(this.input.getDocument(), xpath);
		}
		
		logger.info("Finished evaluating rule: " + expression + " with result: " + result);
		
		return result;
	}
	
	/**
	 * 
	 * @param expression
	 * @return
	 */
	public final String buildXPath(final String expression) {
		
		StringBuffer xpath = new StringBuffer();
		
		RuleExpression[] expressions = parseExpression(expression);
		
		if (expressions.length > 0) {
			
			String[] context = findContext(expressions);
			xpath.append(openIsExists(context));
			
			int count = 0;
			for (int i = 0; i < expressions.length; i++) {
				if (!expressions[i].getOperator().equals(ISEXISTS)) {
					String sub = writeQuery(expressions[i], context.length);
					if (StringUtils.isNotBlank(sub)) {
						if (count == 0)
							xpath.append("[");
						else
							xpath.append(" and ");
						xpath.append(sub);
						count++;
					}
				}
			}
			
			if (count > 0)
				xpath.append("]");
			
			xpath.append(")");
			
			
		}
		
		return xpath.toString();
	}
	
	/**
	 * 
	 * @param re
	 * @param startElement
	 * @return
	 */
	private final String writeQuery(final RuleExpression re, final int startElement) {
		
		String query = null;
		if (re.getOperator().equals(IN) || re.getOperator().equals(NOTIN)) {
			query = writeListQuery(re, startElement);
		} else if (re.getOperator().equals(ISEMPTY) || re.getOperator().equals(NOTEMPTY)) {
			query = writeEmptyQuery(re, startElement);
		} else if (re.getOperator().equals(INRANGE) || re.getOperator().equals(NOTINRANGE)) {
			query = writeRangeQuery(re, startElement);
		} else {
			query = writeComparatorQuery(re, startElement);
		}
		
		return query;
 	}
	
	/**
	 * 
	 * @param re
	 * @param startEl
	 * @return
	 */
	private final String writeRangeQuery(final RuleExpression re, final int startEl) {
		
		StringBuffer buffer = new StringBuffer("(");
		
		if (!re.getValue().startsWith("(") && re.getValue().endsWith(")")) {
			throw new RulesEngineRuntimeException("Invalid range syntax, must be enclosed in parenthesis " + re.getValue());
		}
		
		String[] values = re.getValue().substring(1, re.getValue().length() - 1).split(SEPARATOR);
		if (values.length != 2) {
			throw new RulesEngineRuntimeException("Invalid range expression, incorrect number of arguments " + re.getValue());
		}
		
		if (StringUtils.isEmpty(values[0]) || StringUtils.isEmpty(values[1])) {
			throw new RulesEngineRuntimeException("Invalid range expression, empty arguments " + re.getValue());
		}
		
		boolean notInRange = re.getOperator().equals(NOTINRANGE);
		
		String path = toPath(re.getField(), startEl);
		buffer.append("exists(");
		buffer.append(path);
		buffer.append(") and (");
		
		buffer.append(path);
		
		if (notInRange) {
			buffer.append(" < ");
		} else {
			buffer.append(" >= ");
		}
		
		buffer.append(values[0].trim());
		
		if (notInRange) {
			buffer.append(" or ");
		} else {
			buffer.append(" and ");
		}
		
		buffer.append(path);
		
		if (notInRange) {
			buffer.append(" > ");
		} else {
			buffer.append(" <= ");
		}
		
		buffer.append(values[1].trim());
		
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param re
	 * @param startEl
	 * @return
	 */
	private final String writeEmptyQuery(final RuleExpression re, final int startEl) {
		
		StringBuffer buffer = new StringBuffer("(");
		
		boolean notEmpty = re.getOperator().equals(NOTEMPTY);
		
		String path = toPath(re.getField(), startEl);
		
		if (notEmpty) {
			buffer.append("exists(");
			buffer.append(path);
			buffer.append(") and ");
			buffer.append(path);
			buffer.append(" != \"\"");
		} else {
			buffer.append("not(exists(");
			buffer.append(path);
			buffer.append(")) or ");
			buffer.append(path);
			buffer.append(" = \"\"");
		}
			
		buffer.append(")");
		
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param re
	 * @param startEl
	 * @return
	 */
	private final String writeListQuery(final RuleExpression re, final int startEl) {
		
		StringBuffer buffer = new StringBuffer("(");
		
		if (!re.getValue().startsWith("(") && re.getValue().endsWith(")")) {
			throw new RulesEngineRuntimeException("Invalid list syntax, must be enclosed in parenthesis " + re.getValue());
		}
		
		String[] values = re.getValue().substring(1, re.getValue().length() - 1).split(SEPARATOR);
		
		String path = toPath(re.getField(), startEl);
		buffer.append("exists(");
		buffer.append(path);
		buffer.append(") and (");
		
		
		for (int i = 0; i < values.length; i++) {
			if (i > 0)  {
				if (re.getOperator().equals(NOTIN))
					buffer.append(" and ");
				else
					buffer.append(" or ");
			}
			buffer.append(path);
			if (re.getOperator().equals(NOTIN))
				buffer.append(" != \"");
			else
				buffer.append(" = \"");
			String value = values[i].trim();
			// strip out any quotes since lists are always strings
			if ((value.startsWith("'") && value.endsWith("'")) || 
					(value.startsWith("\"") && value.endsWith("\"")))
				buffer.append(value.substring(1, value.length() - 1));
			else
				buffer.append(value);
			buffer.append("\"");
		}
		
		buffer.append("))");
		
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param re
	 * @param startEl
	 * @return
	 */
	private final String writeComparatorQuery(final RuleExpression re, final int startEl) {
		
		StringBuffer buffer = new StringBuffer("(");
		
		boolean isCount = re.getOperator().startsWith(COUNT_SUFFIX);
		String path = toPath(re.getField(), startEl);
		if (!isCount) {
			buffer.append("exists(");
			buffer.append(path);
			buffer.append(") and ");
		}
		
		if (isCount)
			buffer.append("count(");
		buffer.append(path);
		if (isCount)
			buffer.append(")");
		buffer.append(" ");
		if (re.getOperator().endsWith(NOTEQUALS)) {
			buffer.append("!=");
		} else if (re.getOperator().endsWith(GREATER)) {
			buffer.append(">");
		} else if (re.getOperator().endsWith(GREATER_OR_EQUALS)) {
			buffer.append(">=");
		} else if (re.getOperator().endsWith(LESS)) {
			buffer.append("<");
		} else if (re.getOperator().endsWith(LESS_OR_EQUALS)) {
			buffer.append("<=");
		} else {
			buffer.append("=");
		}
		
		buffer.append(" ");
		buffer.append(re.getValue());
		buffer.append(")");
		
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	private final String[] tokenizeNodes(final String field) {
		String[] tokens = new String[0];
		if (StringUtils.isNotBlank(field)) {
			if (!field.startsWith(NODE_SEPARATOR)) {
				throw new RulesEngineRuntimeException("Fields must specify absolute path and must start with '/'");
			}
			tokens = field.substring(1).split(NODE_SEPARATOR);
			
			// make sure the expression is valid
			validateNodeNames(tokens, true);
			
			if (!tokens[0].equals(this.input.getDocumentRootName())) {
				tokens = (String[]) ArrayUtils.add(tokens, 0, this.input.getDocumentRootName());
			}
			
		}
		return tokens;
	}
	
	/**
	 * 
	 * @param field
	 * @param not
	 * @return
	 */
	private final String openIsExists(final String[] context) {
		StringBuffer xpath = new StringBuffer();
		if (context != null && context.length > 0) {
			xpath.append("exists(");
			xpath.append(toPath(context, 0));
		}
		
		return xpath.toString();
	}
	
	/**
	 * 
	 * @param field
	 * @param start
	 * @return
	 */
	private final String toPath(final String[] field, final int start) {
		StringBuffer path = new StringBuffer();
		if (start == 0 && field[0].equals(this.input.getDocumentRootName())) {
			path.append(NODE_SEPARATOR);
		}
		for (int i = start; i < field.length; i++) {
			path.append(field[i]);
			if (i < field.length - 1)
				path.append(NODE_SEPARATOR);
		}
		return path.toString();
	}
	
	/**
	 * 
	 * @param expression
	 * @return
	 */
	private final String[] splitExpressions(final String expression) {
		String[] expressions = new String[] { expression }; 
		if (StringUtils.isNotBlank(expression)) {
			String norm = StringUtils.normalizeSpace(expression);
			expressions = norm.split(" " + AND + " ");
		}
		
		return expressions;
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	private final RuleExpression[] parseExpression(final String expression) {
		
		ArrayList<RuleExpression> ruleExpressions = new ArrayList<RuleExpression>();
		if (expression != null) {
			String[] expressions = splitExpressions(expression);
			for (int i = 0; i < expressions.length; i++) {
				String field = StringUtils.substringBefore(expressions[i].trim(), " ").trim();
				if (field.length() == 0) {
					throw new RulesEngineRuntimeException("Invalid Expression " + expressions[i] + ". No operators");
				}
				String[] fields = tokenizeNodes(field);
				String rhs = StringUtils.substringAfter(expressions[i].trim(), " ").trim();
				if (rhs.length() == 0) {
					throw new RulesEngineRuntimeException("Invalid Expression " + expressions[i] + ". No operators");
				}
				boolean validOper = false;
				for (int j = 0; !validOper && j < OPERATORS.length; j++) {
					RuleExpression re = createRuleExpression(fields, rhs, OPERATORS[j], expressions[i]);
					if (re != null) {
						ruleExpressions.add(re);
						validOper = true;
					}
				}
				if (!validOper) {
					throw new RulesEngineRuntimeException("Invalid Operation " + expressions[i]);
				}
				
			}
		}
		
		return ruleExpressions.toArray(new RuleExpression[0]);
		
	}
	
	
	/**
	 * 
	 * @param re
	 * @return
	 */
	private final String[] findContext(final RuleExpression[] re) {
		String[] context = null;
		if (re != null && re.length > 0) {
			String[] base = re[0].getField();
			if (!re[0].getOperator().equals(ISEXISTS))
				base = (String[]) ArrayUtils.subarray(base, 0, base.length - 1);
			
			for (int i = 1; i < re.length; i++) {
				String[] compare = re[i].getField();
				int compLen = compare.length;
				if (!re[i].getOperator().equals(ISEXISTS)) {
					compLen = compare.length - 1;
				}
				int max = NumberUtils.min(base.length, compLen, 1000000);
				int count = 0;
				while (count < max && base[count].equals(compare[count])) {
					count++;
				}
				if (count > 0 && count < base.length) {
					base = (String[]) ArrayUtils.subarray(base, 0, count);
				}
			}
			context = base;
		}
		
		return context;
	}
	
	/**
	 * 
	 * @param fields
	 * @param rhs
	 * @param operator
	 * @return
	 */
	private final RuleExpression createRuleExpression(
			final String[] fields, final String rhs, final String operator, final String expression) {
		RuleExpression re = null;
		if (rhs != null && rhs.startsWith(operator)) {
			if (operator.equals(ISEXISTS) || operator.equals(ISEMPTY) || operator.equals(NOTEMPTY)) {
				re = new RuleExpression(fields, operator);
			} else {
				String value = StringUtils.substringAfter(rhs, operator).trim();
				if (value.length() == 0)
					throw new RulesEngineRuntimeException("Invalid Expression " + expression + " missing comparator value");
				re = new RuleExpression(fields, operator, value);
			}
		}
		return re;
	}
	
	/**
	 * 
	 * @param nodes
	 * @param exception
	 * @return
	 */
	private final boolean validateNodeNames(final String[] nodes, final boolean exception) {
		
		if (nodes != null) {
			
			for (int i = 0; i < nodes.length; i++) {
				if (!XmlUtils.isValidName(nodes[i])) {
					if (exception) {
						throw new RulesEngineRuntimeException("Invalid field name: " + nodes[i]);
					} else {
						return false;
					}
				}
			}
			
		}
		
		return true;
	}
	
	
	
	/**
	 * 
	 * @param errorCode
	 */
	public final void addErrorCode(final String errorCode) {
		if (errorCode != null) {
			this.output.getValidations().add(new ValidationOutput(errorCode, null));
		}
			
	}
	
	/**
	 * 
	 * @param errorMessage
	 */
	public final void addErrorMessage(final  String errorMessage) {
		if (errorMessage != null) {
			if (this.output.getValidations().size() > 0) {
				this.output.getValidations().get(this.output.getValidations().size() - 1).setErrorMessage(errorMessage);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.wvus.esb.rules.RuleFact#getInput()
	 */
	@Override
	public InputValidationFact getInput() {
		return this.input;
	}

	/* (non-Javadoc)
	 * @see org.wvus.esb.rules.RuleFact#setInput(java.lang.Object)
	 */
	@Override
	public void setInput(InputValidationFact obj) {
		this.input = obj;
	}

	/* (non-Javadoc)
	 * @see org.wvus.esb.rules.RuleFact#getOutput()
	 */
	@Override
	public InputValidationFact getOutput() {
		return this.output;
	}

	/* (non-Javadoc)
	 * @see org.wvus.esb.rules.RuleFact#setOutput(java.lang.Object)
	 */
	@Override
	public void setOutput(InputValidationFact obj) {
		this.output = obj;
	}


	/**
	 * 
	 * @author benhahn
	 *
	 */
	private static class RuleExpression {
		
		private String[] field;
		private String operator;
		private String value;
		
		/**
		 * 
		 * @param field
		 * @param operator
		 */
		RuleExpression(String[] field, String operator) {
			this.field = field;
			this.operator = operator;
		}
		
		RuleExpression(String[] field, String operator, String value) {
			this(field, operator);
			this.value = value;
		}

		public String[] getField() {
			return field;
		}


		public String getOperator() {
			return operator;
		}

		public String getValue() {
			return value;
		}

		
	}

	
	

}
