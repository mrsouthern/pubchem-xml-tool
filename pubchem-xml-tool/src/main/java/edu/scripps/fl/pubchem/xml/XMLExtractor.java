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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class XMLExtractor {
	
	public static void getTextFromXML(Document doc, OutputStream outputStream) throws IOException{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		Node node = doc.selectSingleNode("//PC-AssayDescription");
		List<Element> descriptionElements = (List<Element>) node.selectNodes("PC-AssayDescription_description/PC-AssayDescription_description_E");
		List<Element> protocolElements = (List<Element>) node.selectNodes("PC-AssayDescription_protocol/PC-AssayDescription_protocol_E");
		List<Element> commentElements = (List<Element>) node.selectNodes("PC-AssayDescription_comment/PC-AssayDescription_comment_E");
		for(Element ee: descriptionElements)
			writer.write(ee.getText() + "\n");
		for(Element ee: protocolElements)
			writer.write(ee.getText() + "\n");
		for(Element ee: commentElements)
			writer.write("\n" + ee.getText() + "\n");
		
		writer.close();
	}
	
	public void fillPanelExcelTemplate(ExcelTableModel model, List<Panel> panelValues) throws Exception {
		model.setSheet("Panel");
		Map<Integer, String> map = getColumnsMap(model);
		String[] panelProperties = { "panelName", "panelTargetGi", "panelProteinName", "panelGene", "panelTargetType", "panelTaxonomy" };
		for (int ii = 0; ii <= panelValues.size() - 1; ii++) {
			Panel panelValue = panelValues.get(ii);
			for (String property : panelProperties) {
				for (int jj = 0; jj <= map.size() - 1; jj++) {
					if (property.equalsIgnoreCase(map.get(jj)))
						model.setValueAt(BeanUtils.getProperty(panelValue, property), ii + 1, jj);
				}
			}
		}
	}

	
	public void fillTidExcelTemplate(ExcelTableModel model, List<ResultTid> tidValues) throws Exception {
		model.setSheet("Tids");
		Map<Integer, String> map = getColumnsMap(model);
		String[] tidProperties = { "tidName", "tidDescription", "tidType", "tidUnit", "tidConcentration", "tidPlot", "tidPanelNum",
		"tidPanelReadout" };
		for (int ii = 0; ii <= tidValues.size() - 1; ii++) {
			ResultTid tidValue = tidValues.get(ii);
			for (String property : tidProperties) {
				for (int jj = 0; jj <= map.size() - 1; jj++) {
					if (property.equalsIgnoreCase(map.get(jj)))
						model.setValueAt(BeanUtils.getProperty(tidValue, property), ii + 1, jj);
				}
			}
		}
	}
	
	
	public void fillXrefExcelTemplate(ExcelTableModel model, List<Xref> xrefs) throws Exception {
		model.setSheet("Xrefs");
		Map<Integer, String> map = getColumnsMap(model);
		String[] xrefProperties = {"xrefType", "xrefValue", "xrefComment"};
		for (int ii = 0; ii <= xrefs.size() - 1; ii++) {
			Xref xref = xrefs.get(ii);
			for (String property : xrefProperties) {
				for (int jj = 0; jj <= map.size() - 1; jj++) {
					if (property.equalsIgnoreCase(map.get(jj)))
						model.setValueAt(BeanUtils.getProperty(xref, property), ii + 1, jj);
				}
			}
		}
	}
	
	
	public Map<Integer, String> getColumnsMap(TableModel tableModel) {
		Map<Integer, String> map = new CaseInsensitiveMap();
		for (int ii = 0; ii < tableModel.getColumnCount(); ii++) {
			String name = tableModel.getColumnName(ii);
			if (null != name) { // excel sometimes adds null columns
				name = name.replaceAll("[-\\s+]", "");
				map.put(ii, name);
			}
		}
		return map;
	}
	
	
	protected List<Panel> getPanelValuesFromXML(Document doc) throws Exception {
		List<Panel> panelValues = new ArrayList<Panel>();
		List<Node> nodes = doc.selectNodes("//PC-AssayDescription_panel-info/PC-AssayPanel/PC-AssayPanel_member/PC-AssayPanelMember");
		for(Node nn: nodes) {
			Panel panelValue = new Panel();
			panelValue.setPanelName(nn.selectSingleNode("PC-AssayPanelMember_name").getText());			
			Node node2 = nn.selectSingleNode("PC-AssayPanelMember_target/PC-AssayTargetInfo");
			if( node2 != null){
				panelValue.setPanelProteinName(node2.selectSingleNode("PC-AssayTargetInfo_name").getText());
				BeanUtils.setProperty(panelValue, "panelTargetGi", node2.selectSingleNode("PC-AssayTargetInfo_mol-id").getText());
				panelValue.setPanelTargetType(node2.selectSingleNode("PC-AssayTargetInfo_molecule-type").valueOf("@value"));
			}
			for(Node p: (List<Node>) nn.selectNodes("PC-AssayPanelMember_xref/PC-AnnotatedXRef/PC-AnnotatedXRef_xref/PC-XRefData") ){
				if( p == null)
					continue;
				Node node = p.selectSingleNode("PC-XRefData_taxonomy");
				if( node != null)
					BeanUtils.setProperty(panelValue, "panelTaxonomy", node.getText());
				node = p.selectSingleNode("PC-XRefData_gene");
				if( node != null)
					BeanUtils.setProperty(panelValue, "panelGene", node.getText());
			}
			panelValues.add(panelValue);
		}
		return panelValues;
	}
	
	protected List<ResultTid> getTidValuesFromXML(Document doc) throws Exception {	

		List<ResultTid> tidValues = new ArrayList<ResultTid>();
		List<Node> nodes = doc.selectNodes("//PC-AssayDescription_results/PC-ResultType");
		for(Node nn: nodes) {
			ResultTid tidValue = new ResultTid();
			tidValue.setTidName(nn.selectSingleNode("PC-ResultType_name").getText());
			tidValue.setTidType(nn.selectSingleNode("PC-ResultType_type").valueOf("@value"));
			Node node = nn.selectSingleNode("PC-ResultType_unit");
			if( node != null )
				tidValue.setTidUnit(node.valueOf("@value"));
			node = nn.selectSingleNode("PC-ResultType_description/PC-ResultType_description_E");
			if(node != null)
				tidValue.setTidDescription(node.getText());
			node = nn.selectSingleNode("PC-ResultType_tc");
			if( node != null ) {
				Node node2 = node.selectSingleNode("PC-ConcentrationAttr/PC-ConcentrationAttr_dr-id");
				if( node2 != null ) {
					BeanUtils.setProperty(tidValue, "tidPlot", node2.getText());
				}
				BeanUtils.setProperty(tidValue, "tidConcentration",node.selectSingleNode("PC-ConcentrationAttr/PC-ConcentrationAttr_concentration").getText());
			}
			node = nn.selectSingleNode("PC-ResultType_panel-info/PC-AssayPanelTestResult");
			if( node != null ) {
				BeanUtils.setProperty(tidValue, "tidPanelNum", node.selectSingleNode("PC-AssayPanelTestResult_mid").getText());
				tidValue.setTidPanelReadout(node.selectSingleNode("PC-AssayPanelTestResult_readout-annot").valueOf("@value"));
			}
			tidValues.add(tidValue);
		}
		return tidValues;
	}
	
	
	protected List<Xref> getXrefValuesFromXML(Document doc) throws Exception {
		List<Xref> xrefs = new ArrayList<Xref>();
		List<Node> nodes = doc.selectNodes("//PC-AssayDescription_xref/PC-AnnotatedXRef");
		for(Node nn: nodes) {
			Xref xref = new Xref();
			Node node = nn.selectSingleNode("PC-AnnotatedXRef_xref/PC-XRefData");
			selectXref(xref, node, "PC-XRefData_aid", "aid");
			selectXref(xref, node, "PC-XRefData_protein", "protein");
			selectXref(xref, node, "PC-XRefData_gene", "gene");
			selectXref(xref, node, "PC-XRefData_taxonomy", "taxonomy");
			selectXref(xref, node, "PC-XRefData_mim", "omim");
			selectXref(xref, node, "PC-XRefData_aid", "aid");
			selectXref(xref, node, "PC-XRefData_dburl", "source web page");
			selectXref(xref, node, "PC-XRefData_pmid", "pmid");
			selectXref(xref, node, "PC-XRefData_sid", "sid");
			if(nn.selectSingleNode("PC-AnnotatedXRef_comment") != null)
				xref.setXrefComment(nn.selectSingleNode("PC-AnnotatedXRef_comment").getText());
			xrefs.add(xref);
		}
		return xrefs;
	}
	
	
	public void selectXref(Xref xref, Node nn, String xrefNode, String xrefType) throws Exception{
		if(nn.selectSingleNode(xrefNode) != null){
			BeanUtils.setProperty(xref, "xrefValue", nn.selectSingleNode(xrefNode).getText());
			xref.setXrefType(xrefType);
		}
	}
	
	
//	public void writeTextFile(ExcelTableModel model, String outputFile) throws Exception {
//		FileWriter writer = new FileWriter(new File(outputFile));
//		model.write(writer, '\t', true);
//	}
}
