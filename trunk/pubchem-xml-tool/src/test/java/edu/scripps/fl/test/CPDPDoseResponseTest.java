package edu.scripps.fl.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.dom4j.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import edu.scripps.fl.pubchem.cpdp.CPDPException;
import edu.scripps.fl.pubchem.cpdp.CPDPExtractPCAssayFactory;
import edu.scripps.fl.pubchem.cpdp.CPDPExtractTIDsFactory;
import edu.scripps.fl.pubchem.cpdp.CPDPExtractXRefsFactory;
import edu.scripps.fl.pubchem.cpdp.CPDPXMLProcess;
import edu.scripps.fl.pubchem.xml.model.CategorizedComment;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;
import edu.scripps.fl.xml.XMLUtils;

public class CPDPDoseResponseTest {

	InputStream stream;
	private static final Logger log = LoggerFactory.getLogger(CPDPDoseResponseTest.class);

	@BeforeClass
	public static void configureLogger() {
		DOMConfigurator.configure(CPDPDoseResponseTest.class.getClassLoader().getResource("log4j.config.xml"));
	}

	/*
	 * Loads test CPDP XML
	 */
	@Before
	public void uploadTestCPDP() {
		stream = CPDPDoseResponseTest.class.getClassLoader().getResourceAsStream("test_CPDP_Doc_DR.xml");
	}

	@After
	public void closeStream() throws IOException {
		stream.close();
	}
	

	@Test
	public void assayExtraction() throws CPDPException, SAXException, IOException, ParserConfigurationException {
		PubChemAssay assay = CPDPExtractPCAssayFactory.getPubChemAssay(XMLUtils.readXMLInputStream(stream));
		String name = "Luminescence-based cell-based primary high throughput screening assay to identify antagonists of COUP-TFII (NR2F2)";
		assertEquals("Assay name is not correct.", name, assay.getName());
		assertEquals("Assay activity outcome method is not correct.","confirmatory",assay.getActivityOutcomeMethod());
		assertEquals("External Reg ID is not correct.", "COUPTFII_INH_LUMI_1536_3XIC50 DRUN",assay.getExternalRegId());
		assertEquals("Grant number is not correct.","R01DK45641",assay.getGrantNumber());
		assertEquals("Project Category is not correct.", "mlpcn-ap",assay.getProjectCategory());
		assertEquals("Source is not correct.","The Scripps Research Institute Molecular Screening Center",assay.getSource());

		
		//description checking
		List<String> description = new ArrayList<String>();
		description.add("Source (MLPCN Center Name): The Scripps Research Institute Molecular Screening Center");
		description.add("Affiliation: Baylor College of Medicine");
		description.add("Assay Provider: Ming-Jer Tsai, Baylor College of Medicine");
		description.add("Network: Molecular Library Probe Production Centers Network (MLPCN)");
		description.add("Grant Proposal Number: R01DK45641");
		description.add("Grant Proposal Pi: Ming-Jer Tsai, Baylor College of Medicine");
		description.add("External Assay ID: COUPTFII_INH_LUMI_1536_3XIC50 DRUN");
		description.add("");
		description.add("Name: Luminescence-based cell-based primary high throughput screening assay to identify antagonists of COUP-TFII (NR2F2)");
		description.add("");
		description.add("Description:");
		description.add("");
		description.add("Steroid receptor chicken ovalbumin upstream promoter-transcription factor II (COUP-TFII) (1), an orphan nuclear receptor and member of the nuclear receptor superfamily, has been shown to be a critical transcriptional regulator in many different cancer types by promoting angiogenesis (2-4), cell proliferation and metastasis (5-11). COUP-TFII has widespread tissue distribution in human; detectable expression has been found in every tissue type examined (12). Currently, the treatment for tumor angiogenesis focuses mainly on blocking VEGFR-2 signaling and has not been effective due to limited efficacy, eventually leading to resistance and/or relapse. COUP-TFII has been shown to promote tumor angiogenesis through modulating multiple angiogenic signals (VEGF/VEGFR-2, Angiopoietin 1/Tie2 and E2F-1) in many different types of cancer (13-14). In addition, COUP-TFII is overexpressed in prostate and several other cancers and is an excellent prognostic marker. By including COUP-TFII data with Cyclin D1, p21, PTEN, and Smad4 data in the prognosis, the prognostic accuracy is improved. The expression level of COUP-TFII and its role in regulating tumor growth and metastasis in prostate cancer has been examined, and these data indicate that COUP-TFII positively promotes prostate tumor growth and metastasis (15). These results provide the rational basis to posit that inhibition of COUP-TFII may offer a novel and broadly efficacious approach for anticancer intervention.");
		description.add("COUP-TFII has also been shown to regulate energy storage and expenditure. We have found that COUP-TFII heterozygous mice have increased mitochondrial biogenesis in white adipose tissue, which results in higher energy expenditure, resulting in resistance to high fat diet-induced obesity and improved glucose homeostasis due to increased insulin sensitivity at peripheral tissues (16). These results indicate that COUP-TFII has an important role in regulating adipocyte differentiation and energy metabolism. Therefore, COUP-TFII inhibitors could potentially serve as agents to improve insulin sensitivity, enhance energy metabolism, and decrease high fat diet-induced obesity.");
		description.add("");
		description.add("References:");
		description.add("");
		description.add("1. Sagami, I., Tsai, S. Y., Wang, H., Tsai, M. J., and O'Malley, B. W. (1986) Identification of two factors required for transcription of the ovalbumin gene, Mol Cell Biol 6, 4259-4267.");
		description.add("");
		description.add("Keywords:");
		description.add("");
		description.add("PRUN, primary, HTS, nuclear hormone receptor, NHR, reporter assay, luciferase, LUMI, COUPTFII, chicken ovalbumin upstream promoter, COUP transcription factor 2, NR2F2, ARP1, COUPTFB, NF-E3, NR2F1, SVP40, TFCOUP2, 1536, Scripps Florida, The Scripps Research Institute Molecular Screening Center, SRIMSC, Molecular Libraries Probe Production Centers Network, MLPCN");
		
		assertEquals("Description not as expected.", description, assay.getDescription());

		
		List<String> protocol = new ArrayList<String>();
		protocol.add("Assay Overview:");
		protocol.add("");
		protocol.add("The purpose of this cell-based assay is to identify compounds that can inhibit COUP-TFII transcriptional activity. The expression vectors for COUP-TFII p(cDNA6.2-COUP-TFII) and the luciferase reporter NGFI-A-Luc (pXP2-168) are transiently cotransfected into HEK-293T cells. COUP-TFII has been shown to efficiently activate NGFI-A-Luc expression (17) and the readout can be measured by a luminometer. Small molecules that inhibit COUP-TFII transcriptional activity will decrease the promoter activity that can be detected by luciferase assay. As designed, compounds that inhibit COUP-TFII activity will decrease luciferase activity, resulting in decreased well luminescence.");
		protocol.add("");
		protocol.add("Protocol Summary:");
		protocol.add("");
		protocol.add("On Day 1, Transfection mix: In 600 ul of Opti-Mem (invitrogen#11058021), add 27 ul of X-tremeGENE9 (Roche Applied Science, part 06365809001). Incubate for 5 min.Add 9 ug total DNA (4.5 ug pXP2-168 + 4.5 ug pcDNA6.2-COUP-TFII /pcDNA6.2-CAT) to the above mix. Mix it gently and let the mixture sit for at least 20 minutes. During the 2nd incubation of 20 minutes, harvest cells from a 15 cm dish as follows:"
				+"\na) Remove media via vacuum aspiration completely."
		+"\nb) Add 1 ml trypsin-EDTA to the cells and evenly spread over plate."
		+"\nc) Bang the plate with the palm of your hand to dislodge cells from the surface of the plate."
		+"\nd) Allow cells to sit in the tissue culture hood for 3-5 minutes."
		+"\ne) Add 10 ml of 37 C full medium to the plate of cells."
		+"\nf) Pipet up and down 10-15 times with a 10 ml pipet until a single cell suspension is obtained. Try to avoid creating too much froth."
		+"\ng) Count cells in a hemocytometer."
		+"\nMix 8.5 ml of medium containing 3 x 106 cells with transfection mix in 10 cm dish and incubate for overnight in the CO2 incubator. On Day 2, In the morning, harvest the cells as above (0.5 ml trypsin-EDTA) in 10 ml of medium and count them. Seed 10,000 cells per 384 well in 20 ul of medium. In the afternoon (4 hr after seeding), treat with the compounds diluted in medium (20 ul). On Day 3, Develop with Luciferase reagent by adding 30 ul of Britelite Plus (after 16-20 hr of compound treatment).");
		protocol.add("");
		protocol.add("");
		protocol.add("List of Reagents:");
		protocol.add("");
		protocol.add("HEK-293T cells, pcDNA6.2-COUP-TFII, pXP2-168");
		protocol.add("");
		protocol.add("List of Consumables:");
		protocol.add("");
		protocol.add("Assay plates (PerkinElmer #6007688)"
					+"\nOptiMEM (Invitrogen #11058021)"
					+"\nX-tremeGENE 9 (Roche Applied Science #06365809001)"
					+"\nDMEM (Mediatech Inc. #10-013-CV)"
					+"\nBriteLite (PerkinElmer #6007688)");
		assertEquals("Protocol is not as expected.", protocol, assay.getProtocol());
		
		List<String> comment = new ArrayList<String>();
		comment.add("");
		assertEquals("Comment is not as expected.", comment, assay.getComment());
		 
	}
	
	@Test
	public void xrefExtraction() throws CPDPException, SAXException, IOException, ParserConfigurationException{
		Set<Xref> xrefs = CPDPExtractXRefsFactory.getXRefs(XMLUtils.readXMLInputStream(stream), null);
		
		Set<Xref> compXrefs = new HashSet<Xref>();
		addXrefToSet(compXrefs, "Source Database Homepage", "http://mlpcn.florida.scripps.edu/","",null);
		addXrefToSet(compXrefs, "AID", "2770", "Summary", null);
		addXrefToSet(compXrefs, "pmid", "3796602", "Reference 1", null);
		addXrefToSet(compXrefs, "protein", "9629429", "", true);
		addXrefToSet(compXrefs, "gene", "7026", "", false);
		addXrefToSet(compXrefs, "taxonomy", "9606", "", false);
		addXrefToSet(compXrefs, "omim", "107773", "", false);
		
		assertEquals("Xrefs were not setup correctly.", compXrefs, xrefs);

	}
	
	private void addXrefToSet(Set<Xref> xrefs, String type, String value, String comment, Boolean isTarget){
		Xref xx = new Xref(type, value, comment, isTarget);
		xrefs.add(xx);
	}
	
	@Test
	public void tidExctraction() throws CPDPException, SAXException, IOException, ParserConfigurationException{
		List<ResultTid> tids = CPDPExtractTIDsFactory.getTIDs(XMLUtils.readXMLInputStream(stream));
		
		List<ResultTid> compTids = new ArrayList<ResultTid>();
		ResultTid tid = new ResultTid("IC50","concentration at which 50 percent of the activity in the antagonist assay is observed.","float","um");
		tid.setIsActiveConcentration(true);
		compTids.add(tid);
		tid = new ResultTid("LogIC50","log of the concentration at which 50 percent of the activity in the antagonist assay is observed.","float","none");
		compTids.add(tid);
		tid = new ResultTid("Qualifier","Activity Qualifier identifies if the resultant data IC50 came from a fitted curve or was determined manually to be less than or greater than its listed IC50 concentration.","string","none");
		compTids.add(tid);
		tid = new ResultTid("Maximal Response","The maximal or asymptotic response above the baseline as concentration increases without bound.","float", "none");
		compTids.add(tid);
		tid = new ResultTid("Baseline Response","Adjustable baseline of the curve fit, minimal response value.","float","none");
		compTids.add(tid);
		tid = new ResultTid("Inflection Point Concentration","The concentration value for the inflection point of the curve.","float","um");
		compTids.add(tid);
		tid = new ResultTid("Hill Slope","The variable HillSlope describes the steepness of the curve. This variable is called the Hill slope, the slope factor, or the Hill coefficient. If it is positive, the curve increases as X increases. If it is negative, the curve decreases as X increases. A standard sigmoid dose-response curve (previous equation) has a Hill Slope of 1.0. When HillSlope is less than 1.0, the curve is more shallow. When HillSlope is greater than 1.0, the curve is steeper. The Hill slope has no units.","float","none");
		compTids.add(tid);
		tid = new ResultTid("Response Range","The range of Y.","float","none");
		compTids.add(tid);
		tid = new ResultTid("Chi Square","A measure for the 'goodness' of a fit. The chi-square test (Snedecor and Cochran, 1989) is used to test if a sample of data came from a population with a specific distribution.","float","none");
		compTids.add(tid);
		tid = new ResultTid("Rsquare","This statistic measures how successful the fit explains the variation of the data; R-square is the square of the correlation between the response values and the predicted response values.","float","none");
		compTids.add(tid);
		tid = new ResultTid("Number of DataPoints","Overall number of data points of normalized percent inhibition that was used for calculations (includes all concentration points); in some cases a data point can be excluded as outlier.","int","none");
		compTids.add(tid);
		tid = new ResultTid("Excluded Points","Flags to indicate which of the dose-response points were excluded from analysis. (1) means the point was excluded and (0) means the point was not excluded.","string","none");
		compTids.add(tid);
		Double[] concentrations = new Double[]{50.0,	25.0,	12.5,	6.3,	3.2,	1.6,	0.8,	0.4,	0.2,	0.1};
		for(Double conc: concentrations){
			for(int ii = 1; ii<=3; ii++){
				tid = new ResultTid(String.format("Inhibition at %s uM [%s]",conc,ii),String.format("Percent inhibition at compound concentration %s uM. replicate [%s]", conc, ii),"float","percent",conc,1);
				compTids.add(tid);
			}
		}

		assertEquals("Result TIDs were not setup correctly.", compTids, tids);
	}
	
	@Test
	public void categorizedCommentsExtraction() throws SAXException, IOException, ParserConfigurationException{
		List<CategorizedComment> comments = CPDPXMLProcess.getCategorizedComments(XMLUtils.readXMLInputStream(stream));
		
		
//		for(CategorizedComment cc: comments){
//			System.out.println(String.format("\"%s\",\"%s\"",cc.getCommentTag(), cc.getCommentValue()));
//		}
		
		List<CategorizedComment> compComments = new ArrayList<CategorizedComment>();
		compComments.add(new CategorizedComment("Probe Type","antagonist"));
		compComments.add(new CategorizedComment("BSL","bsl 2"));
		compComments.add(new CategorizedComment("Cell Line","293"));
		compComments.add(new CategorizedComment("Assay Type","signal transduction assay:reporter-gene assay"));
		compComments.add(new CategorizedComment("Result Type","concentration endpoint:concentration response endpoint:IC50"));
		compComments.add(new CategorizedComment("Assay Readout Content","single parameter"));
		compComments.add(new CategorizedComment("Assay Readout Type","measured value"));
		compComments.add(new CategorizedComment("Signal Direction","signal decrease:signal decrease corresponding to inhibition"));
		compComments.add(new CategorizedComment("Assay Detection Method Type","luminescence method:chemiluminescence"));
		compComments.add(new CategorizedComment("Assay Detection Instrument","PerkinElmer ViewLux"));
		
		assertEquals("Categorized Comments not setup correctly.", compComments, comments);
	
	}

	/**
	 * Creates PubChemAssay Object from CPDP XML
	 */
	@Test
	public void createPubChemAssayObject() throws SAXException, IOException, ParserConfigurationException, CPDPException {
		Document cpdp = XMLUtils.readXMLInputStream(stream);
		PubChemAssay assay = CPDPXMLProcess.processCPDPXML(cpdp);

	}

	private void printStringList(List<String> strings, String string) {
		for (String dd : strings)
			log.debug(string + dd);
	}

	/*
	 * Creates PubChem XML File from CPDP XML
	 */
	@Test
	public void createPubChemXMLFile() throws SAXException, IOException, ParserConfigurationException, CPDPException {
		File file = CPDPXMLProcess.createPubChemXMLFile(stream);
		log.debug("PubChem XML: " + file.getAbsolutePath());
	}

	/*
	 * Creates PubChem XML Tool Excel File from CPDP XML
	 */
	@Test
	public void createExcelFile() throws InvalidFormatException, SAXException, IOException, ParserConfigurationException,
			CPDPException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		File file = CPDPXMLProcess.createExcel(stream);
		log.debug("PubChem Excel: " + file.getAbsolutePath());
	}

}
