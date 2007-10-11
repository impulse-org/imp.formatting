package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 *<b>
 *<li>Rule 6:  rule ::= begin_rule box end_rule
 *</b>
 */
public class rule extends ASTNode implements Irule
{
    private ASTNodeToken _begin_rule;
    private box _box;
    private ASTNodeToken _end_rule;

    public ASTNodeToken getbegin_rule() { return _begin_rule; }
    public box getbox() { return _box; }
    public ASTNodeToken getend_rule() { return _end_rule; }

    public rule(IToken leftIToken, IToken rightIToken,
                ASTNodeToken _begin_rule,
                box _box,
                ASTNodeToken _end_rule)
    {
        super(leftIToken, rightIToken);

        this._begin_rule = _begin_rule;
        ((ASTNode) _begin_rule).setParent(this);
        this._box = _box;
        ((ASTNode) _box).setParent(this);
        this._end_rule = _end_rule;
        ((ASTNode) _end_rule).setParent(this);
        initialize();
    }

    /**
     * A list of all children of this node, including the null ones.
     */
    public java.util.ArrayList getAllChildren()
    {
        java.util.ArrayList list = new java.util.ArrayList();
        list.add(_begin_rule);
        list.add(_box);
        list.add(_end_rule);
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
        if (! (o instanceof rule)) return false;
        rule other = (rule) o;
        if (! _begin_rule.equals(other._begin_rule)) return false;
        if (! _box.equals(other._box)) return false;
        if (! _end_rule.equals(other._end_rule)) return false;
        return true;
    }

    public int hashCode()
    {
        int hash = 7;
        hash = hash * 31 + (_begin_rule.hashCode());
        hash = hash * 31 + (_box.hashCode());
        hash = hash * 31 + (_end_rule.hashCode());
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
            _begin_rule.accept(v);
            _box.accept(v);
            _end_rule.accept(v);
        }
        v.endVisit(this);
    }
}


