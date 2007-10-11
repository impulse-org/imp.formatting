package org.eclipse.imp.formatting.parser.Ast;

public interface Visitor
{
    boolean preVisit(ASTNode element);

    void postVisit(ASTNode element);

    boolean visit(ASTNodeToken n);
    void endVisit(ASTNodeToken n);

    boolean visit(specification n);
    void endVisit(specification n);

    boolean visit(language n);
    void endVisit(language n);

    boolean visit(rules n);
    void endVisit(rules n);

    boolean visit(ruleList n);
    void endVisit(ruleList n);

    boolean visit(rule n);
    void endVisit(rule n);

    boolean visit(box n);
    void endVisit(box n);

    boolean visit(example n);
    void endVisit(example n);

    boolean visit(ASTNode n);
    void endVisit(ASTNode n);
}


