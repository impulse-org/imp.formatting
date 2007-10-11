package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

public class ASTNodeToken extends ASTNode implements IASTNodeToken
{
    public ASTNodeToken(IToken token) { super(token); }
    public IToken getIToken() { return leftIToken; }
    public String toString() { return leftIToken.toString(); }

    /**
     * A token class has no children. So, we return the empty list.
     */
    public java.util.ArrayList getAllChildren() { return new java.util.ArrayList(); }

    public boolean equals(Object o)
    {
        if (o == this) return true;
        if (! (o instanceof ASTNodeToken)) return false;
        ASTNodeToken other = (ASTNodeToken) o;
        return toString().equals(other.toString());
    }

    public int hashCode()
    {
        return toString().hashCode();
    }

    public void accept(Visitor v)
    {
        if (! v.preVisit(this)) return;
        enter(v);
        v.postVisit(this);
    }

    public void enter(Visitor v)
    {
        v.visit(this);
        v.endVisit(this);
    }
}


