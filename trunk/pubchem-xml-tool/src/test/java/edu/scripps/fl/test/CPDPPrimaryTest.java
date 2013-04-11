package edu.scripps.fl.test;

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

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.dom4j.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import edu.scripps.fl.pubchem.cpdp.CPDPException;
import edu.scripps.fl.pubchem.cpdp.CPDPExtractPCAssayFactory;
import edu.scripps.fl.pubchem.cpdp.CPDPExtractPanelFactory;
import edu.scripps.fl.pubchem.cpdp.CPDPExtractTIDsFactory;
import edu.scripps.fl.pubchem.cpdp.CPDPExtractXRefsFactory;
import edu.scripps.fl.pubchem.cpdp.CPDPXMLProcess;
import edu.scripps.fl.pubchem.xml.model.CategorizedComment;
import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;
import edu.scripps.fl.xml.XMLUtils;

/**
 * 
 * @author scanny
 * @purpose
 * 
 */
@RunWith(JUnit4.class)
public class CPDPPrimaryTest {

	InputStream stream;
	private static final Logger log = LoggerFactory.getLogger(CPDPPrimaryTest.class);

	@BeforeClass
	public static void configureLogger() {
		DOMConfigurator.configure(CPDPPrimaryTest.class.getClassLoader().getResource("log4j.config.xml"));
	}

	/*
	 * Loads test CPDP XML
	 */
	@Before
	public void uploadTestCPDP() {
		stream = CPDPPrimaryTest.class.getClassLoader().getResourceAsStream("test_CPDP_Doc.xml");
	}

	@After
	public void closeStream() throws IOException {
		stream.close();
	}
	

	@Test
	public void assayExtraction() throws CPDPException, SAXException, IOException, ParserConfigurationException {
		PubChemAssay assay = CPDPExtractPCAssayFactory.getPubChemAssay(XMLUtils.readXMLInputStream(stream));
		String name = "Luminescence-based cell-based primary high throughput screening assay to identify agonists of the DAF-12 from the parasite S. stercoralis (ssDAF-12).";
		assertEquals("Assay name is not correct.", name, assay.getName());
		assertEquals("Assay activity outcome method is not correct.","screening",assay.getActivityOutcomeMethod());
		assertEquals("External Reg ID is not correct.", "SSDAF12_AG_LUMI_1536_1X%ACT PRUN",assay.getExternalRegId());
		assertEquals("Grant number is not correct.","U19 DK062434",assay.getGrantNumber());
		assertEquals("Project Category is not correct.", "mlpcn",assay.getProjectCategory());
		assertEquals("Source is not correct.","The Scripps Research Institute Molecular Screening Center",assay.getSource());

		
		//description checking
		List<String> description = new ArrayList<String>();
		description.add("Source (MLPCN Center Name): The Scripps Research Institute Molecular Screening Center");
		description.add("Affiliation: UT Southwestern");
		description.add("Assay Provider: David Mangelsdorf, UT Southwestern");
		description.add("Network: Molecular Library Probe Production Centers Network (MLPCN)");
		description.add("Grant Proposal Number: U19 DK062434");
		description.add("Grant Proposal Pi: David Mangelsdorf, UT Southwestern");
		description.add("External Assay ID: SSDAF12_AG_LUMI_1536_1X%ACT PRUN");
		description.add("");
		description.add("Name: Luminescence-based cell-based primary high throughput screening assay to identify agonists of the DAF-12 from the parasite S. stercoralis (ssDAF-12).");
		description.add("");
		description.add("Description:");
		description.add("");
		description.add("Parasitic helminthes (worms) are a significant health and economic burden: over two billion people are infected by helminthes [1], and parasitic nematodes cause billions of dollars of crop damage each year in the United States [2]. The developmental stages of these organisms are widely studied [3, 4].  One stage, dauer (German for “duration,” also known as an alternative L3 larval stage) covers an alternative larval stage in which development stops and the worms enter a hibernation-like state in which they can survive extremely harsh environmental conditions, often for years.  In the case of parasitic nematodes, this resting state is quite often the infectious state [5]. As the burden of parasitic nematodes grows in the face of emerging resistance to the few existing antihelminthic agents, it is becoming increasingly important to understand the life cycles of parasitic worms so that new drugs may be developed [1]. The nuclear receptor DAF-12 (for “dauer formation”), first identified in C. elegans, is known to control many nematode species’ entry into and exit from the dauer resting state [6].  Daf-12 belongs to a family of over 30 genes which transduce environmental signals to influence the choice between dauer or reproductive development.  Favorable environments activate insulin/IGF and TGF-beta pathways converge, leading to production of the steroid hormone dafachronic acid (DA), which binds and activates Daf-12 [7].  Currently available antihelminthic agents, to which resistance is beginning to emerge, act primarily on the feeding stages of the worms and have little effect on the infectious stages [8].  Therefore, pharmacologic agonists developed through high-throughput screening would be used both practically as nematicides and academically as tools to characterize the role of DAF-12 in modulating life cycle [8, 9].");
		description.add("");
		description.add("References:");
		description.add("");
		description.add("1.	Jasmer, D.P., A. Goverse, and G. Smant, Parasitic nematode interactions with mammals and plants. Annu Rev Phytopathol, 2003. 41: p. 245-70.");
		description.add("2.	Hotez, P.J., J. Bethony, M.E. Bottazzi, S. Brooker, D. Diemert, and A. Loukas, New technologies for the control of human hookworm infection. Trends Parasitol, 2006. 22(7): p. 327-31");
		description.add("3.	Mooijaart, S.P., B.W. Brandt, E.A. Baldal, J. Pijpe, M. Kuningas, M. Beekman, B.J. Zwaan, P.E. Slagboom, R.G. Westendorp, and D. van Heemst, C. elegans DAF-12, Nuclear Hormone Receptors and human longevity and disease at old age. Aging Res Rev, 2005. 4(3): p. 351-71");
		description.add("4.	Brenner, S., The genetics of Caenorhabditis elegans. Genetics, 1974. 77(1): p. 71-94");
		description.add("5.	Motola, D.L., C.L. Cummins, V. Rottiers, K.K. Sharma, T. Li, Y. Li, K. Suino-Powell, H.E. Xu, R.J. Auchus, A. Antebi, and D.J. Mangelsdorf, Identification of ligands for DAF-12 that govern dauer formation and reproduction in C. elegans. Cell, 2006. 124(6): p. 1209-23");
		description.add("6.	Antebi, A., W.H. Yeh, D. Tait, E.M. Hedgecock, and D.L. Riddle, daf-12 encodes a nuclear receptor that regulates the dauer diapause and developmental age in C. elegans. Genes Dev, 2000. 14(12): p. 1512-27.");
		description.add("7.	Gerisch, B. and A. Antebi, Hormonal signals produced by DAF-9/cytochrome P450 regulate C. elegans dauer diapause in response to environmental cues. Development, 2004. 131(8): p. 1765-76.");
		description.add("8.	Wang, Z., X.E. Zhou, D.L. Motola, X. Gao, K. Suino-Powell, A. Conneely, C. Ogata, K.K. Sharma, R.J. Auchus, J.B. Lok, J.M. Hawdon, S.A. Kliewer, H.E. Xu, and D.J. Mangelsdorf, Identification of the nuclear receptor DAF-12 as a therapeutic target in parasitic nematodes. Proc Natl Acad Sci U S A, 2009. 106(23): p. 9138-43");
		description.add("9.	Schroeder, F.C., Small molecule signaling in Caenorhabditis elegans. ACS Chem Biol, 2006. 1(4): p. 198-200.");
		description.add("10.	Lok, J.B., Strongyloides stercoralis: a model for translational research on parasitic nematode biology. WormBook, 2007: p. 1-18.");
		description.add("");
		description.add("Keywords:");
		description.add("");
		description.add("ssDAF12, daf12, daf-12, S. stercoralis,  Caenorhabditis elegans, C. elegans, primary screen, primary, PRUN, lumi, luminescence, HTS, high throughput screen, 1536, Scripps Florida, The Scripps Research Institute Molecular Screening Center, SRIMSC, Molecular Libraries Probe Production Centers Network, MLPCN.");
		
		assertEquals("Description not as expected.", description, assay.getDescription());
		
		List<String> protocol = new ArrayList<String>();
		protocol.add("Assay Overview:");
		protocol.add("");
		protocol.add("The purpose of this assay is to identify compounds that act as agonists of the nuclear receptor Daf-12 from S.stercoralis. In this assay, HEK293 cells are co-transfected with a DAF12-responsive reporter plasmid (lit1-tk-luc) and expression vectors encoding ssDAF12 and GRIP1. The ability of compounds to increase transcriptional activity is assessed by measuring luciferase expression from the reporter gene plasmid. Compounds were tested in singlicate at a final nominal concentration of 6.8 uM.");
		protocol.add("");
		protocol.add("Protocol Summary:");
		protocol.add("");
		protocol.add("HEK293 cells were routinely cultured in T-175 flasks containing 25 mL of DMEM media supplemented with 10% v/v fetal bovine serum and 1% v/v antibiotic-antimycotic mix at 37 C, 5% CO2 and 95% relative humidity (RH). The day prior to run the assay, the HEK293 cells were harvested using 5 mL of TrypLE reagents and seeded in fresh media at a density of 10 million cells per T175 flask. The following day, cells were transfected with 1 mL of serum-free OptiMEM containing 8 ug of the ssDAF12-expressing vector, 8 ug of the GRIP1-expressing vector, 20 ug of the lit1-tk-luc reporter plasmid and 80 uL of transfection reagent. Twenty four hours post transfection, cells were harvested using 5 mL of preheated TrypLE and resuspended at a concentration of 1 million cells per mL in phenol-red free DMEM supplemented as above. Delta7-dafachronic acid (D7-DA), a well-characterized agonist for ssDAF12, was used as a positive control.\nThe assay was started by dispensing 5 uL of cell suspension into each well of white, solid-bottom 1536-well plates using a flying reagent dispenser (Aurora) and placed in the incubator for 3 hours. Cells were then treated with 34 nL/well of either test compounds, DMSO (Low Control, final concentration 0.68%) or 3 uM of D7-DA (High Control). Plates were incubated for 24 hours at 37 C, 5% CO2 and 95%RH and then removed from the incubator and equilibrated to room temperature for 10 minutes. Luciferase activity was detected by addition of 5 uL of One-Glo reagent to each well. After a 15 minute incubation time, light emission was measured with the ViewLux reader (PerkinElmer).");
		protocol.add("The percent activation of each test compound was calculated as follows:"
		+ "\n% Activation = 100 * ( ( Test_Compound - Median_Low_Control ) / ( Median_High_Control - Median__Low_Control ) )"
		+"\nWhere:"
		+"\nHigh_Control is defined as wells treated with 3 uM Delta7 Dafachronic Acid"
		+"\nLow_Control is defined as wells treated with DMSO only."
		+"\nTest_Compound is defined as wells treated with test compound."
		+"\n"
		+"\nA mathematical algorithm was used to determine nominally active compounds. Two values were calculated: (1) the average percent activation of all compounds tested, and (2) three times their standard deviation. The sum of these two values was used as a cutoff parameter, i.e. any compound that exhibited greater % activation than the cutoff parameter was declared active."
		+"\n"
		+"\nThe reported PubChem Activity Score has been normalized to 100% observed primary activation. Negative % activation values are reported as activity score zero.");
		protocol.add("");
		protocol.add("List of Reagents:");
		protocol.add("");
		protocol.add("lit1-tk-luc luciferase reporter plasmid (Assay Provider)"
		+ "\nssDAF12 expressiong plasmid (Assay Provider)"
		+"\n"
		+"\nGRIP1 expression plasmid (Assay Provider)"
		+ "\n");
		protocol.add("");
		protocol.add("List of Consumables:");
		protocol.add("");
		protocol.add("HEK293 cells (ATCC, part CRL-1573)"
		+"\nDMEM (Invitrogen, part 11965)"
		+"\nFBS (Hyclone, part SH30088.03)"
		+"\nAntibiotic-Antimycotic 100X Liquid Solution (Gibco, part 15240)"
		+"\nTransIT 293 (Mirus Corporation, part MIR-2700)"
		+"\nOptiMEM (Invitrogen, part 31985)"
		+"\nTrypLE Trypsin Replacement Enzyme (Invitrogen, part 12604)"
		+"\nOne-Glo (Promega, part E6130)"
		+"\n1536-well plates (Greiner part 789173)");

		assertEquals("Protocol is not as expected.", protocol, assay.getProtocol());
		
		
		List<String> comment = new ArrayList<String>();
		comment.add("Due to the increasing size of the MLPCN compound library, this assay may have been run as two or more separate campaigns, each campaign testing a unique set of compounds. All data reported were normalized on a per-plate basis. Possible artifacts of this assay can include, but are not limited to: dust or lint located in or on wells of the microtiter plate, and compounds that modulate luciferase activity and hence well luminescence. All test compound concentrations reported above and below are nominal; the specific test concentration(s) for a particular compound may vary based upon the actual sample provided by the MLSMR.");
		assertEquals("Comment is not as expected.", comment, assay.getComment());
		 
	}
	
	@Test
	public void xrefExtraction() throws CPDPException, SAXException, IOException, ParserConfigurationException{
		Set<Xref> xrefs = CPDPExtractXRefsFactory.getXRefs(XMLUtils.readXMLInputStream(stream), null);
		Set<Xref> compXrefs = new HashSet<Xref>();
		addXrefToSet(compXrefs, "Source Database Homepage", "http://mlpcn.florida.scripps.edu/","",null);
		addXrefToSet(compXrefs, "pmid", "14527330", "Reference 1", null);
		addXrefToSet(compXrefs, "pmid", "16709466", "Reference 2", null);
		addXrefToSet(compXrefs, "pmid", "16051528", "Reference 3", null);
		addXrefToSet(compXrefs, "pmid", "4366476", "Reference 4", null);
		addXrefToSet(compXrefs, "pmid", "16529801", "Reference 5", null);
		addXrefToSet(compXrefs, "pmid", "10859169", "Reference 6", null);
		addXrefToSet(compXrefs, "pmid", "15084461", "Reference 7", null);
		addXrefToSet(compXrefs, "pmid", "19497877", "Reference 8", null);
		addXrefToSet(compXrefs, "pmid", "17163670", "Reference 9", null);
		addXrefToSet(compXrefs, "pmid", "18050500", "Reference 10", null);
		addXrefToSet(compXrefs, "protein", "71987181", "", false);
		addXrefToSet(compXrefs, "gene", "181263", "", false);
		addXrefToSet(compXrefs, "taxonomy", "6239", "", false);
		
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
		ResultTid tid = new ResultTid("Activation at 6.8 uM","Percent activation at compound concentration 6.8 uM.","float","percent");
		tid.setTidConcentration(6.8);
		compTids.add(tid);
		
		assertEquals("Result TIDs were not setup correctly.", compTids, tids);
	}
	
	@Test
	public void categorizedCommentsExtraction() throws SAXException, IOException, ParserConfigurationException{
		List<CategorizedComment> comments = CPDPXMLProcess.getCategorizedComments(XMLUtils.readXMLInputStream(stream));
		
		List<CategorizedComment> compComments = new ArrayList<CategorizedComment>();
		compComments.add(new CategorizedComment("Probe Type","agonist"));
		compComments.add(new CategorizedComment("BSL","bsl 1"));
		compComments.add(new CategorizedComment("Cell Line","293"));
		compComments.add(new CategorizedComment("Assay Type","signal transduction assay:reporter-gene assay"));
		compComments.add(new CategorizedComment("Result Type","response endpoint:percent response:percent activation"));
		compComments.add(new CategorizedComment("Assay Readout Content","single parameter"));
		compComments.add(new CategorizedComment("Assay Readout Type","measured value"));
		compComments.add(new CategorizedComment("Signal Direction","signal increase:signal increase corresponding to activation"));
		compComments.add(new CategorizedComment("Assay Detection Method Type","luminescence method:bioluminescence"));
		compComments.add(new CategorizedComment("Assay Detection Instrument","ViewLux ultraHTS microplate imager"));
		
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
