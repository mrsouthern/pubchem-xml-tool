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
public class Xref {

	private String xrefType;
	private Integer xrefTargetValue;
	private String xrefTargetType;
	private Object xrefValue;
	private String xrefComment;
	private Boolean isTarget;
	private Boolean isPrimaryCitation;
	


	public Boolean getIsPrimaryCitation() {
		return isPrimaryCitation;
	}

	public Boolean getIsTarget() {
		return isTarget;
	}

	public String getXrefComment() {
		return xrefComment;
	}

	public String getXrefTargetType() {
		return xrefTargetType;
	}

	public Integer getXrefTargetValue() {
		return xrefTargetValue;
	}

	public String getXrefType() {
		return xrefType;
	}

	public Object getXrefValue() {
		return xrefValue;
	}

	public void setIsPrimaryCitation(Boolean isPrimaryCitation) {
		this.isPrimaryCitation = isPrimaryCitation;
	}

	public void setIsTarget(Boolean isTarget) {
		this.isTarget = isTarget;
	}

	public void setXrefComment(String xrefComment) {
		this.xrefComment = xrefComment;
	}

	public void setXrefTargetType(String xrefTargetType) {
		this.xrefTargetType = xrefTargetType;
	}

	public void setXrefTargetValue(Integer xrefTypeValue) {
		this.xrefTargetValue = xrefTypeValue;
		if(this.xrefTargetValue == 1)
			this.xrefType = "protein";
		else if(this.xrefTargetValue == 2 | this.xrefTargetValue == 3)
			this.xrefType = "nucleotide";
		else if(this.xrefTargetValue == 4)
			this.xrefType = "gene";
		else if(this.xrefTargetValue == 5)
			this.xrefType = "biosystems id";
			
	}

	public void setXrefType(String xrefType) {
		this.xrefType = xrefType;
		
		if(xrefType.equalsIgnoreCase("protein")){
			this.xrefTargetValue = 1;
			this.xrefTargetType = "protein";
		}
	}

	public void setXrefValue(Object xrefValue) {
		this.xrefValue = xrefValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((xrefType == null) ? 0 : xrefType.hashCode());
		result = prime * result + ((xrefValue == null) ? 0 : xrefValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Xref other = (Xref) obj;
		if (xrefType == null) {
			if (other.xrefType != null)
				return false;
		} else if (!xrefType.equals(other.xrefType))
			return false;
		if (xrefValue == null) {
			if (other.xrefValue != null)
				return false;
		} else if (!xrefValue.equals(other.xrefValue))
			return false;
		return true;
	}



}
