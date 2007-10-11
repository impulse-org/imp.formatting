package org.eclipse.imp.formatting.spec;

import lpg.runtime.IAst;
import org.eclipse.imp.box.parser.Ast.IBox;

/**
 * A formatting rule is currently still a simple tuple of an Ast pattern
 * and a corresponding Box pattern. The Ast pattern should be searched in 
 * an AST, and when found be replaced by the Box term. In the future, rules
 * can carry more complex firing conditions.
 * 
 * @author Jurgen Vinju
 *
 */
public class FormattingRule {
	/* the pattern is the part to be matched */
	private IAst patternAst;
	private String patternString;
	
	/* the box is the part to be constructed when a match has succeeded */
	private IBox BoxAst;
	private String BoxString;
	
	public String getBoxString() {
		return BoxString;
	}
	
	public void setBoxString(String BoxString) {
		this.BoxString = BoxString;
	}
	
	public IBox getBoxAst() {
		return BoxAst;
	}
	
	public void setBoxAst(IBox BoxAst) {
		this.BoxAst = BoxAst;
	}
	
	public IAst getPatternAst() {
		return patternAst;
	}
	
	public void setPatternAst(IAst patternAst) {
		this.patternAst = patternAst;
	}
	
	public String getPatternString() {
		return patternString;
	}
	
	public void setPatternString(String patternString) {
		this.patternString = patternString;
	}

}
