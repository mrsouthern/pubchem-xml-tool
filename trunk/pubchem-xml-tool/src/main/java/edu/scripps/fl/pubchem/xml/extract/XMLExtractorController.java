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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.PubChemXMLDoc;
import edu.scripps.fl.pubchem.xml.model.Panel;



/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class XMLExtractorController {
	
	private static final Logger logger = LoggerFactory.getLogger(XMLExtractorController.class);
	
	public File extractPubChemXML(URL template, InputStream inputStream) throws Exception {
		
		Document doc = new PubChemXMLDoc().loadPubChemXML(inputStream);
		
//		URL url = getClass().getClassLoader().getResource("ExcelTemplate.xlsx");
//		logger.info("Loading Excel template");
		ExcelTableModel model = ExcelTableModel.load(template.openStream(), true);

		ResultTidExtractor rte = new ResultTidExtractor();
		PanelExtractor pe = new PanelExtractor();
		XrefExtractor xe = new XrefExtractor();
		CategorizedCommentExtractor ce = new CategorizedCommentExtractor();
		AssayExtractor ae = new AssayExtractor();
		
		
		logger.info("Filling Excel Table Model");
		rte.fillTidExcelTemplate(model, rte.getTidValuesFromXML(doc));
		List<Panel> panel = pe.getPanelValuesFromXML(doc);
		pe.fillPanelExcelTemplate(model, panel);
		xe.fillXrefExcelTemplate(model, xe.getXrefValuesFromXML(doc, panel));
		ce.fillCategorizedCommentExcelTemplate(model, ce.getCategorizedCommentsFromXML(doc));
		ae.fillAssayExcelTemplate(model, ae.getAssayValuesFromXML(doc));
		
		File output= null;
		String aid = "";
		if(null != doc.selectSingleNode("//PC-ID_id"))
			aid = doc.selectSingleNode("//PC-ID_id").getText();
		String extRegId = "";
		if(null != doc.selectSingleNode("//Object-id_str"))
			extRegId = doc.selectSingleNode("//Object-id_str").getText();
		
		//replace illegal file name characters with an underscore
		extRegId = extRegId.replaceAll(":", "_");
		extRegId = extRegId.replaceAll("/", "_");
		Log.info(extRegId);
		
		output = File.createTempFile("AID_" + aid + "_" + extRegId + "_", ".xlsx");
		output.deleteOnExit();
		OutputStream outputStream = new FileOutputStream(output);
		model.getWorkbook().write(outputStream);
		outputStream.close();
		
	return output;
}


}
