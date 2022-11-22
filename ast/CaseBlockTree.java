package ast;
import visitor.*;

public class CaseBlockTree extends AST{
 public CaseBlockTree() {
 }

 public Object accept(ASTVisitor v) {
  return v.visitCaseBlockTree(this);
 }
}
