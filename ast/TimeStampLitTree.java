package ast;
import visitor.*;

public class TimeStampLitTree extends AST{
 public TimeStampLitTree() {
 }

 public Object accept(ASTVisitor v) {
  return v.visitTimeStampLitTree(this);
 }
}
