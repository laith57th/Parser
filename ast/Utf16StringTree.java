package ast;
import visitor.*;

public class Utf16StringTree extends AST{
 public Utf16StringTree() {
 }

 public Object accept(ASTVisitor v) {
  return v.visitUtf16StringTree(this);
 }
}
