package edu.scripps.fl.test;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class JasperTest {

	public static void main(String[] args) throws Exception {
		URL url = JasperTest.class.getResource("/report1.jasper");
		JasperPrint print = JasperFillManager.fillReport(url.openStream(), new HashMap(), new JREmptyDataSource());
		File file = File.createTempFile("pubchem_xml_tool", ".pdf");
		JasperExportManager.exportReportToPdfFile(print, file.getAbsolutePath());
		Desktop.getDesktop().open(file);
	}

}
