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
package edu.scripps.fl.pubchem.xml.model;

import java.util.List;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class Panel {

	private String panelName = "";

	private List<Integer> panelGene;
	private List<Integer> panelTaxonomy;
	private List<PanelTarget> panelTarget;
	public String getPanelName() {
		return panelName;
	}
	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}
	public List<Integer> getPanelGene() {
		return panelGene;
	}
	public void setPanelGene(List<Integer> panelGene) {
		this.panelGene = panelGene;
	}
	public List<Integer> getPanelTaxonomy() {
		return panelTaxonomy;
	}
	public void setPanelTaxonomy(List<Integer> panelTaxonomy) {
		this.panelTaxonomy = panelTaxonomy;
	}
	public List<PanelTarget> getPanelTarget() {
		return panelTarget;
	}
	public void setPanelTarget(List<PanelTarget> panelTarget) {
		this.panelTarget = panelTarget;
	}



}
