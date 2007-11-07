package org.eclipse.imp.formatting.spec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Unparser {
	
	private Document dom;

	public String unparse(Specification spec) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
		DocumentBuilder db = dbf.newDocumentBuilder();

		dom = db.newDocument();
		Element root = dom.createElement("formatter");
		addSpec(spec, root);
		dom.appendChild(root);
		return printToString(dom);

		}catch(ParserConfigurationException pce) {
			return null;
		}
	}
	
	private String printToString(Document dom){
		try
		{
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			//to generate a file output use fileoutputstream instead of system.out
			XMLSerializer serializer = new XMLSerializer(
			output, format);

			serializer.serialize(dom);
			
			String result = output.toString();
			
			System.err.println("result of output:" + result);
			output.close();
			return result;
		} catch(IOException ie) {
		    ie.printStackTrace();
		    return "";
		}
	}

	private void addSpec(Specification spec, Element root) {
		Element language = dom.createElement("language");
		language.setTextContent(spec.getLanguage());
		
		Element rules = dom.createElement("rules");
		addRules(spec.ruleIterator(), rules);
		
		Element example = dom.createElement("example");
		example.setTextContent(spec.getExample());
		
		root.appendChild(language);
		root.appendChild(rules);
		root.appendChild(example);
	}

	private void addRules(Iterator<Rule> list, Element rules) {
		while (list.hasNext()) {
			Rule rule = list.next();
			Element elem = dom.createElement("rule");
			Element box = dom.createElement("box");
			elem.appendChild(box);
			box.setTextContent(rule.getBoxString());
			rules.appendChild(elem);
		}
	}

}
