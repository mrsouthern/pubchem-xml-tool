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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.scripps.fl.pubchem.PubChemDeposition;
import edu.scripps.fl.pubchem.web.session.PCDepositionSystemSession;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class DepositionLoginGUI extends JPanel implements ActionListener {

	private JFrame jfMain = new JFrame();
	private JDialog jdLogin = new JDialog(jfMain, "PubChem Deposition Login", true);
	private static final JTextField jtfUsername = new JTextField(20);
	private static final JPasswordField jpfPassword = new JPasswordField(20);
	private JButton jbnLogin;
	private PubChemDeposition pcDep;
	private static final Logger log = LoggerFactory.getLogger(DepositionLoginGUI.class);

	
	public DepositionLoginGUI(PubChemDeposition pcDep){
		this.pcDep = pcDep;
		DOMConfigurator.configure(DepositionLoginGUI.class.getClassLoader().getResource("log4j.config.xml"));
	}
	
	public Component createComponents() {
		JPanel jpMain = new JPanel();
		JPanel jpUsername = new JPanel();
		JPanel jpPassword = new JPanel();
		JPanel jpLogin = new JPanel();
		JLabel jlbUserName = new JLabel("User name:");
		JLabel jlbPassword = new JLabel("Password:");
		jlbUserName.setForeground(Color.black);
		jlbPassword.setForeground(Color.black);

		jbnLogin = new JButton("Login");
		jbnLogin.addActionListener(this);

		jpUsername.setLayout(new BoxLayout(jpUsername, BoxLayout.X_AXIS));
		jpUsername.add(jlbUserName);
		jpUsername.add(jtfUsername);

		jpPassword.setLayout(new BoxLayout(jpPassword, BoxLayout.X_AXIS));
		jpPassword.add(jlbPassword);
		jpPassword.add(jpfPassword);

		jpLogin.setLayout(new BoxLayout(jpLogin, BoxLayout.X_AXIS));
		jpLogin.add(jbnLogin);

		jpMain.setLayout(new BoxLayout(jpMain, BoxLayout.Y_AXIS));
		jpMain.add(jpUsername);
		jpMain.add(jpPassword);
		jpMain.add(jpLogin);

		return jpMain;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String userName = jtfUsername.getText();
			char[] password = jpfPassword.getPassword();
			String passwordString = "";
			for (int i = 0; i < password.length; i++)
				passwordString = passwordString + password[i];
			PCDepositionSystemSession session = pcDep.getSession();
			session.login(userName, passwordString);
			pcDep.setLoggedIn(true);
			pcDep.setSession(session);
			int n = JOptionPane.showOptionDialog(jfMain, "Would you like your user name and password to be saved?", "",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (n == JOptionPane.YES_OPTION)
				new PubChemDeposition().createPropertiesFile(userName, passwordString);
			jfMain.dispose();
			jdLogin.dispose();
		} catch (Throwable throwable) {
			SwingGUI.handleError(this, throwable);
		}
	}

	public void displayDialog() {
		jdLogin.setSize(new Dimension(250, 100));
		jdLogin.getContentPane().add(createComponents());
		jdLogin.getRootPane().setDefaultButton(jbnLogin);
		jdLogin.setVisible(true);
		jdLogin.setModalityType(ModalityType.APPLICATION_MODAL);
	}

	


	
}
