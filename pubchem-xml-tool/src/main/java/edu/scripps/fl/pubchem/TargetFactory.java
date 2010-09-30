package edu.scripps.fl.pubchem;

import edu.scripps.fl.pubchem.xml.model.Target;

public class TargetFactory {
	
	public void setGenesTaxonomiesAndOmims(Target target) throws Exception{
		Long id = target.getId().longValue();
		String type = target.getType();
		target.setGenes(EUtilsFactory.getInstance().getIds(id, type, "gene"));
		target.setTaxonomies(EUtilsFactory.getInstance().getIds(id, type, "taxonomy"));
		target.setOmims(EUtilsFactory.getInstance().getIds(id, type, "omim"));
	}

}
