package edu.scripps.fl.pubchem;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.xml.model.Publication;

public class PublicationIterator implements Iterator<Publication> {
	
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
