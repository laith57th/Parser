package ast;
import visitor.*;

public class CaseStatementTree extends AST{
 public CaseStatementTree() {
 }

 public Object accept(ASTVisitor v) {
  return v.visitCaseStatementTree(this);
 }
}
