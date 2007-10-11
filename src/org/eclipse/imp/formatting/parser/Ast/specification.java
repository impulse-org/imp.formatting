package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 *<b>
 *<li>Rule 1:  specification ::= begin_formatter language rules example end_formatter
 *</b>
 */
public class specification extends ASTNode implements Ispecification
{
    private ASTNodeToken _begin_formatter;
    private language _language;
    private rules _rules;
    private example _example;
    private ASTNodeToken _end_formatter;

    public ASTNodeToken getbegin_formatter() { return _begin_formatter; }
    public language getlanguage() { return _language; }
    public rules getrules() { return _rules; }
    public example getexample() { return _example; }
    public ASTNodeToken getend_formatter() { return _end_formatter; }

    public specification(IToken leftIToken, IToken rightIToken,
                         ASTNodeToken _begin_formatter,
                         language _language,
                         rules _rules,
                         example _example,
                         ASTNodeToken _end_formatter)
    {
        super(leftIToken, rightIToken);

        this._begin_formatter = _begin_formatter;
        ((ASTNode) _begin_formatter).setParent(this);
        this._language = _language;
        ((ASTNode) _language).setParent(this);
        this._rules = _rules;
        ((ASTNode) _rules).setParent(this);
        this._example = _example;
        ((ASTNode) _example).setParent(this);
        this._end_formatter = _end_formatter;
        ((ASTNode) _end_formatter).setParent(this);
        initialize();
    }

    /**
     * A list of all children of this node, including the null ones.
     */
    public java.util.ArrayList getAllChildren()
    {
        java.util.ArrayList list = new java.util.ArrayList();
        list.add(_begin_formatter);
        list.add(_language);
        list.add(_rules);
        list.add(_example);
        list.add(_end_formatter);
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
        if (! (o instanceof specification)) return false;
        specification other = (specification) o;
        if (! _begin_formatter.equals(other._begin_formatter)) return false;
        if (! _language.equals(other._language)) return false;
        if (! _rules.equals(other._rules)) return false;
        if (! _example.equals(other._example)) return false;
        if (! _end_formatter.equals(other._end_formatter)) return false;
        return true;
    }

    public int hashCode()
    {
        int hash = 7;
        hash = hash * 31 + (_begin_formatter.hashCode());
        hash = hash * 31 + (_language.hashCode());
        hash = hash * 31 + (_rules.hashCode());
        hash = hash * 31 + (_example.hashCode());
        hash = hash * 31 + (_end_formatter.hashCode());
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
            _begin_formatter.accept(v);
            _language.accept(v);
            _rules.accept(v);
            _example.accept(v);
            _end_formatter.accept(v);
        }
        v.endVisit(this);
    }
}


