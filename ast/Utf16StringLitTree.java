package ast;
import visitor.*;

public class Utf16StringLitTree extends AST{
 public Utf16StringLitTree() {
 }

 public Object accept(ASTVisitor v) {
  return v.visitUtf16StringLitTree(this);
 }
}
