package edu.scripps.fl.pubchem.xmltool.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Dom4JTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		File file = new File(args[0]);
		InputStream inputStream = new FileInputStream(file);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document doc2 = builder.parse(inputStream);
		org.w3c.dom.Node node = doc2.getElementsByTagName("PC-AssaySubmit_data").item(0);
		org.w3c.dom.Node parent = node.getParentNode();
		parent.removeChild(node);
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(System.out, format);
		
		Document doc;
		
		DOMReader reader = new DOMReader();		
		doc = reader.read(doc2);
		writer.write(doc);
		
		
//		XPP3Reader xpp3 = new XPP3Reader();
//		doc = xpp3.read(inputStream);


		// this one not so much
//		SAXReader saxReader = new SAXReader();
//	 	doc = saxReader.read(file);
	 	
	 	

	}

}
