package org.eclipse.imp.formatting.spec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lpg.runtime.IMessageHandler;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.box.builders.BoxFactory;
import org.eclipse.imp.box.parser.BoxParseController;
import org.eclipse.imp.box.parser.Ast.IBox;
import org.eclipse.imp.formatting.Activator;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IParseController;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This parser reads .fdl files. These are XML files containing lists of Box
 * expressions and an example program. The fun of parsing .fdl files is that it
 * contains embedded Box, and embedded object language code. We use SAX to parse
 * the XML backbone, we use the org.eclipse.imp.box package to parse the box
 * expressions. Each box expressions represents a pattern in the object
 * language, another parser is called to parse those. Finally, the example
 * source code is parsed with that same parser.
 * 
 * The parser constructs a
 * 
 * @see Specification object that contains all relevant information.
 * 
 * @author jurgenv
 * 
 */
public class Parser extends DefaultHandler {

	private static final String PROBLEM_TYPE = "org.eclipse.imp.formatting.parsing";

	static protected SAXParserFactory spf = SAXParserFactory.newInstance();

	protected Specification spec;

	protected Rule tmpRule;

	protected String tmpContents;

	private ISourceProject project;

	private IPath path;

	private IParseController objectParser;

	private Language objectLanguage;

	private IParseController boxParser;

	private IProgressMonitor boxParserMonitor;

	private IProgressMonitor objectParserMonitor;

	private IMessageHandler handler;

	private String currenObjectString;

	public Parser(IPath path, ISourceProject project) throws ModelException {
		this.path = path;
		this.spec = new Specification();
		this.project = project;
	}

	/**
	 * Use this method to parse a specification file of which you have a path
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public Specification parse(IPath path) throws ParseException,
			FileNotFoundException {
		this.path = path;
		return parse(new InputSource(new FileReader(path.toOSString())));
	}

	public Specification parse(String inputString) throws ParseException {
		// TODO: throw some sensible exception
		return parse(new InputSource(new StringReader(inputString)));
	}

	private Specification parse(InputSource input) throws ParseException {
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, this);

			if (spec == null) {
				throw new ParseException("Parsing of " + input
						+ "failed miserably");
			}

			return spec;
		} catch (SAXException se) {
			throw new ParseException("Parsing of " + input
					+ " failed because XML was invalid", se);
		} catch (ParserConfigurationException pce) {
			throw new ParseException("Parsing of " + input
					+ " failed because XML configuration is wrong", pce);
		} catch (IOException ie) {
			throw new ParseException("Parsing of " + input
					+ " failed because of an input error", ie);
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
			try {
				if (tmpRule != null) {
				  parseBoxAndObject(tmpContents, tmpRule);
				}
			} catch (ParseException e) {
				// TODO It is not much of a problem if the box expression does
				// not parse,
				// but it has to be reported properly to the user
			}
		} else if (qName.equals("example")) {
			spec.setExample(tmpContents);
			spec.setExampleAst(parseObject(tmpContents));
		} else if (qName.equals("language")) {
			spec.setLanguage(tmpContents);
			objectLanguage = LanguageRegistry.findLanguage(tmpContents);
			ExtensionPointBinder b = new ExtensionPointBinder(objectLanguage);
			objectParser = b.getObjectParser();
		}
	}

	public void parseBoxAndObject(String boxString, Rule rule)
			throws ParseException {
		rule.setBoxString(boxString);
		rule.setBoxAst(parseBox(boxString));

		if (rule.getBoxAst() != null) {
			rule.setPatternString(BoxFactory.extractText(rule.getBoxAst()));
			rule.setPatternAst(parseObject(rule.getPatternString()));
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tmpContents += new String(ch, start, length);
	}

	public IBox parseBox(String boxString) {
		initializeBoxParser();
		return (IBox) boxParser.parse(boxString, false, boxParserMonitor);
	}

	private void initializeBoxParser() {
		if (boxParser == null) {
			boxParser = new BoxParseController();
			IMessageHandler handler = new IMessageHandler() {

				public void handleMessage(int errorCode, int[] msgLocation,
						int[] errorLocation, String filename, String[] errorInfo) {
					Activator.getInstance()
							.writeErrorMsg("box term is invalid");
				}

			};
			boxParserMonitor = new NullProgressMonitor();
			boxParser.initialize(path, project, handler);
		}
	}

	public Object parseObject(String objectString) {
		initializeObjectParser(objectParser);

		this.currenObjectString = objectString;
		
		return objectParser.parse(objectString, false, objectParserMonitor);
	}

	private void initializeObjectParser(IParseController parseController) {
		if (objectParserMonitor == null) {
			objectParserMonitor = new NullProgressMonitor();
			handler = new IMessageHandler() {
			
							public void handleMessage(int errorCode, int[] msgLocation,
									int[] errorLocation, String filename, String[] errorInfo) {
								StringBuffer buf = new StringBuffer();
								buf.append("parse error in object language code \"");
								buf.append(currenObjectString);
								buf.append("\" @");
								for (int i = 0; i < errorLocation.length; i++) {
									if (i != 0) buf.append(", ");
									buf.append(errorLocation[i]);
								}
								Activator.getInstance().writeErrorMsg(buf.toString());
							}
			
						};
			parseController.initialize(path, project, handler);
		}
	}
}
