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

import java.util.Map;

import javax.swing.table.TableModel;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.poi.ss.usermodel.Sheet;

import com.googlecode.exceltablemodel.ExcelTableModel;


/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class XMLExtractor {	
	
	public XMLExtractor(){
		
	}
	
	public Map<Integer, String> getColumnsMap(TableModel tableModel) {
		Map<Integer, String> map = new CaseInsensitiveMap();
		for (int ii = 0; ii < tableModel.getColumnCount(); ii++) {
			String name = tableModel.getColumnName(ii);
			if (null != name) { // excel sometimes adds null columns
				name = name.replaceAll("[-\\s+]", "");
				map.put(ii, name);
			}
		}
		return map;
	}
	
	public void autoSizeSheet(ExcelTableModel model){
		Sheet sheet = model.getSheet();
		for(int ii = 0; ii <= model.getColumnCount(); ii++)
			sheet.autoSizeColumn(ii);
	}
	

}
