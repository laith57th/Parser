package ast;
import visitor.*;

public class DefaultTree extends AST{
 public DefaultTree() {
 }

 public Object accept(ASTVisitor v) {
  return v.visitDefaultTree(this);
 }
}
