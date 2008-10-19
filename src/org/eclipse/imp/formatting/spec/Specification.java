/*******************************************************************************
* Copyright (c) 2008 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jurgen Vinju (jurgenv@cwi.nl) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.formatting.spec;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A formatting specification contains rules that map Ast's of a source language
 * to box. The specification is applicable to a certain lanuage only. Every
 * specification contains an example term for user reference.
 * 
 * @author Jurgen Vinju
 *
 */
public class Specification {
	private String language;
	private List<Item> items;
	private Map<String, Integer> spaceOptions;
	private String example;
	private Object exampleAst;
	private Parser parser;
	
	public Specification(String language, Parser parser) {
		this(parser);
		this.language = language;
	}
	
	public Specification(Parser parser) {
		this.items = new LinkedList<Item>();
		this.spaceOptions = new HashMap<String,Integer>();
		this.example = "";
		this.parser = parser;
	}
	
	public void setSpaceOption(String key, int value) {
		spaceOptions.put(key, value);
	}
	
	public int getSpaceOption(String key) {
		return spaceOptions.get(key);
	}
	
	public void removeSpaceOption(String key) {
		spaceOptions.remove(key);
	}

	public Iterator<String> getSpaceOptions() {
		return spaceOptions.keySet().iterator();
	}
	
	public Parser getParser() {
		return parser;
	}
	
	public void addRule(Item rule) {
		items.add(rule);
	}
	
	public void addRule(int index, Item rule) {
		items.add(index, rule);
	}
	
	public void removeRule(int index) {
		items.remove(index);
	}
	
	public List getRules() {
		return items;
	}
	
	public Iterator<Item> ruleIterator() {
		return items.listIterator();
	}
	
	public String getExample() {
		return example;
	}
	
	public void setExample(String example) {
		this.example = example;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	public Object getExampleAst() {
		return exampleAst;
	}
	
	public void setExampleAst(Object object) {
		this.exampleAst = object;
	}

	public void removeRule(Item tmpRule) {
		items.remove(tmpRule);
	}


	public void addSeparator(int i, Separator sep) {
		items.add(i, sep);
	}
	
	public void addSeparator(Separator sep) {
		items.add(sep);
	}
	
	public void removeSeparator(Separator sep) {
		items.remove(sep);
	}
	
	public void move(int index, Item item) {
		items.remove(item);
		items.add(index, item);
	}
	
	public void move(int index, Item[] toBeMoved) {
		for (Item item : toBeMoved) {
			items.remove(item);
			items.add(index++, item);
		}
	}
}
