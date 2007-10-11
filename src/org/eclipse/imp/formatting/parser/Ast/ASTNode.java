package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

public abstract class ASTNode implements IAst
{
    public IAst getNextAst() { return null; }
    protected IToken leftIToken,
                     rightIToken;
    protected IAst parent = null;
    protected void setParent(IAst parent) { this.parent = parent; }
    public IAst getParent() { return parent; }

    public IToken getLeftIToken() { return leftIToken; }
    public IToken getRightIToken() { return rightIToken; }
    public IToken[] getPrecedingAdjuncts() { return leftIToken.getPrecedingAdjuncts(); }
    public IToken[] getFollowingAdjuncts() { return rightIToken.getFollowingAdjuncts(); }

    public String toString()
    {
        return leftIToken.getPrsStream().toString(leftIToken, rightIToken);
    }

    public ASTNode(IToken token) { this.leftIToken = this.rightIToken = token; }
    public ASTNode(IToken leftIToken, IToken rightIToken)
    {
        this.leftIToken = leftIToken;
        this.rightIToken = rightIToken;
    }

    void initialize() {}

    /**
     * A list of all children of this node, excluding the null ones.
     */
    public java.util.ArrayList getChildren()
    {
        java.util.ArrayList list = getAllChildren();
        int k = -1;
        for (int i = 0; i < list.size(); i++)
        {
            Object element = list.get(i);
            if (element != null)
            {
                if (++k != i)
                    list.set(k, element);
            }
        }
        for (int i = list.size() - 1; i > k; i--) // remove extraneous elements
            list.remove(i);
        return list;
    }

    /**
     * A list of all children of this node, including the null ones.
     */
    public abstract java.util.ArrayList getAllChildren();

    /**
     * Since the Ast type has no children, any two instances of it are equal.
     */
    public boolean equals(Object o) { return o instanceof ASTNode; }
    public abstract int hashCode();
    public abstract void accept(Visitor v);
}


