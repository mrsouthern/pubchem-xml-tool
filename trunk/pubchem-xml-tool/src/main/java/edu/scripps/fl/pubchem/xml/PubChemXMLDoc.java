/*
 * Copyright 2010 The Scripps Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.scripps.fl.pubchem.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.scripps.fl.pubchem.EUtilsFactory;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PubChemXMLDoc {
	
	private static final Logger logger = LoggerFactory.getLogger(PubChemXMLDoc.class);
	
	private static String  dbTracking = "//PC-AssayDescription_aid-source/PC-Source/PC-Source_db/PC-DBTracking",
			name = "PC-AssayDescription_name",
			source = "PC-AssayDescription_aid-source",
			description = "PC-AssayDescription_description", 
			protocol = "PC-AssayDescription_protocol", 
			comment = "PC-AssayDescription_comment", 
			xref = "PC-AssayDescription_xref", 
			results = "PC-AssayDescription_results", 
			revision = "PC-AssayDescription_revision", 
			target = "PC-AssayDescription_target", 
			activityOutcome = "PC-AssayDescription_activity-outcome-method", 
			dr = "PC-AssayDescription_dr", 
			grantNumber = "PC-AssayDescription_grant-number", 
			projectCategory = "PC-AssayDescription_project-category", 
			isPanel = "PC-AssayDescription_is-panel", 
			panelInfo = "PC-AssayDescription_panel-info",
			categorizedComment = "PC-AssayDescription_categorized-comment",
			rootString = "/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription";
			

	
	public String getTargetInformation(Document doc, String info) throws Exception{
		try {
			Element element = (Element) doc.selectSingleNode("//Item[@Name='" + info + "']");
			return element.getText();
		}
		catch(Exception ex) {
			logger.error(String.format("Error fetching text in summary document: %s %s", info, doc));
			throw new UnsupportedOperationException("Unable to retrieve text for proteins, genes and targets.");
		}
	}
	
	public Document getDocument(Object value, String db) throws Exception{
		try {
			Document doc = EUtilsFactory.getInstance().getSummary(value, db);
			if(doc == null)
				doc = EUtilsFactory.getInstance().getSummary(value, db);

			return doc;
		}
		catch(Exception ex) {
			logger.error(String.format("Error fetching summary: %s %s", value, db));
			throw new UnsupportedOperationException(String.format("Unable to retrieve text for %s %s. Please remove from worksheet.",db,value));
		}	
	}


	public void fixAttribute(Document doc){
		Element documentRoot = (Element) doc.selectSingleNode("PC-AssayContainer");
		List<Attribute> attributes = documentRoot.attributes();
		documentRoot.remove(attributes.get(0));
		documentRoot.addAttribute("xs:schemaLocation", "http://www.ncbi.nlm.nih.gov ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem.xsd");
	}

	
	
	public Document loadPubChemXML(InputStream inputStream) throws Exception{
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		org.w3c.dom.Document doc2 = builder.parse(inputStream);
		logger.info("Parsing xml: " + inputStream.toString());
		org.w3c.dom.Node node = doc2.getElementsByTagName("PC-AssaySubmit_data").item(0);
		if(node != null){
			logger.info("Removing data");
			org.w3c.dom.Node parent = node.getParentNode();
			parent.removeChild(node);
		}
		DOMReader reader = new DOMReader();	
		logger.info("Reading parsed xml");
		Document doc = reader.read(doc2);
		
		return doc;
	}
	
	
//	public Document loadPubChemXML(InputStream inputStream) throws Exception{
//		
//		SAXReader reader = new SAXReader();
//		reader.addHandler("/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_data/PC-AssayResults", new ElementHandler() {
//		     public void onStart(ElementPath path) {
//		     }
//		     public void onEnd(ElementPath path) {
//		          path.getCurrent().detach();
//		     }
//		});
//		Document doc = reader.read(inputStream);
//		
//		return doc;
//
//	}
	
	public void organizeXMLDoc(Document doc){
		Element parent = (Element) doc.selectSingleNode(rootString);
		String[] nodesOrder = {name, description, protocol, comment, xref, results, revision, target, 
						activityOutcome, dr, grantNumber, projectCategory, isPanel, 
						panelInfo, categorizedComment};
		for(String nodeString: nodesOrder){
			Node node = parent.selectSingleNode(nodeString);
			if(node != null){
				Node clone = (Node) node.clone();
				node.detach();
				parent.add(clone);
			}
		}
		parent = (Element) doc.selectSingleNode(dbTracking);
		String[] secondNodesOrder = {"PC-DBTracking_name", "PC-DBTracking_source-id", "PC-DBTracking_date" };
		for(String nodeString: secondNodesOrder){
			Node node = parent.selectSingleNode(nodeString);
			if(node != null){
				Node clone = (Node) node.clone();
				node.detach();
				parent.add(clone);
			}
		}
		
	}
	
	public void write(Document doc, File toFile) throws IOException {
		fixAttribute(doc);
		organizeXMLDoc(doc);
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileWriter(toFile), format);
		writer.write(doc);
		writer.close();
	}

}
