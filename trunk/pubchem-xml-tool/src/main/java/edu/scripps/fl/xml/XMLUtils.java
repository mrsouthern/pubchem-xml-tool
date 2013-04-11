package edu.scripps.fl.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;

public class XMLUtils {


	public static Document readXMLInputStream(InputStream is) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); 
		org.w3c.dom.Document doc2 = builder.parse(is);                                       
		DOMReader reader = new DOMReader();                                                  
		Document doc = reader.read(doc2);
		return doc;
	}
	
}
