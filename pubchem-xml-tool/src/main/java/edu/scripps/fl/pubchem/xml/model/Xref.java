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
	private Object xrefValue;
	private String xrefComment;
	
	public Object getXrefValue() {
		return xrefValue;
	}

	public void setXrefValue(Object xrefValue) {
		this.xrefValue = xrefValue;
	}

	public String getXrefType() {
		return xrefType;
	}

	public void setXrefType(String xrefType) {
		this.xrefType = xrefType;
	}

	public String getXrefComment() {
		return xrefComment;
	}

	public void setXrefComment(String xrefComment) {
		this.xrefComment = xrefComment;
	}

}
