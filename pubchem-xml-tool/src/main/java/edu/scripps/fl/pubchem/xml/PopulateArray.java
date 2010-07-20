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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PopulateArray {

	public Map<String, Integer> getColumnsMap(TableModel tableModel) {
		Map<String, Integer> map = new CaseInsensitiveMap();
		for (int ii = 0; ii < tableModel.getColumnCount(); ii++) {
			String name = tableModel.getColumnName(ii);
			if (null != name) { // excel sometimes adds null columns
				name = name.replaceAll("[-\\s+]", "");
				map.put(name, ii);
			}
		}
		return map;
	}

	public List<Panel> getPanelValues(File file) throws Exception {
		ExcelTableModel model = ExcelTableModel.load(file, true);
		model.setSheet("Panel");
		model.setValueType(ExcelTableModel.ValueType.ACTUAL);
		return getPanelValues(model);
	}

	public List<Panel> getPanelValues(TableModel tableModel) throws Exception {
		Map<String, Integer> map = getColumnsMap(tableModel);
		List<Panel> panelValues = getPanelValues(tableModel, map, new String[] { "panelName", "panelTargetGi", "panelProteinName",
				"panelGene", "panelTargetType", "panelTaxonomy" });
		return panelValues;
	}

	public List<Panel> getPanelValues(TableModel tableModel, Map<String, Integer> map, String[] properties) throws Exception {
		List<Panel> panelValues = new ArrayList<Panel>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			Panel panelValue = new Panel();

			for (String property : properties) {
				Object obj = tableModel.getValueAt(row, map.get(property));
				if (obj != null && !"".equals(obj))
					BeanUtils.setProperty(panelValue, property, obj);

			}
			panelValues.add(panelValue);

		}
		return panelValues;
	}

	public List<ResultTid> getTidValues(File file) throws Exception {
		ExcelTableModel model = ExcelTableModel.load(file, true);
		model.setSheet("TIDs");
		model.setValueType(ExcelTableModel.ValueType.ACTUAL);
		return getTidValues(model);
	}

	public List<ResultTid> getTidValues(TableModel tableModel) throws Exception {
		Map<String, Integer> map = getColumnsMap(tableModel);
		List<ResultTid> tidValues = getTidValues(tableModel, map, new String[] { "tidName", "tidDescription", "tidType", "tidUnit",
				"tidConcentration", "tidPlot", "tidPanelNum", "tidPanelReadout" });
		return tidValues;
	}

	public List<ResultTid> getTidValues(TableModel tableModel, Map<String, Integer> map, String[] properties) throws Exception {
		List<ResultTid> tidValues = new ArrayList<ResultTid>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			ResultTid tidValue = new ResultTid();

			for (String property : properties) {
				Object obj = tableModel.getValueAt(row, map.get(property));
				if (obj != null && !"".equals(obj))
					BeanUtils.setProperty(tidValue, property, obj);

			}
			tidValues.add(tidValue);

		}
		return tidValues;
	}

	public List<Xref> getXrefs(File file) throws Exception {
		ExcelTableModel model = ExcelTableModel.load(file, true);
		model.setSheet("Xrefs");
		model.setValueType(ExcelTableModel.ValueType.ACTUAL);
		return getXrefs(model);
	}

	public List<Xref> getXrefs(TableModel tableModel) throws Exception {
		Map<String, Integer> map = getColumnsMap(tableModel);
		List<Xref> xrefs = getXrefs(tableModel, map, new String[] { "xrefType", "xrefValue", "xrefComment" });
		return xrefs;
	}

	public List<Xref> getXrefs(TableModel tableModel, Map<String, Integer> map, String[] properties) throws Exception {
		List<Xref> xrefs = new ArrayList<Xref>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			Xref xref = new Xref();

			for (String property : properties) {
				Object obj = tableModel.getValueAt(row, map.get(property));
				if (obj != null && !"".equals(obj))
					BeanUtils.setProperty(xref, property, obj);

			}
			xrefs.add(xref);

		}
		return xrefs;
	}

}
