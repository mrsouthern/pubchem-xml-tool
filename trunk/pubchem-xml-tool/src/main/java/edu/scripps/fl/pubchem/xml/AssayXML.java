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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.xml.model.PubChemAssay;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class AssayXML {
	
	public static String rootString = "/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription",
						aid = "PC-AssayDescription_aid",					
						source = "//PC-AssayDescription_aid-source/PC-Source/PC-Source_db/PC-DBTracking",
						dateNode = "PC-DBTracking_date/Date/Date_std/Date-std",
						yearElement = "Date-std_year",
						monthElement = "Date-std_month",
						dayElement = "Date-std_day",
						description = "PC-AssayDescription_description",
						protocol = "PC-AssayDescription_protocol",
						comment = "PC-AssayDescription_comment",
						grantNumber = "PC-AssayDescription_grant-number",
						activityOutcome = "PC-AssayDescription_activity-outcome-method",
						projectCategory = "PC-AssayDescription_project-category";
	
	public void buildAssayDocument(Document document, PubChemAssay assay) throws Exception {
		PubChemXMLUtils utils = new PubChemXMLUtils();
		
		Element root = (Element) document.selectSingleNode(rootString);
		
		if(assay.getAid() != null){
			Element element = (Element) root.selectSingleNode(aid);
			if(element == null)
				element = root.addElement(aid);	
			else
				element = (Element) element.selectSingleNode("PC-ID/PC-ID_id");
			if(element == null){
				element = element.addElement("PC-ID");
				element.addElement("PC-ID_id").addText("" + assay.getAid());
			}
			else
				element.setText("" + assay.getAid());
		}
		
		Element sourceNode = (Element) root.selectSingleNode(source);
		if(assay.getSource() != null)
			utils.add("PC-DBTracking_name", assay.getSource(), sourceNode);
		sourceNode = (Element) sourceNode.selectSingleNode("PC-DBTracking_source-id/Object-id");
		if(assay.getExternalRegId() != null)
			utils.add("Object-id_str", assay.getExternalRegId(), sourceNode);
		if(assay.getName() != null)
			utils.add("PC-AssayDescription_name", assay.getName(), root);
		if(assay.getDescription() != null)
			utils.add_E(description, assay.getDescription(), root);
		if(assay.getProtocol() != null)
			utils.add_E(protocol, assay.getProtocol(), root);
		if(assay.getComment() != null)
			utils.add_E(comment, assay.getComment(), root);
		if(assay.getGrantNumber() != null)
			utils.add_E(grantNumber, assay.getGrantNumber(), root);
		if(assay.getActivityOutcomeMethod()!= null)
			utils.attributeAndTextAdd(activityOutcome, assay.getActivityOutcomeMethod(), assay.getActivityOutcomeMethodValue().toString(), root);
		if(assay.getProjectCategory() != null)
			utils.attributeAndTextAdd(projectCategory, assay.getProjectCategory(), assay.getProjectCategoryValue().toString(), root);

		Date date = assay.getHoldUntilDate();
		if (date != null) {
			Element element = (Element) document.selectSingleNode(source);
			if (element != null) {
				String day = new SimpleDateFormat("dd").format(date);
				String month = new SimpleDateFormat("MM").format(date);
				String year = new SimpleDateFormat("yyyy").format(date);
				Node node = element.selectSingleNode(dateNode);
				if(node == null){
					Element child = element.addElement("PC-DBTracking_date");
					child = child.addElement("Date");
					child = child.addElement("Date_std");
					child = child.addElement("Date-std");
					child.addElement(yearElement);
					child.addElement(monthElement);
					child.addElement(dayElement);
				}
				element.selectSingleNode(dateNode + "/" + yearElement).setText(year);
				element.selectSingleNode(dateNode +"/" + monthElement).setText(month);
				element.selectSingleNode(dateNode + "/" + dayElement).setText(day);
			}
		}
	}
	

	


}
