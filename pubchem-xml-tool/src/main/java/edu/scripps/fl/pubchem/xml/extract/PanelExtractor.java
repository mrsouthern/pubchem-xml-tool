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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.PanelTarget;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PanelExtractor {

	public void fillPanelExcelTemplate(ExcelTableModel model, List<Panel> panelValues) throws Exception {
		model.setSheet("Panel");
		Map<Integer, String> map = new XMLExtractor().getColumnsMap(model);
		String[] panelProperties = { "panelName", "panelGene", "panelTaxonomy" };
		String[] panelTargetProperties = { "panelTargetGi", "panelTargetName", "panelTargetType" };
		for (int ii = 0; ii <= panelValues.size() - 1; ii++) {
			Panel panelValue = panelValues.get(ii);
			List<PanelTarget> panelTargets = panelValue.getPanelTarget();
			if (panelTargets != null && panelTargets.size() > 0) {
				List<Integer> gis = new ArrayList<Integer>();
				List<String> names = new ArrayList<String>();
				List<String> types = new ArrayList<String>();
				for (PanelTarget target : panelTargets) {
					gis.add(target.getPanelTargetGi());
					names.add(target.getPanelTargetName());
					types.add(target.getPanelTargetType());
				}
				Map<String, List<?>> valueMap = new HashMap<String, List<?>>();
				valueMap.put(panelTargetProperties[0], gis);
				valueMap.put(panelTargetProperties[1], names);
				valueMap.put(panelTargetProperties[2], types);
				for (String targetProperty : panelTargetProperties) {
					for (int kk = 0; kk <= map.size() - 1; kk++) {
						if (targetProperty.equalsIgnoreCase(map.get(kk))) {
							model.setValueAt(StringUtils.join(valueMap.get(targetProperty),"|"), ii, kk);
						}
					}
				}
			}
			for (String property : panelProperties) {
				for (int jj = 0; jj <= map.size() - 1; jj++) {
					if(property.equalsIgnoreCase(map.get(jj))){
						if(property.equalsIgnoreCase("panelGene"))
							model.setValueAt(StringUtils.join(panelValue.getPanelGene(),"|"), ii, jj);
						else if(property == "panelTaxonomy")
							model.setValueAt(StringUtils.join(panelValue.getPanelTaxonomy(),"|"), ii, jj);
						else
							model.setValueAt(BeanUtils.getProperty(panelValue, property), ii, jj);
					}
				}
			}
		}
		new XMLExtractor().autoSizeSheet(model);
	}

	public List<Panel> getPanelValuesFromXML(Document doc) throws Exception {
		List<Panel> panelValues = new ArrayList<Panel>();
		List<Node> nodes = doc.selectNodes("//PC-AssayDescription_panel-info/PC-AssayPanel/PC-AssayPanel_member/PC-AssayPanelMember");
		for (Node nn : nodes) {
			Panel panelValue = new Panel();
			panelValue.setPanelName(nn.selectSingleNode("PC-AssayPanelMember_name").getText());
			List<Node> targetNodes = nn.selectNodes("PC-AssayPanelMember_target/PC-AssayTargetInfo");
			if (targetNodes != null && targetNodes.size() > 0) {
				List<PanelTarget> targets = new ArrayList<PanelTarget>();
				for (Node targetNode : targetNodes) {
					PanelTarget target = new PanelTarget();
					target.setPanelTargetName(targetNode.selectSingleNode("PC-AssayTargetInfo_name").getText());
					BeanUtils.setProperty(target, "panelTargetGi", targetNode.selectSingleNode("PC-AssayTargetInfo_mol-id").getText());
					target.setPanelTargetType(targetNode.selectSingleNode("PC-AssayTargetInfo_molecule-type").valueOf("@value"));
					targets.add(target);
				}
				panelValue.setPanelTarget(targets);
			}
			
			Node p = nn.selectSingleNode("PC-AssayPanelMember_xref");
				if (p != null){
				List<Integer> taxonomies = getXrefValues("PC-AnnotatedXRef/PC-AnnotatedXRef_xref/PC-XRefData/PC-XRefData_taxonomy", p);
				panelValue.setPanelTaxonomy(taxonomies);
				List<Integer> genes = getXrefValues("PC-AnnotatedXRef/PC-AnnotatedXRef_xref/PC-XRefData/PC-XRefData_gene", p);
				panelValue.setPanelGene(genes);
			}
			panelValues.add(panelValue);
		}
		return panelValues;
	}

	private List<Integer> getXrefValues(String nodeName, Node parent) {
		List<Node> nodes = parent.selectNodes(nodeName);
		List<Integer> list = new ArrayList<Integer>();
		if (nodes != null) {
			for (Node node : nodes)
				list.add(Integer.parseInt(node.getText()));
		}
		return list;
	}
}
