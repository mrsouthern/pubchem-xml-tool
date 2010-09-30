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
import org.apache.poi.ss.usermodel.Sheet;
import org.dom4j.Document;
import org.dom4j.Node;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.model.Panel;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PanelExtractor {
	
	public void fillPanelExcelTemplate(ExcelTableModel model, List<Panel> panelValues) throws Exception {
		model.setSheet("Panel");
		Map<Integer, String> map = new XMLExtractor().getColumnsMap(model);
		String[] panelProperties = { "panelName", "panelTargetGi", "panelTargetName", "panelGene", "panelTargetType", "panelTaxonomy" };
		for (int ii = 0; ii <= panelValues.size() - 1; ii++) {
			Panel panelValue = panelValues.get(ii);
			for (String property : panelProperties) {
				for (int jj = 0; jj <= map.size() - 1; jj++) {
					if (property.equalsIgnoreCase(map.get(jj)))
						model.setValueAt(BeanUtils.getProperty(panelValue, property), ii , jj);
				}
			}
		}
		new XMLExtractor().autoSizeSheet(model);
	}
	
	protected List<Panel> getPanelValuesFromXML(Document doc) throws Exception {
		List<Panel> panelValues = new ArrayList<Panel>();
		List<Node> nodes = doc.selectNodes("//PC-AssayDescription_panel-info/PC-AssayPanel/PC-AssayPanel_member/PC-AssayPanelMember");
		for(Node nn: nodes) {
			Panel panelValue = new Panel();
			panelValue.setPanelName(nn.selectSingleNode("PC-AssayPanelMember_name").getText());			
			Node node2 = nn.selectSingleNode("PC-AssayPanelMember_target/PC-AssayTargetInfo");
			if( node2 != null){
				panelValue.setPanelTargetName(node2.selectSingleNode("PC-AssayTargetInfo_name").getText());
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

}
