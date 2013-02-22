package edu.scripps.fl.pubchem.xml;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.scripps.fl.pubchem.xml.model.Assay;

public class PubChemXMLUtils {
	
	protected void add_E(String nodeName, String text, Element parent) {
		check(nodeName, parent);
		Element element = parent.addElement(nodeName);
		for(String line: text.split("\\r?\\n")) {
			Element child = (element).addElement(nodeName + "_E");
			child.addText(line);
		}
	}
	
	protected void add_E(String nodeName, List<String> text, Element parent) {
		check(nodeName, parent);
		Element element = parent.addElement(nodeName);
		for(String line: text) {
			for(String partLine: line.split("\\r?\\n")){
				Element child = (element).addElement(nodeName + "_E");
				child.addText(partLine);
			}
		}
	}
	
	protected void attributeAndTextAdd(String nodeName, String attribute, String text, Element parent){
			check(nodeName, parent);
			Element element = parent.addElement(nodeName).addText(text);
			element.addAttribute("value", attribute);	
	}
	
	protected void add(String nodeName, String text, Element parent){
		check(nodeName, parent);
		parent.addElement(nodeName).addText(text);
	}
	
	private void check(String nodeName, Element parent){
		Node node = parent.selectSingleNode(nodeName);
		if(node != null)
			node.detach();
	}
	
	public void getElementText(Object object, String nodeName, Document doc, String property) throws IllegalAccessException, InvocationTargetException {
		List<Node> nodes = doc.selectNodes(nodeName);
		String text = "";
		for(Node nn : nodes){
			if (null != nn.getText())
				text = text + nn.getText() + "\n";
		}
		BeanUtils.setProperty(object, property, text);
	}
	
	public void getListofElementsText(Object object, String nodeName, Document doc, String property) throws IllegalAccessException, InvocationTargetException{
		List<Node> nodes = doc.selectNodes(nodeName);
		List<String> strings = new ArrayList<String>();
		for(Node nn: nodes){
			if (null != nn.getText())
				strings.add(nn.getText());
		}
		BeanUtils.setProperty(object, property, strings);
		
	}
	
	public String getElementAttribute(String elementName, Document doc){
		Node node = doc.selectSingleNode(elementName);
		String attribute;
		if(null == node)
			attribute = "";
		else
			attribute = node.valueOf("@value");
		if(null == attribute)
			attribute = "";
		return attribute;
	}

}
