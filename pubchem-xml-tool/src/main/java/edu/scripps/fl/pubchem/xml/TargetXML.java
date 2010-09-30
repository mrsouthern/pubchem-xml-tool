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

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.scripps.fl.pubchem.xml.model.Target;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class TargetXML {

	private static final Logger logger = LoggerFactory.getLogger(TargetXML.class);

	public static String rootString = "/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription",
			target = "PC-AssayDescription_target";
	
	
//	public void buildTargetDocument(Document document, List<Xref> xrefs) throws Exception {
//		int numTargets = 0;
//		for (Xref xx : xrefs) {
//			if (xx.getIsTarget() != null) {
//				if (xx.getIsTarget() == true)
//					numTargets = numTargets + 1;
//			}
//		}
//		if (numTargets > 0) {
//			Element root = (Element) document.selectSingleNode(rootString);
//			Node targetNode = root.selectSingleNode("//" + target);
//			if (targetNode != null) {
//				targetNode.detach();
//			}
//			Element targetElement = root.addElement(target);
//			PubChemXMLDoc xmlDoc = new PubChemXMLDoc();
//			for (Xref xx : xrefs) {
//				if (xx.getIsTarget() != null) {
//					if (xx.getIsTarget() == true) {
//
//						String type = xx.getXrefType();
//						Double num = Double.parseDouble(xx.getXrefValue().toString());
//						Integer value = num.intValue();
//						Document targetDoc = xmlDoc.getDocument(value, type);
//						String name = xmlDoc.getTargetInformation(targetDoc, "Title");
//						if (type.equalsIgnoreCase("nucleotide")) {
//							if (name.contains("dna") | name.contains("DNA") | name.contains("gene")) {
//								xx.setXrefTargetValue(2);
//								xx.setXrefTargetType("dna");
//							} else if (name.contains("RNA") | name.contains("rna")) {
//								xx.setXrefTargetValue(3);
//								xx.setXrefTargetType("rna");
//							}
//						}
//						Object taxon = xmlDoc.getTargetInformation(targetDoc, "TaxId");
//						Document taxonDoc = xmlDoc.getDocument(taxon, "taxonomy");
//
//						Element info = targetElement.addElement("PC-AssayTargetInfo");
//						info.addElement("PC-AssayTargetInfo_name").addText(name);
//						info.addElement("PC-AssayTargetInfo_mol-id").addText(String.valueOf(num.intValue()));
//						info.addElement("PC-AssayTargetInfo_molecule-type").addAttribute("value", xx.getXrefTargetType()).addText(
//								xx.getXrefTargetValue().toString());
//						Element organism = info.addElement("PC-AssayTargetInfo_organism");
//						Element element = organism.addElement("BioSource");
//						element = element.addElement("BioSource_org");
//						element = element.addElement("Org-ref");
//						element.addElement("Org-ref_taxname").addText(xmlDoc.getTargetInformation(taxonDoc, "ScientificName"));
//						element.addElement("Org-ref_common").addText(xmlDoc.getTargetInformation(taxonDoc, "CommonName"));
//						element = element.addElement("Org-ref_db");
//						element = element.addElement("Dbtag");
//						element.addElement("Dbtag_db").addText("taxon");
//						element = element.addElement("Dbtag_tag");
//						element = element.addElement("Object-id");
//						element.addElement("Object-id_id").addText(taxon.toString());
//					}
//				}
//			}
//		}
//	}

	public List<Target> buildTargetDocument(Document document, List<Target> targets) throws Exception {
		List<Target> targetsOfAssay = new ArrayList<Target>();
		List<Target> otherTargets = new ArrayList<Target>();
		for (Target xx : targets) {
			otherTargets.add(xx);
				if (xx.isAssayTarget()){
					targetsOfAssay.add(xx);
					otherTargets.remove(xx);
				}
			}
		if(targetsOfAssay != null){
			
			Element root = (Element) document.selectSingleNode(rootString);
			Node targetNode = root.selectSingleNode("//" + target);
			if (targetNode != null) {
				targetNode.detach();
			}
			Element targetElement = root.addElement(target);
			PubChemXMLDoc xmlDoc = new PubChemXMLDoc();
			for(Target xx: targetsOfAssay){
						String type = xx.getType();
						String name = xx.getName();
						if (type.equalsIgnoreCase("nucleotide")) {
							if (name.contains("dna") | name.contains("DNA") | name.contains("gene"))
								xx.setXMLTargetValue(2);
							else if (name.contains("RNA") | name.contains("rna")) 
								xx.setXMLTargetValue(3);
						}
						else if(type.equalsIgnoreCase("protein")){
							xx.setXMLTargetValue(1);
						}
						Document taxonDoc = xmlDoc.getDocument(xx.getTaxonomy(), "taxonomy");

						Element info = targetElement.addElement("PC-AssayTargetInfo");
						info.addElement("PC-AssayTargetInfo_name").addText(name);
						info.addElement("PC-AssayTargetInfo_mol-id").addText(String.valueOf(xx.getId()));
						info.addElement("PC-AssayTargetInfo_molecule-type").addAttribute("value", xx.getXMLTargetType()).addText(xx.getXMLTargetValue().toString());
						Element organism = info.addElement("PC-AssayTargetInfo_organism");
						Element element = organism.addElement("BioSource");
						element = element.addElement("BioSource_org");
						element = element.addElement("Org-ref");
						element.addElement("Org-ref_taxname").addText(xmlDoc.getTargetInformation(taxonDoc, "ScientificName"));
						element.addElement("Org-ref_common").addText(xmlDoc.getTargetInformation(taxonDoc, "CommonName"));
						element = element.addElement("Org-ref_db");
						element = element.addElement("Dbtag");
						element.addElement("Dbtag_db").addText("taxon");
						element = element.addElement("Dbtag_tag");
						element = element.addElement("Object-id");
						element.addElement("Object-id_id").addText(xx.getTaxonomy().toString());
					}
		}
		return otherTargets;
	}


}
