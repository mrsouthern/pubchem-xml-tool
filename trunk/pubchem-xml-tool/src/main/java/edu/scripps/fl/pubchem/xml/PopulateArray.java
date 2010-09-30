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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PopulateArray {

	public PubChemAssay getAssayValues(ExcelTableModel tableModel) throws Exception {
		tableModel.setSheet("Assay");
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		tableModel.setUseFirstRowAsColumnHeadings(false);
		String[] sections = new String[] { "aid", "grantNumber", "projectCategory", "holdUntilDate", "activityOutcomeMethod", "source",
				"description", "protocol", "comment" };
		PubChemAssay assay = new PubChemAssay();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			String section = tableModel.getValueAt(row, 0).toString();
			section = section.replaceAll("[-\\s+]", "");
			for (String ss : sections) {
				if (ss.equalsIgnoreCase(section) && tableModel.getValueAt(row, 1) != null)
					BeanUtils.setProperty(assay, ss, tableModel.getValueAt(row, 1));
			}
		}
		
//		tableModel.setUseFirstRowAsColumnHeadings(true);
//		assay.setXrefs(getXrefs(tableModel));
//		assay.replaceCitationsInDescription(assay.getDescription(), assay.citationString(false));
		return assay;
	}

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

	public List<Panel> getPanelValues(ExcelTableModel tableModel) throws IllegalAccessException, InvocationTargetException {
		tableModel.setSheet("Panel");
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		tableModel.setUseFirstRowAsColumnHeadings(true);
		Map<String, Integer> map = getColumnsMap(tableModel);
		List<Panel> panelValues = getPanelValues(tableModel, map, new String[] { "panelName", "panelTargetGi", "panelTargetName",
				"panelGene", "panelTargetType", "panelTaxonomy" });
		return panelValues;
	}
	
	public List<Panel> getPanelValues(ExcelTableModel tableModel, Map<String, Integer> map, String[] properties) throws IllegalAccessException, InvocationTargetException {
		List<Panel> panelValues = new ArrayList<Panel>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			if(checkIfRowBlank(tableModel, row) == false){
			Panel panelValue = new Panel();
			for (String property : properties) {
				Object obj = tableModel.getValueAt(row, map.get(property));
				if (obj != null && !"".equals(obj))
					BeanUtils.setProperty(panelValue, property, obj);
			}
			panelValues.add(panelValue);
			}
		}
		return panelValues;
	}

	public List<ResultTid> getTidValues(ExcelTableModel tableModel) throws IllegalAccessException, InvocationTargetException{
		tableModel.setSheet("TIDs");
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		tableModel.setUseFirstRowAsColumnHeadings(true);
		Map<String, Integer> map = getColumnsMap(tableModel);
		List<ResultTid> tidValues = getTidValues(tableModel, map, new String[] { "tidName", "tidDescription", "tidType", "tidUnit",
				"tidConcentration", "tidPlot", "tidPanelNum", "tidPanelReadout" });
		return tidValues;
	}

	public List<ResultTid> getTidValues(ExcelTableModel tableModel, Map<String, Integer> map, String[] properties) throws IllegalAccessException, InvocationTargetException{
		List<ResultTid> tidValues = new ArrayList<ResultTid>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			if (checkIfRowBlank(tableModel, row) == false) {
				ResultTid tidValue = new ResultTid();
				for (String property : properties) {
					Object obj = tableModel.getValueAt(row, map.get(property));
					if (obj != null && !"".equals(obj))
						BeanUtils.setProperty(tidValue, property, obj);
				}
				tidValues.add(tidValue);
			}
		}
		return tidValues;
	}

	public List<Xref> getXrefs(ExcelTableModel tableModel) throws IllegalAccessException, InvocationTargetException {
		tableModel.setSheet("Xrefs");
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		tableModel.setUseFirstRowAsColumnHeadings(true);
		
		Map<String, Integer> map = getColumnsMap(tableModel);
		List<Xref> xrefs = getXrefs(tableModel, map, new String[] { "xrefType", "xrefValue", "xrefComment", "isTarget" });
		return xrefs;
	}

	public List<Xref> getXrefs(ExcelTableModel tableModel, Map<String, Integer> map, String[] properties) throws IllegalAccessException, InvocationTargetException {
		List<Xref> xrefs = new ArrayList<Xref>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			if (checkIfRowBlank(tableModel, row) == false) {
				Xref xref = new Xref();
				for (String property : properties) {
					Object obj = tableModel.getValueAt(row, map.get(property));
					if (obj != null && !"".equals(obj))
						BeanUtils.setProperty(xref, property, obj);
					if(property.equals("isTarget") && obj == null)
						BeanUtils.setProperty(xref, "isTarget", false);
				}
				xrefs.add(xref);
			}
		}
		return xrefs;
	}
	
	public boolean checkIfRowBlank(ExcelTableModel model, int row){
		boolean bool = false;
		Integer count = 0;
		for(int ii = 0; ii < model.getColumnCount(); ii++){
			Object value = model.getCellValueAt(row, ii);
			if(value == null || value == "")
				count = count + 1;
		}
		if(count == model.getColumnCount())
			bool = true;
		return bool;
	}

}
