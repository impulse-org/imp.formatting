package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 * is implemented by <b>rules</b>
 */
public interface Irules
{
    public IToken getLeftIToken();
    public IToken getRightIToken();

    void accept(Visitor v);
}


