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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
@SuppressWarnings("unchecked")
public class PubChemAssay extends Assay {
	private String message = "";
	private List<ResultTid> resultTids = new ArrayList<ResultTid>();
	private Set<Xref> xrefs = new HashSet<Xref>();
	private List<Xref> aids = new ArrayList<Xref>();
	private List<Xref> pmids = new ArrayList<Xref>();
	private List<Xref> nonPmidReferences = new ArrayList<Xref>();
	private Map<Integer, Xref> pmidIndices = new HashMap<Integer, Xref>();
	private List<Panel> panels = new ArrayList<Panel>();
	private List<CategorizedComment> categorizedComments = new ArrayList<CategorizedComment>();

	private List<Integer> taxonomyIDs = new ArrayList<Integer>();
	private List<Integer> omimIDs = new ArrayList<Integer>();

	private List<Target> targets = new ArrayList<Target>();
	private List<Gene> genes = new ArrayList<Gene>();
	private List<Publication> publications = new ArrayList<Publication>();

	private static Set numericXrefTypes = new HashSet(Arrays.asList("protein", "nucleotide", "gene", "omim", "taxonomy", "aid"));

	public PubChemAssay() {

	}

	public List<Xref> getAids() {
		return aids;
	}

	public List<Gene> getGenes() {
		return genes;
	}

	public String getMessage() {
		return message;
	}

	public Map<Integer, Xref> getPmidIndices() {
		return pmidIndices;
	}

	public List<Xref> getNonPmidReferences() {
		return nonPmidReferences;
	}

	public List<Integer> getOmimIDs() {
		return omimIDs;
	}

	public List<Panel> getPanels() {
		return panels;
	}

	public List<Xref> getPmids() {
		return pmids;
	}

	public List<Publication> getPublications() {
		return publications;
	}

	public List<ResultTid> getResultTids() {
		return resultTids;
	}

	public List<Target> getTargets() {
		return targets;
	}

	public List<Integer> getTaxonomyIDs() {
		return taxonomyIDs;
	}

	public Set<Xref> getXrefs() {
		return xrefs;
	}

	public void setAids(List<Xref> aids) {
		this.aids = aids;
	}

	public void setGenes(List<Gene> genes) {
		this.genes = genes;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPmidIndices(Map<Integer, Xref> pmidIndices) {
		this.pmidIndices = pmidIndices;
	}

	public void setNonPmidReferences(List<Xref> nonPmidReferences) {
		this.nonPmidReferences = nonPmidReferences;
	}

	public void setOmimIDs(List<Integer> omimIDs) {
		this.omimIDs = omimIDs;
	}

	public void setPanels(List<Panel> panels) {
		this.panels = panels;
		for (Panel xx : panels) {
			List<PanelTarget> panelTargets = xx.getPanelTarget();
			if (panelTargets != null && panelTargets.size() > 0) {
				for (PanelTarget panelTarget : panelTargets) {
					if (panelTarget.getPanelTargetType() != null) {
						String type = panelTarget.getPanelTargetType();
						if (type.equalsIgnoreCase("protein") || type.equalsIgnoreCase("dna") || type.equalsIgnoreCase("rna")
								|| type.equalsIgnoreCase("gene") || type.equalsIgnoreCase("biosystems")) {
							Integer gi = panelTarget.getPanelTargetGi();
							if (gi != null) {
								Target target = null;
								if (type.equalsIgnoreCase("protein"))
									target = new Target(gi, type);
								else if (type.equalsIgnoreCase("dna") || type.equalsIgnoreCase("rna"))
									target = new Target(gi, "nucleotide");
								else if (type.equalsIgnoreCase("gene")){
									Gene gene = new Gene(gi);
									if (!genes.contains(gene))
										genes.add(gene);
								}
								else if (type.equalsIgnoreCase("biosystems")){
									Xref xref = new Xref();
									xref.setXrefValue(gi);
									xref.setXrefType("biosystems id");
								}
								if (!targets.contains(target))
									this.targets.add(target);
							}
						}
					}
				}
				List<Integer> geneIDs = xx.getPanelGene();
				if (geneIDs != null) {
					for (Integer geneID : geneIDs) {
						Gene gene = new Gene(geneID);
						if (!genes.contains(gene))
							genes.add(gene);
					}
				}
				List<Integer> taxonomies = xx.getPanelTaxonomy();
				if (taxonomies != null) {
					for (Integer taxonomyID : taxonomies) {
						if (!taxonomyIDs.contains(taxonomyID))
							taxonomyIDs.add(taxonomyID);
					}
				}
			}
		}
	}

	public void setPmids(List<Xref> pmids) {
		this.pmids = pmids;
	}

	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}

	public void setResultTids(List<ResultTid> resultTids) {
		this.resultTids = resultTids;
	}

	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}

	public void setTaxonomyIDs(List<Integer> taxonomyIDs) {
		this.taxonomyIDs = taxonomyIDs;
	}

	public Integer processIntegerXref(Object value) {
		Double idD = Double.parseDouble(value.toString());
		Integer id = idD.intValue();
		return id;
	}

	public void setXrefs(Set<Xref> xrefs) {
		Integer pmidIndex = 0;
		
		Iterator<Xref> xrefIt = xrefs.iterator();
		while(xrefIt.hasNext()) {
			Xref xx = xrefIt.next();
			String type = xx.getXrefType().toLowerCase();
			Object value = xx.getXrefValue();
			if (value == null)
				throw new UnsupportedOperationException("In the excel file, a " + type + " xref does not have a value.");

			if (numericXrefTypes.contains(type)) {
				Double idD = Double.parseDouble(value.toString());
				Integer id = idD.intValue();
				xx.setXrefValue(id);
				if (type.equalsIgnoreCase("AID"))
					aids.add(xx);
				else if (type.equalsIgnoreCase("Protein") || type.equalsIgnoreCase("Nucleotide")) {
					Target target = new Target(id, type);
					target.setAssayTarget(xx.getIsTarget());
					if (!targets.contains(target))
						targets.add(target);
				} else if (type.equalsIgnoreCase("Gene")) {
					Gene gene = new Gene(id);
					gene.setIsTarget(xx.getIsTarget());
					if (!genes.contains(gene))
						genes.add(gene);
				} else if (type.equalsIgnoreCase("OMIM")) {
					if (!omimIDs.contains(id))
						omimIDs.add(id);
					this.xrefs.add(xx);
				} else if (type.equalsIgnoreCase("Taxonomy")) {
					if (!taxonomyIDs.contains(id))
						taxonomyIDs.add(id);
					this.xrefs.add(xx);
				}
			} else if (type.equalsIgnoreCase("PMID")) {
				try {
					Double idD = Double.parseDouble(value.toString());
					Integer id = idD.intValue();
					xx.setXrefValue(id);
					pmids.add(xx);
				} catch (Exception ex) {
					nonPmidReferences.add(xx);
				}
				pmidIndices.put(pmidIndex, xx);
				pmidIndex = pmidIndex + 1;
			} else
				this.xrefs.add(xx);
		}
	}

	public void setCategorizedComments(List<CategorizedComment> categorizedComments) {
		this.categorizedComments = categorizedComments;
	}

	public List<CategorizedComment> getCategorizedComments() {
		return categorizedComments;
	}

}