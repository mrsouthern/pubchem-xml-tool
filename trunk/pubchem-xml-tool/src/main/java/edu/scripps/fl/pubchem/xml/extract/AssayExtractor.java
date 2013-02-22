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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.dom4j.Document;
import org.dom4j.Node;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.PubChemXMLUtils;
import edu.scripps.fl.pubchem.xml.model.Assay;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class AssayExtractor {
	
	public void fillAssayExcelTemplate(ExcelTableModel model, Assay assay) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		model.setSheet("Assay");
		model.setUseFirstRowAsColumnHeadings(false);
		String[] tags = {"AID", "Name", "External Reg Id", "Grant Number", "Project Category", "Hold Until Date", "Activity Outcome Method", "Source"};
		Integer count = tags.length;
		
		for(int ii=0; ii< count; ii++){
			model.setValueAt(tags[ii], ii,0);
			String property = StringUtils.uncapitalize(tags[ii].replaceAll(" ", ""));
			if(tags[ii].equals("AID"))
				property = property.toLowerCase();
			if(property.equals("holdUntilDate"))
				model.setValueAt(assay.getHoldUntilDate(), ii, 1);
			else{
				Object value = BeanUtils.getProperty(assay, property);
				model.setValueAt(value, ii, 1);
			}
		}
		List<String> descriptionS = assay.getDescription();
		putStringListInModel(model, descriptionS, count, "Description");
		
		List<String> protocolS = assay.getProtocol();
		count = count + descriptionS.size(); 
		putStringListInModel(model, protocolS, count, "Protocol");
		
		List<String> commentS = assay.getComment();
		count = count + protocolS.size(); 
		putStringListInModel(model, commentS, count, "Comment");
		
//		Sheet sheet = model.getSheet();
//		for(int ii = 0; ii <= model.getColumnCount(); ii++)
//			sheet.autoSizeColumn(ii);
	}
	
	private void putStringListInModel(ExcelTableModel model, List<String> strings, Integer count, String name){
		for(int hh = 0; hh< strings.size(); hh++){
			String ss = strings.get(hh);
			Integer row = hh + count;
			model.setValueAt(name, row, 0);
			model.setValueAt(ss, row, 1);
			Cell cell = model.getCellAt(row, 1);
			CellStyle style = cell.getCellStyle();
			style.setWrapText(true);
			cell.setCellStyle(style);
		}
	}
	
	public Assay getAssayValuesFromXML(Document doc) throws ParseException, IllegalAccessException, InvocationTargetException {
		Assay assay = new Assay();
		PubChemXMLUtils utils = new PubChemXMLUtils();
		
		utils.getElementText(assay, "//PC-DBTracking_source-id/Object-id/Object-id_str", doc, "externalRegId");
		utils.getElementText(assay, "//PC-AssayDescription_name", doc, "name");
		utils.getListofElementsText(assay, "//PC-AssayDescription_description_E", doc, "description");
		utils.getListofElementsText(assay, "//PC-AssayDescription_protocol_E", doc, "protocol");
		utils.getListofElementsText(assay, "//PC-AssayDescription_comment_E", doc, "comment");
		utils.getElementText(assay, "//PC-ID_id", doc, "aid");
		utils.getElementText(assay, "//PC-AssayDescription_grant-number_E", doc, "grantNumber");
		utils.getElementText(assay, "//PC-DBTracking_name", doc, "source");
	
		String attribute = utils.getElementAttribute("//PC-AssayDescription_activity-outcome-method", doc);
		if(! attribute.equals(""))
			assay.setActivityOutcomeMethod(attribute);
		attribute = utils.getElementAttribute("//PC-AssayDescription_project-category", doc);
		if(! attribute.equals(""))
			assay.setProjectCategory(attribute);		
		Node node = doc.selectSingleNode("//PC-DBTracking_date/Date/Date_std/Date-std");
		if(node != null)
			assay.setHoldUntilDate(new SimpleDateFormat("MM/dd/yyyy").parse(node.selectSingleNode("Date-std_month").getText() + "/" + node.selectSingleNode("Date-std_day").getText() + "/" + node.selectSingleNode("Date-std_year").getText()));
		
		return assay;
	}

}
