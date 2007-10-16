package org.eclipse.imp.formatting.spec;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lpg.runtime.IMessageHandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.box.builders.BoxFactory;
import org.eclipse.imp.box.parser.BoxParseController;
import org.eclipse.imp.box.parser.Ast.IBox;
import org.eclipse.imp.builder.BuilderUtils;
import org.eclipse.imp.builder.MarkerCreator;
import org.eclipse.imp.formatting.Activator;
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

import polyglot.ast.Node;
import polyglot.util.Position;

public class Parser extends DefaultHandler {

	private static final String PROBLEM_TYPE = "org.eclipse.imp.formatting.parsing";

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
		this.transformer = new Transformer(spec, new PolyglotASTAdapter() {
			public int getOffset(Object astNode) {
				Node node = (Node) astNode;

				return ((Position) node.position()).offset();
			}

			public int getLength(Object astNode) {
				Node node = (Node) astNode;

				return ((Position) node.position()).endOffset() - ((Position) node.position()).offset() + 1;
			}
		});
		
		
	}

	public Specification parse() throws Exception {
		return parse(BuilderUtils.getFileContents(file));
	}

	public Specification parse(String inputString) throws Exception {
		// TODO: throw some sensible exception
		InputSource input = new InputSource(new StringReader(inputString));

		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, this);

			// TODO: move this outside of the parser
			if (spec != null) {
				String boxString = transformer.transformToBox(spec.getExample(), spec
						.getExampleAst());

				if (boxString != null) {
					spec.setExample(BoxFactory.box2text(boxString));
				}

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

	public void parseBoxAndObject(String boxString, Rule rule)
			throws SAXException {
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

			public void handleMessage(int errorCode, int[] msgLocation,
					int[] errorLocation, String filename, String[] errorInfo) {
				
				try {
					IMarker marker = file.createMarker(PROBLEM_TYPE);
					marker.setAttribute(IMarker.MESSAGE, "box term is invalid");
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Activator.getInstance().writeErrorMsg("box term is invalid");
			}

		};
		IProgressMonitor monitor = new NullProgressMonitor();
		parseController.initialize(file.getProjectRelativePath(), project,
				handler);
		return (IBox) parseController.parse(boxString, false, monitor);
	}

	public Object parseObject(String objectString) {
		IParseController parseController = new ParseController();
		IProgressMonitor monitor = new NullProgressMonitor();
		IMessageHandler handler = new IMessageHandler() {

			public void handleMessage(int errorCode, int[] msgLocation,
					int[] errorLocation, String filename, String[] errorInfo) {
				Activator.getInstance().writeErrorMsg("not a valid sentence in the object language");
			}

		};
		parseController.initialize(file.getProjectRelativePath(), project,
				handler);
		return parseController.parse(objectString, false, monitor);
	}

}
