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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.scripps.fl.pubchem.PubChemFactory;
import edu.scripps.fl.pubchem.xml.XMLExtractorController;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class PubChemXMLExtractorGUI extends JPanel implements ActionListener, MouseListener {
	
	private JLabel jlbFileXML;
	private JTextField jtfFileXML;
	private JButton jbnFileXML, jbnRunExtractor;
	private GridBagConstraints gbc01, gbc02, gbc03, gbc04;
	private String aidText = "If you type in an AID number, the AID will be fetched directly from PubChem.";
	private GUIComponent gc = new GUIComponent();
	private static final Logger log = LoggerFactory.getLogger(PubChemXMLExtractorGUI.class);
	
	public PubChemXMLExtractorGUI() {
		DOMConfigurator.configure(PubChemXMLExtractorGUI.class.getClassLoader().getResource("log4j.config.xml"));
		
		setBorder(BorderFactory.createTitledBorder("PubChem XML Extractor"));
		setLayout(new GridBagLayout());
		
		setUpGUIComponents();

		add(jlbFileXML, gbc01);
		add(jtfFileXML, gbc02);
		add(jbnFileXML, gbc03);
		add(jbnRunExtractor, gbc04);
		addMouseListener(this);
	}
	
	public void setUpGUIComponents(){
		jlbFileXML = gc.createJLabel("PubChem XML:");
		jtfFileXML = gc.createJTextField(aidText);
		jtfFileXML.addMouseListener(this);
		jbnFileXML = gc.createJButton("Open16",
				"Choose a PubChem xml file to extract TID, Panel, and Xref information from or type in a PubChem AID number.", "icon");
		jbnRunExtractor = gc.createJButton("Extract PubChem XML", "Run the extractor program.", "text");

		jbnFileXML.addActionListener(this);
		jbnRunExtractor.addActionListener(this);
		
		gbc01 = gc.createGridBagConstraint(0, 0, jlbFileXML, "line start");
		gbc02 = gc.createGridBagConstraint(1, 0, jtfFileXML, "line start");
		gbc03 = gc.createGridBagConstraint(2, 0, jbnFileXML, "center");
		gbc04 = gc.createGridBagConstraint(1, 2, jbnRunExtractor, "center");
	}
	

	public void actionPerformed(ActionEvent e) {
		try{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (e.getSource() == jbnFileXML)
				gc.fileChooser(jtfFileXML, ".xml", "open");
			else if (e.getSource() == jbnRunExtractor) {
				String xml = jtfFileXML.getText();
				InputStream is;
				if(FilenameUtils.getExtension(xml).equals("xml")){
					File fileXMLInput = new File(xml);
					if( ! fileXMLInput.exists() )
						throw new Exception("File does not exist: " + fileXMLInput);
					is = new FileInputStream(fileXMLInput);
				}
				else{
					URL xmlURL = PubChemFactory.getInstance().getPubChemXmlDescURL(Long.parseLong(xml));
					File temp = File.createTempFile("PubChemXML", "xml");
					temp.deleteOnExit();
					FileUtils.copyURLToFile(xmlURL, temp);
					is = new GZIPInputStream(new FileInputStream(temp));
				}
				File fileExcelOutput = File.createTempFile("pubchem", ".xlsx");
				fileExcelOutput.deleteOnExit();
				new XMLExtractorController().extractPubChemXML(is, new FileOutputStream(fileExcelOutput));
				Desktop.getDesktop().open(fileExcelOutput);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		catch(Exception ex){
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			log.error(ex.getMessage(), ex);
			JOptionPane.showMessageDialog(this, ex.getMessage(), SwingGUI.APP_NAME, JOptionPane.ERROR_MESSAGE);
		}
		
	}

	public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() > 0){
				if(e.getSource() == jtfFileXML){
					if(jtfFileXML.getText().equals(aidText))
						jtfFileXML.setText("");
				}
				if(e.getSource() == this){
					if(jtfFileXML.getText().equals("")){
						jtfFileXML.setText(aidText);
					}
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
