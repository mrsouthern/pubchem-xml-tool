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
package edu.scripps.fl.pubchem.report;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;

import com.googlecode.exceltablemodel.ExcelTableModel;
import edu.scripps.fl.pubchem.PubChemDeposition;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;

/*
 * @author S Canny (scanny at scripps dot edu)
 */
public class ReportController {
	
	public void createReport(File fileExcel, File filePDFOutput, File fileWordOutput) throws Exception {
		ExcelTableModel tableModel = ExcelTableModel.load(fileExcel, false); 
		HashMap parameters = new HashMap();
		
		File file = imagesInTempDir();
		String path = file.getAbsolutePath();
		URL url = getClass().getClassLoader().getResource("report1.jasper");
		
		JasperPrint print = JasperFillManager.fillReport(path + "\\report1.jasper" , parameters, new JRBeanCollectionDataSource(Report.getBeanCollection(tableModel)));
		JasperExportManager.exportReportToPdfFile(print, filePDFOutput.getAbsolutePath());
		JRDocxExporter docxExporter = new JRDocxExporter();
		docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
		docxExporter.setParameter(JRExporterParameter.OUTPUT_FILE, fileWordOutput);
		docxExporter.exportReport();
	}
	
	public ArrayList<PubChemAssay> createReport(PubChemDeposition pcDep, File fileExcel, File filePDFOutput, File fileWordOutput) throws Exception {
		ExcelTableModel tableModel = ExcelTableModel.load(fileExcel, true); 
		HashMap parameters = (HashMap) new Report().createParameterMap(tableModel, pcDep);
		
		ArrayList<PubChemAssay> assay = Report.getBeanCollection(tableModel);
		
		File file = imagesInTempDir();
		String path = file.getAbsolutePath();
//		Scripps Report
		URL url = getClass().getClassLoader().getResource("report1.jasper");
		
		JasperPrint print = JasperFillManager.fillReport(path + "\\report1.jasper" , parameters, new JRBeanCollectionDataSource(assay));
//		External Report
//		URL url = getClass().getClassLoader().getResource("ExternalReport.jasper");
//		
//		JasperPrint print = JasperFillManager.fillReport(path + "\\ExternalReport.jasper" , parameters, new JRBeanCollectionDataSource(assay));
		JasperExportManager.exportReportToPdfFile(print, filePDFOutput.getAbsolutePath());
		JRDocxExporter docxExporter = new JRDocxExporter();
		docxExporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
		docxExporter.setParameter(JRExporterParameter.OUTPUT_FILE, fileWordOutput);
		docxExporter.exportReport();
		
		return assay;
	}
	
	
	public File imagesInTempDir() throws IOException{
		File file = new DirUtils().createTempDirectory("JasperReport", "");
//		Scripps Report
		URL url = getClass().getClassLoader().getResource("report1.jasper");
		FileUtils.copyURLToFile(url,new File(file.getAbsolutePath() + "\\report1.jasper"));
		url = getClass().getClassLoader().getResource("logo.png");
		FileUtils.copyURLToFile(url, new File(file.getAbsolutePath() + "\\logo.png"));
		url = getClass().getClassLoader().getResource("Screening Center Logo.png");
		FileUtils.copyURLToFile(url, new File(file.getAbsolutePath() + "\\Screening Center Logo.png"));
//		External Report
//		URL url = getClass().getClassLoader().getResource("ExternalReport.jasper");
//		FileUtils.copyURLToFile(url,new File(file.getAbsolutePath() + "\\ExternalReport.jasper"));
//		url = getClass().getClassLoader().getResource("YourLogoHere.png");
//		FileUtils.copyURLToFile(url, new File(file.getAbsolutePath() + "\\YourLogoHere.png"));

		
		return file;
	}
	


}
