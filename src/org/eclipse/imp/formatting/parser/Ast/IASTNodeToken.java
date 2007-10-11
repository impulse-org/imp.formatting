package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
 *<b>
 *<ul>
 *</ul>
 *</b>
 */
public interface IASTNodeToken
{
    public IToken getLeftIToken();
    public IToken getRightIToken();

    void accept(Visitor v);
}


