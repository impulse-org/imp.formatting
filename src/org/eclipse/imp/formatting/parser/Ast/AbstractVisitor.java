package org.eclipse.imp.formatting.parser.Ast;

public abstract class AbstractVisitor implements Visitor
{
    public abstract void unimplementedVisitor(String s);

    public boolean preVisit(ASTNode element) { return true; }

    public void postVisit(ASTNode element) {}

    public boolean visit(ASTNodeToken n) { unimplementedVisitor("visit(ASTNodeToken)"); return true; }
    public void endVisit(ASTNodeToken n) { unimplementedVisitor("endVisit(ASTNodeToken)"); }

    public boolean visit(specification n) { unimplementedVisitor("visit(specification)"); return true; }
    public void endVisit(specification n) { unimplementedVisitor("endVisit(specification)"); }

    public boolean visit(language n) { unimplementedVisitor("visit(language)"); return true; }
    public void endVisit(language n) { unimplementedVisitor("endVisit(language)"); }

    public boolean visit(rules n) { unimplementedVisitor("visit(rules)"); return true; }
    public void endVisit(rules n) { unimplementedVisitor("endVisit(rules)"); }

    public boolean visit(ruleList n) { unimplementedVisitor("visit(ruleList)"); return true; }
    public void endVisit(ruleList n) { unimplementedVisitor("endVisit(ruleList)"); }

    public boolean visit(rule n) { unimplementedVisitor("visit(rule)"); return true; }
    public void endVisit(rule n) { unimplementedVisitor("endVisit(rule)"); }

    public boolean visit(box n) { unimplementedVisitor("visit(box)"); return true; }
    public void endVisit(box n) { unimplementedVisitor("endVisit(box)"); }

    public boolean visit(example n) { unimplementedVisitor("visit(example)"); return true; }
    public void endVisit(example n) { unimplementedVisitor("endVisit(example)"); }


    public boolean visit(ASTNode n)
    {
        if (n instanceof ASTNodeToken) return visit((ASTNodeToken) n);
        else if (n instanceof specification) return visit((specification) n);
        else if (n instanceof language) return visit((language) n);
        else if (n instanceof rules) return visit((rules) n);
        else if (n instanceof ruleList) return visit((ruleList) n);
        else if (n instanceof rule) return visit((rule) n);
        else if (n instanceof box) return visit((box) n);
        else if (n instanceof example) return visit((example) n);
        throw new UnsupportedOperationException("visit(ASTNode)");
    }
    public void endVisit(ASTNode n)
    {
        if (n instanceof ASTNodeToken) endVisit((ASTNodeToken) n);
        else if (n instanceof specification) endVisit((specification) n);
        else if (n instanceof language) endVisit((language) n);
        else if (n instanceof rules) endVisit((rules) n);
        else if (n instanceof ruleList) endVisit((ruleList) n);
        else if (n instanceof rule) endVisit((rule) n);
        else if (n instanceof box) endVisit((box) n);
        else if (n instanceof example) endVisit((example) n);
        throw new UnsupportedOperationException("visit(ASTNode)");
    }
}

