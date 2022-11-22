package ast;
import visitor.*;

public class TimeStampTypeTree extends AST{
 public TimeStampTypeTree() {
  
 }

 public Object accept(ASTVisitor v) {
  return v.visitTimeStampTypeTree(this);
 }
 
}
