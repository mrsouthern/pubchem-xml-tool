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

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class Panel {
	
	private String panelName = "";
	private Integer panelTargetGi;
	private String panelProteinName;
	private String panelTargetType;
	private Integer panelGene;
	private Integer panelTaxonomy;
	private Integer panelTargetTypeValue;
	public static final String protein = "protein";
	public static final String DNA = "DNA";
	public static final String RNA = "RNA";
	public static final String otherBioPolymer = "other-biopolymer";
	
	public Integer getPanelGene() {
		return panelGene;
	}

	public String getPanelName() {
		return panelName;
	}

	public String getPanelProteinName() {
		return panelProteinName;
	}

	public Integer getPanelTargetGi() {
		return panelTargetGi;
	}

	public String getPanelTargetType() {
		return panelTargetType;
	}

	public Integer getPanelTargetTypeValue() {
		return panelTargetTypeValue;
	}

	public Integer getPanelTaxonomy() {
		return panelTaxonomy;
	}
	
	public void setPanelGene(Integer panelGene) {
		this.panelGene = panelGene;
	}
	
	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}
	public void setPanelProteinName(String panelProteinName) {
		this.panelProteinName = panelProteinName;
	}
	
	public void setPanelTargetGi(Integer panelTargetGi) {
		this.panelTargetGi = panelTargetGi;
	}
	public void setPanelTargetType(String panelTargetType) {
		
		if(protein.equalsIgnoreCase(panelTargetType)){
			this.panelTargetTypeValue = 1;
		}
		else if(DNA.equalsIgnoreCase(panelTargetType)){
			this.panelTargetTypeValue = 2;
		}
		else if(RNA.equalsIgnoreCase(panelTargetType)){
			this.panelTargetTypeValue = 3;
		}
		else if(otherBioPolymer.equalsIgnoreCase(panelTargetType)){
			this.panelTargetTypeValue = 4;
		}
		else{
			throw new UnsupportedOperationException("Unknown target type" + panelTargetType);
		}
		
		this.panelTargetType = panelTargetType;
	}
	public void setPanelTaxonomy(Integer panelTaxonomy) {
		this.panelTaxonomy = panelTaxonomy;
	}

}
