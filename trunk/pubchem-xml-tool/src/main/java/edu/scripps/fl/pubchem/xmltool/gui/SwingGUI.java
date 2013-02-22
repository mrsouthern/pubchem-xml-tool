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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class SwingGUI extends JPanel {
	
	private static final Logger log = LoggerFactory.getLogger(SwingGUI.class);
	
	public static final String APP_NAME = "PubChem XML Tool";
//	isInternal is used in CreatorGUI, ExtractorGUI and ReportController
	private static final Boolean isInternal = false;
	
	public static void main(String args[]) throws Exception {
//		BasicConfigurator.configure();
		DOMConfigurator.configure(SwingGUI.class.getClassLoader().getResource("log4j.config.xml"));
		createGUI();
	}

	public SwingGUI() {
		super(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setAlignmentX(CENTER_ALIGNMENT);
		tabbedPane.setAlignmentY(CENTER_ALIGNMENT);
		
		JPanel xmlCreator = new JPanel();
		xmlCreator.add(new PubChemXMLCreatorGUI(isInternal));
		tabbedPane.addTab("Create PubChem XML", xmlCreator);
		
		JPanel xmlExtractor = new JPanel();
		xmlExtractor.add(new PubChemXMLExtractorGUI(isInternal));
		tabbedPane.addTab("Extract PubChem XML", xmlExtractor);
		
		JPanel about = new JPanel();
		about.add(new AboutGUI());
		tabbedPane.addTab("About", about);
		
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	public static void handleError(JComponent comp, Throwable throwable) {
		comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		String msg = "";
		if(null != throwable.getCause() )
			msg = throwable.getCause().getMessage();
		else if(throwable instanceof FileNotFoundException)
			msg = "Could not find file: " + throwable.getMessage();
		else if(throwable instanceof IOException)
			msg = "Error with file:" + throwable.getMessage();
		else{
			if(throwable != null)
				msg = throwable.getMessage();
		}
		throwable.printStackTrace();
		log.error(msg, throwable);
		JOptionPane.showMessageDialog(comp, msg, SwingGUI.APP_NAME, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void createGUI() {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		SwingGUI newContentPane = new SwingGUI();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);
		frame.setSize(780, 300);
		frame.setVisible(true);
		frame.setTitle(APP_NAME);
		frame.setResizable(true);
		frame.setIconImage(new GUIComponent().loadIcon("scripps.png").getImage());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}	
	}
	

	
}
