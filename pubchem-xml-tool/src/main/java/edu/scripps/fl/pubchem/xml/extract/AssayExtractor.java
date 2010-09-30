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
import org.apache.poi.ss.usermodel.Sheet;
import org.dom4j.Document;
import org.dom4j.Node;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.model.Assay;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class AssayExtractor {
	
	public void fillAssayExcelTemplate(ExcelTableModel model, Assay assay) {
		model.setSheet("Assay");
		model.setUseFirstRowAsColumnHeadings(false);
		String description = "description";
		String protocol = "protocol";
		String comment = "comment";
		String aid = "aid";
		String grantNum = "grant number";
		String activityOutcome = "activity outcome method";
		String projectCateg	= "project category";
		String holdDate = "hold until date";
		String source = "source";
		for(int ii = 0; ii < model.getRowCount(); ii++){
			String section = model.getValueAt(ii, 0).toString();
			if(description.equalsIgnoreCase(section))
					model.setValueAt(assay.getDescription(), ii, 1);
			else if(protocol.equalsIgnoreCase(section))
				model.setValueAt(assay.getProtocol(), ii, 1);
			else if(comment.equalsIgnoreCase(section))
				model.setValueAt(assay.getComment(), ii , 1);
			else if(aid.equalsIgnoreCase(section))
				model.setValueAt(assay.getAid(), ii, 1);
			else if(grantNum.equalsIgnoreCase(section))
				model.setValueAt(assay.getGrantNumber(), ii, 1);
			else if(activityOutcome.equalsIgnoreCase(section))
				model.setValueAt(assay.getActivityOutcomeMethod(), ii, 1);
			else if(projectCateg.equalsIgnoreCase(section))
				model.setValueAt(assay.getProjectCategory(), ii, 1);
			else if(holdDate.equalsIgnoreCase(section))
				model.setValueAt(assay.getHoldUntilDate(), ii, 1);
			else if(source.equalsIgnoreCase(section))
				model.setValueAt(assay.getSource(), ii, 1);
		}
//		Sheet sheet = model.getSheet();
//		for(int ii = 0; ii <= model.getColumnCount(); ii++)
//			sheet.autoSizeColumn(ii);
	}
	
	protected Assay getAssayValuesFromXML(Document doc) throws ParseException, IllegalAccessException, InvocationTargetException {
		Assay assay = new Assay();
		
		getElementText(assay, "//PC-AssayDescription_description_E", doc, "description");
		getElementText(assay, "//PC-AssayDescription_protocol_E", doc, "protocol");
		getElementText(assay, "//PC-AssayDescription_comment_E", doc, "comment");
		getElementText(assay, "//PC-ID_id", doc, "aid");
		getElementText(assay, "//PC-AssayDescription_grant-number_E", doc, "grantNumber");
		getElementText(assay, "//PC-DBTracking_name", doc, "source");
	
		String attribute = getElementAttribute("//PC-AssayDescription_activity-outcome-method", doc);
		if(! attribute.equals(""))
			assay.setActivityOutcomeMethod(attribute);
		attribute = getElementAttribute("//PC-AssayDescription_project-category", doc);
		if(! attribute.equals(""))
			assay.setProjectCategory(attribute);		
		Node node = doc.selectSingleNode("//PC-DBTracking_date/Date/Date_std/Date-std");
		if(node != null)
			assay.setHoldUntilDate(new SimpleDateFormat("MM/dd/yyyy").parse(node.selectSingleNode("Date-std_month").getText() + "/" + node.selectSingleNode("Date-std_day").getText() + "/" + node.selectSingleNode("Date-std_year").getText()));
		
		return assay;
	}
	
	protected void getElementText(Assay assay, String nodeName, Document doc, String property) throws IllegalAccessException, InvocationTargetException {
		List<Node> nodes = doc.selectNodes(nodeName);
		String text = "";
		for(Node nn : nodes){
			if (null != nn.getText())
				text = text + nn.getText() + "\n";
		}
		BeanUtils.setProperty(assay, property, text);
	}
	
	protected String getElementAttribute(String elementName, Document doc){
		Node node = doc.selectSingleNode(elementName);
		String attribute;
		if(null == node)
			attribute = "";
		else
			attribute = node.valueOf("@value");
		if(null == attribute)
			attribute = "";
		return attribute;
	}

}
