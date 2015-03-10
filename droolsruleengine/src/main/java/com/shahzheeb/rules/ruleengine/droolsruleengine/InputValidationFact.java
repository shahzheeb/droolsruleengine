package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class InputValidationFact implements Serializable {

	private static final long serialVersionUID = 474241687694856397L;
	private static final Logger logger = LoggerFactory.getLogger(InputValidationFact.class);
	
	private Document document;
	private String xml;
	private List<ValidationOutput> validations;
	private String root;
	
	
	/**
	 * default CTOR
	 */
	public InputValidationFact() {
		this.validations = new ArrayList<ValidationOutput>();
	}
	
	/**
	 * 
	 * @param fields
	 */
	public InputValidationFact(String xml) {
		this();
		this.xml = xml;
	}
	
	/**
	 * 
	 * @param fields
	 */
	public InputValidationFact(Document doc) {
		this();
		try {
			this.document = XmlUtils.stripNamespaces(doc);
		} catch (Exception e){
			logger.error("Error while parsing xml", e);
		}
		this.xml = XmlUtils.prettyPrint(doc);
	}
	
	
	public Document getDocument() {
		if (document == null) {
			synchronized (this) {
				if (document == null) {
					try {
						this.document = XmlUtils.stripNamespaces(this.xml);
					} catch (Exception e){
						logger.error("Error while parsing xml", e);
					}
				}
			}
		}
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
	public String getDocumentRootName() {
		if (this.root == null) {
			synchronized (this) {
				getDocument();
				if (root == null) {
					if (document != null) {
						this.root = document.getDocumentElement().getNodeName();
					}
				}
			}
			
		}
		return root;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public final void addValidation(final ValidationOutput validation) {
		
		if (validation != null) {
			this.validations.add(validation);
		}
		
	}
	
	public final List<ValidationOutput> getValidations() {
		return this.validations;
	}
	
	public final ValidationOutput[] getValidationsAsArray() {
		return this.validations.toArray(new ValidationOutput[0]);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((xml == null) ? 0 : xml.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InputValidationFact other = (InputValidationFact) obj;
		if (xml == null) {
			if (other.xml != null)
				return false;
		} else if (!xml.equals(other.xml))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InputValidationFact [xml=" + xml + ", validations="
				+ validations + "]";
	}
	
	
	

}
