package edu.scripps.fl.pubchem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.scripps.fl.pubchem.web.session.PCDepositionSystemSession;
import edu.scripps.fl.pubchem.xmltool.gui.DepositionLoginGUI;

public class PubChemDeposition {
	
private static final Logger log = LoggerFactory.getLogger(PubChemDeposition.class);
private PCDepositionSystemSession session = new PCDepositionSystemSession();
private boolean isLoggedIn;

	public void logonToPubChem(PCDepositionSystemSession session) throws Exception {

		Properties loginProps = new Properties();
		File file = new File(System.getProperty("user.home") + "\\PubChemXMLToolProperties.txt");
		if (file.exists() ) {
			FileInputStream in = new FileInputStream(file);
			loginProps.load(in);
			in.close();
			String userName = loginProps.getProperty("user.name");
			String password = loginProps.getProperty("password");
			if (!userName.equals(null) && !password.equals(null)) {
				session.login(userName, password);
				setLoggedIn(true);
			} else
				setLoggedIn(false);
		} else
			setLoggedIn(false);
	}
	
	public void createPropertiesFile(String userName, String password) throws Exception {
		String fileName = System.getProperty("user.home") + "\\PubChemXMLToolProperties.txt";
		log.info("Writing to file named " + fileName);
		Writer out = new OutputStreamWriter(new FileOutputStream(fileName));
		try {
			out.write("user.name=" + userName + "\n" + "password=" + password);
		} finally {
			out.close();
		}
	}
	
	public InputStream getPubChemAID(int aid) throws Exception {
		logonToPubChem(session);
		if (! isLoggedIn) {
			DepositionLoginGUI dlg = new DepositionLoginGUI(this);
			dlg.displayDialog();
			session = getSession();
		}
		InputStream is = session.getDescrXML(aid);
		return is;
	}
	
	public PCDepositionSystemSession getLoggedInSession(int aid) throws Exception {
		logonToPubChem(session);
		if (isLoggedIn == false) {
			DepositionLoginGUI dlg = new DepositionLoginGUI(this);
			dlg.displayDialog();
			session = getSession();
		}
		log.info("Logged in to PubChem Deposition");
		return session;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	public PCDepositionSystemSession getSession() {
		return session;
	}

	public void setSession(PCDepositionSystemSession session) {
		this.session = session;
	}

}
