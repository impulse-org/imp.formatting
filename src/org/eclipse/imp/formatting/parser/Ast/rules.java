package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 *<b>
 *<li>Rule 3:  rules ::= begin_rules rulelist end_rules
 *</b>
 */
public class rules extends ASTNode implements Irules
{
    private ASTNodeToken _begin_rules;
    private ruleList _rulelist;
    private ASTNodeToken _end_rules;

    public ASTNodeToken getbegin_rules() { return _begin_rules; }
    public ruleList getrulelist() { return _rulelist; }
    public ASTNodeToken getend_rules() { return _end_rules; }

    public rules(IToken leftIToken, IToken rightIToken,
                 ASTNodeToken _begin_rules,
                 ruleList _rulelist,
                 ASTNodeToken _end_rules)
    {
        super(leftIToken, rightIToken);

        this._begin_rules = _begin_rules;
        ((ASTNode) _begin_rules).setParent(this);
        this._rulelist = _rulelist;
        ((ASTNode) _rulelist).setParent(this);
        this._end_rules = _end_rules;
        ((ASTNode) _end_rules).setParent(this);
        initialize();
    }

    /**
     * A list of all children of this node, including the null ones.
     */
    public java.util.ArrayList getAllChildren()
    {
        java.util.ArrayList list = new java.util.ArrayList();
        list.add(_begin_rules);
        list.add(_rulelist);
        list.add(_end_rules);
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
        if (! (o instanceof rules)) return false;
        rules other = (rules) o;
        if (! _begin_rules.equals(other._begin_rules)) return false;
        if (! _rulelist.equals(other._rulelist)) return false;
        if (! _end_rules.equals(other._end_rules)) return false;
        return true;
    }

    public int hashCode()
    {
        int hash = 7;
        hash = hash * 31 + (_begin_rules.hashCode());
        hash = hash * 31 + (_rulelist.hashCode());
        hash = hash * 31 + (_end_rules.hashCode());
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
            _begin_rules.accept(v);
            _rulelist.accept(v);
            _end_rules.accept(v);
        }
        v.endVisit(this);
    }
}


