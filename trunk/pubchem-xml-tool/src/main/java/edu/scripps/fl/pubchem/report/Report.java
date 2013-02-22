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
package edu.scripps.fl.pubchem.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.PubChemAssayFactory;
import edu.scripps.fl.pubchem.PubChemDeposition;
import edu.scripps.fl.pubchem.web.PCOutcomeCounts;
import edu.scripps.fl.pubchem.web.session.PCDepositionSystemSession;
import edu.scripps.fl.pubchem.xml.PopulateArray;
import edu.scripps.fl.pubchem.xml.model.CategorizedComment;
import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class Report {

	private static final Logger logger = LoggerFactory.getLogger(Report.class);

	public static ArrayList<PubChemAssay> getBeanCollection(ExcelTableModel model) throws Exception {
		PopulateArray pa = new PopulateArray();
		ArrayList<PubChemAssay> pubChemAssay = new ArrayList<PubChemAssay>();
		Set<Xref> xrefs = (Set<Xref>) pa.getXrefs(model);
		ArrayList<ResultTid> tids = (ArrayList<ResultTid>) pa.getTidValues(model);
		ArrayList<Panel> panels = (ArrayList<Panel>) pa.getPanelValues(model);
		ArrayList<CategorizedComment> comments = (ArrayList<CategorizedComment>) pa.getCategorizedComments(model);
		PubChemAssay pca = pa.getAssayValues(model);
		PubChemAssayFactory factory = new PubChemAssayFactory();
		factory.setUpPubChemAssay(pca, tids, xrefs, panels, comments);
		factory.placeCitationsInDescription(pca, true);
		pubChemAssay.add(pca);
		logger.info("PubChemAssay has been set up.");

		return pubChemAssay;
	}

	public Map createParameterMap(ExcelTableModel model, PubChemDeposition pcDep) throws Exception {
		Integer aidNum = new PopulateArray().getAssayValues(model).getAid();
		Map<String, Integer> parameters = new HashMap();
		PCDepositionSystemSession session = pcDep.getSession();
		if (aidNum != null) {
			PCOutcomeCounts count = session.getSubstanceOutcomeCounts(aidNum);
			if (count == null && pcDep.isLoggedIn() == false) {
				session = pcDep.getLoggedInSession(aidNum);
				count = session.getSubstanceOutcomeCounts(aidNum);
			}
			if (count != null) {
				parameters.put("activeCount", count.active);
				parameters.put("totalCount", count.all);
				logger.info("Total and active counts have been retrieved.");
			}
		}
		return parameters;
	}

}
