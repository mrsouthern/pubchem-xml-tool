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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.PubChemXMLDoc;



/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class XMLExtractorController {
	
	private static final Logger logger = LoggerFactory.getLogger(XMLExtractorController.class);
	
	public void extractPubChemXML(InputStream inputStream, OutputStream outputStream) throws Exception {
		
			Document doc = new PubChemXMLDoc().loadPubChemXML(inputStream);
			
			URL url = getClass().getClassLoader().getResource("ExcelTemplate.xlsx");
			logger.info("Loading Excel template");
			ExcelTableModel model = ExcelTableModel.load(url.openStream(), true);

			ResultTidExtractor rte = new ResultTidExtractor();
			PanelExtractor pe = new PanelExtractor();
			XrefExtractor xe = new XrefExtractor();
			AssayExtractor ae = new AssayExtractor();
			
			logger.info("Filling Excel Table Model");
			rte.fillTidExcelTemplate(model, rte.getTidValuesFromXML(doc));
			pe.fillPanelExcelTemplate(model, pe.getPanelValuesFromXML(doc));
			xe.fillXrefExcelTemplate(model, xe.getXrefValuesFromXML(doc));
			ae.fillAssayExcelTemplate(model, ae.getAssayValuesFromXML(doc));
			
			logger.info("Writing to Output: " + outputStream.toString());
			model.getWorkbook().write(outputStream);
			outputStream.close();
		
	}


}
