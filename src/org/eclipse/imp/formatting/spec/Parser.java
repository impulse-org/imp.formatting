/*******************************************************************************
* Copyright (c) IBM Corporation 2008 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jurgen Vinju (jurgenv@cwi.nl) - initial API and implementation
*******************************************************************************/

package org.eclipse.imp.formatting.spec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.box.builders.BoxFactory;
import org.eclipse.imp.box.parser.BoxParseController;
import org.eclipse.imp.box.parser.Ast.IBox;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This parser reads .fsp files. These are XML files containing lists of Box
 * expressions and an example program. The fun of parsing .fdl files is that it
 * contains embedded Box, and embedded object language code. We use SAX to parse
 * the XML backbone, we use the org.eclipse.imp.box package to parse the box
 * expressions. Each box expressions represents a pattern in the object
 * language, another parser is called to parse those. Finally, the example
 * source code is parsed with that same parser.
 * 
 * The parser constructs a {@link Specification} object that contains all relevant information.
 * @author jurgenv
 */
public class Parser extends DefaultHandler {

	static protected SAXParserFactory spf = SAXParserFactory.newInstance();

	protected Specification fSpec;

	protected Rule fTmpRule;

	protected String fTmpContents;

	private ISourceProject fProject;

	private IPath fPath;

    private Language fObjectLanguage;

	private IParseController fObjectParser;

	private IParseController fBoxParser;

	private IProgressMonitor fBoxParserMonitor;

	private IProgressMonitor fObjectParserMonitor;

	private IMessageHandler fHandler;

	private Separator fTmpSeparator;

	private boolean fNoObjectParsing;

	public Parser(IPath path, ISourceProject project, IMessageHandler handler) throws ModelException {
		this.fPath = path;
		this.fSpec = new Specification(this);
		this.fProject = project;
		this.fHandler = handler;
		this.fNoObjectParsing = false;
	}

	public IMessageHandler getMessageHandler() {
	    return fHandler;
    }

	/**
	 * Use this method to parse a specification file of which you have a path
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public Specification parse(IPath path) throws ParseException, FileNotFoundException {
		this.fPath = path;
		fNoObjectParsing = false;
		return parse(new InputSource(new FileReader(path.toOSString())));
	}

	public Specification parse(String inputString) throws ParseException {
		// TODO: throw some sensible exception
		fNoObjectParsing = false;
		return parse(new InputSource(new StringReader(inputString)));
	}
	
	/**
	 * This method will read in the XML, but will not apply prepare
	 * the specification for direct use as a formatter. The patterns
	 * will not be parsed. This is useful for the specification editor.
	 * @param inputString
	 * @return
	 * @throws ParseException
	 */
	public Specification load(String inputString) throws ParseException {
		fNoObjectParsing = true;
		return parse(new InputSource(new StringReader(inputString)));
	}

	private Specification parse(InputSource input) throws ParseException {
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(input, this);

			if (fSpec == null) {
				throw new ParseException("Parsing of " + input + "failed.");
			}

			return fSpec;
		} catch (SAXException se) {
			throw new ParseException("Parsing of " + input + " failed because XML was invalid", se);
		} catch (ParserConfigurationException pce) {
			throw new ParseException("Parsing of " + input + " failed because XML configuration is wrong", pce);
		} catch (IOException ie) {
			throw new ParseException("Parsing of " + input + " failed because of an input error", ie);
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("rule")) {
			fTmpRule = new Rule();
		} else if (qName.equals("box")) {
			fTmpContents = "";
		}
	    else if (qName.equals("preview")) {
			fTmpContents = "";
		} else if (qName.equals("example")) {
			fTmpContents = "";
		} else if (qName.equals("language")) {
			fTmpContents = "";
		} else if (qName.equals("separator")) {
			fTmpSeparator = new Separator();
			fTmpContents = "";
		} else if (qName.equals("space-option")) {
			String name = attributes.getValue("name");
			Integer value = Integer.parseInt(attributes.getValue("value"));
			fSpec.setSpaceOption(name, value);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("rule")) {
			fSpec.addRule(fTmpRule);
		} else if (qName.equals("box")) {
			if (fTmpRule != null) {
				if (fNoObjectParsing) {
				    fTmpRule.setBoxString(fTmpContents);
				} else {
				  try {
				      parseBoxAndObject(fTmpContents, fTmpRule);
				  } catch (ParseException e) {
				      e.printStackTrace();
				  }
				}
			}
		} else if (qName.equals("preview")) {
			if (fTmpRule != null) {
				fTmpRule.setPatternString(fTmpContents);
			}
		} else if (qName.equals("example")) {
			fSpec.setExample(fTmpContents);
			fSpec.setExampleAst(parseObject(fTmpContents));
		} else if (qName.equals("language")) {
			fSpec.setLanguage(fTmpContents);
			setLanguage(fTmpContents);
		} else if (qName.equals("separator")) {
			fTmpSeparator.setLabel(fTmpContents);
			fSpec.addSeparator(fTmpSeparator);
		} 
	}

	public void setLanguage(String langName) {
		if (fObjectParser == null) {
			try {
				fObjectLanguage = LanguageRegistry.findLanguage(langName);
				if (fObjectLanguage != null) {
				    ExtensionPointBinder b = new ExtensionPointBinder(fObjectLanguage);

				    fObjectParser = b.getObjectParser();
				} else {
				    displayAlert("Unknown target language", "A language descriptor for the language '" + langName + "' has not been registered.");
				}
			} catch (final Exception e) {
			    String msg= e.getMessage().contains("astAdapter") ?
			          "Unable to find specified AST adapter class; check the formatting extension in the plugin.xml" :
			          "Unable to find target language parser class; check the formatting extension in the plugin.xml";

			    displayAlert("Error initializing target language pattern parser", msg);
			}
		}
	}

	private void displayAlert(final String title, final String msg) {
        Display display= PlatformUI.getWorkbench().getDisplay();
        final Shell shell= display.getActiveShell();
        display.asyncExec(new Runnable() {
            public void run() {
                MessageDialog.openError(shell, title, msg);
            }
        });
	}

	public void parseBoxAndObject(String boxString, Rule rule) throws ParseException {
		rule.setBoxString(boxString);
		rule.setBoxAst(parseBox(boxString));

		if (rule.getBoxAst() != null) {
			rule.setPatternString(BoxFactory.extractText(rule.getBoxAst()));
			rule.setPatternAst(parseObject(rule.getPatternString()));
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		fTmpContents += new String(ch, start, length);
	}

	public IBox parseBox(String boxString) {
		initializeBoxParser();
		return (IBox) fBoxParser.parse(boxString, fBoxParserMonitor);
	}

	private void initializeBoxParser() {
		if (fBoxParser == null) {
			fBoxParser = new BoxParseController();
			fBoxParserMonitor = new NullProgressMonitor();
		}
		fBoxParser.initialize(fPath, fProject, fHandler);
	}

	public Object parseObject(String objectString) {
	    if (fObjectParser == null) {
	        fHandler.clearMessages();
	        fHandler.handleSimpleMessage("Unable to parse formatted text: no parser is configured", 0, 0, 0, 0, 0, 0);
	        fHandler.endMessages();
	        return null;
	    }
		initializeObjectParser(fObjectParser);
		return fObjectParser.parse(objectString, fObjectParserMonitor);
	}

	private void initializeObjectParser(IParseController parseController) {
		if (fObjectParserMonitor == null) {
			fObjectParserMonitor = new NullProgressMonitor();
		}
		parseController.initialize(fPath, fProject, fHandler);
	}
}
