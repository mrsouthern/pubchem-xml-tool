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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.xml.model.ResultTid;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class ResultTidXML {
	
	public static String rootString = "/PC-AssayContainer/PC-AssaySubmit/PC-AssaySubmit_assay/PC-AssaySubmit_assay_descr/PC-AssayDescription",
								results = "PC-AssayDescription_results";
	
	
	public void addTidPlots(Document document, Integer numberPlots) {
		Element root = (Element) document.selectSingleNode(rootString);
		
		Node dr = root.selectSingleNode("PC-AssayDescription_dr");
		if (dr != null)
			dr.detach();

		Element tidPlotDR = root.addElement("PC-AssayDescription_dr");
		
		for (int ii = 1; ii <= numberPlots; ii++) {
			Element assayDRAttr = tidPlotDR.addElement("PC-AssayDRAttr");
			assayDRAttr.addElement("PC-AssayDRAttr_id").addText("" + ii);
			assayDRAttr.addElement("PC-AssayDRAttr_descr").addText("Response vs Concentration [" + ii + "]");
			assayDRAttr.addElement("PC-AssayDRAttr_dn").addText("Concentration");
			assayDRAttr.addElement("PC-AssayDRAttr_rn").addText("Response");
		}
	}
	
	
	public void buildTidDocument(Document document, List<ResultTid> tidValues) {
		Integer tidInt = decideTids(tidValues);
		if (tidInt > 0) {
			Integer numberPlots = getNumberPlots(tidValues);
			if (numberPlots > 1)
				addTidPlots(document, numberPlots);

			Element root = (Element) document.selectSingleNode(rootString + "/" + results);

			List<Node> nodes = root.selectNodes("PC-ResultType");
			for (Node n : nodes)
				n.detach();

			for (int tt = 0; tt < tidValues.size(); tt++) {
				ResultTid tidValue = tidValues.get(tt);
				Element resultType = root.addElement("PC-ResultType");
				resultType.addElement("PC-ResultType_tid").addText("" + (tt + 1));
				resultType.addElement("PC-ResultType_name").addText(tidValue.getTidName());

				String description = tidValue.getTidDescription();
				if (null != description)
					resultType.addElement("PC-ResultType_description").addElement("PC-ResultType_description_E").addText(description);
				String tidType = tidValue.getTidType();
				if (null != tidType)
					resultType.addElement("PC-ResultType_type").addAttribute("value", tidType).addText("" + tidValue.getTidTypeValue());
				String tidUnit = tidValue.getTidUnit();
				if (null != tidUnit & "" != tidUnit & tidUnit != "null" )
					resultType.addElement("PC-ResultType_unit").addAttribute("value", tidUnit).addText("" + tidValue.getTidUnitValue());
				
				Boolean isAC = tidValue.getIsActiveConcentration();
				if (null != isAC)
					resultType.addElement("PC-ResultType_ac").addAttribute("value", isAC.toString());
				
				Double concentration = tidValue.getTidConcentration();
				if (null != concentration) {
					Element tc = resultType.addElement("PC-ResultType_tc");
					Element concentrationattr = tc.addElement("PC-ConcentrationAttr");
					concentrationattr.addElement("PC-ConcentrationAttr_concentration").addText("" + concentration);
					concentrationattr.addElement("PC-ConcentrationAttr_unit").addAttribute("value", "um").addText("5");
					Integer plot = tidValue.getTidPlot();
					if (null != plot)
						concentrationattr.addElement("PC-ConcentrationAttr_dr-id").addText("" + plot);
				}
				Integer panelNum = tidValue.getTidPanelNum();
				if (null != panelNum) {
					Element panel = resultType.addElement("PC-ResultType_panel-info");
					Element passay = panel.addElement("PC-AssayPanelTestResult");
					passay.addElement("PC-AssayPanelTestResult_mid").addText("" + panelNum);
					passay.addElement("PC-AssayPanelTestResult_readout-annot").addAttribute("value", tidValue.getTidPanelReadout())
							.addText("" + tidValue.getTidPanelReadoutValue());
				}
			}
		}
	}
	
	public Integer decideTids(List<ResultTid> tids){
		Integer tidInt = 0;
		for(int ii = 0; ii <= tids.size()-1; ii++){
			ResultTid tid = tids.get(ii);
			if(null != tid.getTidName())
				tidInt = tidInt + 1;
		}
		return tidInt;
	}
	
	public Integer getNumberPlots(List<ResultTid> tidValues) {
		Set<Integer> plotNumbers = new HashSet<Integer>();

		for (int ii = 0; ii <= tidValues.size() - 1; ii++) {
			ResultTid tidValue = tidValues.get(ii);
			if (null != tidValue.getTidPlot())
				plotNumbers.add(tidValue.getTidPlot());
		}
		Integer numPlots = plotNumbers.size();
		return numPlots;
	}

}
