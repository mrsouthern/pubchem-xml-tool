package edu.scripps.fl.pubchem.cpdp;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

import edu.scripps.fl.pubchem.xml.model.PubChemAssay;

public class CPDPExtractPCAssayFactory {
	
	
	public static PubChemAssay getPubChemAssay(Document cpdp) throws CPDPException {

		PubChemAssay assay = new PubChemAssay();

		String grantNum = CPDPExtractUtils.getProjectInfoNodePath(CPDPExtractUtils.GRANT_NUM);
		String submInst = CPDPExtractUtils.getProjectInfoNodePath(CPDPExtractUtils.SCREENING_CENTER);
		String assayProvider = cpdp.selectSingleNode("//Project/Contacts/Contact[@id='AssayProvider']/Name").getText().trim();
		String affiliation = cpdp.selectSingleNode(CPDPExtractUtils.getProjectInfoNodePath(CPDPExtractUtils.SUBMITTER_INSTITUTION)).getText().trim();
		
		assay.setGrantNumber(cpdp.selectSingleNode(grantNum).getText().trim());
		assay.setProjectCategory(cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.PROJECT_CATEGORY)).getText().trim());
		assay.setHoldUntilDate(CPDPExtractUtils.parseDateNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.HOLD_UNTIL_DATE), cpdp));

		String stage = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.CARS_HTS_STAGE)).getText();
		if (stage.equalsIgnoreCase("primary") || stage.equalsIgnoreCase("secondary")) {
			assay.setActivityOutcomeMethod("screening");
		}
		else if (stage.equalsIgnoreCase("tertiary:dose response")) {
			assay.setActivityOutcomeMethod("confirmatory");
		}
		else {
			assay.setActivityOutcomeMethod("other");
		}

		assay.setSource(cpdp.selectSingleNode(submInst).getText());

		// Description section
		List<String> descriptions = new ArrayList<String>();
		CPDPExtractUtils.addSingleNodeTextToList("Source (MLPCN Center Name): ", cpdp, submInst, descriptions);
		descriptions.add("Affiliation: " + affiliation);
		descriptions.add(String.format("Assay Provider: %s, %s", assayProvider, affiliation));
		descriptions.add("Network: Molecular Library Probe Production Centers Network (MLPCN)");
		CPDPExtractUtils.addSingleNodeTextToList("Grant Proposal Number: ", cpdp, grantNum, descriptions);
		descriptions.add(String.format("Grant Proposal Pi: %s, %s", assayProvider , affiliation));
		
		String extRegId = CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.SHORT_NAME);
		assay.setExternalRegId(cpdp.selectSingleNode(extRegId).getText().trim());
		CPDPExtractUtils.addSingleNodeTextToList("External Assay ID: ", cpdp, extRegId, descriptions);
		descriptions.add("");
			
		String nameAtt = CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.AID_TITLE);
		assay.setName(cpdp.selectSingleNode(nameAtt).getText().trim());		
		CPDPExtractUtils.addSingleNodeTextToList("Name: ", cpdp, nameAtt, descriptions);
			
		addSection("Description:", cpdp, "//ProjectDescription/Paragraph", descriptions, false);
		addSection("References:", cpdp, CPDPExtractUtils.getReferenceNodesPath("Text"), descriptions, false);
		addSection("Keywords:", cpdp, CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.KEYWORDS), descriptions, false);

		assay.setDescription(descriptions);
		

		// Protocol section
		List<String> protocols = new ArrayList<String>();
		addSection("Assay Overview:", cpdp, CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.ASSAY_PURPOSE), protocols, true);
		addSection("Protocol Summary:", cpdp, CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.PROTOCOL), protocols, false);
		CPDPExtractUtils.addNodeTextToList(cpdp, CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.CALCULATION_SUMMARY), protocols);
		addSection("List of Reagents:", cpdp, CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.CRITICAL_REAGENTS), protocols, false);
		addSection("List of Consumables:", cpdp, CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.CONSUMABLES), protocols, false);

		assay.setProtocol(protocols);
		
		// comment section
		List<String> comments = new ArrayList<String>();
		CPDPExtractUtils.addNodeTextToList(cpdp, CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.COMMENTS), comments);

		assay.setComment(comments);

		return assay;
	}
	
	private static void addSection(String section, Document cpdp, String node, List<String> list, boolean isFirst) {
		if(! isFirst)
			list.add("");
		list.add(section);
		list.add("");
		CPDPExtractUtils.addNodeTextToList(cpdp, node, list);
	}
	
	
	
//	public static PubChemAssay getSummaryAssay(Document cpdp){
//		PubChemAssay assay = new PubChemAssay();
//
//		String grantNum = CPDPExtractUtils.getProjectInfoNodePath(CPDPExtractUtils.GRANT_NUM);
//		String submInst = CPDPExtractUtils.getProjectInfoNodePath(CPDPExtractUtils.SCREENING_CENTER);
//		String assayProvider = cpdp.selectSingleNode("//Project/Contacts/Contact[@id='AssayProvider']/Name").getText();
//		String affiliation = cpdp.selectSingleNode(CPDPExtractUtils.getProjectInfoNodePath(CPDPExtractUtils.SUBMITTER_INSTITUTION)).getText();
//		assay.setGrantNumber(cpdp.selectSingleNode(grantNum).getText());		
//		assay.setProjectCategory("MLPCN");
//		assay.setActivityOutcomeMethod("summary");
//		assay.setSource(cpdp.selectSingleNode(submInst).getText());
//
//		// Description section
//		List<String> descriptions = new ArrayList<String>();
//		CPDPExtractUtils.addSingleNodeTextToList("Source (MLPCN Center Name): ", cpdp, submInst, descriptions);
//		descriptions.add("Affiliation: " + affiliation);
//		descriptions.add(String.format("Assay Provider: %s, %s", assayProvider, affiliation));
//		descriptions.add("Network: Molecular Library Probe Production Centers Network (MLPCN)");
//		CPDPExtractUtils.addSingleNodeTextToList("Grant Proposal Number: ", cpdp, grantNum, descriptions);
//		descriptions.add(String.format("Grant Proposal Pi: %s, %s", assayProvider , affiliation));
//		
//		descriptions.add("External Assay ID: ");
//		descriptions.add("");
//		descriptions.add("Name: ");
//		
//		addSection("Description:", cpdp, "//ProjectDescription/Paragraph", descriptions, false);
//		addSection("References:", cpdp, CPDPExtractUtils.getReferenceNodesPath("Text"), descriptions, false);
//		addSection("Keywords:", cpdp, CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.KEYWORDS), descriptions, false);
//
//		assay.setDescription(descriptions);
//		return assay;
//	}


}
