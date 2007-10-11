package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 * is implemented by <b>rule</b>
 */
public interface Irule
{
    public IToken getLeftIToken();
    public IToken getRightIToken();

    void accept(Visitor v);
}


