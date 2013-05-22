package com.BibleQuote.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Nikita K.
 * Date: 22.05.13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Метод fromXML – статический билдер, строящий DOM document из XML.
 * Метод toXML – статический билдер, сериализующий DOM document в строку с XML.
 */
public class XmlUtil {
	public static Document fromXML(String xml) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		return builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
	}

	public static Document fromXMLfile(String xmlfile) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringElementContentWhitespace(true);
		//factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		DocumentBuilder builder = factory.newDocumentBuilder();

		//return builder.parse(XmlUtil.class.getResourceAsStream(xmlfile));
		return builder.parse(new File(xmlfile));
	}

	/**
	 * see http://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
	 */
	public static String toXML(Document document) throws Exception {

		removeWhitespaceNodes(document.getDocumentElement());
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 2);
		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		Writer out = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(out));
		//transformer.transform(new DOMSource(document), new StreamResult(Sysrem.out));
		// вместо Sysrem.out попробовать FileStreamer (как-то так)

		return out.toString();
	}

	public static String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}

	/**
	 * see http://www.java.net/node/667186
	 */
	public static void removeWhitespaceNodes(Element e) {
		NodeList children = e.getChildNodes();
		for (int i = children.getLength() - 1; i >= 0; i--) {
			Node child = children.item(i);
			if (child instanceof Text && ((Text) child).getData().trim().length() == 0) {
				e.removeChild(child);
			}
			else if (child instanceof Element) {
				removeWhitespaceNodes((Element) child);
			}
		}
	}

}