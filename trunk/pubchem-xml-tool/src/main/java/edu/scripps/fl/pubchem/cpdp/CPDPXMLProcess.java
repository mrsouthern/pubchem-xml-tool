package edu.scripps.fl.pubchem.cpdp;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.PubChemAssayFactory;
import edu.scripps.fl.pubchem.xml.AssayXML;
import edu.scripps.fl.pubchem.xml.CategorizedCommentXML;
import edu.scripps.fl.pubchem.xml.PanelXML;
import edu.scripps.fl.pubchem.xml.PopulateArray;
import edu.scripps.fl.pubchem.xml.PubChemXMLDoc;
import edu.scripps.fl.pubchem.xml.ResultTidXML;
import edu.scripps.fl.pubchem.xml.XrefXML;
import edu.scripps.fl.pubchem.xml.extract.AssayExtractor;
import edu.scripps.fl.pubchem.xml.extract.CategorizedCommentExtractor;
import edu.scripps.fl.pubchem.xml.extract.PanelExtractor;
import edu.scripps.fl.pubchem.xml.extract.ResultTidExtractor;
import edu.scripps.fl.pubchem.xml.extract.XrefExtractor;
import edu.scripps.fl.pubchem.xml.model.CategorizedComment;
import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.PanelTarget;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;
import edu.scripps.fl.pubchem.xmltool.gui.SwingGUI;

public class CPDPXMLProcess {

	private static final Logger log = LoggerFactory.getLogger(CPDPXMLProcess.class);

	public static void main(String[] args) throws Exception {
		InputStream is = new FileInputStream(new File("C:\\home\\temp\\test.xml"));
		Desktop.getDesktop().open(createExcel(is));
	}
	
	private static Document readXMLInputStream(InputStream is) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); 
		org.w3c.dom.Document doc2 = builder.parse(is);                                       
		DOMReader reader = new DOMReader();                                                  
		Document cpdp = reader.read(doc2);
		return cpdp;
	}
	
	private static PubChemAssay processCPDPXML(Document cpdp) throws Exception{

		PubChemAssay assay = new PubChemAssay();
		Set<Xref> xrefs = new LinkedHashSet<Xref>();
		List<CategorizedComment> comments = new ArrayList<CategorizedComment>();
		List<Panel> panels = new ArrayList<Panel>();
		List<ResultTid> tids = new ArrayList<ResultTid>();
		
		if(null == cpdp.selectSingleNode("//AIDs/AID[@create='true']")){
			// assume you want a summary
			assay = CPDPExtractPCAssayFactory.getSummaryAssay(cpdp);
			xrefs = CPDPExtractXRefsFactory.getSummaryXrefs(cpdp);
		}else{
			//chosen assay
			panels = CPDPExtractPanelFactory.getPanels(cpdp);
			tids = CPDPExtractTIDsFactory.getTIDs(cpdp);
			if(panels.size() > 0)
				tids = CPDPExtractTIDsFactory.getPanelTids(panels, tids);
			
			xrefs = CPDPExtractXRefsFactory.getXRefs(cpdp, panels);
			comments = getCategorizedComments(cpdp);

			assay = CPDPExtractPCAssayFactory.getPubChemAssay(cpdp);
			new PubChemAssayFactory().setUpPubChemAssay(assay, tids, xrefs, panels, comments);
			
		}
		return assay;
	}
	
	private static String getActivityCutoff(Document cpdp) throws ParserConfigurationException, SAXException, IOException{
		if(null == cpdp.selectSingleNode(CPDPExtractUtils.chosenAIDNodePath))
			return "";
		String qualifier = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.CUT_OFF_QUALIFIER)).getText();
		String number = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.CUT_OFF_NUMBER)).getText();
		String units = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.CUT_OFF_UNITS)).getText();
		units = StringUtils.substring(units, units.lastIndexOf(":")+1);
		return String.format("%s %s %s", qualifier, number, units);
	}
	
	
	public static File createPubChemXMLFile(InputStream is) throws Exception{
		Document cpdp = readXMLInputStream(is);
		Document doc = createXML(cpdp);
		File fileOutput = File.createTempFile("pubchem", ".xml", new File("C:\\home\\temp\\"));
		new PubChemXMLDoc().write(doc, fileOutput);
		return fileOutput;
	}
	
	private static Document createXML(Document cpdp) throws Exception{
		PubChemAssay assay = processCPDPXML(cpdp);
		PubChemXMLDoc xmldoc = new PubChemXMLDoc();
		URL url = CPDPXMLProcess.class.getClassLoader().getResource("blank.xml");
		InputStream fileTemplate = url.openStream();
		Document doc = xmldoc.loadPubChemXML(fileTemplate);
		new AssayXML().buildAssayDocument(doc, assay);
		new ResultTidXML().buildTidDocument(doc, assay.getResultTids());
		new XrefXML().buildXrefDocument(doc, assay);
		new PanelXML().buildPanelDocument(doc, assay.getPanels());
		new CategorizedCommentXML().buildCategorizedCommentDocument(doc, assay.getCategorizedComments());
		return doc;
	}
	
	public static File createExcel(InputStream is) throws Exception{
		Document cpdp = readXMLInputStream(is);
		Document doc = createXML(cpdp);
		
		ResultTidExtractor rte = new ResultTidExtractor();
		PanelExtractor pe = new PanelExtractor();
		XrefExtractor xe = new XrefExtractor();
		CategorizedCommentExtractor ce = new CategorizedCommentExtractor();
		AssayExtractor ae = new AssayExtractor();
		
		URL template = CPDPXMLProcess.class.getClassLoader().getResource("ExcelTemplate_Internal.xlsx");
		ExcelTableModel model = ExcelTableModel.load(template.openStream(), true);
		
		rte.fillTidExcelTemplate(model, rte.getTidValuesFromXML(doc));
		List<Panel> panel = pe.getPanelValuesFromXML(doc);
		pe.fillPanelExcelTemplate(model, panel);
		xe.fillXrefExcelTemplate(model, xe.getXrefValuesFromXML(doc, panel));
		ce.fillCategorizedCommentExcelTemplate(model, ce.getCategorizedCommentsFromXML(doc));
		ae.fillAssayExcelTemplate(model, ae.getAssayValuesFromXML(doc));
		
		model.setSheet("Activity Cutoff");
		model.setUseFirstRowAsColumnHeadings(false);
		model.setValueAt(getActivityCutoff(cpdp), 0, 1);
		
		File output = File.createTempFile("AID_", ".xlsx");
		OutputStream outputStream = new FileOutputStream(output);
		model.getWorkbook().write(outputStream);
		outputStream.close();
		return output;
	}
	
	
	public static void createSummary(){
		
	}
	


	public static List<CategorizedComment> getCategorizedComments(Document cpdp) {
		List<CategorizedComment> comments = new ArrayList<CategorizedComment>();
		String[] commentNodes = new String[] { CPDPExtractUtils.PROBE_TYPE, CPDPExtractUtils.BSL, CPDPExtractUtils.CELL_LINE, CPDPExtractUtils.ASSAY_TYPE, CPDPExtractUtils.RESULT_TYPE, CPDPExtractUtils.ASSAY_READOUT_CONTENT,
				CPDPExtractUtils.ASSAY_READOUT_TYPE, CPDPExtractUtils.SIGNAL_DIRECTION, CPDPExtractUtils.ASSAY_DETECTION_METHOD_TYPE, CPDPExtractUtils.ASSAY_DETECTION_INSTRUMENT,
				CPDPExtractUtils.EXCITATION_WAVELENGTH, CPDPExtractUtils.EMISSION_WAVELENGTH, CPDPExtractUtils.ABSORBANCE_WAVELENGTH , CPDPExtractUtils.CURVE_FIT_EQUATION, CPDPExtractUtils.CURVE_FIT_EQUATION_V2};
		
		for(String nodeS: commentNodes){
			Node node = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(nodeS));
			if(null == node) continue;
			
			String text = node.getText();
			if(null != text && ! "".equals(text) && ! "na".equalsIgnoreCase(text)){
				CategorizedComment cc = new CategorizedComment();
				cc.setCommentTag(nodeS.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2"));
				cc.setCommentValue(text);
				comments.add(cc);
			}
		}
		return comments;
	}



	
	
}
