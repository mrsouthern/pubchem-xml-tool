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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.VFS;
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
	private static final String pubchemBioAssayUrlFormat = "ftp://%s:%s@ftp.ncbi.nlm.nih.gov/pubchem/Bioassay/CSV/%s/%s.zip";

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

	protected static String getAIDArchive(long aid) {
		long low = nextLowestMultiple(aid, 1000);
		return String.format("%07d_%07d", low + 1, low + 1000);
	}

	// http://mindprod.com/jgloss/round.html
	// rounding m down to multiple of n
	protected static long nextLowestMultiple(long m, long n) {
		long floor = m / n * n;
		return floor;
	}

	public InputStream getPubChemXmlDesc(long aid) throws IOException {
		String archive = getAIDArchive(aid);
		String sUrl = String.format(pubchemBioAssayUrlFormat, ftpUser, ftpPass, "Description", archive);
		String szip = String.format("zip:%s!/%s/%s.descr.xml.gz", sUrl,archive, aid);
		log.debug(sUrl);
		FileObject fo = VFS.getManager().resolveFile(szip);
		log.debug("Resolved file: " + szip);
		try{
		InputStream is = fo.getContent().getInputStream();
		return new GZIPInputStream(is);
		}catch(Exception e){
			log.info("AID: " + aid + " is not in the ftp site.", e.getMessage());
			return null;
		}
	}

	public InputStream getPubChemCsv(long aid) throws IOException {
		String archive = getAIDArchive(aid);
		String sUrl = String.format(pubchemBioAssayUrlFormat, ftpUser, ftpPass, "Data", archive);
		String szip = String.format("zip:%s!/%s/%s.csv.gz", sUrl,archive, aid);
		log.debug(sUrl);
		FileObject fo = VFS.getManager().resolveFile(szip);
		log.debug("Resolved file: " + szip);
		InputStream is = fo.getContent().getInputStream();
		return new GZIPInputStream(is);
	}

}