package org.eclipse.imp.formatting.spec;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FormattingSpecificationParser extends DefaultHandler {
	static SAXParserFactory spf = SAXParserFactory.newInstance();

	protected FormattingSpecification spec;

	protected FormattingRule tmpRule;

	protected String tmpContents;

	public FormattingSpecification parse(File input) throws Exception {
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(input.getAbsolutePath(), this);

			if (spec != null) {
				return spec;
			} else {
				throw new Exception("Parsing of " + input + " failed");
			}

		} catch (SAXException se) {
			throw new Exception("Parsing of " + input + " failed", se);
		} catch (ParserConfigurationException pce) {
			throw new Exception("Parsing of " + input + " failed", pce);
		} catch (IOException ie) {
			throw new Exception("Parsing of " + input + " failed", ie);
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("formatter")) {
			spec = new FormattingSpecification();
		} else if (qName.equals("rule")) {
			tmpRule = new FormattingRule();
		} else if (qName.equals("box")) {
			tmpContents = "";
		} else if (qName.equals("example")) {
			tmpContents = "";
		} else if (qName.equals("language")) {
			tmpContents = "";
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("rule")) {
			spec.addRule(tmpRule);
		}
		else if (qName.equals("box")) {
			tmpRule.setBoxString(tmpContents);
		}
		else if (qName.equals("example")) {
			spec.setExample(tmpContents);
		}
		else if (qName.equals("language")) {
			spec.setLanguage(tmpContents);
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		tmpContents += new String(ch,start,length);
	}

}
