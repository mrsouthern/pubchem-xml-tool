package edu.scripps.fl.pubchem.cpdp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.PanelTarget;

public class CPDPExtractPanelFactory {

	public static List<Panel> getPanels(Document cpdp) throws CPDPException{
		String targetDescription = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.TARGET_DESCRIPTION)).getText();
		boolean isPanel = false;
		if (targetDescription.equalsIgnoreCase("panel"))
			isPanel = true;
		
		if (!isPanel)
			return new ArrayList<Panel>();
		
		List<Panel> panels = new ArrayList<Panel>();
		List<Node> targets = CPDPExtractUtils.getChosenAIDTargetNodes(cpdp);
		for (Node tt : targets) {
			Panel panel = new Panel();
			String targetType = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.TARGET_TYPE)).getText();
			String proteinId = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.PROTEIN_ID)).getText();
			String geneId = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.GENE_ID)).getText();
		

			String taxonomyId = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.TAXONOMY_ID)).getText();

			if (null != proteinId && !"".equals(proteinId) && !"na".equalsIgnoreCase(proteinId)
					&& "protein".equalsIgnoreCase(targetType)) {
				CPDPExtractUtils.checkId(proteinId, "protein");
				PanelTarget target = createPanelTarget(Integer.parseInt(proteinId), targetType);
				panel.setPanelTarget(Arrays.asList(target));
				String proteinName = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.PROTEIN_NAME)).getText();
				panel.setPanelName(proteinName);
			}

			if (null != geneId && !"".equals(geneId) && !"na".equalsIgnoreCase(geneId)) {
				CPDPExtractUtils.checkId(geneId, "gene");
				if (!"gene".equalsIgnoreCase(targetType)) {
					panel.setPanelGene(Arrays.asList(Integer.parseInt(geneId)));
				}
				else {
					PanelTarget target = createPanelTarget(Integer.parseInt(geneId), targetType);
					panel.setPanelTarget(Arrays.asList(target));
					String geneName = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.GENE_NAME)).getText();
					panel.setPanelName(geneName);
				}
			}

			if (null != taxonomyId && !"".equals(taxonomyId) && !"na".equalsIgnoreCase(taxonomyId)){
				CPDPExtractUtils.checkId(taxonomyId, "taxonomy");
				panel.setPanelTaxonomy(Arrays.asList(Integer.parseInt(taxonomyId)));
			}

			panels.add(panel);
		}

		return panels;
	}


	private static PanelTarget createPanelTarget(Integer gi, String type) {
		PanelTarget target = new PanelTarget();
		target.setPanelTargetGi(gi);
		target.setPanelTargetType(type);
		return target;
	}

}
