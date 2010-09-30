package edu.scripps.fl.pubchem;

import edu.scripps.fl.pubchem.xml.model.Gene;

public class GeneFactory {
	
	public void setProteinsAndNucleotides(Gene gene) throws Exception{
		gene.setProteins(EUtilsFactory.getInstance().getIds(gene.getId().longValue(), "gene", "protein"));
		gene.setNucleotides(EUtilsFactory.getInstance().getIds(gene.getId().longValue(), "gene", "nucleotide"));
		gene.setOmims(EUtilsFactory.getInstance().getIds(gene.getId().longValue(), "gene", "omim"));
		gene.setTaxonomies(EUtilsFactory.getInstance().getIds(gene.getId().longValue(), "gene", "taxonomy"));
	}

	
}
