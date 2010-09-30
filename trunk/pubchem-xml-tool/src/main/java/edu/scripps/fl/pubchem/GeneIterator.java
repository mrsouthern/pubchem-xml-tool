package edu.scripps.fl.pubchem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.xml.model.Gene;

public class GeneIterator implements Iterator<Gene> {
	
	private Iterator<Element> iter = null;
	
	public GeneIterator(Iterator<Element> parentIterator){
		this.iter = parentIterator;
	}
	
	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}
	
//	public static String join(Node node, String separator, String... attributes) {
//		StringBuffer sb = new StringBuffer();
//		for(int ii = 0; ii < attributes.length; ii++) {
//			Node n = node.selectSingleNode(String.format("Item[@Name='%s']", attributes[ii]));
//			sb.append(n == null ? "" : n.getText());
//			if( ii < attributes.length - 1)
//				sb.append(separator);
//		}
//		return sb.toString();
//	}
	
	public static List<String> getAttributes(Node node, String... attributes) {
		List<String> attrs = new ArrayList(attributes.length);
		for(String attr: attributes) {
			Node n = node.selectSingleNode(String.format("Item[@Name='%s']", attr));
			attrs.add(n == null ? "" : n.getText());
		}
		return attrs;
	}
	
	@Override
	public Gene next() {
		Gene gene = new Gene();
		Element elem = iter.next();
		
		Node node = elem.selectSingleNode("Id");
		gene.setId(Integer.parseInt(node.getText()));

//		gene.setName(join(elem, " ", new String[]{"Name", "Description", "OrgName"}));
		
		gene.setName(StringUtils.join(getAttributes(elem,new String[]{"Name", "Description", "OrgName"}), " "));
		
//		gene.setName(elem.selectSingleNode("Item[@Name='Name']").getText() + " " + elem.selectSingleNode("Item[@Name='Description']").getText() + " [" + elem.selectSingleNode("Item[@Name='Orgname']").getText() + "]");
		
		return gene;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
}
