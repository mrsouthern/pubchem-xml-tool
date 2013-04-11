package edu.scripps.fl.pubchem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jfree.util.Log;

import edu.scripps.fl.pubchem.xml.model.CategorizedComment;
import edu.scripps.fl.pubchem.xml.model.Gene;
import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.PanelTarget;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;
import edu.scripps.fl.pubchem.xml.model.Publication;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Target;
import edu.scripps.fl.pubchem.xml.model.Xref;

public class PubChemAssayFactory {

	public void setUpPubChemAssay(PubChemAssay assay, List<ResultTid> resultTids, Set<Xref> xrefs, List<Panel> panels,
			List<CategorizedComment> cComments) throws Exception {
		if (resultTids != null)
			assay.setResultTids(resultTids);
		if (xrefs != null)
			assay.setXrefs(xrefs);
		if (panels != null)
			assay.setPanels(panels);
		if (cComments != null)
			assay.setCategorizedComments(cComments);

		for(ResultTid tid: resultTids){
			if("".equals(tid.getTidType()) || null == tid.getTidType())
				assay.setMessage(assay.getMessage() + tid.getTidName() + " needs to be assigned a TYPE or the xml will not work.\n");
		}
		setUpTargets(assay);
		setUpGenes(assay);
		setPanelTargetNames(assay);
		checkTargets(assay);
		checkGenes(assay);
		setUpPublications(assay);
	}

	public void setUpPublications(PubChemAssay assay) throws Exception {
		List<Publication> publications = new ArrayList<Publication>();
		if (assay.getPmids().size() > 0) {
			List<Xref> pmids = new ArrayList<Xref>();
			String string = copyListAndTransform(assay.getPmids(), pmids, "getXrefValue", ",");
			Document doc = EUtilsFactory.getInstance().getSummary(string, "pubmed");
			Iterator<Publication> iter = new PublicationIterator((Iterator<Element>) doc.getRootElement().elementIterator("DocSum"));
			while (iter.hasNext()) {
				publications.add(iter.next());
			}
		} else
			Log.info("There are no pmids.");
		assay.setPublications(publications);
	}

	public void setPanelTargetNames(PubChemAssay assay) {
		for (Panel xx : assay.getPanels()) {
			List<PanelTarget> panelTargets = xx.getPanelTarget();
			if (panelTargets != null) {
				for (PanelTarget pTarget : panelTargets) {
					Integer pTargetId = pTarget.getPanelTargetGi();
					if (pTargetId != null) {
						for (Target yy : assay.getTargets()) {
							if (pTargetId.equals(yy.getId()))
								pTarget.setPanelTargetName(yy.getName());
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setUpTargets(List<Target> proteinOrNucleotideTargets, List<Target> allTargets, String type) throws Exception {
		if (proteinOrNucleotideTargets.size() > 0) {
			List<Target> targets = new ArrayList<Target>();
			String targetString = copyListAndTransform(proteinOrNucleotideTargets, targets, "getId", ",");
			Document doc = EUtilsFactory.getInstance().getSummary(targetString, type);
			Iterator<Target> iter = new TargetIterator((Iterator<Element>) doc.getRootElement().elementIterator("DocSum"));
			while (iter.hasNext()) {
				Target target = iter.next();
				target.setType(type);
				new TargetFactory().setGenesTaxonomiesAndOmims(target);
				for (Target tt : proteinOrNucleotideTargets) {
					if (tt.equals(target))
						target.setAssayTarget(tt.isAssayTarget());
				}
				allTargets.add(target);
			}
		}
	}

	public void setUpTargets(PubChemAssay assay) throws Exception {
		List<Target> proteins = new ArrayList<Target>();
		List<Target> nucleotides = new ArrayList<Target>();

		for (Target tt : assay.getTargets()) {
			if (tt.getType().equalsIgnoreCase("protein")) {
				proteins.add(tt);
			} else if (tt.getType().equalsIgnoreCase("nucleotide"))
				nucleotides.add(tt);
		}

		List<Target> processedTargets = new ArrayList<Target>();
		setUpTargets(proteins, processedTargets, "protein");
		setUpTargets(nucleotides, processedTargets, "nucleotide");
		assay.setTargets(processedTargets);
		for(Target target: processedTargets){
			if(! assay.getTaxonomyIDs().contains(target.getTaxonomy())){
				Set<Xref> xrefs = assay.getXrefs();
				xrefs.add(new Xref("taxonomy", target.getTaxonomy().toString(), "", null));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setUpGenes(PubChemAssay assay) throws Exception {
		if (assay.getGenes().size() > 0) {
			List<Gene> geneIds = new ArrayList<Gene>();
			String geneString = copyListAndTransform(assay.getGenes(), geneIds, "getId", ",");
			Document doc = EUtilsFactory.getInstance().getSummary(geneString, "gene");
			Iterator<Gene> iter = new GeneIterator((Iterator<Element>) doc.getRootElement().elementIterator("DocSum"));
			List<Gene> genes = new ArrayList<Gene>();
			while (iter.hasNext()) {
				Gene gene = iter.next();
				if (gene.getId() != null)
					new GeneFactory().setProteinsAndNucleotides(gene);

				for (Gene geneA : assay.getGenes()) {
					if (geneA.equals(gene))
						gene.setIsTarget(geneA.getIsTarget());
				}
				genes.add(gene);
			}
			assay.setGenes(genes);
		}
	}

	public void checkGenes(PubChemAssay assay) throws Exception {
		Integer count = 0;
		for (Gene gene : assay.getGenes()) {
			for (Long omim : gene.getOmims()) {
				if (assay.getOmimIDs().contains(omim.intValue()))
					BeanUtils.setProperty(gene, "omim", omim.intValue());
			}
			for (Long taxonomy : gene.getTaxonomies()) {
				if (assay.getTaxonomyIDs().contains(taxonomy.intValue()))
					BeanUtils.setProperty(gene, "taxonomy", taxonomy.intValue());
			}
			for (Target tt : assay.getTargets()) {
				if (gene.getProteins().contains(tt.getId().longValue()) == true
						|| gene.getNucleotides().contains(tt.getId().longValue()) == true) {
					count = count + 1;
					gene.setTarget(tt);
					if (tt.getOmim() == null)
						tt.setOmim(gene.getOmim());
					if (tt.getTaxonomy() == null)
						tt.setTaxonomy(gene.getTaxonomy());
				}
			}
			if (count == 0) {
				String string = "The XRefs and/or Panels list does not contain a protein or nucleotide gi that is included in the NCBI eLink list for gene: "
						+ gene.getId() + "\n";
				String message = assay.getMessage();
				String proteinString = "";
				String nucleotideString = "";
				if (gene.getProteins().size() > 0)
					proteinString = "NCBI list of proteins " + gene.getProteins() + "\n";
				if (gene.getNucleotides().size() > 0)
					nucleotideString = "NCBI list of nucleotides " + gene.getNucleotides() + "\n";
				assay.setMessage(String.format("%s%s%s%s", message, string, proteinString, nucleotideString));
			}
		}
	}

	public void checkTargets(PubChemAssay assay) throws Exception {
		for (Target xx : assay.getTargets()) {
			int geneCount = 0;
			for (Long yy : xx.getGenes()) {
				for (Gene gene : assay.getGenes()) {
					if (gene.getId().equals(yy.intValue())) {
						xx.setGene(gene);
						geneCount = geneCount + 1;
					}
				}
			}
			String geneMessage = "";
			if (geneCount == 0 && xx.getGenes().size() > 0)
				geneMessage = "The XRefs and/or Panels list does not contain the NCBI eLink gene ids " + xx.getGenes() + " for target: "
						+ xx.getId() + "\n";
			String message = assay.getMessage();
			String taxonomyMessage = check(xx.getTaxonomies(), assay.getTaxonomyIDs(), "taxonomy", xx);
			String omimMessage = check(xx.getOmims(), assay.getOmimIDs(), "omim", xx);
			assay.setMessage(String.format("%s%s%s%s", message, geneMessage, taxonomyMessage, omimMessage));
		}
	}

	public String check(Collection<Long> propertyValues, List<Integer> assay, String property, Target target) throws Exception {
		int count = 0;
		String message = "";
		for (Long yy : propertyValues) {
			if (assay.contains(yy.intValue())) {
				BeanUtils.setProperty(target, property, yy.intValue());
				count = count + 1;
			}
		}
		if (count == 0 && propertyValues.size() > 0)
			message = "The XRefs list does not contain any of the NCBI eLink " + property + " ids " + propertyValues + " for target: "
					+ target.getId() + "\n";
		return message;
	}

	public List<Gene> removePanelsFromGenes(PubChemAssay assay) {
		List<Gene> genesWithPanels = assay.getGenes();
		List<Gene> genesWithoutPanels = new ArrayList<Gene>();
		for (Gene yy : genesWithPanels) {
			genesWithoutPanels.add(yy);
			for (Panel xx : assay.getPanels()) {
				List<Integer> panelGenes = xx.getPanelGene();
				if (panelGenes != null) {
					if (panelGenes.contains(yy.getId()))
						genesWithoutPanels.remove(yy);
				}
			}
		}
		return genesWithoutPanels;
	}

	public List<Target> removePanelsFromTargets(PubChemAssay assay) {
		List<Target> targetsWithPanels = assay.getTargets();
		List<Target> targetsWithoutPanels = new ArrayList<Target>();
		for (Target yy : targetsWithPanels) {
			targetsWithoutPanels.add(yy);
			for (Panel xx : assay.getPanels()) {
				List<PanelTarget> panelTargets = xx.getPanelTarget();
				if (panelTargets != null) {
					for(PanelTarget pTarget: panelTargets){
						Integer pTargetId = pTarget.getPanelTargetGi();
						if(pTargetId != null && pTargetId.equals(yy.getId()))
							targetsWithoutPanels.remove(yy);
					}
				}
			}
		}
		return targetsWithoutPanels;
	}

	public void placeCitationsInDescription(PubChemAssay assay, boolean bool) throws Exception {
		List<String> description = assay.getDescription();
		Integer size = description.size();
		for (int ii = 0; ii < size; ii++) {
			String string = description.get(ii);
			if (string.equalsIgnoreCase("[References]")) {
				description.remove(ii);
				List<String> citationStrings = citations(assay, bool);
				description.add(ii, "References:\n\n");
				for (int jj = 0; jj < citationStrings.size(); jj++)
					description.add(jj + ii + 1, citationStrings.get(jj));
			}
		}
	}

	public List<String> citations(PubChemAssay assay, boolean bool) {
		String citationString = "";
		List<String> citations = new ArrayList<String>();
		Integer size = assay.getPmids().size() + assay.getNonPmidReferences().size();

		int index = 0;
		for (int ii = 1; ii <= size; ii++) {
			Xref xx = assay.getPmidIndices().get(ii - 1);
			if (assay.getNonPmidReferences().contains(xx))
				citations.add(ii + ". " + xx.getXrefValue().toString());
			else {
				Publication publication = assay.getPublications().get(index);
				citations.add(ii + ". " + publication.citation(bool));
				index = index + 1;
			}
		}

		return citations;
	}

	public String copyListAndTransform(List objects, List copy, String method, String join) {
		copy.addAll(objects);
		CollectionUtils.transform(copy, TransformerUtils.invokerTransformer(method));
		String string = StringUtils.join(copy, join);
		return string;
	}
}
