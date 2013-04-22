package edu.scripps.fl.pubchem.cpdp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CPDPExtractUtils {

	private static final Logger log = LoggerFactory.getLogger(CPDPExtractUtils.class);

	public static final String SUMMARY_AID = "SummaryAID#";
	public static final String PROJECT_UID = "ProjectUID(CARS)";
	public static final String AID = "AssignedAID#";
	public static final String PROJECT_CATEGORY = "ProjectCategory";
	public static final String CARS_HTS_STAGE = "CARSHTSStage";
	public static final String PROBE_TYPE = "ProbeType";
	public static final String AID_TITLE = "DescriptiveName(AIDtitle)";
	public static final String SHORT_NAME = "ShortName";
	public static final String ASSAY_CENTER = "AssayCenter";
	public static final String PHENOTYPIC_SCREEN = "PhenotypicScreen";
	public static final String MULTIPLEXING = "Multiplexing";
	public static final String BSL = "BSL";
	public static final String COMPOUND_SCREENING_CONCENTRATION = "CompoundScreeningConcentration";
	public static final String CONCENTRATION_UNITS = "ConcentrationUnits";
	public static final String CONCENTRATION_POINTS = "#ConcentrationPoints";
	public static final String DILUTION_FACTOR = "#DilutionFactor";
	public static final String REPLICATES = "#Replicates";
	public static final String ASSAY_FORMAT = "AssayFormat";
	public static final String CELL_LINE = "CellLine";
	public static final String ASSAY_TYPE = "AssayType";
	public static final String ASSAY_METHOD = "AssayMethod";
	public static final String RESULT_TYPE = "ResultType";
	public static final String ASSAY_READOUT_CONTENT = "AssayReadoutContent";
	public static final String ASSAY_READOUT_TYPE = "AssayReadoutType";
	public static final String TARGET_TYPE = "TargetType";
	public static final String TARGET_DESCRIPTION = "TargetDescription";
	public static final String ASSAY_DETECTION = "AssayDetection";
	public static final String SIGNAL_DIRECTION = "SignalDirection";
	public static final String ASSAY_DETECTION_METHOD_TYPE = "AssayDetectionMethodType";
	public static final String ASSAY_DETECTION_INSTRUMENT = "AssayDetectionInstrument";
	public static final String EXCITATION_WAVELENGTH = "ExcitationWavelength";
	public static final String EMISSION_WAVELENGTH = "EmissionWavelength";
	public static final String ABSORBANCE_WAVELENGTH = "AbsorbanceWavelength";
	public static final String HIT_VALIDATION = "UsedforHitValidation?";
	public static final String SAR = "UsedforSAR?";
	public static final String IMPLEMENTATION_START_DATE = "ImplementationStartDate";
	public static final String VALIDATION_START_DATE = "ValidationStartDate";
	public static final String CRITICAL_REAGENTS = "CriticalReagents";
	public static final String CONSUMABLES = "Consumables";
	public static final String ASSAY_PURPOSE = "Description-AssayPurpose";
	public static final String PROTOCOL = "Description-DetailedProtocols";
	public static final String CALCULATION_SUMMARY = "CalculationSummary";
	public static final String CURVE_FIT_EQUATION = "Curve-fitequation";
	public static final String CURVE_FIT_EQUATION_V2 = "Curve-fitEquation";
	public static final String KEYWORDS = "Keywords";
	public static final String COMMENTS = "Comments";
	public static final String CUT_OFF_NUMBER = "AssayCut-offNumber";
	public static final String CUT_OFF_QUALIFIER = "AssayCut-offQualifier";
	public static final String CUT_OFF_UNITS = "AssayCut-offUnits";
	public static final String START_DATE = "Est.StartDate";
	public static final String THROUGHPUT = "Est.Throughput";
	public static final String HOLD_UNTIL_DATE = "HoldUntilDate";

	public static final String PROTEIN_ID = "NCBITargetProteinID#";
	public static final String GENE_ID = "NCBITargetGeneID#";
	public static final String TAXONOMY_ID = "NCBITargetTaxonomyID#";
	public static final String PROTEIN_NAME = "NCBITargetProteinName";
	public static final String GENE_NAME = "NCBITargetGeneName";
	public static final String OMIM_ID = "NCBITargetOMIM#";

	public static final String GRANT_NUM = "GrantApplicationNumber";
	public static final String SCREENING_CENTER = "ScreeningCenterName";
	public static final String SUBMITTER_INSTITUTION = "SubmitterInstitution";

	public static String getProjectInfoNodePath(String id) {
		return String.format("//Project/%s", getInformationNodeItemPath(id));
	}

	public static String chosenAIDNodePath = "//AIDs/AID[@create='true']";

	public static String getChosenAIDInfoNodePath(String id) {
		return String.format("%s/%s", chosenAIDNodePath, getInformationNodeItemPath(id));
	}

	public static String getInformationNodeItemPath(String id) {
		return String.format("Information/%s", getItemNodePath(id));
	}

	public static String getItemNodePath(String id) {
		return String.format("Item[@id='%s']", id);
	}

	public static List<Node> getChosenAIDTargetNodes(Document doc) {
		return doc.selectNodes(String.format("%s/%s", chosenAIDNodePath, targetPath));
	}

	public static String targetPath = "Targets/Target";

	public static List<Node> geTargetNodes(Node aidNode) {
		return aidNode.selectNodes(targetPath);
	}

	public static String getReferenceNodesPath(String node) {
		return String.format("//References/Reference/%s", node);
	}

	public static Integer parseIntNode(String node, Document doc) {
		Integer number = 0;
		String text = doc.selectSingleNode(node).getText();
		if (null != text && !"".equals(text))
			number = Integer.parseInt(text);
		return number;
	}

	public static Double parseDoubleNode(String node, Document doc) {
		Double number = 0.0;
		String text = doc.selectSingleNode(node).getText();
		if (null != text && !"".equals(text))
			number = Double.parseDouble(text);
		return number;
	}

	public static Date parseDateNode(String node, Document doc) throws CPDPException {
		Date date = null;
		String text = doc.selectSingleNode(node).getText();
		if (null != text && !"".equals(text))
			try {
				date = new SimpleDateFormat("yyyy/MM/dd").parse(text);
			}
			catch (ParseException e) {
				throw new CPDPException(String.format("Unable to parse %s : %s", node, text));
			}
		return date;
	}

	public static void addSingleNodeTextToList(String beforeText, Document cpdp, String nodeS, List<String> list) {
		Node node = cpdp.selectSingleNode(nodeS);
		String nodeText = node.getText().trim();
		list.add(beforeText + nodeText);
	}

	public static void addNodeTextToList(Document cpdp, String node, List<String> list) {
		List<Node> nodes = cpdp.selectNodes(node);
		for (Node nn : nodes) {
			if (null != nn.getText() || "" != nn.getText()) {
				list.add(nn.getText());
			}
		}
	}

	public static void checkId(String id, String type) throws CPDPException {
		try {
			Integer.parseInt(id);
		}
		catch (Exception ex) {
			throw new CPDPException(type + " id: " + id + " is not a number.");
		}
	}

	// public static List<String> getListOfTargetShortNames(List<Node>
	// targetNodes) throws Exception{
	// List<String> shortNames = new ArrayList<String>();
	// List<String> geneTargets = new ArrayList<String>();
	// List<String> proteinTargets = new ArrayList<String>();
	// String type = "";
	// for (Node targetN : targetNodes) {
	// String targetType =
	// targetN.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.TARGET_TYPE)).getText();
	// if("protein".equalsIgnoreCase(targetType)){
	// String proteinId =
	// targetN.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.PROTEIN_ID)).getText();
	// proteinTargets.add(proteinId);
	// }
	// else if("gene".equalsIgnoreCase(targetType)){
	// String geneId =
	// targetN.selectSingleNode(CPDPExtractUtils.getItemNodePath(CPDPExtractUtils.GENE_ID)).getText();
	// geneTargets.add(geneId);
	// }
	// }
	// if(proteinTargets.size() > 0){
	// Document doc =
	// EUtilsFactory.getInstance().getSummary(StringUtils.join(proteinTargets,
	// ","), "protein");
	// doc.selectNodes("");
	// }
	// return shortNames;
	// }

}
