package org.eclipse.imp.formatting.spec;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A formatting specification contains rules that map Ast's of a source language
 * to box. The specification is applicable to a certain lanuage only. Every
 * specification contains an example term for user reference.
 * 
 * @author Jurgen Vinju
 *
 */
public class FormattingSpecification {
	private String language;
	private List<FormattingRule> rules;
	private String example;
	private Object exampleAst;
	
	public FormattingSpecification(String language) {
		this();
		this.language = language;
	}
	
	public FormattingSpecification() {
		this.rules = new LinkedList<FormattingRule>();
		this.example = "";
	}

	public void addRule(FormattingRule rule) {
		rules.add(rule);
	}
	
	public void removeRule(int index) {
		rules.remove(index);
	}
	
	public List getRules() {
		return rules;
	}
	
	public Iterator<FormattingRule> ruleIterator() {
		return rules.listIterator();
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
}
