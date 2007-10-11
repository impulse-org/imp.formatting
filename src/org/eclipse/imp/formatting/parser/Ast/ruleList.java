package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 *<b>
 *<li>Rule 4:  rulelist ::= $Empty
 *<li>Rule 5:  rulelist ::= rulelist rule
 *</b>
 */
public class ruleList extends AbstractASTNodeList implements Irulelist
{
    public rule getruleAt(int i) { return (rule) getElementAt(i); }

    public ruleList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
    {
        super(leftIToken, rightIToken, leftRecursive);
    }

    public ruleList(rule _rule, boolean leftRecursive)
    {
        super((ASTNode) _rule, leftRecursive);
        ((ASTNode) _rule).setParent(this);
    }

    public void add(rule _rule)
    {
        super.add((ASTNode) _rule);
        ((ASTNode) _rule).setParent(this);
    }

    public boolean equals(Object o)
    {
        if (o == this) return true;
        if (! (o instanceof ruleList)) return false;
        ruleList other = (ruleList) o;
        if (size() != other.size()) return false;
        for (int i = 0; i < size(); i++)
        {
            rule element = getruleAt(i);
            if (! element.equals(other.getruleAt(i))) return false;
        }
        return true;
    }

    public int hashCode()
    {
        int hash = 7;
        for (int i = 0; i < size(); i++)
            hash = hash * 31 + (getruleAt(i).hashCode());
        return hash;
    }

    public void accept(Visitor v)
    {
        if (! v.preVisit(this)) return;
        enter(v);
        v.postVisit(this);
    }
    public void enter(Visitor v)
    {
        boolean checkChildren = v.visit(this);
        if (checkChildren)
        {
            for (int i = 0; i < size(); i++)
            {
                rule element = getruleAt(i);
                if (! v.preVisit(element)) continue;
                element.enter(v);
                v.postVisit(element);
            }
        }
        v.endVisit(this);
    }
}


