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

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.xml.model.CategorizedComment;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;

public class CategorizedCommentXML {
	public static String rootString = "/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription";
	
	public void buildCategorizedCommentDocument(Document document, List<CategorizedComment> cComments){ 
		PubChemXMLUtils utils = new PubChemXMLUtils();
		if(cComments.size() > 0){
			Element rootElement = (Element) document.selectSingleNode(rootString);
			Element cCommentElement = (Element) rootElement.selectSingleNode("PC-AssayDescription_categorized-comment");
			
			if(cCommentElement != null){
				List<Node> nodes = cCommentElement.selectNodes("PC-CategorizedComment");
				for (Node nn : nodes)
					nn.detach();
			}
			else
				cCommentElement = rootElement.addElement("PC-AssayDescription_categorized-comment");
			
			for( CategorizedComment cc: cComments){
				Element element = cCommentElement.addElement("PC-CategorizedComment");
				element.addElement("PC-CategorizedComment_title").addText(cc.getCommentTag());
				utils.add_E("PC-CategorizedComment_comment", cc.getCommentValue(), element);
			}
		}
	}

}
