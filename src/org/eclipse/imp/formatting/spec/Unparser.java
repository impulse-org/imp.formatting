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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        DocumentBuilderFactory dbf= DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db= dbf.newDocumentBuilder();
            dom= db.newDocument();
            Element root= dom.createElement("formatter");
            addSpec(spec, root);
            dom.appendChild(root);
            return printToString(dom);
        } catch (ParserConfigurationException pce) {
            return null;
        }
    }

    private String printToString(Document dom) {
        try {
            ByteArrayOutputStream output= new ByteArrayOutputStream();
            TransformerFactory tf= TransformerFactory.newInstance();

            tf.setAttribute("indent-number", 4); // work around bug in Sun's JRE 5.0: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6296446

            Transformer t= tf.newTransformer();

            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
            t.transform(new DOMSource(dom), new StreamResult(new OutputStreamWriter(output, "utf-8")));

            String result= output.toString();

            output.close();
            return result;
        } catch (IOException ie) {
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
        Element language= dom.createElement("language");
        language.setTextContent(spec.getLanguage());
        Element rules= dom.createElement("rules");
        addRules(spec.ruleIterator(), rules);
        Element options= dom.createElement("space-options");
        addOptions(spec, options);
        Element example= dom.createElement("example");
        example.setTextContent(spec.getExample());
        root.appendChild(language);
        root.appendChild(rules);
        root.appendChild(options);
        root.appendChild(example);
    }

    private void addRules(Iterator<Item> list, Element rules) {
        while (list.hasNext()) {
            Item item= list.next();
            if (item instanceof Rule) {
                Rule rule= (Rule) item;
                Element elem= dom.createElement("rule");
                Element box= dom.createElement("box");
                elem.appendChild(box);
                box.setTextContent(rule.getBoxString());
                Element preview= dom.createElement("preview");
                elem.appendChild(preview);
                preview.setTextContent(rule.getPatternString());
                rules.appendChild(elem);
            } else if (item instanceof Separator) {
                Separator sep= (Separator) item;
                Element elem= dom.createElement("separator");
                String label= sep.getLabel();
                elem.setTextContent(label.length() > 0 ? label : "anonymous");
                rules.appendChild(elem);
            }
        }
    }

    private void addOptions(Specification spec, Element options) {
        Iterator<String> names= spec.getSpaceOptions();
        while (names.hasNext()) {
            String name= names.next();
            Integer value= spec.getSpaceOption(name);
            Element elem= dom.createElement("space-option");
            elem.setAttribute("name", name);
            elem.setAttribute("value", value.toString());
            options.appendChild(elem);
        }
    }
}
