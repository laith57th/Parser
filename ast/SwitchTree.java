package ast;
import visitor.*;

public class SwitchTree extends AST{
 public SwitchTree() {
 }

 public Object accept(ASTVisitor v) {
  return v.visitSwitchTree(this);
 }
}