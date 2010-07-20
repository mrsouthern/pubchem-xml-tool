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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTextPane;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class AboutGUI extends JPanel {
	
	private GUIComponent gc = new GUIComponent();
	
	public AboutGUI(){
		
		setLayout(new GridBagLayout());
		
		JTextPane jtpAbout = gc.createJTextPane();
		JTextPane jtpLogo = new JTextPane();
		jtpLogo.insertIcon(gc.loadIcon("scripps.png"));
		jtpLogo.setOpaque(false);
		
		GridBagConstraints gbc01 = gc.createGridBagConstraint(0, 0, jtpLogo, "line end");
		add(jtpLogo, gbc01);
		GridBagConstraints gbc02 = gc.createGridBagConstraint(1, 0, jtpAbout, "line start");
		add(jtpAbout, gbc02);
		
	}

}
