package edu.scripps.fl.test;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

import com.googlecode.exceltablemodel.ExcelTableModel;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;

public class JasperTest {

	public static void main(String[] args) throws Exception {
		URL url = JasperTest.class.getResource("/report1.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(url.openStream());
		File fileExcel = new File("C:\\Documents and Settings\\scanny\\Desktop\\test2.xlsx");
		ExcelTableModel tableModel = ExcelTableModel.load(fileExcel, true); 
		
		JasperPrint print = JasperFillManager.fillReport(jasperReport, new HashMap(), new JRBeanCollectionDataSource(TestPubChemAssay.getBeanCollection3(tableModel)));
		File file = File.createTempFile("pubchem_xml_tool", ".pdf");
		
		JasperExportManager.exportReportToPdfFile(print, file.getAbsolutePath());
		Desktop.getDesktop().open(file);
		
		JRDocxExporter docxExporter = new JRDocxExporter();
		docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
		file = File.createTempFile("pubchem_xml_tool", ".docx");
		docxExporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
		docxExporter.exportReport();
		Desktop.getDesktop().open(file);
		
	}
	
//	public static void main(String[] args) throws Exception {
//		URL url = JasperTest.class.getResource("/report1.jasper");
//		File fileExcel = new File("C:\\Documents and Settings\\scanny\\Desktop\\test2.xlsx");
//		ExcelTableModel tableModel = ExcelTableModel.load(fileExcel, false); 
//		JasperPrint print = JasperFillManager.fillReport(url.openStream(), createAssayHashMap(tableModel), new JREmptyDataSource());
//		File file = File.createTempFile("pubchem_xml_tool", ".pdf");
//		JasperExportManager.exportReportToPdfFile(print, file.getAbsolutePath());
//		Desktop.getDesktop().open(file);
//	}

}
