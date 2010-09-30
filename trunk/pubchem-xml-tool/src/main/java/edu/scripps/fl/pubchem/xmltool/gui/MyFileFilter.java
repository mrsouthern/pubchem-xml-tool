package edu.scripps.fl.pubchem.xmltool.gui;

import java.io.File;

public class MyFileFilter extends javax.swing.filechooser.FileFilter {
	
	String ext;
	
	public MyFileFilter(String ext){
		this.ext = ext;
	}
	
	public boolean accept(File file) {
		if (file.isDirectory()) { 
			return true; }
		
        String filename = file.getName();
        return filename.endsWith(ext);
    }
	
    public String getDescription() {
        return ext;
    }

}
