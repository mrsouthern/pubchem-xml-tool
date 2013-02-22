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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.TableModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.model.CategorizedComment;
import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.PanelTarget;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PopulateArray {
	private static final Logger log = LoggerFactory.getLogger(PopulateArray.class);

	public PubChemAssay getAssayValues(ExcelTableModel tableModel) throws Exception {
		tableModel.setSheet("Assay");
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		tableModel.setUseFirstRowAsColumnHeadings(false);
		String[] sections = new String[] { "name", "externalRegId", "aid", "grantNumber", "projectCategory", "holdUntilDate",
				"activityOutcomeMethod", "source" };
		PubChemAssay assay = new PubChemAssay();
		List<String> descriptions = new ArrayList<String>();
		List<String> protocols = new ArrayList<String>();
		List<String> comments = new ArrayList<String>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			Object sectionO = tableModel.getValueAt(row, 0);
			if (sectionO != null) {
				String section = sectionO.toString();
				section = section.replaceAll("[-\\s+]", "");
				Object obj = tableModel.getValueAt(row, 1);
				String objS = "";
				if (obj != null)
					objS = obj.toString();

				if (section.equalsIgnoreCase("description") || section.contains("description") || section.contains("Description"))
					descriptions.add(objS);
				else if (section.equalsIgnoreCase("protocol") || section.contains("protocol") || section.contains("Protocol"))
					protocols.add(objS);
				else if (section.equalsIgnoreCase("comment") || section.contains("comment") || section.contains("Comment"))
					comments.add(objS);
				else {
					for (String ss : sections) {
						if (ss.equalsIgnoreCase(section) && obj != null)
							BeanUtils.setProperty(assay, ss, obj);
					}
				}
			}
		}
		assay.setDescription(descriptions);
		assay.setProtocol(protocols);
		assay.setComment(comments);

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

	public List<CategorizedComment> getCategorizedComments(ExcelTableModel tableModel) throws IllegalAccessException,
			InvocationTargetException {
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		tableModel.setUseFirstRowAsColumnHeadings(true);

		List<CategorizedComment> comments = new ArrayList<CategorizedComment>();
		addCategorizedComments(tableModel, "Categorized Comments", comments);
		
		//check for BAO Categorized Comments sheet
		if (comments.size() == 0)
			addCategorizedComments(tableModel, "BAO Categorized Comments", comments);
		
		return comments;
	}

	private void addCategorizedComments(ExcelTableModel tableModel, String sheetName, List<CategorizedComment> comments) throws IllegalAccessException, InvocationTargetException {
		try {
			tableModel.setSheet(sheetName);
		} catch (Exception ex) {
			log.info("No " + sheetName);
			return;
		}
		Map<String, Integer> map = getColumnsMap(tableModel);
		comments.addAll(getCategorizedComments(tableModel, map, new String[] { "commentTag", "commentValue" }));
	}

	private List<CategorizedComment> getCategorizedComments(ExcelTableModel tableModel, Map<String, Integer> map, String[] properties)
			throws IllegalAccessException, InvocationTargetException {
		List<CategorizedComment> comments = new ArrayList<CategorizedComment>();
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			if (checkIfRowBlank(tableModel, row) == false) {
				CategorizedComment comment = new CategorizedComment();
				for (String property : properties) {
					Object obj = tableModel.getValueAt(row, map.get(property));
					if (obj != null && obj.getClass().equals(new Date().getClass())) {
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
						String date = dateFormat.format(obj);
						obj = date;
					}
					if (obj != null && !"".equals(obj))
						BeanUtils.setProperty(comment, property, obj);
				}
				if (null != comment.getCommentValue() && !comment.getCommentValue().equals(""))
					comments.add(comment);
			}
		}
		return comments;
	}

	public List<Panel> getPanelValues(ExcelTableModel tableModel) throws IllegalAccessException, InvocationTargetException {
		List<Panel> panelValues = new ArrayList<Panel>();
		try {
			tableModel.setSheet("Panel");
		} catch (Exception ex) {
			log.info("No panel sheet.");
			return panelValues;
		}
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		tableModel.setUseFirstRowAsColumnHeadings(true);
		Map<String, Integer> map = getColumnsMap(tableModel);
		panelValues = getPanelValues(tableModel, map, new String[] { "panelName", "panelTargetGi", "panelTargetName", "panelGene",
				"panelTargetType", "panelTaxonomy" });
		return panelValues;
	}

	public List<Panel> getPanelValues(ExcelTableModel tableModel, Map<String, Integer> map, String[] properties)
			throws IllegalAccessException, InvocationTargetException {
		List<Panel> panelValues = new ArrayList<Panel>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			if (checkIfRowBlank(tableModel, row) == false) {
				Panel panelValue = new Panel();
				List<PanelTarget> panelTargets = new ArrayList<PanelTarget>();
				List<Object> panelTargetGis = new ArrayList<Object>();
				List<Object> panelTargetTypes = new ArrayList<Object>();
				List<Object> panelTargetNames = new ArrayList<Object>();
				for (String property : properties) {
					if (null == map.get(property) && (property.equalsIgnoreCase("panelTargetName")))
						continue;
					Object obj = tableModel.getValueAt(row, map.get(property));

					if (obj != null && !"".equals(obj)) {
						String objSt = obj.toString();
						if (!property.equals("panelName")) {
							if (objSt.contains("|")) {
								if (!property.equalsIgnoreCase("panelTargetName"))
									objSt = objSt.replace(" ", "");
								String[] listObj = objSt.split("\\|");
								if (property.equalsIgnoreCase("panelGene") || property.equalsIgnoreCase("panelTaxonomy")) {
									List<Integer> listO = new ArrayList<Integer>();
									for (String ss : listObj) {
										Double objDoub = Double.parseDouble(ss);
										listO.add(objDoub.intValue());
									}
									obj = listO;
								} else
									obj = new ArrayList<String>(Arrays.asList(listObj));
							} else {
								if (property.equalsIgnoreCase("panelGene") || property.equalsIgnoreCase("panelTaxonomy")) {
									Double objDoub = Double.parseDouble(objSt);
									obj = new ArrayList<Integer>(Arrays.asList(new Integer[] { objDoub.intValue() }));
								} else
									obj = new ArrayList<Object>(Arrays.asList(new Object[] { objSt }));
							}
						}
						if (property.equalsIgnoreCase("panelTargetGi"))
							panelTargetGis = (List<Object>) obj;
						else if (property.equalsIgnoreCase("panelTargetType"))
							panelTargetTypes = (List<Object>) obj;
						else if (property.equals("panelTargetName"))
							panelTargetNames = (List<Object>) obj;
						else
							BeanUtils.setProperty(panelValue, property, obj);
					}
				}
				if (panelTargetGis.size() > 0 && panelTargetTypes.size() > 0) {
					for (int ii = 0; ii < panelTargetGis.size(); ii++) {
						PanelTarget target = new PanelTarget();
						Object gi = panelTargetGis.get(ii);
						Double giD = Double.parseDouble((String) gi);
						target.setPanelTargetGi(giD.intValue());
						if (panelTargetTypes.size() == panelTargetGis.size())
							BeanUtils.setProperty(target, "panelTargetType", panelTargetTypes.get(ii));
						else
							BeanUtils.setProperty(target, "panelTargetType", panelTargetTypes.get(0));

						panelTargets.add(target);
					}
					panelValue.setPanelTarget(panelTargets);
				}

				panelValues.add(panelValue);
			}
		}
		return panelValues;
	}

	public List<ResultTid> getTidValues(ExcelTableModel tableModel) throws IllegalAccessException, InvocationTargetException {
		List<ResultTid> tidValues = new ArrayList<ResultTid>();
		try{
		tableModel.setSheet("TIDs");
		}catch (Exception ex) {
			log.info("No TIDs sheet.");
			return tidValues;
		}
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		tableModel.setUseFirstRowAsColumnHeadings(true);
		Map<String, Integer> map = getColumnsMap(tableModel);
		tidValues = getTidValues(tableModel, map, new String[] { "tidName", "tidDescription", "tidType", "tidUnit",
				"tidConcentration", "tidPlot", "tidPanelNum", "tidPanelReadout", "isActiveConcentration" });
		return tidValues;
	}

	public List<ResultTid> getTidValues(ExcelTableModel tableModel, Map<String, Integer> map, String[] properties)
			throws IllegalAccessException, InvocationTargetException {
		List<ResultTid> tidValues = new ArrayList<ResultTid>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			if (checkIfRowBlank(tableModel, row) == false) {
				ResultTid tidValue = new ResultTid();
				for (String property : properties) {
					if (null == map.get(property) && (property.equalsIgnoreCase("isActiveConcentration")))
						continue;
					Object obj = tableModel.getValueAt(row, map.get(property));
					if (obj != null && !"".equals(obj))
						BeanUtils.setProperty(tidValue, property, obj);
				}
				tidValues.add(tidValue);
			}
		}
		return tidValues;
	}

	public Set<Xref> getXrefs(ExcelTableModel tableModel) throws IllegalAccessException, InvocationTargetException {
		Set<Xref> xrefs = new LinkedHashSet<Xref>();
		try{
			tableModel.setSheet("Xrefs");
		}catch (Exception ex) {
			log.info("No Xrefs sheet.");
			return xrefs;
		}
		tableModel.setValueType(ExcelTableModel.ValueType.ACTUAL);
		tableModel.setUseFirstRowAsColumnHeadings(true);

		Map<String, Integer> map = getColumnsMap(tableModel);
		xrefs = getXrefs(tableModel, map,
				new String[] { "xrefType", "xrefValue", "xrefComment", "isTarget", "isPrimaryCitation" });
		return xrefs;
	}

	public Set<Xref> getXrefs(ExcelTableModel tableModel, Map<String, Integer> map, String[] properties) throws IllegalAccessException,
			InvocationTargetException {
		Set<Xref> xrefs = new LinkedHashSet<Xref>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			if (checkIfRowBlank(tableModel, row) == false) {
				Xref xref = new Xref();
				for (String property : properties) {
					if (null == map.get(property)
							&& (property.equalsIgnoreCase("isPrimaryCitation") || property.equalsIgnoreCase("isTarget")))
						continue;
					Object obj = tableModel.getValueAt(row, map.get(property));
					if (obj != null && !"".equals(obj))
						BeanUtils.setProperty(xref, property, obj);
					if (property.equals("isTarget") && obj == null)
						BeanUtils.setProperty(xref, "isTarget", false);
					if (property.equals("isPrimaryCitation") && obj == null)
						BeanUtils.setProperty(xref, "isPrimaryCitation", false);
				}
				xrefs.add(xref);
			}
		}
		return xrefs;
	}

	public boolean checkIfRowBlank(ExcelTableModel model, int row) {
		boolean bool = false;
		Integer count = 0;
		for (int ii = 0; ii < model.getColumnCount(); ii++) {
			Object value = model.getCellValueAt(row, ii);
			if (value == null || value == "")
				count = count + 1;
		}
		if (count == model.getColumnCount())
			bool = true;
		return bool;
	}

}
