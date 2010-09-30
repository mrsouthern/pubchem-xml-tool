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

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.xml.model.Panel;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PanelXML {
	
	public static String rootString = "/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription",
						isPanel = "PC-AssayDescription_is-panel", 
						panelInfo = "PC-AssayDescription_panel-info";
	
	
	public void buildPanelDocument(Document document, List<Panel> panelValues) throws Exception {
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
					String name = panelValue.getPanelTargetName();
					String type = panelValue.getPanelTargetType();
					targetInfo.addElement("PC-AssayTargetInfo_name").addText(name);
					targetInfo.addElement("PC-AssayTargetInfo_mol-id").addText("" + targetGi);
					targetInfo.addElement("PC-AssayTargetInfo_molecule-type").addAttribute("value", type)
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
	
//	public void buildPanelDocument(Document document, List<Panel> panelValues) throws Exception {
//		Integer panel = decidePanel(panelValues);
//		if (panel > 0) {
//			Element root = (Element) document.selectSingleNode(rootString);
//			Node node = root.selectSingleNode(panelInfo);
//			if(node != null){
//				node.detach();
//				root.selectSingleNode(isPanel).detach();
//			}
//			root.addElement(isPanel).addAttribute("value", "true");
//			Element pI = root.addElement(panelInfo);
//			Element assayPanel = pI.addElement("PC-AssayPanel");
//			assayPanel.addElement("PC-AssayPanel_name").addText("Assays");
//			assayPanel.addElement("PC-AssayPanel_descr").addText("");
//			Element assayPanelMember = assayPanel.addElement("PC-AssayPanel_member");
//
//			for (int rr = 0; rr < panelValues.size(); rr++) {
//				Panel panelValue = panelValues.get(rr);
//				Element member = assayPanelMember.addElement("PC-AssayPanelMember");
//				member.addElement("PC-AssayPanelMember_mid").addText(rr + 1 + "");
//				member.addElement("PC-AssayPanelMember_name").addText(panelValue.getPanelName());
//
//				Integer targetGi = panelValue.getPanelTargetGi();
//				if (null != targetGi) {
//					Element target = member.addElement("PC-AssayPanelMember_target");
//					Element targetInfo = target.addElement("PC-AssayTargetInfo");
//					String name = panelValue.getPanelTargetName();
//					String type = panelValue.getPanelTargetType();
//					PubChemXMLDoc doc = new PubChemXMLDoc();
//					if(name == null && type.equalsIgnoreCase("protein")){
//						Document targetDoc = doc.getDocument(targetGi, panelValue.getPanelTargetType().toLowerCase());
//						name = doc.getTargetInformation(targetDoc, "Title");
//					}
//					else if(name == null && (type.equalsIgnoreCase("DNA") || type.equalsIgnoreCase("RNA"))){
//						Document targetDoc = doc.getDocument(targetGi, "nucleotide");
//						name = doc.getTargetInformation(targetDoc, "Title");
//					}
//					else if(name == null)
//						throw new UnsupportedOperationException("Enter Panel Target Name for: " + targetGi);
//					
//					targetInfo.addElement("PC-AssayTargetInfo_name").addText(name);
//					targetInfo.addElement("PC-AssayTargetInfo_mol-id").addText("" + targetGi);
//					targetInfo.addElement("PC-AssayTargetInfo_molecule-type").addAttribute("value", type)
//							.addText("" + panelValue.getPanelTargetTypeValue());
//				}
//				Integer taxonomy = panelValue.getPanelTaxonomy();
//				Integer gene = panelValue.getPanelGene();
//				if (null != taxonomy | null != gene) {
//					Element xref = member.addElement("PC-AssayPanelMember_xref");
//					if (null != taxonomy) {
//						Element annotatedXref = xref.addElement("PC-AnnotatedXRef");
//						Element annotatedXref_xref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
//						Element XrefData = annotatedXref_xref.addElement("PC-XRefData");
//						XrefData.addElement("PC-XRefData_taxonomy").addText("" + taxonomy);
//					}
//					if (null != gene) {
//						Element annotatedXref = xref.addElement("PC-AnnotatedXRef");
//						Element annotatedXref_xref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
//						Element XrefData = annotatedXref_xref.addElement("PC-XRefData");
//						XrefData.addElement("PC-XRefData_gene").addText("" + gene);
//					}
//				}
//			}
//		}
//	}
	
	public Integer decidePanel(List<Panel> panelValues){
		Integer panel = 0;
		for(int ii = 0; ii <= panelValues.size()-1; ii++){
			Panel panelValue = panelValues.get(ii);
			if(null != panelValue.getPanelName())
				panel = panel + 1;
		}
		return panel;
	}

}
