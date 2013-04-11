package edu.scripps.fl.pubchem.cpdp;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.scripps.fl.pubchem.xml.model.Panel;
import edu.scripps.fl.pubchem.xml.model.ResultTid;

public class CPDPExtractTIDsFactory {

	private static final Logger log = LoggerFactory.getLogger(CPDPExtractTIDsFactory.class);
	
	
	
	private static ResultTid concentrationTID(String type, String unit, String probeType) {
		
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(type);
		String percent = "";
		while (m.find()) {
			percent = m.group();
		}

		String description = "";
		if (!"".equals(percent)) {
			description = String.format("concentration at which %s percent of the activity in the %s assay is observed.",
					percent, probeType);
		}
		else if (type.contains("MIC")) {
			description = "minimum inhibitory concentration";
		}
		else if ("TGI".equals(type)) {
			description = "total growth inhibitory concentration";
		}
		else if ("Ki".equals(type)) {

		}
		if (type.contains("log") || type.contains("Log"))
			description = "log of the " + description;
		
		ResultTid tid = new ResultTid(type, description, "float", unit);
		if (!type.contains("log") && !type.contains("Log"))
			tid.setIsActiveConcentration(true);

		return tid;
	}
	
	private static List<ResultTid> curveFitTIDs() {
		List<ResultTid> curveFitTIDs = new ArrayList<ResultTid>();

		curveFitTIDs
				.add(new ResultTid(
						"Qualifier",
						"Activity Qualifier identifies if the resultant data IC50 came from a fitted curve or was determined manually to be less than or greater than its listed IC50 concentration.",
						"string", "none"));
		curveFitTIDs.add(new ResultTid("Maximal Response",
				"The maximal or asymptotic response above the baseline as concentration increases without bound.", "float",
				"none"));
		curveFitTIDs.add(new ResultTid("Baseline Response", "Adjustable baseline of the curve fit, minimal response value.",
				"float", "none"));
		curveFitTIDs.add(new ResultTid("Inflection Point Concentration",
				"The concentration value for the inflection point of the curve.", "float", "um"));
		curveFitTIDs
				.add(new ResultTid(
						"Hill Slope",
						"The variable HillSlope describes the steepness of the curve. This variable is called the Hill slope, the slope factor, or the Hill coefficient. If it is positive, the curve increases as X increases. If it is negative, the curve decreases as X increases. A standard sigmoid dose-response curve (previous equation) has a Hill Slope of 1.0. When HillSlope is less than 1.0, the curve is more shallow. When HillSlope is greater than 1.0, the curve is steeper. The Hill slope has no units.",
						"float", "none"));
		curveFitTIDs.add(new ResultTid("Response Range", "The range of Y.", "float", "none"));
		curveFitTIDs
				.add(new ResultTid(
						"Chi Square",
						"A measure for the 'goodness' of a fit. The chi-square test (Snedecor and Cochran, 1989) is used to test if a sample of data came from a population with a specific distribution.",
						"float", "none"));
		curveFitTIDs
				.add(new ResultTid(
						"Rsquare",
						"This statistic measures how successful the fit explains the variation of the data; R-square is the square of the correlation between the response values and the predicted response values.",
						"float", "none"));
		curveFitTIDs
				.add(new ResultTid(
						"Number of DataPoints",
						"Overall number of data points of normalized percent inhibition that was used for calculations (includes all concentration points); in some cases a data point can be excluded as outlier.",
						"int", "none"));
		curveFitTIDs
				.add(new ResultTid(
						"Excluded Points",
						"Flags to indicate which of the dose-response points were excluded from analysis. (1) means the point was excluded and (0) means the point was not excluded.",
						"string", "none"));
		return curveFitTIDs;
	}

	

	private static String determineUnit(String resultType) {
		String unit = "none";
		if (resultType.contains("percent"))
			unit = "percent";
		else if (resultType.contains("fold")) {
			unit = "ratio";
		}
		else if (resultType.contains("concentration endpoint") && !resultType.contains("log")) {
			unit = "uM";
		}
		else {
			log.info("Unsure of units for endpoint: " + resultType);
		}
		return unit;
	}
	
	
	private static String doseTerm(String concentrationEndPoint) {
		String doseTerm = "percent inhibition";
		if (concentrationEndPoint.contains("E") || concentrationEndPoint.contains("A")) {
			doseTerm = "percent activation";
		}
		else if (concentrationEndPoint.contains("CC")) {
			doseTerm = "percent cell viability";
		}
		return doseTerm;
	}


	
	public static List<ResultTid> getPanelTids(List<Panel> panels, List<ResultTid> tids){
		int count = 1;
		List<ResultTid> panelTids = new ArrayList<ResultTid>();
		for(Panel panel: panels){
			
			ResultTid outcome = panelResultTID("Outcome [" + panel.getPanelName() + "]", "BioAssay outcome for compound: active or inactive.", count, "outcome");
			panelTids.add(outcome);
			ResultTid score = panelResultTID("Score [" + panel.getPanelName() + "]", "BioAssay score for compound.", count, "score");
			panelTids.add(score);
			
			for(ResultTid tid: tids){
				ResultTid nTid = new ResultTid();
				if(tid.getTidName().contains("]"))
					nTid.setTidName(tid.getTidName().replace("]", ", " + panel.getPanelName() +"]"));
				else
					nTid.setTidName(tid.getTidName() + " [" + panel.getPanelName() + "]");

				if(null != tid.getIsActiveConcentration() && tid.getIsActiveConcentration())
					nTid.setTidPanelReadout("ac");
				else
					nTid.setTidPanelReadout("regular");
				
				if(null != tid.getTidPlot())
					nTid.setTidPlot(count);
				nTid.setTidPanelNum(count);
				
				nTid.setIsActiveConcentration(tid.getIsActiveConcentration());
				nTid.setTidConcentration(tid.getTidConcentration());
				nTid.setTidDescription(tid.getTidDescription());
				nTid.setTidType(tid.getTidType());
				nTid.setTidUnit(tid.getTidUnit());
				
				panelTids.add(nTid);
			}
			count = count + 1;
		}
		return panelTids;
	}
	
	
	public static List<ResultTid> getTIDs(Document cpdp) throws CPDPException{

		List<ResultTid> tids = new ArrayList<ResultTid>();

		String resultType = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.RESULT_TYPE)).getText();
		if("".equals(resultType))
			throw new CPDPException("Result type has not been specified.");

		Integer replicates = CPDPExtractUtils.parseIntNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.REPLICATES), cpdp);
		Integer points = CPDPExtractUtils.parseIntNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.CONCENTRATION_POINTS), cpdp);

		Double concentration = CPDPExtractUtils.parseDoubleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.COMPOUND_SCREENING_CONCENTRATION), cpdp);

		//determine dilution factor
		String dilutionS = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.DILUTION_FACTOR)).getText();
		Double dilution = 1.0;
		if (null != dilutionS && !"".equals(dilutionS) && ! "na".equalsIgnoreCase(dilutionS)) {
			if(dilutionS.contains(":")){
				String[] dilutionNums = dilutionS.split(":");
				dilution = Double.parseDouble(dilutionNums[0])/Double.parseDouble(dilutionNums[1]);
			}else{
				dilution = Double.parseDouble(dilutionS);
			}
		}

		String origType = resultType.substring(resultType.lastIndexOf(":") + 1);
		String unit = determineUnit(resultType);
		
		if (!resultType.contains("concentration endpoint")) {
			//Examples: % Inhibition, % Activation
			responseTIDLogic(concentration, origType, unit, replicates, tids, true, null);
		}
		else  {
			//Examples: IC50, EC50, CC50
			String probeType = cpdp.selectSingleNode(CPDPExtractUtils.getChosenAIDInfoNodePath(CPDPExtractUtils.PROBE_TYPE)).getText();
			
			//IC50/EC50 ... TID
			ResultTid ac = concentrationTID(origType, unit, probeType);
			tids.add(ac);
			
			if(!origType.contains("log")){
				//Add log of concentration endpoint
				ResultTid acLog = concentrationTID("Log"+origType, "none", probeType);
				tids.add(acLog);
			}
			
			// standard curve fit parameters for lead id
			tids.addAll(curveFitTIDs());
			
			//dilution points
			for (int ii = 1; ii <= points; ii++) {
				// what if response is fold change??
				responseTIDLogic(concentration, doseTerm(origType), "percent", replicates, tids, false,1);
				concentration = concentration*dilution;
				
				//concentration formatting
				if(concentration < 1 && concentration >= 0.001){
				   concentration = Double.parseDouble(new Formatter().format("%1.3f", concentration).toString());
				}else if(concentration < 0.001){
					concentration =   Double.parseDouble(new Formatter().format("%1.4f", concentration).toString());
				}else{
					concentration = Double.parseDouble( new Formatter().format("%1.1f", concentration).toString());
				}
			}
			
			
		}
		return tids;
	}
	
	private static ResultTid panelResultTID(String name, String description, Integer count, String panelReadout) {
		ResultTid outcome = new ResultTid(name, description, "int", "none");
		outcome.setTidPanelNum(count);
		outcome.setTidPanelReadout(panelReadout);
		return outcome;
	}

	private static ResultTid responseTID(Double concentration, String type, String unit) {
		ResultTid tid = new ResultTid();
		tid.setTidConcentration(concentration);
		tid.setTidName(String.format("%s at %s uM", StringUtils.capitalize(type.replace("percent ", "")), concentration));
		tid.setTidUnit(unit);
		tid.setTidType("float");
		tid.setTidDescription(String.format("%s at compound concentration %s uM.", StringUtils.capitalize(type), concentration));
		return tid;
	}
	
	private static void responseTIDLogic(Double concentration, String origType, String unit, Integer replicates,
			List<ResultTid> tids, boolean includeAverage, Integer plotNum) {

		if (replicates <= 1) {
			ResultTid tid = responseTID(concentration, origType, unit);
			tids.add(tid);
		}
		else {
			if (includeAverage) {
				ResultTid tid = responseTID(concentration, origType, unit);
				tid.setTidName("Average " + tid.getTidName());
				tid.setTidDescription("Average " + tid.getTidDescription());
				tids.add(standardDeviationTID(origType));
				tids.add(tid);
			}

			for (int ii = 1; ii <= replicates; ii++) {
				ResultTid replicate = responseTID(concentration, origType, unit);
				replicate.setTidName(String.format("%s [%s]", replicate.getTidName(), ii));
				if (null != plotNum) {
					replicate.setTidPlot(plotNum);
				}
				tids.add(replicate);

			}
		}

	}
	
	private static ResultTid standardDeviationTID(String output) {
		ResultTid tid = new ResultTid();
		tid.setTidName("Standard Deviation");
		tid.setTidUnit("none");
		tid.setTidType("float");
		tid.setTidDescription(String.format("Standard deviation of %s.", output));
		return tid;
	}

}
