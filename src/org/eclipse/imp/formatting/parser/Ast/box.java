package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 *<b>
 *<li>Rule 7:  box ::= begin_box any end_box
 *</b>
 */
public class box extends ASTNode implements Ibox
{
    private ASTNodeToken _begin_box;
    private ASTNodeToken _any;
    private ASTNodeToken _end_box;

    public ASTNodeToken getbegin_box() { return _begin_box; }
    public ASTNodeToken getany() { return _any; }
    public ASTNodeToken getend_box() { return _end_box; }

    public box(IToken leftIToken, IToken rightIToken,
               ASTNodeToken _begin_box,
               ASTNodeToken _any,
               ASTNodeToken _end_box)
    {
        super(leftIToken, rightIToken);

        this._begin_box = _begin_box;
        ((ASTNode) _begin_box).setParent(this);
        this._any = _any;
        ((ASTNode) _any).setParent(this);
        this._end_box = _end_box;
        ((ASTNode) _end_box).setParent(this);
        initialize();
    }

    /**
     * A list of all children of this node, including the null ones.
     */
    public java.util.ArrayList getAllChildren()
    {
        java.util.ArrayList list = new java.util.ArrayList();
        list.add(_begin_box);
        list.add(_any);
        list.add(_end_box);
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
        if (! (o instanceof box)) return false;
        box other = (box) o;
        if (! _begin_box.equals(other._begin_box)) return false;
        if (! _any.equals(other._any)) return false;
        if (! _end_box.equals(other._end_box)) return false;
        return true;
    }

    public int hashCode()
    {
        int hash = 7;
        hash = hash * 31 + (_begin_box.hashCode());
        hash = hash * 31 + (_any.hashCode());
        hash = hash * 31 + (_end_box.hashCode());
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
            _begin_box.accept(v);
            _any.accept(v);
            _end_box.accept(v);
        }
        v.endVisit(this);
    }
}


