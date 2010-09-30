package edu.scripps.fl.test;

import java.io.File;

import java.util.ArrayList;

import java.util.List;

import com.googlecode.exceltablemodel.ExcelTableModel;

import edu.scripps.fl.pubchem.xml.PopulateArray;
import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.PubChemAssay;
import edu.scripps.fl.pubchem.xml.model.ResultTid;
import edu.scripps.fl.pubchem.xml.model.Xref;

public class TestPubChemAssay {
	private static ExcelTableModel tableModel = null;	
	
	public ExcelTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(ExcelTableModel tableModel) {
		this.tableModel = tableModel;
	}

	public static ArrayList<PubChemAssay> getBeanCollection(){
		File fileExcel = new File("C:\\Documents and Settings\\scanny\\Desktop\\test2.xlsx");	
		PopulateArray pa = new PopulateArray();
		ExcelTableModel model = null;
		ArrayList<PubChemAssay> pubChemAssay = new ArrayList<PubChemAssay>();
		try{
			model = ExcelTableModel.load(fileExcel, true);
			ArrayList<Xref> xrefs = (ArrayList<Xref>) pa.getXrefs(model);
			ArrayList<ResultTid> tids = (ArrayList<ResultTid>) pa.getTidValues(model);
			model.setUseFirstRowAsColumnHeadings(false);
			PubChemAssay pca = pa.getAssayValues(model);
			pca.setResultTids(tids);
			pca.setXrefs(xrefs);
			
			pubChemAssay.add(pca);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return pubChemAssay;
		
		
	}
	
	public static ArrayList<PubChemAssay> getBeanCollection3(ExcelTableModel model){
		PopulateArray pa = new PopulateArray();
		ArrayList<PubChemAssay> pubChemAssay = new ArrayList<PubChemAssay>();
		try{
			ArrayList<Xref> xrefs = (ArrayList<Xref>) pa.getXrefs(model);
			ArrayList<ResultTid> tids = (ArrayList<ResultTid>) pa.getTidValues(model);
			model.setUseFirstRowAsColumnHeadings(false);
			PubChemAssay pca = pa.getAssayValues(model);
			pca.setResultTids(tids);
			pca.setXrefs(xrefs);
			
			pubChemAssay.add(pca);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return pubChemAssay;
		
		
	}
	
	public static PubChemAssay[] getBeanCollection2(){
		File fileExcel = new File("C:\\Documents and Settings\\scanny\\Desktop\\463100.xlsx");	
		PopulateArray pa = new PopulateArray();
		ExcelTableModel model = null;
		PubChemAssay[] pubChemAssay = new PubChemAssay[1];
		try{
			model = ExcelTableModel.load(fileExcel, true);
			ArrayList<Xref> xrefs = (ArrayList<Xref>) pa.getXrefs(model);
			ArrayList<ResultTid> tids = (ArrayList<ResultTid>) pa.getTidValues(model);
			model.setUseFirstRowAsColumnHeadings(false);
			PubChemAssay pca = pa.getAssayValues(model);
			pca.setResultTids(tids);
			pca.setXrefs(xrefs);
			pubChemAssay[0] = pca;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return pubChemAssay;
		
		
	}


}
