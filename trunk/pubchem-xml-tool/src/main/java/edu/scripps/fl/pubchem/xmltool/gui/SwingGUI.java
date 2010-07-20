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

import javax.swing.JFrame;
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
	
	public static void main(String args[]) throws Exception {
//		BasicConfigurator.configure();
		DOMConfigurator.configure(SwingGUI.class.getClassLoader().getResource("log4j.config.xml"));
		createGUI();
	}

	public SwingGUI() {
		super(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel xmlCreator = new JPanel();
		xmlCreator.add(new PubChemXMLCreatorGUI());
		tabbedPane.addTab("Create PubChem XML", xmlCreator);
		
		JPanel xmlExtractor = new JPanel();
		xmlExtractor.add(new PubChemXMLExtractorGUI());
		tabbedPane.addTab("Extract PubChem XML", xmlExtractor);
		
		JPanel about = new JPanel();
		about.add(new AboutGUI());
		tabbedPane.addTab("About", about);
		
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	public static void createGUI() {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		SwingGUI newContentPane = new SwingGUI();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);
		frame.setSize(800, 300);
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
