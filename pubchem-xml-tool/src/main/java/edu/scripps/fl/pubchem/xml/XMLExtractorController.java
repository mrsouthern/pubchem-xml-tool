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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.io.DOMReader;

import com.googlecode.exceltablemodel.ExcelTableModel;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class XMLExtractorController {
	
//	public void extractPubChemXML(File xmlFile, File outputFile) throws Exception{
//		InputStream is = new FileInputStream(xmlFile);
//		if( xmlFile.getName().endsWith(".gz") ) {
//			is = new GZIPInputStream(is);
//		}
//		is = new BufferedInputStream(is);
//		extractPubChemXML(is, new BufferedOutputStream(new FileOutputStream(outputFile)));
//	}
	
	public void extractPubChemXML(InputStream inputStream, OutputStream outputStream) throws Exception{
		XMLExtractor ac = new XMLExtractor();

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		DOMReader reader = new DOMReader();
		org.w3c.dom.Document doc2 = builder.parse(inputStream);
		Document doc = reader.read(doc2);
		
		URL url = getClass().getClassLoader().getResource("ExcelTemplate.xlsx");
		ExcelTableModel model = ExcelTableModel.load(url.openStream(), true);

		ac.fillTidExcelTemplate(model, ac.getTidValuesFromXML(doc));
		ac.fillPanelExcelTemplate(model, ac.getPanelValuesFromXML(doc));
		ac.fillXrefExcelTemplate(model, ac.getXrefValuesFromXML(doc));

		model.getWorkbook().write(outputStream);
		outputStream.close();
	}
	

}
