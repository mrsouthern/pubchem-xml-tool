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

import edu.scripps.fl.pubchem.xml.model.ResultTid;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class ResultTidExtractor {
	
	public void fillTidExcelTemplate(ExcelTableModel model, List<ResultTid> tidValues) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		model.setSheet("Tids");
		Map<Integer, String> map = new XMLExtractor().getColumnsMap(model);
		String[] tidProperties = { "tidName", "tidDescription", "tidType", "tidUnit", "tidConcentration", "tidPlot", "tidPanelNum",
		"tidPanelReadout" };
		for (int ii = 0; ii <= tidValues.size() - 1; ii++) {
			ResultTid tidValue = tidValues.get(ii);
			for (String property : tidProperties) {
				for (int jj = 0; jj <= map.size() - 1; jj++) {
					if (property.equalsIgnoreCase(map.get(jj)))
						model.setValueAt(BeanUtils.getProperty(tidValue, property), ii, jj);
				}
			}
		}
		new XMLExtractor().autoSizeSheet(model);
	}
	
	protected List<ResultTid> getTidValuesFromXML(Document doc) throws IllegalAccessException, InvocationTargetException  {	

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

}
