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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.scripps.fl.pubchem.PubChemDeposition;
import edu.scripps.fl.pubchem.report.ReportController;
import edu.scripps.fl.pubchem.xml.PubChemXMLCreatorController;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PubChemXMLCreatorGUI extends JPanel implements ActionListener, MouseListener {
	
	private JLabel jlbFileTemplate, jlbFileExcel;
	private JTextField jtfFileExcel, jtfFileTemplate;
	private JTextPane jtpExcelTemplate, jtpExample;
	private JButton jbnFileExcel, jbnFileTemplate, jbnRunCreator, jbnReportCreator;
	private GUIComponent gc = new GUIComponent();
	private String template = "If this field is left alone or empty, a blank xml template will be used.";
	private GridBagConstraints gbc01, gbc02, gbc03, gbc04, gbc05, gbc06, gbc07, gbc08, gbc09, gbc10;
	private PubChemDeposition pcDep = new PubChemDeposition();
	private static final Logger log = LoggerFactory.getLogger(PubChemXMLCreatorGUI.class);
	private Boolean isInternal;
	private String notError = "This is not an error message.\n\nHere are a few suggestions for your Excel Workbook:\n\n";
	
	public PubChemXMLCreatorGUI(Boolean isInternal){
		DOMConfigurator.configure(PubChemXMLCreatorGUI.class.getClassLoader().getResource("log4j.config.xml"));
//		isInternal is initially set in SwingGUI
		this.isInternal = isInternal;
	
			setBorder(BorderFactory.createTitledBorder("PubChem XML Creator"));
	        setLayout(new GridBagLayout());
			
			setUpGUIComponents();

			add(jlbFileTemplate, gbc01);			
			add(jtfFileTemplate, gbc02);
			add(jbnFileTemplate, gbc03);
			add(jlbFileExcel, gbc04);
			add(jtfFileExcel, gbc05);
			add(jbnFileExcel, gbc06);
			add(jbnRunCreator, gbc07);
			add(jbnReportCreator, gbc08);
			add(jtpExcelTemplate, gbc09);
			add(jtpExample, gbc10);
			addMouseListener(this);
		
	}
	
	public void setUpGUIComponents(){
		jlbFileTemplate = gc.createJLabel("XML Template:");
		jlbFileExcel = gc.createJLabel("Excel Workbook:");
		
		jtfFileTemplate = gc.createJTextField(template);
		jtfFileExcel = gc.createJTextField("");

		jbnFileTemplate = gc.createJButton("Open16", "Choose a PubChem xml template to add tid, panel, and xref information to. A blank template is already provided.", "icon");
		jbnFileExcel = gc.createJButton("Open16", "Choose an Excel file containing tid, panel, or xref information in the appropriate format.", "icon");
		jbnRunCreator = gc.createJButton("Create PubChem XML", "Run the program.", "text");
		jbnReportCreator = gc.createJButton("Create Report", "Creates a report from excel workbook.", "text");
		
		jtpExcelTemplate = gc.createJTextPane("Excel Template");
		jtpExcelTemplate.setToolTipText("Save excel template under a new file name and add tid, panel, and/or xref information.");
		jtpExample = gc.createJTextPane("Example Excel File");
		
		if( ! Desktop.isDesktopSupported() ){
			jtpExcelTemplate.setVisible(false);
			jtpExample.setVisible(false);
		}
		
		jbnFileTemplate.addActionListener(this);
		jbnFileExcel.addActionListener(this);		
		jbnRunCreator.addActionListener(this);
		jbnReportCreator.addActionListener(this);
		jtpExcelTemplate.addMouseListener(this);
		jtpExample.addMouseListener(this);
		jtfFileTemplate.addMouseListener(this);
		
		
		gbc01 = gc.createGridBagConstraint(0, 0, jlbFileTemplate, "line start");
		gbc02 = gc.createGridBagConstraint(1, 0, jtfFileTemplate, "line start");
		gbc03 = gc.createGridBagConstraint(2, 0, jbnFileTemplate, "line start");
		gbc04 = gc.createGridBagConstraint(0, 1, jlbFileExcel, "line start");
		gbc05 = gc.createGridBagConstraint(1, 1, jtfFileExcel, "line start");
		gbc06 = gc.createGridBagConstraint(2, 1, jbnFileExcel, "line start");
		gbc07 = gc.createGridBagConstraint(1, 4, jbnRunCreator, "line start");
		gbc08 = gc.createGridBagConstraint(1, 4, jbnReportCreator, "line end");
		gbc09 = gc.createGridBagConstraint(3, 1, jtpExcelTemplate, "center");
		gbc10 = gc.createGridBagConstraint(3, 0, jtpExample, "center");
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (e.getSource() == jbnFileTemplate) {
				gc.fileChooser(jtfFileTemplate, ".xml", "open");
				jtfFileTemplate.setEnabled(true);
			} 
			else if (e.getSource() == jbnFileExcel) {
				gc.fileChooser(jtfFileExcel, ".xlsx", "open");
			} 
			else if (e.getSource() == jbnRunCreator) {
				String stringTemplate = jtfFileTemplate.getText();
				InputStream fileTemplate;
				if (stringTemplate.equals(template) | stringTemplate.equals("")){
					URL url = getClass().getClassLoader().getResource("blank.xml");
					fileTemplate = url.openStream();
				}
				else
					fileTemplate = new FileInputStream(jtfFileTemplate.getText());
				File fileExcel = new File(jtfFileExcel.getText());
				File fileOutput = File.createTempFile("pubchem", ".xml");
				fileOutput.deleteOnExit();
				PubChemAssay assay = new PubChemXMLCreatorController().createPubChemXML(fileTemplate, fileExcel, fileOutput);
				String message = assay.getMessage();
				if(! message.equals("")){
					int nn = JOptionPane.showOptionDialog(this, notError + message + "\nWould you like to edit your Excel Workbook?", SwingGUI.APP_NAME, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
					if(nn == JOptionPane.YES_OPTION){
						log.info("Opening Excel Workbook with Desktop: " + fileExcel);
						Desktop.getDesktop().open(fileExcel);
					}
					else{
						log.info("Opening XML file: " + fileOutput);
						Desktop.getDesktop().open(fileOutput);
					}
				}
				else{
					log.info("Opening XML file: " + fileOutput);
					Desktop.getDesktop().open(fileOutput);
				}
			}
			else if(e.getSource() == jbnReportCreator){
				File fileExcel = new File(jtfFileExcel.getText());
				File filePDFOutput = File.createTempFile("PubChem_PDF_Report", ".pdf");
				File fileWordOutput	= File.createTempFile("PubChem_Word_Report", ".docx");
				filePDFOutput.deleteOnExit();
				fileWordOutput.deleteOnExit();
				ArrayList<PubChemAssay> assay = new ReportController().createReport(pcDep, fileExcel, filePDFOutput, fileWordOutput, isInternal);
				String message = null;
				for(PubChemAssay xx: assay){
					message = xx.getMessage();
					if(! message.equals("")){
						int nn = JOptionPane.showOptionDialog(this, notError + message + "\nWould you like to edit your Excel Workbook?", SwingGUI.APP_NAME, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
						if(nn == JOptionPane.YES_OPTION){
							log.info("Opening Excel Workbook with Desktop: " + fileExcel);
							Desktop.getDesktop().open(fileExcel);
						}
						else{
							gc.openPDF(isInternal, filePDFOutput, this);
							Desktop.getDesktop().open(fileWordOutput);
						}
					}
					else{
						gc.openPDF(isInternal, filePDFOutput, this);
						Desktop.getDesktop().open(fileWordOutput);
					}
				}
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} 
		catch (Throwable throwable) {
			SwingGUI.handleError(this, throwable);
		}
	}

	
	public void mouseClicked(MouseEvent e) {
		try {
			if (e.getClickCount() > 0) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if (e.getSource() == jtpExample) {
					URL url = getClass().getClassLoader().getResource("ExampleExcel.xlsx");
					File tmpFile = File.createTempFile("example", ".xlsx");
					FileUtils.copyURLToFile(url, tmpFile);
					tmpFile.deleteOnExit();
					Desktop.getDesktop().open(tmpFile);
				} else if (e.getSource() == jtpExcelTemplate) {
					File saveFile = gc.fileChooser(jtfFileExcel, ".xlsx", "save");
					if (saveFile != null) {
						URL url = getClass().getClassLoader().getResource("ExcelTemplate_withBAO.xlsx");
						OutputStream out = new FileOutputStream(saveFile, true);
						IOUtils.copy(url.openStream(), out);
						String output = FilenameUtils.concat(FilenameUtils.getFullPath(saveFile.toString()), FilenameUtils
								.getBaseName(saveFile.toString()));
						Desktop.getDesktop().open(new File(saveFile.toString()));
					}
				}
				else if (e.getSource() == jtfFileTemplate && jtfFileTemplate.getText().equals(template)){
					jtfFileTemplate.setText("");
				}
				else if(e.getSource() == this ){
					if(jtfFileTemplate.getText().equals("")){
						jtfFileTemplate.setText(template);
					}
				}
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} catch (Throwable throwable) {
			SwingGUI.handleError(this, throwable);
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
