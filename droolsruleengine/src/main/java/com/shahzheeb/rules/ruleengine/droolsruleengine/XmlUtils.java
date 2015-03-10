package com.shahzheeb.rules.ruleengine.droolsruleengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.registry.Registry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.lib.NamespaceConstant;

import org.apache.xmlbeans.impl.common.XMLChar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

public class XmlUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

	private static DocumentBuilderFactory documentBuilderFactory;

	private static DocumentBuilder documentBuilder;

	private static XPathFactory xPathFactory;

	private static TransformerFactory transformerFactory = new net.sf.saxon.TransformerFactoryImpl();

	private static Templates stripNamespaceXsl;
	
	static {
		try {
			compileStripNamespaceTemplates();
		} catch (Exception e) {
			logger.error("Could not compile StripNamespace XSLT", e);
		}
	}

	/**
	 * Generates XML document from the given file
	 */
	public static Document getDocumentFromFile(String fileName)
			throws Exception {
		if (documentBuilder == null) {
			initializeDocumentBuilder();
		}
		return getDocumentFromFile(documentBuilder, fileName);
	}

	/**
	 * Generates XML document from the given file
	 */
	public static Document getDocumentFromFile(DocumentBuilder docBuilder,
			String fileName) throws Exception {
		Document document = null;
		File xmlDocument = new File(fileName);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(xmlDocument);

			synchronized (docBuilder) {
				document = docBuilder.parse(new InputSource(inputStream));
			}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					// ignore this exception
				}
			}
		}
		return document;
	}

	/**
	 * Generates XML document from the supplied string XML content.
	 * 
	 * @param xmlContent
	 *            XML string to be parsed
	 * @return Parsed XML document
	 * @throws Exception
	 */
	public static Document getDocument(String xmlContent) throws Exception {
		Document document = null;
		if (xmlContent != null) {
			if (documentBuilder == null) {
				initializeDocumentBuilder();
			}
			document = getDocument(documentBuilder, xmlContent);
		}
		return document;
	}

	/**
	 * Generates XML document from the supplied string XML content.
	 * 
	 * @param DocumentBuilder
	 *            docBuilder
	 * @param xmlContent
	 *            XML string to be parsed
	 * @return Parsed XML document
	 * @throws Exception
	 */
	public static Document getDocument(DocumentBuilder docBuilder,
			String xmlContent) throws Exception {
		Document document = null;
		synchronized (docBuilder) {
			document = docBuilder.parse(new InputSource(new StringReader(
					xmlContent)));
		}
		return document;
	}

	private synchronized static void initializeDocumentBuilder()
			throws Exception {
		if (documentBuilder == null) {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(false);
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		}

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isValidName(String name) {
		if (name != null)
			return XMLChar.isValidName(name);
		else
			return false;
	}

	public static boolean evaluateXPath(Document document, String xPath)
			throws Exception {
		if (xPathFactory == null) {
			synchronized (XmlUtils.class) {
				if (xPathFactory == null) {
					// System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON,
					// "net.sf.saxon.xpath.XPathFactoryImpl");
					xPathFactory = XPathFactory
							.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
				}
			}
		}
		Boolean result = false;
		XPath xPathObj = xPathFactory.newXPath();
		result = (Boolean) xPathObj.evaluate(xPath, document,
				XPathConstants.BOOLEAN);
		return result;
	}

	public static String prettyPrint(Document document) {
		// Pretty-prints a DOM document to XML using DOM Load and Save's
		// LSSerializer.
		// Note that the "format-pretty-print" DOM configuration parameter can
		// only be set in JDK 1.6+.
		
		DOMImplementationRegistry domImplementationRegistry;
		try {
			domImplementationRegistry = DOMImplementationRegistry.newInstance();
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		DOMImplementation domImplementation = document.getImplementation();
		if (domImplementation.hasFeature("LS", "3.0")
				&& domImplementation.hasFeature("Core", "2.0")) {
			/*DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation
					.getFeature("LS", "3.0");*/
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementationRegistry.getDOMImplementation("LS");
			
			LSSerializer lsSerializer = domImplementationLS
					.createLSSerializer();
			DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
			if (domConfiguration.canSetParameter("format-pretty-print",
					Boolean.TRUE)) {
				lsSerializer.getDomConfig().setParameter("format-pretty-print",
						Boolean.TRUE);
				LSOutput lsOutput = domImplementationLS.createLSOutput();
				lsOutput.setEncoding("UTF-8");
				StringWriter stringWriter = new StringWriter();
				lsOutput.setCharacterStream(stringWriter);
				lsSerializer.write(document, lsOutput);
				return stringWriter.toString();
			} else {
				throw new RuntimeException(
						"DOMConfiguration 'format-pretty-print' parameter isn't settable.");
			}
		} else {
			throw new RuntimeException(
					"DOM 3.0 LS and/or DOM 2.0 Core not supported.");
		}
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Document stripNamespaces(String xml) throws Exception {
		
		if (xml != null) {
			
			Source inputXML = new StreamSource(new StringReader(xml));
			DOMResult result = new DOMResult();
			
			stripNamespaceXsl.newTransformer().transform(inputXML, result);
			
			return (Document) result.getNode();
			
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Document stripNamespaces(Document xml) throws Exception {
		
		if (xml != null) {
			
			Source inputXML = new DOMSource(xml);
			DOMResult result = new DOMResult();
			
			stripNamespaceXsl.newTransformer().transform(inputXML, result);
			
			return (Document) result.getNode();
			
		}
		
		return null;
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private static void compileStripNamespaceTemplates() throws Exception {

		StringBuffer buffer = new StringBuffer();
		buffer.append("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n");
		buffer.append("<xsl:output method=\"xml\" indent=\"no\"/>\n");
		buffer.append("<xsl:template match=\"/|comment()|processing-instruction()\">\n");
		buffer.append("<xsl:copy>\n");
		buffer.append("<xsl:apply-templates/>\n");
		buffer.append("</xsl:copy>\n");
		buffer.append("</xsl:template>\n");
		buffer.append("<xsl:template match=\"*\">\n");
		buffer.append("<xsl:element name=\"{local-name()}\">\n");
		buffer.append("<xsl:apply-templates select=\"@*|node()\"/>\n");
		buffer.append("</xsl:element>\n");
		buffer.append("</xsl:template>\n");
		buffer.append("<xsl:template match=\"@*\">\n");
		buffer.append("<xsl:attribute name=\"{local-name()}\">\n");
		buffer.append("<xsl:value-of select=\".\"/>\n");
		buffer.append("</xsl:attribute>\n");
		buffer.append("</xsl:template>\n");
		buffer.append("</xsl:stylesheet>\n");

		StringReader input = new StringReader(buffer.toString());
		try {
			Source xsltSource = new StreamSource(input);
			stripNamespaceXsl = transformerFactory.newTemplates(xsltSource);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
