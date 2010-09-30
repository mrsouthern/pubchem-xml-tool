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
public class Target {
	
	private String type, name, xmlTargetType;
	private Integer id, xmlTargetValue, taxonomy, omim;
	private Collection<Long> genes, taxonomies, omims;
	private Gene gene;
	private boolean isAssayTarget;
	


	public Target(){
		
	}
	
	public Target(Integer id, String type) {
		setId(id);
		setType(type);
	}
	
	public boolean isAssayTarget() {
		return isAssayTarget;
	}

	public void setAssayTarget(boolean isAssayTarget) {
		this.isAssayTarget = isAssayTarget;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Gene getGene() {
		return gene;
	}

	public void setGene(Gene gene) {
		this.gene = gene;
	}

	public Integer getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(Integer taxonomy) {
		this.taxonomy = taxonomy;
	}

	public Integer getOmim() {
		return omim;
	}

	public void setOmim(Integer omim) {
		this.omim = omim;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;

	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setXMLTargetValue(Integer xmlTargetValue) {
		this.xmlTargetValue = xmlTargetValue;
		if(xmlTargetValue.equals(1))
			xmlTargetType = "protein";
		if(xmlTargetValue.equals(2))
			xmlTargetType = "dna";
		else if(xmlTargetValue.equals(3))
			xmlTargetType = "rna";
	}
	
	public Integer getXMLTargetValue() {
		return xmlTargetValue;
	}
	
	public String getXMLTargetType(){
		return xmlTargetType;
	}

	public void setGenes(Collection<Long> genes) {
		this.genes = genes;
	}

	public Collection<Long> getGenes() {
		return genes;
	}

	public void setTaxonomies(Collection<Long> taxonomies) {
		this.taxonomies = taxonomies;
	}

	public Collection<Long> getTaxonomies() {
		return taxonomies;
	}

	public void setOmims(Collection<Long> omims) {
		this.omims = omims;
	}

	public Collection<Long> getOmims() {
		return omims;
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
		Target other = (Target) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	

}
