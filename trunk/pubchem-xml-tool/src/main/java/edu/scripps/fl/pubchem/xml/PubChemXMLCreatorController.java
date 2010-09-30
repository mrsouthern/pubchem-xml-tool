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
import java.io.InputStream;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.PubChemAssayFactory;
import edu.scripps.fl.pubchem.xml.model.*;


/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PubChemXMLCreatorController {

	private static final Logger logger = LoggerFactory.getLogger(PubChemXMLCreatorController.class);

	public PubChemAssay createPubChemXML(InputStream fileTemplate, File fileExcel, File fileXMLOutput) throws Exception {
		PubChemXMLDoc xmldoc = new PubChemXMLDoc();
		PopulateArray array = new PopulateArray();
		Document doc;
		doc = xmldoc.loadPubChemXML(fileTemplate);
		ExcelTableModel model = ExcelTableModel.load(fileExcel, true);
		
		PubChemAssay assay = array.getAssayValues(model);
		PubChemAssayFactory factory = new PubChemAssayFactory();
		factory.setUpPubChemAssay(assay, array.getTidValues(model), array.getXrefs(model), array.getPanelValues(model));
		factory.placeCitationsInDescription(assay, false);
		
		new AssayXML().buildAssayDocument(doc, assay);
		new ResultTidXML().buildTidDocument(doc, assay.getResultTids());
		new XrefXML().buildXrefDocument(doc, assay);
		new PanelXML().buildPanelDocument(doc, assay.getPanels());

		xmldoc.write(doc, fileXMLOutput);
		
		return assay;
	}
}
