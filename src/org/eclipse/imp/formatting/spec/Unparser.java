package org.eclipse.imp.formatting.spec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
			Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");  
            
			t.transform(new DOMSource(dom), new StreamResult(output));
			
			String result = output.toString();
			
			output.close();
			return result;
		} catch(IOException ie) {
		    ie.printStackTrace();
		    return "";
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
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

	private void addRules(Iterator<Item> list, Element rules) {
		while (list.hasNext()) {
			Item item = list.next();

			if (item instanceof Rule) {
				Rule rule = (Rule) item;
				Element elem = dom.createElement("rule");
				Element box = dom.createElement("box");
				elem.appendChild(box);
				box.setTextContent(rule.getBoxString());
				rules.appendChild(elem);
			}
			else if (item instanceof Separator) {
				Separator sep = (Separator) item;
				Element elem = dom.createElement("separator");
				String label = sep.getLabel();
				elem.setTextContent(label.length() > 0 ? label : "anonymous");
				rules.appendChild(elem);
			}
		}
	}

}
