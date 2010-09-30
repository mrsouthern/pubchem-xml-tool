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
public class ResultTid {
	
	private String tidName = "";
	private String tidDescription = "";
	private String tidType = "";
	private Integer tidTypeValue;
	private String tidUnit = "";
	private Integer tidUnitValue;
	private Double tidConcentration;
	private Integer tidPlot;
	private Integer tidPanelNum;
	private String tidPanelReadout;
	private Integer tidPanelReadoutValue;

	public Double getTidConcentration() {
		return tidConcentration;
	}

	public String getTidDescription() {
		return tidDescription;
	}

	public String getTidName() {
		return tidName;
	}

	public Integer getTidPanelNum() {
		return tidPanelNum;
	}

	public String getTidPanelReadout() {
		return tidPanelReadout;
	}

	public Integer getTidPanelReadoutValue() {
		return tidPanelReadoutValue;
	}
	public Integer getTidPlot() {
		return tidPlot;
	}
	public String getTidType() {
		return tidType;
	}
	public Integer getTidTypeValue() {
		return tidTypeValue;
	}
	public String getTidUnit() {
		return tidUnit;
	}
	public Integer getTidUnitValue() {
		return tidUnitValue;
	}
	
	public void setTidPlot(Integer tidPlot) {
		this.tidPlot = tidPlot;
	}
	public void setTidConcentration(Double tidConcentration) {
		this.tidConcentration = tidConcentration;
	}
	public void setTidDescription(String tidDescription) {
		this.tidDescription = tidDescription;
	}
	public void setTidName(String tidName) {
		this.tidName = tidName;
	}
	public void setTidPanelNum(Integer tidPanelNum) {
		this.tidPanelNum = tidPanelNum;
	}
	public void setTidPanelReadout(String tidPanelReadout) {
		if ("regular".equalsIgnoreCase(tidPanelReadout))
			this.tidPanelReadoutValue = 1;
		else if ("outcome".equalsIgnoreCase(tidPanelReadout))
			this.tidPanelReadoutValue = 2;
		else if ("score".equalsIgnoreCase(tidPanelReadout))
			this.tidPanelReadoutValue = 3;
		else if ("ac".equalsIgnoreCase(tidPanelReadout))
			this.tidPanelReadoutValue = 4;
		else 
			throw new UnsupportedOperationException("Unknown Panel Readout Type: " + tidPanelReadout);

		this.tidPanelReadout = tidPanelReadout.toLowerCase();
	}
	public void setTidType(String tidType) {
		if ("float".equalsIgnoreCase(tidType))
			this.tidTypeValue = 1;
		else if ("int".equalsIgnoreCase(tidType))
			this.tidTypeValue = 2;
		else if ("bool".equalsIgnoreCase(tidType))
			this.tidTypeValue = 3;
		else if ("string".equalsIgnoreCase(tidType))
			this.tidTypeValue = 4;
		else 
			throw new UnsupportedOperationException("Unknown Result Type: " + tidType);
		
		this.tidType = tidType.toLowerCase();
	}
	public void setTidUnit(String tidUnit) {

		if ("ppt".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 1;
		else if ("ppm".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 2;
		else if ("ppb".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 3;
		else if ("mm".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 4;
		else if ("um".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 5;
		else if ("nm".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 6;
		else if ("pm".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 7;
		else if ("fm".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 8;
		else if ("mgml".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 9;
		else if ("ugml".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 10;
		else if ("ngml".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 11;
		else if ("pgml".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 12;
		else if ("fgml".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 13;
		else if ("m".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 14;
		else if ("percent".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 15;
		else if ("ratio".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 16;
		else if ("sec".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 17;
		else if ("rsec".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 18;
		else if ("min".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 19;
		else if ("rmin".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 20;
		else if ("day".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 21;
		else if ("rday".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 22;
		else if ("none".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 254;
		else if ("unspecified".equalsIgnoreCase(tidUnit))
			this.tidUnitValue = 255;
		else
			throw new UnsupportedOperationException("Unknown Result Unit: " + tidUnit);

		this.tidUnit = tidUnit.toLowerCase();
	}

}
