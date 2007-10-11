package org.eclipse.imp.formatting.parser.Ast;

import lpg.runtime.*;

import org.eclipse.imp.parser.IParser;

/**
 *<b>
 *<li>Rule 2:  language ::= begin_language IDENTIFIER end_language
 *</b>
 */
public class language extends ASTNode implements Ilanguage
{
    private ASTNodeToken _begin_language;
    private ASTNodeToken _IDENTIFIER;
    private ASTNodeToken _end_language;

    public ASTNodeToken getbegin_language() { return _begin_language; }
    public ASTNodeToken getIDENTIFIER() { return _IDENTIFIER; }
    public ASTNodeToken getend_language() { return _end_language; }

    public language(IToken leftIToken, IToken rightIToken,
                    ASTNodeToken _begin_language,
                    ASTNodeToken _IDENTIFIER,
                    ASTNodeToken _end_language)
    {
        super(leftIToken, rightIToken);

        this._begin_language = _begin_language;
        ((ASTNode) _begin_language).setParent(this);
        this._IDENTIFIER = _IDENTIFIER;
        ((ASTNode) _IDENTIFIER).setParent(this);
        this._end_language = _end_language;
        ((ASTNode) _end_language).setParent(this);
        initialize();
    }

    /**
     * A list of all children of this node, including the null ones.
     */
    public java.util.ArrayList getAllChildren()
    {
        java.util.ArrayList list = new java.util.ArrayList();
        list.add(_begin_language);
        list.add(_IDENTIFIER);
        list.add(_end_language);
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
        if (! (o instanceof language)) return false;
        language other = (language) o;
        if (! _begin_language.equals(other._begin_language)) return false;
        if (! _IDENTIFIER.equals(other._IDENTIFIER)) return false;
        if (! _end_language.equals(other._end_language)) return false;
        return true;
    }

    public int hashCode()
    {
        int hash = 7;
        hash = hash * 31 + (_begin_language.hashCode());
        hash = hash * 31 + (_IDENTIFIER.hashCode());
        hash = hash * 31 + (_end_language.hashCode());
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
            _begin_language.accept(v);
            _IDENTIFIER.accept(v);
            _end_language.accept(v);
        }
        v.endVisit(this);
    }
}


