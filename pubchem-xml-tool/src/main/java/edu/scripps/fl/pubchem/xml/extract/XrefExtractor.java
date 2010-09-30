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
package edu.scripps.fl.pubchem.xml.extract;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.Node;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.Xref;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class XrefExtractor {
	
	public void checkXrefAgainstPanel(List<Xref> xrefs, List<Panel> panel) {
		for (Panel yy : panel) {
			for (int ii = 0; ii < xrefs.size(); ii++) {
				Xref xx = xrefs.get(ii);
				String type = xx.getXrefType();
				if (type.equalsIgnoreCase("gene") || type.equalsIgnoreCase("protein") || type.equalsIgnoreCase("nucleotide")) {
					Double idD = Double.parseDouble(xx.getXrefValue().toString());
					Integer id = idD.intValue();
					if (type.equalsIgnoreCase("gene")) {
						Integer gene = yy.getPanelGene();
						if (gene != null) {
							if (gene.equals(id))
								xrefs.remove(xx);
						}
					} else if (type.equalsIgnoreCase("protein") || type.equalsIgnoreCase("nucleotide")) {
						Integer target = yy.getPanelTargetGi();
						if (target != null) {
							if (target.equals(id))
								xrefs.remove(xx);
						}
					} else if (type.equalsIgnoreCase("taxonomy")) {
						Integer taxonomy = yy.getPanelTaxonomy();
						if (taxonomy != null) {
							if (taxonomy.equals(id))
								xrefs.remove(xx);
						}
					}
				}
			}
		}

	}
	
	public void fillXrefExcelTemplate(ExcelTableModel model, List<Xref> xrefs) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		model.setSheet("Xrefs");
		Map<Integer, String> map = new XMLExtractor().getColumnsMap(model);
		String[] xrefProperties = {"xrefType", "xrefValue", "xrefComment", "isTarget"};
		for (int ii = 0; ii <= xrefs.size() - 1; ii++) {
			Xref xref = xrefs.get(ii);
			for (String property : xrefProperties) {
				for (int jj = 0; jj <= map.size() - 1; jj++) {
					if (property.equalsIgnoreCase(map.get(jj)))
						model.setValueAt(BeanUtils.getProperty(xref, property), ii , jj);
				}
			}
		}
		new XMLExtractor().autoSizeSheet(model);
	}
	
	protected List<Xref> getXrefValuesFromXML(Document doc) throws Exception {
		List<Xref> xrefs = new ArrayList<Xref>();
		List<Node> nodes = doc.selectNodes("//PC-AssayDescription_xref/PC-AnnotatedXRef");
		for(Node nn: nodes) {
			Xref xref = new Xref();
			Node node = nn.selectSingleNode("PC-AnnotatedXRef_xref/PC-XRefData");
			selectXref(xref, node, "PC-XRefData_aid", "AID");
			selectXref(xref, node, "PC-XRefData_protein-gi", "Protein");
			selectXref(xref, node, "PC-XRefData_gene", "Gene");
			selectXref(xref, node, "PC-XRefData_taxonomy", "Taxonomy");
			selectXref(xref, node, "PC-XRefData_mim", "OMIM");
			selectXref(xref, node, "PC-XRefData_dburl", "Source Database Homepage");
			selectXref(xref, node, "PC-XRefData_mesh", "MESH");
			selectXref(xref, node, "PC-XRefData_sburl", "Substance Homepage");
			selectXref(xref, node, "PC-XRefData_asurl", "Assay Homepage");
			selectXref(xref, node, "PC-XRefData_rn", "Substance Registry #");
			selectXref(xref, node, "PC-XRefData_pmid", "PMID");
			selectXref(xref, node, "PC-XRefData_sid", "SID");
			selectXref(xref, node, "PC-XRefData_cid", "CID");
			selectXref(xref, node, "PC-XRefData_nucleotide-gi", "Nucleotide");
			selectXref(xref, node, "PC-XRefData_gi", "GenBank");
			selectXref(xref, node, "PC-XRefData_mmdb", "MMDB");
			selectXref(xref, node, "PC-XRefData_biosystem", "Biosystems Id");
			if(nn.selectSingleNode("PC-AnnotatedXRef_comment") != null)
				xref.setXrefComment(nn.selectSingleNode("PC-AnnotatedXRef_comment").getText());
			xrefs.add(xref);
		}
		nodes = doc.selectNodes("//PC-AssayDescription_target/PC-AssayTargetInfo");
		for(Node nn: nodes){
			Xref xref = new Xref();
			BeanUtils.setProperty(xref, "xrefValue", nn.selectSingleNode("PC-AssayTargetInfo_mol-id").getText());
			BeanUtils.setProperty(xref, "xrefTargetValue", nn.selectSingleNode("PC-AssayTargetInfo_molecule-type").getText());
			BeanUtils.setProperty(xref, "isTarget", true);
			xrefs.add(xref);
		}
		List<Panel> panel = new PanelExtractor().getPanelValuesFromXML(doc);
		if(panel != null)
			checkXrefAgainstPanel(xrefs, panel);
		return xrefs;
	}
	
	
	public void selectXref(Xref xref, Node nn, String xrefNode, String xrefType) throws IllegalAccessException, InvocationTargetException {
		if(nn.selectSingleNode(xrefNode) != null){
			BeanUtils.setProperty(xref, "xrefValue", nn.selectSingleNode(xrefNode).getText());
			xref.setXrefType(xrefType);
		}
	}

}
