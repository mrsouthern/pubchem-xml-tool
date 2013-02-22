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

import edu.scripps.fl.pubchem.PubChemAssayFactory;
import edu.scripps.fl.pubchem.xml.model.Gene;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;
import edu.scripps.fl.pubchem.xml.model.Target;
import edu.scripps.fl.pubchem.xml.model.Xref;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class XrefXML {

	public static String rootString = "/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription",
			xref = "PC-AssayDescription_xref";
	
	public void buildXrefDocument(Document document, PubChemAssay assay) throws Exception {
		List<Xref> xrefs = assay.getXrefs();
		PubChemAssayFactory factory = new PubChemAssayFactory();
		List<Target> targets = factory.removePanelsFromTargets(assay);
		List<Gene> genes = factory.removePanelsFromGenes(assay);
		List<Xref> aids = assay.getAids();
		List<Xref> pmids = assay.getPmids();

		if (targets != null && targets.size() > 0)
			targets = new TargetXML().buildTargetDocument(document, targets);

		if (xrefs.size() > 0 || genes.size() > 0 || aids.size() > 0 || pmids.size() > 0 || targets.size() > 0) {
			Element rootElement = (Element) document.selectSingleNode(rootString);
			Element adXref = (Element) rootElement.selectSingleNode((String) xref);
			if (adXref != null) {
				List<Node> nodes = adXref.selectNodes("PC-AnnotatedXRef");
				for (Node nn : nodes)
					nn.detach();
			} else
				adXref = rootElement.addElement((String) xref);
			
			for(Xref xx: aids)
				addXref(adXref, "PC-XRefData_aid", xx.getXrefValue().toString(), xx.getXrefComment());
			
			for(Target tt: targets){
				String elementName = "PC-XRefData_" + tt.getType().toLowerCase() + "-gi";
				addXref(adXref, elementName, tt.getId().toString(), tt.getName());
			}
			
			for(Gene gg: genes){
				if(!gg.getIsTarget())
					addXref(adXref, "PC-XRefData_gene", gg.getId().toString(), gg.getName());
				else
					new TargetXML().addGeneTargetToDocument(document, gg);
			}

			for (Xref xx : xrefs) {
				Element annotatedXref = adXref.addElement("PC-AnnotatedXRef");
				Element annotatedXrefXref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
				Element xrefData = annotatedXrefXref.addElement("PC-XRefData");
				Object xrefValue = xx.getXrefValue();
				String xrefType = xx.getXrefType();
				String strValue = xrefValue.toString();
				// these are strings
				if (xrefType.equalsIgnoreCase("source database homepage"))
					xrefData.addElement("PC-XRefData_dburl").addText(strValue);
				else if (xrefType.equalsIgnoreCase("mesh"))
					xrefData.addElement("PC-XRefData_mesh").addText(strValue);
				else if (xrefType.equalsIgnoreCase("substance homepage"))
					xrefData.addElement("PC-XRefData_sburl").addText(strValue);
				else if (xrefType.equalsIgnoreCase("assay homepage"))
					xrefData.addElement("PC-XRefData_asurl").addText(strValue);
				else if (xrefType.equalsIgnoreCase("substance registry #"))
					xrefData.addElement("PC-XRefData_rn").addText(strValue);
				// all these others are really numbers
				else {
					Double idD = Double.parseDouble(strValue);
					Integer id = idD.intValue();
					if (xrefType.equalsIgnoreCase("sid"))
						xrefData.addElement("PC-XRefData_sid").addText(id.toString());
					else if (xrefType.equalsIgnoreCase("omim"))
						xrefData.addElement("PC-XRefData_mim").addText(id.toString());
					else if (xrefType.equalsIgnoreCase("taxonomy"))
						xrefData.addElement("PC-XRefData_taxonomy").addText(id.toString());
					else if (xrefType.equalsIgnoreCase("cid"))
						xrefData.addElement("PC-XRefData_cid").addText(id.toString());
					else if (xrefType.equalsIgnoreCase("genbank"))
						xrefData.addElement("PC-XRefData_gi").addText(id.toString());
					else if (xrefType.equalsIgnoreCase("mmdb"))
						xrefData.addElement("PC-XRefData_mmdb").addText(id.toString());
					else if (xrefType.equalsIgnoreCase("biosystems id"))
						xrefData.addElement("PC-XRefData_biosystem").addText(id.toString());
				}
				String comment = xx.getXrefComment();
				if (null != comment)
					annotatedXref.addElement("PC-AnnotatedXRef_comment").addText(comment);
			}	
			for(Xref xx: pmids){
				Element annotatedXref = adXref.addElement("PC-AnnotatedXRef");
				Element annotatedXrefXref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
				Element xrefData = annotatedXrefXref.addElement("PC-XRefData");
				xrefData.addElement("PC-XRefData_pmid").addText(xx.getXrefValue().toString());
				if (null != xx.getXrefComment())
					annotatedXref.addElement("PC-AnnotatedXRef_comment").addText(xx.getXrefComment());
				if(xx.getIsPrimaryCitation() != null){
					if(xx.getIsPrimaryCitation())
						new PubChemXMLUtils().attributeAndTextAdd("PC-AnnotatedXRef_type", "pcit", "1", annotatedXref);
				}
			}
		}
	}
	
	public void addXref(Element parentElement, String elementName, String elementText, String comment){
		Element annotatedXref = parentElement.addElement("PC-AnnotatedXRef");
		Element annotatedXrefXref = annotatedXref.addElement("PC-AnnotatedXRef_xref");
		Element xrefData = annotatedXrefXref.addElement("PC-XRefData");
		xrefData.addElement(elementName).addText(elementText);
		if (null != comment)
			annotatedXref.addElement("PC-AnnotatedXRef_comment").addText(comment);
	}

}
