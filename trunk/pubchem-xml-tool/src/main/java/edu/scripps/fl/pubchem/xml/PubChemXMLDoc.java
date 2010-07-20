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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PubChemXMLDoc {
	
	String  description = "PC-AssayDescription_description", 
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
			rootString = "/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription";

	
	public void addTidPlots(Document document, Integer numberPlots) {
		Element root = (Element) document.selectSingleNode(rootString);
		
		Node dr = root.selectSingleNode("PC-AssayDescription_dr");
		if (dr != null)
			dr.detach();

		Element tidPlotDR = root.addElement("PC-AssayDescription_dr");
		
		for (int ii = 1; ii <= numberPlots; ii++) {
			Element assayDRAttr = tidPlotDR.addElement("PC-AssayDRAttr");
			assayDRAttr.addElement("PC-AssayDRAttr_id").addText("" + ii);
			assayDRAttr.addElement("PC-AssayDRAttr_descr").addText("CR Plot Labels " + ii);
			assayDRAttr.addElement("PC-AssayDRAttr_dn").addText("Concentration");
			assayDRAttr.addElement("PC-AssayDRAttr_rn").addText("Response");
		}
	}
	
	public void buildPanelDocument(Document document, List<Panel> panelValues) {
		Integer panel = decidePanel(panelValues);
		if (panel > 0) {
			Element root = (Element) document.selectSingleNode(rootString);
			Node node = root.selectSingleNode(panelInfo);
			if(node != null){
				node.detach();
				root.selectSingleNode(isPanel).detach();
			}
			root.addElement(isPanel).addAttribute("value", "true");
			Element pI = root.addElement(panelInfo);
			Element assayPanel = pI.addElement("PC-AssayPanel");
			assayPanel.addElement("PC-AssayPanel_name").addText("Assays");
			assayPanel.addElement("PC-AssayPanel_descr").addText("");
			Element assayPanelMember = assayPanel.addElement("PC-AssayPanel_member");

			for (int rr = 0; rr < panelValues.size(); rr++) {
				Panel panelValue = panelValues.get(rr);
				Element member = assayPanelMember.addElement("PC-AssayPanelMember");
				member.addElement("PC-AssayPanelMember_mid").addText(rr + 1 + "");
				member.addElement("PC-AssayPanelMember_name").addText(panelValue.getPanelName());

				Integer targetGi = panelValue.getPanelTargetGi();
				if (null != targetGi) {
					Element target = member.addElement("PC-AssayPanelMember_target");
					Element targetInfo = target.addElement("PC-AssayTargetInfo");
					targetInfo.addElement("PC-AssayTargetInfo_name").addText(panelValue.getPanelProteinName());
					targetInfo.addElement("PC-AssayTargetInfo_mol-id").addText("" + targetGi);
					targetInfo.addElement("PC-AssayTargetInfo_molecule-type").addAttribute("value", panelValue.getPanelTargetType())
							.addText("" + panelValue.getPanelTargetTypeValue());
				}
				Integer taxonomy = panelValue.getPanelTaxonomy();
				Integer gene = panelValue.getPanelGene();
				if (null != taxonomy | null != gene) {
					Element xref = member.addElement("PC-AssayPanelMember_xref");
					if (null != taxonomy) {
						Element annotatedXref = xref.addElement("PC-AnnotatedXRef");
						Element annotatedXref_xref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
						Element XrefData = annotatedXref_xref.addElement("PC-XRefData");
						XrefData.addElement("PC-XRefData_taxonomy").addText("" + taxonomy);
					}
					if (null != gene) {
						Element annotatedXref = xref.addElement("PC-AnnotatedXRef");
						Element annotatedXref_xref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
						Element XrefData = annotatedXref_xref.addElement("PC-XRefData");
						XrefData.addElement("PC-XRefData_gene").addText("" + gene);
					}
				}
			}
		}
	}
	
	
	public void buildTidDocument(Document document, List<ResultTid> tidValues) {
		Integer tidInt = decideTids(tidValues);
		if (tidInt > 0) {
			Integer numberPlots = getNumberPlots(tidValues);
			if (numberPlots > 1)
				addTidPlots(document, numberPlots);

			Element root = (Element) document.selectSingleNode(rootString + "/" + results);

			List<Node> nodes = root.selectNodes("PC-ResultType");
			for (Node n : nodes)
				n.detach();

			for (int tt = 0; tt < tidValues.size(); tt++) {
				ResultTid tidValue = tidValues.get(tt);
				Element resultType = root.addElement("PC-ResultType");
				resultType.addElement("PC-ResultType_tid").addText("" + (tt + 1));
				resultType.addElement("PC-ResultType_name").addText(tidValue.getTidName());

				String description = tidValue.getTidDescription();
				if (null != description)
					resultType.addElement("PC-ResultType_description").addElement("PC-ResultType_description_E").addText(description);
				String tidType = tidValue.getTidType();
				if (null != tidType)
					resultType.addElement("PC-ResultType_type").addAttribute("value", tidType).addText("" + tidValue.getTidTypeValue());
				String tidUnit = tidValue.getTidUnit();
				if (null != tidUnit & "" != tidUnit & tidUnit != "null" )
					resultType.addElement("PC-ResultType_unit").addAttribute("value", tidUnit).addText("" + tidValue.getTidUnitValue());
				Double concentration = tidValue.getTidConcentration();
				if (null != concentration) {
					Element tc = resultType.addElement("PC-ResultType_tc");
					Element concentrationattr = tc.addElement("PC-ConcentrationAttr");
					concentrationattr.addElement("PC-ConcentrationAttr_concentration").addText("" + concentration);
					concentrationattr.addElement("PC-ConcentrationAttr_unit").addAttribute("value", "um").addText("5");
					Integer plot = tidValue.getTidPlot();
					if (null != plot)
						concentrationattr.addElement("PC-ConcentrationAttr_dr-id").addText("" + plot);
				}
				Integer panelNum = tidValue.getTidPanelNum();
				if (null != panelNum) {
					Element panel = resultType.addElement("PC-ResultType_panel-info");
					Element passay = panel.addElement("PC-AssayPanelTestResult");
					passay.addElement("PC-AssayPanelTestResult_mid").addText("" + panelNum);
					passay.addElement("PC-AssayPanelTestResult_readout-annot").addAttribute("value", tidValue.getTidPanelReadout())
							.addText("" + tidValue.getTidPanelReadoutValue());
				}
			}
		}
	}
	
	public void buildXrefDocument(Document document, List<Xref> xrefs) {
		Integer xrefInt = decideXrefs(xrefs);
		if (xrefInt > 0) {
			Element rootElement = (Element) document.selectSingleNode(rootString);
			Element adXref = (Element) rootElement.selectSingleNode((String) xref);
			if (adXref != null) {
				List<Node> nodes = adXref.selectNodes("PC-AnnotatedXRef");
				for (Node nn : nodes)					
					nn.detach();
			} else
				adXref = rootElement.addElement((String) xref);

			for (int ii = 0; ii < xrefs.size(); ii++) {
				Xref xref = xrefs.get(ii);
				Element annotatedXref = adXref.addElement("PC-AnnotatedXRef");
				Element annotatedXrefXref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
				Element xrefData = annotatedXrefXref.addElement("PC-XRefData");
				Object xrefValue = xref.getXrefValue();
				String xrefType = xref.getXrefType();
				if (null != xrefValue) {
					String strValue = xrefValue.toString();
					if (xrefType.equals("source web page")) // this first one is the only string
						xrefData.addElement("PC-XRefData_dburl").addText(strValue);
					else {
						Integer id = Integer.parseInt(strValue);
						if (xrefType.equals("aid")) // all these others are really numbers
							xrefData.addElement("PC-XRefData_aid").addText(id.toString());
						else if (xrefType.equals("protein"))
							xrefData.addElement("PC-XRefData_protein-gi").addText(id.toString());
						else if (xrefType.equals("gene"))
							xrefData.addElement("PC-XRefData_gene").addText(id.toString());
						else if (xrefType.equals("taxonomy"))
							xrefData.addElement("PC-XRefData_taxonomy").addText(id.toString());
						else if (xrefType.equals("omim"))
							xrefData.addElement("PC-XRefData_mim").addText(id.toString());
						else if (xrefType.equals("pmid"))
							xrefData.addElement("PC-XRefData_pmid").addText(id.toString());
						else if (xrefType.equals("sid"))
							xrefData.addElement("PC-XRefData_sid").addText(id.toString());
					}
				}
				String comment = xref.getXrefComment();
				if (null != comment)
					annotatedXref.addElement("PC-AnnotatedXRef_comment").addText(comment);
			}
		}
	}
	
	public Integer decidePanel(List<Panel> panelValues){
		Integer panel = 0;
		for(int ii = 0; ii <= panelValues.size()-1; ii++){
			Panel panelValue = panelValues.get(ii);
			if(null != panelValue.getPanelName())
				panel = panel + 1;
		}
		return panel;
	}

	public Integer decideTids(List<ResultTid> tids){
		Integer tidInt = 0;
		for(int ii = 0; ii <= tids.size()-1; ii++){
			ResultTid tid = tids.get(ii);
			if(null != tid.getTidName())
				tidInt = tidInt + 1;
		}
		return tidInt;
	}
	
	
	public Integer decideXrefs(List<Xref> xrefs){
		Integer xrefInt = 0;
		for(int ii = 0; ii <= xrefs.size()-1; ii++){
			Xref xref = xrefs.get(ii);
			if(null != xref.getXrefType())
				xrefInt = xrefInt + 1;
		}
		return xrefInt;
	}

	public void fixAttribute(Document doc){
		Element documentRoot = (Element) doc.selectSingleNode("PC-AssayContainer");
		List<Attribute> attributes = documentRoot.attributes();
		documentRoot.remove(attributes.get(0));
		documentRoot.addAttribute("xs:schemaLocation", "http://www.ncbi.nlm.nih.gov ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem.xsd");
	}

	public Integer getNumberPlots(List<ResultTid> tidValues) {
		Set<Integer> plotNumbers = new HashSet<Integer>();

		for (int ii = 0; ii <= tidValues.size() - 1; ii++) {
			ResultTid tidValue = tidValues.get(ii);
			if (null != tidValue.getTidPlot())
				plotNumbers.add(tidValue.getTidPlot());
		}
		Integer numPlots = plotNumbers.size();
		return numPlots;
	}
	
	public Document loadPubChemXML(File file) throws Exception{
		
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		DOMReader reader = new DOMReader();
		org.w3c.dom.Document doc2 = builder.parse(is);
		Document doc = reader.read(doc2);
		
		
		return doc;
	}
	
	public void organizeXMLDoc(Document doc){
		Element parent = (Element) doc.selectSingleNode(rootString);
		String[] nodesOrder = {description, protocol, comment, xref, results, revision, target, activityOutcome, dr, grantNumber, projectCategory, isPanel, panelInfo};
		for(String nodeString: nodesOrder){
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
