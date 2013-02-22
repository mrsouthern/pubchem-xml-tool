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

import java.util.Date;
import java.util.List;

public class Assay {
	
	private String name, externalRegId, grantNumber, 
					projectCategory, activityOutcomeMethod, source;
	
	private List<String> description, protocol, comment;

	private Integer aid, projectCategoryValue, activityOutcomeMethodValue;
	
	private Date holdUntilDate = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExternalRegId() {
		return externalRegId;
	}

	public void setExternalRegId(String externalRegId) {
		this.externalRegId = externalRegId;
	}
	
	public Integer getAid() {
		return aid;
	}

	public void setAid(Integer aid) {
		this.aid = aid;
	}
	
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getActivityOutcomeMethodValue() {
		return activityOutcomeMethodValue;
	}

	public void setActivityOutcomeMethodValue(Integer activityOutcomeMethodValue) {
		this.activityOutcomeMethodValue = activityOutcomeMethodValue;
	}

	public Integer getProjectCategoryValue() {
		return projectCategoryValue;
	}

	public void setProjectCategoryValue(Integer projectCategoryValue) {
		this.projectCategoryValue = projectCategoryValue;
	}

	public String getProjectCategory() {
		return projectCategory;
	}

	public void setProjectCategory(String projectCategory) {
		if(projectCategory.equals("MLSCN")){
			this.projectCategory = "mlscn";
			this.projectCategoryValue = 1;
		}
		else if(projectCategory.equals("MLPCN")){
			this.projectCategory = "mlpcn";
			this.projectCategoryValue = 2;
		}
		else if(projectCategory.equalsIgnoreCase("MLSCN (Assay Provider)")){
			this.projectCategory = "mlscn-ap";
			this.projectCategoryValue = 3;
		}
		else if(projectCategory.equalsIgnoreCase("MLPCN (Assay Provider)")){
			this.projectCategory = "mlpcn-ap";
			this.projectCategoryValue = 4;
		}
		else if(projectCategory.equalsIgnoreCase("Literature (Extracted)")){
			this.projectCategory = "literature-extracted";
			this.projectCategoryValue = 7;
		}
		else if(projectCategory.equalsIgnoreCase("Literature (Author)")){
			this.projectCategory = "literature-author";
			this.projectCategoryValue = 8;
		}
		else if(projectCategory.equalsIgnoreCase("Literature (Publisher)")){
			this.projectCategory = "literature-publisher";
			this.projectCategoryValue = 9;
		}
		else if(projectCategory.equalsIgnoreCase("RNAi Global Initiative")){
			this.projectCategory = "rnaigi";
			this.projectCategoryValue = 10;
		}
		else if(projectCategory.equalsIgnoreCase("Assay Vendor")){
			this.projectCategory = "assay-vendor";
			this.projectCategoryValue = 6;
		}
		else if(projectCategory.equals("Other")){
			this.projectCategory = "other";
			this.projectCategoryValue = 255;
		}
		else if(projectCategory.equals("mlscn"))
			this.projectCategory = "MLSCN";
		else if(projectCategory.equals("mlpcn"))
			this.projectCategory = "MLPCN";
		else if(projectCategory.equals("mlscn-ap"))
			this.projectCategory = "MLSCN (Assay Provider)";
		else if(projectCategory.equals("mlpcn-ap"))
			this.projectCategory = "MLPCN (Assay Provider)";
		else if(projectCategory.equals("literature-extracted"))
			this.projectCategory = "Literature (Extracted)";
		else if(projectCategory.equals("literature-author"))
			this.projectCategory = "Literature (Author)";
		else if(projectCategory.equals("literature-publisher"))
			this.projectCategory = "Literature (Publisher)";
		else if(projectCategory.equals("rnaigi"))
			this.projectCategory = "RNAi Global Initiative";
		else if(projectCategory.equals("assay-vendor"))
			this.projectCategory = "Assay Vendor";
		else if(projectCategory.equals("other")){
			this.projectCategory = "Other";
			this.projectCategoryValue = 255;
		}
		else
			throw new UnsupportedOperationException("Unknown Project Category: " + projectCategory);
	}

	public Date getHoldUntilDate() {
		return holdUntilDate;
	}

	public void setHoldUntilDate(Date holdUntilDate){
		this.holdUntilDate = holdUntilDate;
	}

	public String getActivityOutcomeMethod() {
		return activityOutcomeMethod;
	}

	public void setActivityOutcomeMethod(String activityOutcomeMethod) {
		this.activityOutcomeMethod = activityOutcomeMethod;
		if(activityOutcomeMethod.equalsIgnoreCase("other"))
			this.activityOutcomeMethodValue = 0;
		else if(activityOutcomeMethod.equalsIgnoreCase("screening"))
			this.activityOutcomeMethodValue = 1;
		else if(activityOutcomeMethod.equalsIgnoreCase("confirmatory"))
			this.activityOutcomeMethodValue = 2;
		else if(activityOutcomeMethod.equalsIgnoreCase("summary"))
			this.activityOutcomeMethodValue = 3;
		else
			throw new UnsupportedOperationException("Unknown Activity Outcome Method: " + activityOutcomeMethod);
	}

	public String getGrantNumber() {
		return grantNumber;
	}

	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}

	public List<String> getDescription() {
		return description;
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}

	public List<String> getProtocol() {
		return protocol;
	}

	public void setProtocol(List<String> protocol) {
		this.protocol = protocol;
	}

	public List<String> getComment() {
		return comment;
	}

	public void setComment(List<String> comment) {
		this.comment = comment;
	}

}
