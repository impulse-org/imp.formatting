package org.eclipse.imp.formatting.spec;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lpg.runtime.IMessageHandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.box.builders.BoxFactory;
import org.eclipse.imp.box.parser.BoxParseController;
import org.eclipse.imp.box.parser.Ast.IBox;
import org.eclipse.imp.builder.BuilderUtils;
import org.eclipse.imp.java.matching.PolyglotASTAdapter;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.x10dt.ui.parser.ParseController;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Parser extends DefaultHandler {

	static protected SAXParserFactory spf = SAXParserFactory.newInstance();

	protected Specification spec;

	protected Rule tmpRule;

	protected String tmpContents;

	private ISourceProject project;
	private IFile file;

	private Transformer transformer;
	
	public Parser(IFile file) throws ModelException {
		this.file = file;
		this.project = ModelFactory.open(file.getProject());
		this.spec = new Specification();
		this.transformer = new Transformer(spec, new PolyglotASTAdapter());
	}

	public Specification parse(String string) throws Exception {
		return parse(new InputSource(new StringReader(string)));
	}

	public Specification parse() throws Exception {
		return parse(new InputSource(new StringReader(BuilderUtils
				.getFileContents(file))));
	}

	protected Specification parse(InputSource input) throws Exception {
		// TODO: throw some sensible exception

		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, this);

			
			if (spec != null) {
				String boxString = transformer.transformToBox(spec.getExampleAst());
				spec.setExample(BoxFactory.box2text(boxString));
				
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
		if (qName.equals("rule")) {
			tmpRule = new Rule();
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
		} else if (qName.equals("box")) {
			parseBoxAndObject(tmpContents, tmpRule);
		} else if (qName.equals("example")) {
			spec.setExample(tmpContents);
			spec.setExampleAst(parseObject(tmpContents));
		} else if (qName.equals("language")) {
			spec.setLanguage(tmpContents);
		}
	}

	public void parseBoxAndObject(String boxString, Rule rule) throws SAXException {
		rule.setBoxString(boxString);
		rule.setBoxAst(parseBox(boxString));
		try {
			rule.setPatternString(BoxFactory.box2text(boxString));
			rule.setPatternAst(parseObject(rule.getPatternString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tmpContents += new String(ch, start, length);
	}

	public IBox parseBox(String boxString) throws SAXException {
		IParseController parseController = new BoxParseController();
		IMessageHandler handler = new IMessageHandler() {

			public void handleMessage(int errorCode, int[] msgLocation, int[] errorLocation, String filename, String[] errorInfo) {
				System.err.println("error during box parsing: " + errorInfo);
			}
			
		};
		IProgressMonitor monitor = new NullProgressMonitor();
		parseController.initialize(file.getProjectRelativePath(), project, handler);
		return (IBox) parseController.parse(boxString, false, monitor);
	}
	
	public Object parseObject(String objectString) {
		IParseController parseController = new ParseController();
		IProgressMonitor monitor = new NullProgressMonitor();
		IMessageHandler handler = new IMessageHandler() {

			public void handleMessage(int errorCode, int[] msgLocation, int[] errorLocation, String filename, String[] errorInfo) {
				System.err.println("error during object language parsing: " + errorInfo);
			}
			
		};
		parseController.initialize(file.getProjectRelativePath(), project, handler);
		return parseController.parse(objectString, false, monitor);
	}

}
