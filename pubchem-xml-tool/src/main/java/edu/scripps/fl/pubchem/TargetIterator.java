package edu.scripps.fl.pubchem;

import java.util.Iterator;

import org.dom4j.Element;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.xml.model.Target;

public class TargetIterator implements Iterator<Target> {

	private Iterator<Element> iter = null;
	
	public TargetIterator(Iterator<Element> parentIterator){
		this.iter = parentIterator;
	}
	
	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public Target next() {
		Target target = new Target();
		Element elem = iter.next();
		
		Node node = elem.selectSingleNode("Id");
		target.setId(Integer.parseInt(node.getText()));
		
		target.setName(elem.selectSingleNode("Item[@Name='Title']").getText());
		
		return target;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
