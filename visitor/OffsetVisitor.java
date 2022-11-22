package visitor;

import java.util.HashMap;

import ast.AST;

public class OffsetVisitor extends ASTVisitor {
  private int [] offsetValue = new int[ 100 ];

  private HashMap<AST, Integer> offsetValues = new HashMap<>();
  private int depth = 0;
  private int maxDepth = 0;

  private void offset(AST t) {
    if (depth > maxDepth) {
      maxDepth = depth;
    }
    offsetValues.put(t, offsetValue[depth]);
    offsetValue[depth] += 2;
    for (AST kid : t.getKids()) {
      depth++;
      offset(kid);
      depth--;
    }

    if (t.kidCount() > 0) {
      AST rightMostChild = t.getKid(t.kidCount());
      AST leftMostChild = t.getKid(1);

      int calculatedOffset = (offsetValues.get(rightMostChild) + offsetValues.get(leftMostChild)) / 2;
      if (offsetValues.get(t) < offsetValue[depth]) {
        if (calculatedOffset > offsetValues.get(t)) {
          offsetValues.put(t, calculatedOffset);
          offsetValue[depth] = offsetValues.get(t) + 2;
        } else {
          int offsetDifference = offsetValues.get(t) - calculatedOffset;
          shiftNodes(t, offsetDifference);
        }
      }
    }
  }

  private void shiftNodes(AST t, int offsetCount) {
    if (t.kidCount() == 0)
      return;
    if (offsetValues.get(t) != null) {
      for (AST kid : t.getKids()) {
        offsetValues.put(kid, offsetValues.get(kid) + offsetCount);
        offsetValue[depth + 1] = offsetValues.get(t.getKid(t.kidCount())) + 2;
        depth++;
        shiftNodes(kid, offsetCount);
        depth--;
      }
    }
  }
  public int getMaxOffset(){
      int max = 0;
      for(int i = 0; i < offsetValue.length; i++){
          if(offsetValue[i] > max){
              max = (offsetValue[i]);
          }
      }
      return max;
  }

  public HashMap<AST, Integer> getOffset() {
      HashMap<AST, Integer> offset = offsetValues;
      return offset;
  }

 public Object visitProgramTree(AST t) {
  offset(t);
  return null;
 }

 public Object visitBlockTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitFunctionDeclTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitCallTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitDeclTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitIntTypeTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitNumberTypeTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitScientificTypeTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitFloatTypeTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitVoidTypeTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitBoolTypeTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitFormalsTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitActualArgsTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitIfTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitWhileTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitForTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitReturnTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitAssignTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitIntTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitNumberTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitScientificTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitFloatTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitVoidTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitIdTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitRelOpTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitAddOpTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitMultOpTree(AST t) {
   offset(t);
   return null;
 }

 // new methods here
 public Object visitStringTypeTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitCharTypeTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitStringTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitCharTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitUnlessTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitSwitchTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitCaseBlockTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitCaseStatementTree(AST t) {
  offset(t);
  return null;
}

 public Object visitCaseTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitDefaultTree(AST t) {
   offset(t);
   return null;
 }

 public Object visitTimeStampLitTree(AST t) {
    offset(t);
    return null;
  }

  public Object visitTimeStampTypeTree(AST t) {
    offset(t);
    return null;
  }

  public Object visitUtf16StringTree(AST t) {
    offset(t);
    return null;
  }

  public Object visitUtf16StringLitTree(AST t) {
    offset(t);
    return null;
  }
}
