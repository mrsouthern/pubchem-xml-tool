/*
 * Copyright 2010 The Scripps Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.scripps.fl.pubchem.xmltool.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.security.util.Debug;

import edu.scripps.fl.pubchem.PubChemDeposition;
import edu.scripps.fl.pubchem.PubChemFactory;
import edu.scripps.fl.pubchem.report.ReportController;
import edu.scripps.fl.pubchem.xml.extract.XMLExtractorController;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PubChemXMLExtractorGUI extends JPanel implements ActionListener, MouseListener {
	
	private JLabel jlbFileXML;
	private JTextField jtfFileXML;
	private JButton jbnFileXML, jbnRunExtractor, jbnCreateReport;
	private GridBagConstraints gbc01, gbc02, gbc03, gbc04, gbc05;
	private String aidText = "If you type in an AID number, the AID will be fetched directly from PubChem.";
	private GUIComponent gc = new GUIComponent();
	private PubChemDeposition pcDep = new PubChemDeposition();
	private static final Logger log = LoggerFactory.getLogger(PubChemXMLExtractorGUI.class);
//	isInternal is initially set in SwingGUI
	private Boolean isInternal;
	
	public PubChemXMLExtractorGUI(Boolean isInternal) {
		DOMConfigurator.configure(PubChemXMLExtractorGUI.class.getClassLoader().getResource("log4j.config.xml"));
		this.isInternal = isInternal;
		
		setBorder(BorderFactory.createTitledBorder("PubChem XML Extractor"));
		setLayout(new GridBagLayout());
		
		setUpGUIComponents();

		add(jlbFileXML, gbc01);
		add(jtfFileXML, gbc02);
		add(jbnFileXML, gbc03);
		add(jbnRunExtractor, gbc04);
		add(jbnCreateReport, gbc05);
		addMouseListener(this);
	}
	
	public void setUpGUIComponents(){
		jlbFileXML = gc.createJLabel("PubChem XML:");
		jtfFileXML = gc.createJTextField(aidText);
		jtfFileXML.addMouseListener(this);
		jbnFileXML = gc.createJButton("Open16",
				"Choose a PubChem xml file to extract TID, Panel, and Xref information from or type in a PubChem AID number.", "icon");
		jbnRunExtractor = gc.createJButton("Get Excel Workbook", "Run the extractor program.", "text");
		jbnCreateReport = gc.createJButton("Create Report", "Create report from XML file.", "text");
		
		jbnFileXML.addActionListener(this);
		jbnRunExtractor.addActionListener(this);
		jbnCreateReport.addActionListener(this);
		
		gbc01 = gc.createGridBagConstraint(0, 0, jlbFileXML, "line start");
		gbc02 = gc.createGridBagConstraint(1, 0, jtfFileXML, "line start");
		gbc03 = gc.createGridBagConstraint(2, 0, jbnFileXML, "center");
		gbc04 = gc.createGridBagConstraint(1, 2, jbnRunExtractor, "line start");
		gbc05 = gc.createGridBagConstraint(1, 2, jbnCreateReport, "line end");
	}
	
	public void actionPerformed(ActionEvent e) {
		try{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (e.getSource() == jbnFileXML)
				gc.fileChooser(jtfFileXML, "", "open");
			else if (e.getSource() == jbnRunExtractor) {
				URL template = templateForExcel();
				File fileExcelOutput = extract(template);
				if(fileExcelOutput != null){
					log.info("Opening excel file through Desktop: " + fileExcelOutput);
					Desktop.getDesktop().open(fileExcelOutput);
				}
			}
			else if (e.getSource() == jbnCreateReport){
				URL template = getClass().getClassLoader().getResource("ExcelTemplate.xlsx");
				File fileExcelOutput = extract(template);
				if(fileExcelOutput != null){
					log.info("Opening report through Desktop: " + fileExcelOutput);
					String fileName = FilenameUtils.removeExtension(fileExcelOutput.getAbsolutePath());
					File filePDFOutput = new File(fileName + ".pdf");
					File fileWordOutput	= new File(fileName + ".docx");
					filePDFOutput.deleteOnExit();
					fileWordOutput.deleteOnExit();
					new ReportController().createReport(pcDep, fileExcelOutput, filePDFOutput, fileWordOutput, isInternal);
					gc.openPDF(isInternal, filePDFOutput, this);
					Desktop.getDesktop().open(fileWordOutput);
				}
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		catch(Throwable throwable){
			SwingGUI.handleError(this, throwable);
		}
		
	}
	
	private File extract(URL template) throws Exception{
		String xml = jtfFileXML.getText();
		InputStream is = null;
		if(FilenameUtils.getExtension(xml).equals("xml")){
			is = new FileInputStream(new File(xml));
			xml = FilenameUtils.getBaseName(xml);
		}
		else if(FilenameUtils.getExtension(xml).equals("gz")){
			is = new GZIPInputStream(new FileInputStream(new File(xml)));
			xml = FilenameUtils.getBaseName(xml);
		}
		else{
			Integer aid = Integer.parseInt(xml);
			is = PubChemFactory.getInstance().getPubChemXmlDesc(aid);
			if(is == null)
				is = pcDep.getPubChemAID(aid);
			}
		File fileExcelOutput = new XMLExtractorController().extractPubChemXML(template, is);
		return fileExcelOutput;
	}
	
	
	private URL  templateForExcel(){
		URL template; 
		int nn = JOptionPane.showOptionDialog(this, "If you are editing the Excel file, would you like a BAO categorized comments sheet included?", SwingGUI.APP_NAME, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		String resource =  "ExcelTemplate";
		if(nn == JOptionPane.YES_OPTION)
			resource = resource + "_withBAO_EditingVersion";
			
		if(isInternal)
			resource = resource + "_Internal";
		
		log.info("Getting resource: " + resource);
		template = getClass().getClassLoader().getResource(resource + ".xlsx");

		return template;
	}

	public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() > 0){
				if(e.getSource() == jtfFileXML){
					if(jtfFileXML.getText().equals(aidText))
						jtfFileXML.setText("");
				}
				if(e.getSource() == this){
					if(jtfFileXML.getText().equals(""))
						jtfFileXML.setText(aidText);
				}
			}
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
