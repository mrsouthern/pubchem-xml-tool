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

import java.util.Collection;

import edu.scripps.fl.pubchem.EUtilsFactory;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class Gene {

	private Integer id;
	private Collection<Long> proteins, nucleotides, omims, taxonomies;
	private String name;
	private Target target;
	private Integer omim, taxonomy;
	
	
	public Gene(){

	}
	
	public Gene(Integer id){
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id){
		this.id = id;
		
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Gene other = (Gene) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void setProteins(Collection<Long> proteins) {
		this.proteins = proteins;
	}

	public Collection<Long> getProteins() {
		return proteins;
	}

	public void setNucleotides(Collection<Long> nucleotides) {
		this.nucleotides = nucleotides;
	}

	public Collection<Long> getNucleotides() {
		return nucleotides;
	}

	public void setTaxonomies(Collection<Long> taxonomies) {
		this.taxonomies = taxonomies;
	}

	public Collection<Long> getTaxonomies() {
		return taxonomies;
	}

	public void setOmim(Integer omim) {
		this.omim = omim;
	}

	public Integer getOmim() {
		return omim;
	}

	public void setTaxonomy(Integer taxonomy) {
		this.taxonomy = taxonomy;
	}

	public Integer getTaxonomy() {
		return taxonomy;
	}

	public void setOmims(Collection<Long> omims) {
		this.omims = omims;
	}

	public Collection<Long> getOmims() {
		return omims;
	}

}
