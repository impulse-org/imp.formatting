package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

public abstract class AbstractASTNodeList extends ASTNode
{
    private boolean leftRecursive;
    private java.util.ArrayList list;
    public int size() { return list.size(); }
    public ASTNode getElementAt(int i) { return (ASTNode) list.get(leftRecursive ? i : list.size() - 1 - i); }
    public java.util.ArrayList getArrayList()
    {
        if (! leftRecursive) // reverse the list 
        {
            for (int i = 0, n = list.size() - 1; i < n; i++, n--)
            {
                Object ith = list.get(i),
                       nth = list.get(n);
                list.set(i, nth);
                list.set(n, ith);
            }
            leftRecursive = true;
        }
        return list;
    }
    public void add(ASTNode element)
    {
        list.add(element);
        if (leftRecursive)
             rightIToken = element.getRightIToken();
        else leftIToken = element.getLeftIToken();
    }

    public AbstractASTNodeList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
    {
        super(leftIToken, rightIToken);
        this.leftRecursive = leftRecursive;
        list = new java.util.ArrayList();
    }

    public AbstractASTNodeList(ASTNode element, boolean leftRecursive)
    {
        this(element.getLeftIToken(), element.getRightIToken(), leftRecursive);
        list.add(element);
    }

    /**
     * Make a copy of the list and return it. Note that we obtain the local list by
     * invoking getArrayList so as to make sure that the list we return is in proper order.
     */
    public java.util.ArrayList getAllChildren()
    {
        return (java.util.ArrayList) getArrayList().clone();
    }

    public abstract boolean equals(Object o);

    public int hashCode()
    {
        int hash = 7;
        for (int i = 0; i < size(); i++)
        {
            ASTNode element = getElementAt(i);
            hash = hash * 31 + (element == null ? 0 : element.hashCode());
        }
        return hash;
    }
}


