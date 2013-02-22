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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.Node;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.PubChemXMLUtils;
import edu.scripps.fl.pubchem.xml.model.CategorizedComment;

public class CategorizedCommentExtractor {
	
	public void fillCategorizedCommentExcelTemplate(ExcelTableModel model, List<CategorizedComment> cComments) throws Exception{
		model.setSheet("Categorized Comments");
		Map<Integer, String> map = new XMLExtractor().getColumnsMap(model);
		String[] categorizedCommentsProps = {"commentTag", "commentValue"};
		for(int ii = 0; ii <= cComments.size() -1; ii++	){
			CategorizedComment cComment = cComments.get(ii);
			for(String property: categorizedCommentsProps){
				for( int jj= 0; jj<= map.size()-1; jj++){
					if(property.equalsIgnoreCase(map.get(jj)))
						model.setValueAt(BeanUtils.getProperty(cComment, property), ii, jj);
				}
			}
		}
	}
	
	public List<CategorizedComment> getCategorizedCommentsFromXML(Document doc) throws Exception{
		List<CategorizedComment> comments = new ArrayList<CategorizedComment>();
		List<Node> nodes = doc.selectNodes("//PC-AssayDescription_categorized-comment/PC-CategorizedComment");
		PubChemXMLUtils utils = new PubChemXMLUtils();
		for(Node nn: nodes){
			CategorizedComment comment = new CategorizedComment();
			comment.setCommentTag(nn.selectSingleNode("PC-CategorizedComment_title").getText());
			List<Node> valueNodes = nn.selectNodes("PC-CategorizedComment_comment/PC-CategorizedComment_comment_E");
			String text = "";
			for(Node valeuNode : valueNodes){
				if (null != valeuNode.getText())
					text = text + valeuNode.getText() + "\n";
			}
			comment.setCommentValue(text);
			comments.add(comment);
		}
		return comments;
	}

}
