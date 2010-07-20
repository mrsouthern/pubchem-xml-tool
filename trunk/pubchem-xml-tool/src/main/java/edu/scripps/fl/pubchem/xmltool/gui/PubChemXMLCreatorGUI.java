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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.scripps.fl.pubchem.xml.PubChemXMLCreatorController;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PubChemXMLCreatorGUI extends JPanel implements ActionListener, MouseListener {
	
	private JLabel jlbFileTemplate, jlbFileExcel, jlbFileOutput;
	private JTextField jtfFileExcel, jtfFileOutput, jtfFileTemplate;
	private JTextPane jtpExcelTemplate, jtpExample;
	private JButton jbnFileExcel, jbnFileOutput, jbnFileTemplate, jbnRunCreator;
	private GUIComponent gc = new GUIComponent();
	private String template = "If this field is left alone or empty, a blank xml template will be used.",
				   output = "If this field is left alone or empty, a temporary xml output file will be made.";
	private GridBagConstraints gbc01, gbc02, gbc03, gbc04, gbc05, gbc06, gbc07, gbc08, gbc09, gbc10, gbc11, gbc12;
	private static final Logger log = LoggerFactory.getLogger(PubChemXMLCreatorGUI.class);
	
	public PubChemXMLCreatorGUI(){
		DOMConfigurator.configure(PubChemXMLCreatorGUI.class.getClassLoader().getResource("log4j.config.xml"));

			setBorder(BorderFactory.createTitledBorder("PubChem XML Creator"));
	        setLayout(new GridBagLayout());
			
			setUpGUIComponents();

			add(jlbFileTemplate, gbc01);			
			add(jtfFileTemplate, gbc02);
			add(jbnFileTemplate, gbc03);
			add(jlbFileExcel, gbc04);
			add(jtfFileExcel, gbc05);
			add(jbnFileExcel, gbc06);
			add(jlbFileOutput, gbc07);
			add(jtfFileOutput, gbc08);
			add(jbnFileOutput, gbc09);
			add(jbnRunCreator, gbc10);
			add(jtpExcelTemplate, gbc11);
			add(jtpExample, gbc12);
			addMouseListener(this);
		
	}
	
	public void setUpGUIComponents(){
		jlbFileTemplate = gc.createJLabel("XML Template:");
		jlbFileExcel = gc.createJLabel("Excel Workbook:");
		jlbFileOutput = gc.createJLabel("Output XML:");
		
		jtfFileTemplate = gc.createJTextField(template);
		jtfFileExcel = gc.createJTextField("");
		jtfFileOutput = gc.createJTextField(output);

		jbnFileTemplate = gc.createJButton("Open16", "Choose a PubChem xml template to add tid, panel, and xref information to. A blank template is already provided.", "icon");
		jbnFileExcel = gc.createJButton("Open16", "Choose an Excel file containing tid, panel, or xref information in the appropriate format.", "icon");
		jbnFileOutput = gc.createJButton("Open16", "Choose where the new PubChem xml file will be saved.", "icon");
		jbnRunCreator = gc.createJButton("Create PubChem XML", "Run the program.", "text");
		
		jtpExcelTemplate = gc.createJTextPane("Excel Template");
		jtpExcelTemplate.setToolTipText("Save excel template under a new file name and add tid, panel, and/or xref information.");
		jtpExample = gc.createJTextPane("Example Excel File");
		
		if( ! Desktop.isDesktopSupported() ){
			jtpExcelTemplate.setVisible(false);
			jtpExample.setVisible(false);
		}
		
		jbnFileTemplate.addActionListener(this);
		jbnFileExcel.addActionListener(this);
		jbnFileOutput.addActionListener(this);		
		jbnRunCreator.addActionListener(this);
		jtpExcelTemplate.addMouseListener(this);
		jtpExample.addMouseListener(this);
		jtfFileTemplate.addMouseListener(this);
		jtfFileOutput.addMouseListener(this);
		
		
		gbc01 = gc.createGridBagConstraint(0, 0, jlbFileTemplate, "line start");
		gbc02 = gc.createGridBagConstraint(1, 0, jtfFileTemplate, "line start");
		gbc03 = gc.createGridBagConstraint(2, 0, jbnFileTemplate, "line start");
		gbc04 = gc.createGridBagConstraint(0, 1, jlbFileExcel, "line start");
		gbc05 = gc.createGridBagConstraint(1, 1, jtfFileExcel, "line start");
		gbc06 = gc.createGridBagConstraint(2, 1, jbnFileExcel, "line start");
		gbc07 = gc.createGridBagConstraint(0, 3, jlbFileOutput, "line start");
		gbc08 = gc.createGridBagConstraint(1, 3, jtfFileOutput, "line start");
		gbc09 = gc.createGridBagConstraint(2, 3, jbnFileOutput, "line start");
		gbc10 = gc.createGridBagConstraint(1, 4, jbnRunCreator, "center");
		gbc11 = gc.createGridBagConstraint(3, 1, jtpExcelTemplate, "center");
		gbc12 = gc.createGridBagConstraint(3, 0, jtpExample, "center");
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (e.getSource() == jbnFileTemplate) {
				gc.fileChooser(jtfFileTemplate, ".xml", "open");
				jtfFileTemplate.setEnabled(true);
			} else if (e.getSource() == jbnFileExcel) {
				File file = gc.fileChooser(jtfFileExcel, ".xlsx", "open");
				if (file != null) {
					String fileString = file.toString();
					String output = FilenameUtils.concat(FilenameUtils.getFullPath(fileString), FilenameUtils.getBaseName(fileString));
					jtfFileOutput.setText(output + ".xml");
				}
			} else if (e.getSource() == jbnFileOutput)
				gc.fileChooser(jtfFileOutput, ".xml", "open");
			else if (e.getSource() == jbnRunCreator) {
				String stringTemplate = jtfFileTemplate.getText();
				File fileTemplate;
				if (stringTemplate.equals(template) | stringTemplate.equals("")){
					URL url = getClass().getClassLoader().getResource("blank.xml");
					fileTemplate = File.createTempFile("blank", "xml");
					fileTemplate.deleteOnExit();
					FileUtils.copyURLToFile(url, fileTemplate);
				}
				else
					fileTemplate = new File(jtfFileTemplate.getText());
				File fileExcel = new File(jtfFileExcel.getText());
				String stringOutputXML = jtfFileOutput.getText();
				File fileOutput;
				if(stringOutputXML.equals("") | stringOutputXML.equals(output)){
					fileOutput = File.createTempFile("pubchem", ".xml");
					fileOutput.deleteOnExit();
				}
				else
					fileOutput = new File(jtfFileOutput.getText());
				new PubChemXMLCreatorController().createPubChemXML(fileTemplate, fileExcel, fileOutput);
				Desktop.getDesktop().open(fileOutput);
			} 
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} 
		catch (Exception ex) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			log.error(ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this, ex.getMessage(), SwingGUI.APP_NAME, JOptionPane.ERROR_MESSAGE);
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
						URL url = getClass().getClassLoader().getResource("ExcelTemplate.xlsx");
						OutputStream out = new FileOutputStream(saveFile, true);
						IOUtils.copy(url.openStream(), out);
						String output = FilenameUtils.concat(FilenameUtils.getFullPath(saveFile.toString()), FilenameUtils
								.getBaseName(saveFile.toString()));
						jtfFileOutput.setText(output + ".xml");
						Desktop.getDesktop().open(new File(saveFile.toString()));
					}
				}
				else if (e.getSource() == jtfFileTemplate){
					jtfFileTemplate.setText("");
				}
				else if(e.getSource() == jtfFileOutput){
					jtfFileOutput.setText("");
				}
				else if(e.getSource() == this ){
					if(jtfFileTemplate.getText().equals("")){
						jtfFileTemplate.setText(template);
					}
					if(jtfFileOutput.getText().equals("")){
						jtfFileOutput.setText(output);
					}
					
				}
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} catch (IOException ex) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			log.error(ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this, ex.getMessage(), SwingGUI.APP_NAME, JOptionPane.ERROR_MESSAGE);
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
