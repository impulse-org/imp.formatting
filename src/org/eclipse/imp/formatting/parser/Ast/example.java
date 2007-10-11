package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 *<b>
 *<li>Rule 8:  example ::= begin_example any end_example
 *</b>
 */
public class example extends ASTNode implements Iexample
{
    private ASTNodeToken _begin_example;
    private ASTNodeToken _any;
    private ASTNodeToken _end_example;

    public ASTNodeToken getbegin_example() { return _begin_example; }
    public ASTNodeToken getany() { return _any; }
    public ASTNodeToken getend_example() { return _end_example; }

    public example(IToken leftIToken, IToken rightIToken,
                   ASTNodeToken _begin_example,
                   ASTNodeToken _any,
                   ASTNodeToken _end_example)
    {
        super(leftIToken, rightIToken);

        this._begin_example = _begin_example;
        ((ASTNode) _begin_example).setParent(this);
        this._any = _any;
        ((ASTNode) _any).setParent(this);
        this._end_example = _end_example;
        ((ASTNode) _end_example).setParent(this);
        initialize();
    }

    /**
     * A list of all children of this node, including the null ones.
     */
    public java.util.ArrayList getAllChildren()
    {
        java.util.ArrayList list = new java.util.ArrayList();
        list.add(_begin_example);
        list.add(_any);
        list.add(_end_example);
        return list;
    }

    public boolean equals(Object o)
    {
        if (o == this) return true;
        //
        // The super call test is not required for now because an Ast node
        // can only extend the root Ast, AstToken and AstList and none of
        // these nodes contain additional children.
        //
        // if (! super.equals(o)) return false;
        //
        if (! (o instanceof example)) return false;
        example other = (example) o;
        if (! _begin_example.equals(other._begin_example)) return false;
        if (! _any.equals(other._any)) return false;
        if (! _end_example.equals(other._end_example)) return false;
        return true;
    }

    public int hashCode()
    {
        int hash = 7;
        hash = hash * 31 + (_begin_example.hashCode());
        hash = hash * 31 + (_any.hashCode());
        hash = hash * 31 + (_end_example.hashCode());
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
            _begin_example.accept(v);
            _any.accept(v);
            _end_example.accept(v);
        }
        v.endVisit(this);
    }
}


