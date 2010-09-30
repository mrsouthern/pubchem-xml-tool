package edu.scripps.fl.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.EUtilsFactory;

public class PublicationTest {

	public static void main(String[] args) throws Exception {
		Document doc = EUtilsFactory.getInstance().getSummary("17959251,18382464", "pubmed");
		Iterator<Publication> iter = new PublicationIterator((Iterator<Element>) doc.getRootElement().elementIterator("DocSum"));
		while ( iter.hasNext() ) {
			System.out.println(iter.next().citation(true));
		}
	}

	static class PublicationIterator implements Iterator<Publication> {
		
		private Iterator<Element> iter = null;

		public PublicationIterator(Iterator<Element> parentIterator) {
			this.iter = parentIterator;
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public Publication next() {
			Publication pub = new Publication();
			Element elem = iter.next();
			
			Node node = elem.selectSingleNode("Id");
			pub.setPubmedId( Integer.parseInt( node.getText() ) );

			pub.setTitle( elem.selectSingleNode("Item[@Name='Title']").getText() );
		
			for(Node aNode: (List<Node>) elem.selectNodes("Item[@Name='AuthorList']/Item[@Name='Author']") )
				pub.getAuthors().add(aNode.getText());
			
			pub.setJournal( elem.selectSingleNode("Item[@Name='FullJournalName']").getText() );
			
			pub.setDetails( elem.selectSingleNode("Item[@Name='SO']").getText() );
			
			return pub;
		}

		@Override
		public void remove() {
			// no op
		}
	}

	static class Publication {

		private Integer pubmedId;
		private List<String> authors = new ArrayList();
		private String title = "";
		private String journal = "";
		private String details = "";

		public Integer getPubmedId() {
			return pubmedId;
		}

		public void setPubmedId(Integer pubmedId) {
			this.pubmedId = pubmedId;
		}

		public List<String> getAuthors() {
			return authors;
		}

		public void setAuthors(List<String> authors) {
			this.authors = authors;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getJournal() {
			return journal;
		}

		public void setJournal(String journal) {
			this.journal = journal;
		}

		public String getDetails() {
			return details;
		}

		public void setDetails(String details) {
			this.details = details;
		}

		public String citation() {
			return citation(false);
		}
		
		public String citation(boolean includePMID) {
			StringBuffer sb = new StringBuffer();
			for(int ii = 0; ii < authors.size() - 1; ii++ ) {
				sb.append(authors.get(ii));
				if( ii < authors.size() - 2 )
					sb.append("., ");
			}
			if( sb.length() > 0 )
				sb.append(". & ");
			sb.append(authors.get(authors.size() - 1));
			sb.append(".");
			sb.append(" ").append(getTitle());
			sb.append(" ").append(getJournal());
			sb.append(" ").append(getDetails());
			if( includePMID )
				sb.append(" PMID: ").append(getPubmedId());
			return sb.toString();
		}
	}
}
