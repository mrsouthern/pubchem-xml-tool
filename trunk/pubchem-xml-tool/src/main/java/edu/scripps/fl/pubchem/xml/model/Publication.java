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

import java.util.ArrayList;
import java.util.List;
/*
 * @author M Southern (southern at scripps dot edu)
 */
public class Publication {
	
	private Integer pubmedId;
	private List<String> authors = new ArrayList();
	private String title = "";
	private String journal = "";
	private String details = "";

	public Integer getPubmedId() {
		return pubmedId;
	}

	public void setPubmedId(Integer pubmedId) {
		this.pubmedId = pubmedId;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String citation() {
		return citation(false);
	}
	
	public String citation(boolean includePMID) {
		StringBuffer sb = new StringBuffer();
		for(int ii = 0; ii < authors.size() - 1; ii++ ) {
			sb.append(authors.get(ii));
			if( ii < authors.size() - 2 )
				sb.append("., ");
		}
		if( sb.length() > 0 )
			sb.append(". & ");
		sb.append(authors.get(authors.size() - 1));
		sb.append(".");
		sb.append(" ").append(getTitle());
		sb.append(" ").append(getJournal());
		sb.append(" ").append(getDetails());
		if( includePMID )
			sb.append(" PMID: ").append(getPubmedId());
		return sb.toString();
	}

}
