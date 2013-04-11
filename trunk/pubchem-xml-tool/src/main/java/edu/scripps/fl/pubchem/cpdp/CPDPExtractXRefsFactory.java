package edu.scripps.fl.pubchem.cpdp;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.EUtilsFactory;
import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.Xref;

public class CPDPExtractXRefsFactory {

	private static void addTargetXref(String id, String type, String targetType, Set<Xref> xrefs) throws CPDPException {
		if (null != id && !"".equals(id) && !"na".equalsIgnoreCase(id)) {
			CPDPExtractUtils.checkId(id, type);
			Xref targetX = newXref(id, type, "");
			if (type.equalsIgnoreCase(targetType))
				targetX.setIsTarget(true);
			else
				targetX.setIsTarget(false);
			xrefs.add(targetX);
		}
	}

	private static Xref newXref(String value, String type, String comment) {
		Xref xref = new Xref();
		xref.setXrefValue(value);
		xref.setXrefType(type);
		xref.setXrefComment(comment);
		return xref;
	}

	public static Set<Xref> getSummaryXrefs(Document cpdp) throws CPDPException{
		Set<Xref> xrefs = new LinkedHashSet<Xref>();

		xrefs.add(newXref("http://mlpcn.florida.scripps.edu/", "Source Database Homepage", ""));

		pmidXrefs(cpdp, xrefs);
		
		aidXrefs(cpdp, xrefs);
		List<Node> aids = cpdp.selectNodes("//AIDs/AID");
		for(Node aa: aids){
			if("primary".equalsIgnoreCase(aa.selectSingleNode(CPDPExtractUtils.getInformationNodeItemPath(CPDPExtractUtils.CARS_HTS_STAGE)).getText())){
				List<Node> aidTargets = CPDPExtractUtils.geTargetNodes(aa);
				addTargets(aidTargets, xrefs);
			}
		
		}
		

		return xrefs;
	}

	private static void aidXrefs(Document cpdp, Set<Xref> xrefs) {
		List<Node> aids = cpdp.selectNodes("//AIDs/AID");
		for (Node aa : aids) {
			String aidNum = aa.selectSingleNode(CPDPExtractUtils.getInformationNodeItemPath(CPDPExtractUtils.AID)).getText();
			if (null != aidNum && !"".equals(aidNum)) {
				String title = aa.selectSingleNode(CPDPExtractUtils.getInformationNodeItemPath(CPDPExtractUtils.AID_TITLE))
						.getText();
				String probeType = aa.selectSingleNode(CPDPExtractUtils.getInformationNodeItemPath(CPDPExtractUtils.PROBE_TYPE))
						.getText();
				Integer replicate = Integer.parseInt(aa.selectSingleNode(
						CPDPExtractUtils.getInformationNodeItemPath(CPDPExtractUtils.REPLICATES)).getText());
				String comment = assayType(title);

				// List<String> shortNames = new ArrayList<String>();
				// if (panels == null || panels.size() == 0) {
				// List<Node> aidTargets = CPDPExtractUtils.geTargetNodes(aa);
				// shortNames =
				// CPDPExtractUtils.getListOfTargetShortNames(aidTargets);
				// }

				if (!"".equals(replicate) && !"".equals(probeType))
					comment = String.format("%s (%s %ss in %s)", comment, "", probeType, replicate(replicate));

				xrefs.add(newXref(aidNum, "aid", comment));
			}
		}
	}
	
	private static void pmidXrefs(Document cpdp, Set<Xref> xrefs){
		List<Node> pmids = cpdp.selectNodes(CPDPExtractUtils.getReferenceNodesPath("PMID"));
		int count = 0;
		for (Node nn : pmids) {
			count = count + 1;
			String text = nn.getText();
			if (null != text && !"".equals(text))
				xrefs.add(newXref(text, "pmid", "Reference " + count));

		}
	}

	public static Set<Xref> getXRefs(Document cpdp, List<Panel> panels) throws CPDPException {
		Set<Xref> xrefs = new LinkedHashSet<Xref>();
		

		xrefs.add(newXref("http://mlpcn.florida.scripps.edu/", "Source Database Homepage", ""));

		String summary = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.SUMMARY_AID)).getText();
		if (null != summary && !"".equalsIgnoreCase(summary))
			xrefs.add(newXref(summary, "AID", "Summary"));

		pmidXrefs(cpdp, xrefs);

		aidXrefs(cpdp, xrefs);

		List<Node> targets = CPDPExtractUtils.getChosenAIDTargetNodes(cpdp);
		if (panels == null || panels.size() == 0) {
			addTargets(targets, xrefs);
		}
		else {
			// only add omim xrefs
			for (Node tt : targets) {
				String targetType = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.TARGET_TYPE)).getText();
				if("".equals(targetType))
					throw new CPDPException("Target type has not been specified.");
				String omimId = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.OMIM_ID)).getText();
				addTargetXref(omimId, "omim", targetType, xrefs);
			}
			
		}

		return xrefs;
	}
	
	private static void addTargets(List<Node> targets, Set<Xref> xrefs) throws CPDPException{
		for (Node tt : targets) {
			String targetType = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.TARGET_TYPE)).getText();
			String proteinId = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.PROTEIN_ID)).getText();
			
			String geneId = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.GENE_ID)).getText();
			String taxonomyId = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.TAXONOMY_ID)).getText();
			String omimId = tt.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.OMIM_ID)).getText();

			addTargetXref(proteinId, "protein", targetType, xrefs);
			addTargetXref(geneId, "gene", targetType, xrefs);
			addTargetXref(taxonomyId, "taxonomy", targetType, xrefs);
			addTargetXref(omimId, "omim", targetType, xrefs);
		}
	}

	private static String assayType(String title) {
		title = title.toLowerCase();
		String type = "";

		if (title.contains("primary"))
			type = "primary screen";

		if (title.contains("confirmation"))
			type = "confirmation screen";

		if (title.contains("counterscreen"))
			type = "counterscreen";

		if (title.contains("dose response"))
			type = "dose response" + type;

		if (title.contains("late stage") || title.contains("late-stage"))
			type = "late stage" + type;

		return type;
	}

	private static String replicate(Integer repNum) {

		String replicate;

		if (repNum == 1)
			replicate = "singlicate";
		else if (repNum == 2)
			replicate = "duplicate";
		else if (repNum == 3)
			replicate = "triplicate";
		else if (repNum == 4)
			replicate = "quadruplicate";
		else if (repNum >= 5)
			replicate = repNum + " replicates";
		else
			replicate = "";

		return replicate;

	}

}
