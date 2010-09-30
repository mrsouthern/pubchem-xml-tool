package edu.scripps.fl.test;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;

import edu.scripps.fl.pubchem.xml.model.Gene;

public class TransformerTest {

	
	public static void main(String[] args) {
		List<Gene> genes = null;
		Collection<Integer> ids = (Collection<Integer>) CollectionUtils.transformedCollection(genes, TransformerUtils.invokerTransformer("getId"));
		
	}

}
