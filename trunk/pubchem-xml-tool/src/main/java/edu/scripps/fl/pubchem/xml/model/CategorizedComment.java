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

public class CategorizedComment {
	

	public CategorizedComment(){
		
	}
	
	
	private String commentTag, commentValue;

	public void setCommentTag(String tag) {
		this.commentTag = tag;
	}

	public String getCommentTag() {
		return commentTag;
	}

	public void setCommentValue(String value) {
		this.commentValue = value;
	}

	public String getCommentValue() {
		return commentValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commentTag == null) ? 0 : commentTag.hashCode());
		result = prime * result + ((commentValue == null) ? 0 : commentValue.hashCode());
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
		CategorizedComment other = (CategorizedComment) obj;
		if (commentTag == null) {
			if (other.commentTag != null)
				return false;
		}
		else if (!commentTag.equals(other.commentTag))
			return false;
		if (commentValue == null) {
			if (other.commentValue != null)
				return false;
		}
		else if (!commentValue.equals(other.commentValue))
			return false;
		return true;
	}

	public CategorizedComment(String commentTag, String commentValue) {
		this.commentTag = commentTag;
		this.commentValue = commentValue;
	}
}
