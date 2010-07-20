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
package edu.scripps.fl.pubchem;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author Mark Southern (southern at scripps dot edu)
 */
public class PubChemFactory {

	private static final Logger log = LoggerFactory.getLogger(PubChemFactory.class);

	private static final String ftpUser = "anonymous";
	private static final String ftpPass = "scripps.edu";
	private static final String ftpHost = "ftp.ncbi.nlm.nih.gov";
	private static final String pubchemBioAssayUrlFormat = "ftp://%s:%s@ftp.ncbi.nlm.nih.gov/pubchem/Bioassay/CSV/%s/%s/%s%s";

	private static PubChemFactory instance;

	private PubChemFactory() {
	}

	public static PubChemFactory getInstance() {
		if (instance == null) {
			synchronized (PubChemFactory.class) { // 1
				if (instance == null) {
					synchronized (PubChemFactory.class) { // 3
						// inst = new Singleton(); //4
						instance = new PubChemFactory();
					}
					// instance = inst; //5
				}
			}
		}
		return instance;
	}
	 
		
	public static String getAIDFolder(long aid) {
		long low = nextLowestMultiple(aid, 5000);
		return String.format("%d_%d", low, low + 4999);
	}

	// http://mindprod.com/jgloss/round.html
	// rounding m down to multiple of n
	private static long nextLowestMultiple(long m, long n) {
		long floor = m / n * n;
		return floor;
	}
	
	public URL getXMLDescURL(URL parent, int aid) {
		try {
			return new URL(parent.getProtocol(), parent.getHost(), parent.getPort(), String.format("%s/%s/%s.descr.xml.gz", parent.getPath(), getAIDFolder(aid), aid));
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public URL getCsvDataURL(URL parent, int aid) {
		try {
			return new URL(parent.getProtocol(), parent.getHost(), parent.getPort(), String.format("%s/%s/%s.csv.gz", parent.getPath(), getAIDFolder(aid), aid));
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public URL getPubChemXmlDescURL(long aid) {
		try {
			String sURL = String.format(pubchemBioAssayUrlFormat, ftpUser, ftpPass, "Description", getAIDFolder(aid), aid, ".descr.xml.gz");
			return new URL(sURL);
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public URL getPubChemCsvURL(long aid) {
		try {
			String sURL = String.format(pubchemBioAssayUrlFormat, ftpUser, ftpPass, "Data", getAIDFolder(aid), aid, ".csv.gz");
			return new URL(sURL);
		}
		catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}