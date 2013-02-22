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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.DOMReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author M Southern (southern at scripps dot edu)
 */
public class EUtilsFactory {
	private static final Logger log = LoggerFactory.getLogger(EUtilsFactory.class);
	private static EUtilsFactory instance;

	private String tool = "PubChemXMLTool";
	private String email = "scanny@scripps.edu";

	public static EUtilsFactory getInstance() throws Exception {
		if (instance == null) {
			synchronized (EUtilsFactory.class) { // 1
				if (instance == null) {
					synchronized (EUtilsFactory.class) { // 3
						// inst = new Singleton(); //4
						instance = new EUtilsFactory();
					}
					// instance = inst; //5
				}
			}
		}
		return instance;
		//hello world
	}
	
	private EUtilsFactory() {
		
	}
	
	public Callable<InputStream> getInputStream(final String url, final Object... params) throws IOException {
		return new Callable<InputStream>() {
			public InputStream call() throws Exception {
				StringBuffer sb = new StringBuffer();
				sb.append(url).append("?");
				PostMethod post = new PostMethod(url);
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new NameValuePair("tool", EUtilsFactory.this.tool));
				data.add(new NameValuePair("email", EUtilsFactory.this.email));
				for (int ii = 0; ii < params.length; ii += 2) {
					String name = params[ii].toString();
					String value = "";
					if ((ii + 1) < params.length)
						value = params[ii + 1].toString();
					data.add(new NameValuePair(name, value));
					sb.append(name).append("=").append(value).append("&");
				}
				post.setRequestBody(data.toArray(new NameValuePair[0]));
				HttpClient httpclient = new HttpClient();
				int result = httpclient.executeMethod(post);
//				log.debug("Fetching from: " + url + StringUtils.join(params, " "));
				log.debug("Fetching from: " + sb);
				InputStream in = post.getResponseBodyAsStream();
				return in;
			}
		};
	}
	
	public Document getDocument(final String url, final Object... params) throws Exception {
		return getDocument(getInputStream(url, params).call());
	}
	
	public Document getDocument(InputStream in) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		DOMReader reader = new DOMReader();
		Document document = reader.read(builder.parse(new BufferedInputStream(in)));
//		SAXReader reader = new SAXReader();
//		Document document = reader.read(in);
		in.close();
		Node node = document.selectSingleNode("/eSearchResult/ERROR");
		if( node != null )
			throw new Exception(node.getText());
		return document;
	}

	public Document getSummary(Object id, String db) throws Exception {
		return getDocument("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi", "db", db, "id", id, "retmode", "xml");
	}

	public InputStream getSummaries(Collection<Long> ids, String db) throws Exception {
		String idStr = StringUtils.join(ids, ",");
		return getInputStream("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi", "db", db, "id", idStr, "retmode", "xml").call();
	}

//	public Map getSummaryAsMap(Integer id, String db) throws Exception {
//		Document document = getSummary("" + id, db);
//		List<Node> list = document.selectNodes("/DocSum/Item", ".");
//		Map map = new HashMap(list.size());
//		for (Node node : list) {
//			String name = node.valueOf("@Name");
//			String type = node.valueOf("@Type");
//			if (type.equals("List") || type.equals("Structure")) {
//
//			} else {
//				Object value = node.getText();
//				if (type.equals("Date"))
//					value = PubChemFactory.getInstance().parseDate(value);
//				else if (type.equals("Integer"))
//					value = Integer.parseInt(value.toString());
//				map.put(name, value);
//			}
//		}
//		return map;
//	}
	
	public List<Relation> getRelations(Long id, String fromDb, String toDb) throws Exception {
		Document document = EUtilsFactory.getInstance().getDocument("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi", "dbfrom", fromDb, "db",
				toDb, "id", "" + id);
		return getRelations(document);
	}
	
	public List<Relation> getRelations(Document document) {
		String fromDb = document.selectSingleNode("/eLinkResult/LinkSet/DbFrom").getText();
		String idStr = document.selectSingleNode("/eLinkResult/LinkSet/IdList/Id").getText();
		Long id = Long.parseLong(idStr);
		List<Node> linkSetDbs = document.selectNodes("/eLinkResult/LinkSet/LinkSetDb");
		ArrayList<Relation> list = new ArrayList<Relation>();
		for(Node linkSetDb: linkSetDbs) {
			String toDb = linkSetDb.selectSingleNode("DbTo").getText();
			String linkName = linkSetDb.selectSingleNode("LinkName").getText();
			List<Node> ids = linkSetDb.selectNodes("Link/Id");
			list.ensureCapacity(list.size() + ids.size());
			for (Node idNode : ids) {
				long relatedId = Long.parseLong(idNode.getText());
				if( id == relatedId )
					continue;
				Relation relation = new Relation();
				relation.setRelationName(linkName);
				relation.setFromDb(fromDb);
				relation.setFromId(id);
				relation.setToDb(toDb);
				relation.setToId(relatedId);
				list.add(relation);
			}			
		}
		return list;
	}
	
	public Collection<Long> getIds(Long id, String fromDb, String toDb) throws Exception {
		Document document = EUtilsFactory.getInstance().getDocument("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/elink.fcgi", "dbfrom", fromDb, "db",
				toDb, "id", "" + id);
		List<Node> linkSetDbs = document.selectNodes("/eLinkResult/LinkSet/LinkSetDb");
		Set<Long> relatedIds = new HashSet<Long>();
		for (Node linkSetDb : linkSetDbs) {
			String linkName = linkSetDb.selectSingleNode("LinkName").getText();
			List<Node> ids = linkSetDb.selectNodes("Link/Id");
			for (Node idNode : ids) {
				long relatedId = Long.parseLong(idNode.getText());
				if (id.equals(relatedId))
					continue;
				relatedIds.add(relatedId);
			}
		}
		return relatedIds;
	}
	
//	public Collection<Long> getIds(String query, String db, final Collection<Long> ids, int chunkSize) throws Exception {
//		int retStart = 1;
//		while(true) {
//			Document document = getDocument("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi", "db", db, "term", query, "retstart", retStart, "retmax", "" + chunkSize);
//			int count = Integer.parseInt(document.selectSingleNode("/eSearchResult/Count").getText());
//			int retMax = Integer.parseInt(document.selectSingleNode("/eSearchResult/RetMax").getText());
//			retStart = Integer.parseInt(document.selectSingleNode("/eSearchResult/RetStart").getText());
//
//			document.accept(new VisitorSupport() {
//				public void visit(Element element) {
//					if ("Id".equals(element.getName())) {
//						Long id = Long.parseLong(element.getText());
//						ids.add(id);
//					}
//				}
//			});
//			if( (retStart + retMax) < count )
//				retStart += retMax;
//			else
//				break;
//		}
		
//		public List<Long> getIds(String query, String db, int chunkSize) throws Exception {
//			return (List<Long>) getIds(query, db, new ArrayList(), chunkSize);
//		}

}