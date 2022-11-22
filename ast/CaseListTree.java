package ast;

import visitor.*;

public class CaseListTree extends AST{
 public CaseListTree() {
 }

 public Object accept(ASTVisitor v) {
  return v.visitCaseTree(this);
 }
}
