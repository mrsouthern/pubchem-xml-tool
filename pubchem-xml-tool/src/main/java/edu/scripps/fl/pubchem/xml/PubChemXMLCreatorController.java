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

import org.dom4j.Document;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PubChemXMLCreatorController {
	
	public void createPubChemXML(File fileTemplate, File fileExcel, File fileXMLOutput) throws Exception {
		PubChemXMLDoc xmldoc = new PubChemXMLDoc();
		
		Document doc = xmldoc.loadPubChemXML(fileTemplate);
		PopulateArray array = new PopulateArray();
		xmldoc.buildTidDocument(doc, array.getTidValues(fileExcel));
		xmldoc.buildXrefDocument(doc, array.getXrefs(fileExcel));
		xmldoc.buildPanelDocument(doc, array.getPanelValues(fileExcel));
		
		xmldoc.write(doc, fileXMLOutput);
	}
}
