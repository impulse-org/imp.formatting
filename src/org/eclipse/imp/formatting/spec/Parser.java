package org.eclipse.imp.formatting.spec;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lpg.runtime.IAst;
import lpg.runtime.IMessageHandler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.box.builders.BoxFactory;
import org.eclipse.imp.box.parser.BoxParseController;
import org.eclipse.imp.box.parser.Ast.IBox;
import org.eclipse.imp.builder.BuilderUtils;
import org.eclipse.imp.formatting.Activator;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.xform.pattern.parser.ASTAdapterBase;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pico.imp.parser.PicoParseController;
import pico.imp.parser.Ast.Decls1;
import pico.imp.parser.Ast.Expr;
import pico.imp.parser.Ast.IdType1;
import pico.imp.parser.Ast.IdTypes1;
import pico.imp.parser.Ast.Identifier1;
import pico.imp.parser.Ast.Program1;
import pico.imp.parser.Ast.Stat3;
import pico.imp.parser.Ast.Stats1;
import pico.imp.parser.Ast.Type2;

/**
 * This parser reads .fdl files. These are XML files containing lists of Box
 * expressions and an example program. The fun of parsing .fdl files is that
 * it contains embedded Box, and embedded object language code. We use SAX
 * to parse the XML backbone, we use the org.eclipse.imp.box package to parse
 * the box expressions. Each box expressions represents a pattern in the object
 * language, another parser is called to parse those. Finally, the example source
 * code is parsed with that same parser.
 * 
 * The parser constructs a @see Specification object that contains all relevant 
 * information.
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

	private IFile file;

	private Transformer transformer;
	
	public Parser(IFile file) throws ModelException {
		this.file = file;
		this.project = ModelFactory.open(file.getProject());
		this.spec = new Specification();
		this.transformer = new Transformer(spec, new ASTAdapterBase() {
			@Override
			public Object[] getChildren(Object astNode) {
				return ((IAst) astNode).getChildren().toArray();
			}
			
			@Override
			public int getOffset(Object astNode) {
				return ((IAst) astNode).getLeftIToken().getStartOffset();
			}
			
			
			public int getLength(Object astNode) {
				int end = ((IAst) astNode).getLeftIToken().getEndOffset();
				return end - getOffset(astNode) + 1;
			}
			
			public String getTypeOf(Object astNode) {
				return astNode.getClass().getName();
			}
			
			@Override
			public boolean isPlaceholder(Object astNode) {
				return astNode instanceof Expr
				|| astNode instanceof Program1
				|| astNode instanceof Decls1
				|| astNode instanceof Stats1
				|| astNode instanceof IdTypes1
				|| astNode instanceof IdType1
				|| astNode instanceof Identifier1
				|| astNode instanceof Type2
				|| astNode instanceof Stat3;
			}
		});
	}

	

	public Specification parse() throws Exception {
		return parse(BuilderUtils.getFileContents(file));
	}

	public Specification parse(String inputString) throws ParseException {
		// TODO: throw some sensible exception
		InputSource input = new InputSource(new StringReader(inputString));

		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, this);

			if (spec == null) {
				throw new ParseException("Parsing of " + input + "failed miserably");
			}
			
			// TODO: move this outside of the parser
			if (spec.getExampleAst() != null) {
				String boxString = transformer.transformToBox(spec.getExample(), spec
						.getExampleAst());

				if (boxString != null) {
					try {
						spec.setExample(BoxFactory.fastbox2text(boxString));
					} catch (InterruptedException e) {
						throw new ParseException("Formatting of the example code failed", e);
					}
				}
			}
			else {
				System.err.println("Example was not parsed correctly");
			}
			
			return spec;
		} catch (SAXException se) {
			throw new ParseException("Parsing of " + input + " failed because XML was invalid", se);
		} catch (ParserConfigurationException pce) {
			throw new ParseException("Parsing of " + input + " failed because XML configuration is wrong", pce);
		} catch (IOException ie) {
			throw new ParseException("Parsing of " + input + " failed because of an input error", ie);
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
			if (tmpRule.getBoxAst() != null &&
					tmpRule.getPatternAst() != null) {
			  spec.addRule(tmpRule);
			}
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
			 {
		try {
			rule.setBoxString(boxString);
			rule.setBoxAst(parseBox(boxString));
			rule.setPatternString(BoxFactory.fastbox2text(boxString));
			rule.setPatternAst(parseObject(rule.getPatternString()));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		IParseController parseController = new PicoParseController();
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
