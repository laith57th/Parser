package visitor;

import ast.AST;

/**
 *
 * @author Lowell Milliken
 */
public class CountVisitor extends ASTVisitor {

  private int[] nCount = new int[100];
  private int depth = 0;
  private int maxDepth = 0;

  private void count(AST t) {
    nCount[depth]++;

    if (depth > maxDepth) {
      maxDepth = depth;
    }

    depth++;
    visitKids(t);
    depth--;
  }

  public int[] getCount() {
    int[] count = new int[maxDepth + 1];

    for (int i = 0; i <= maxDepth; i++) {
      count[i] = nCount[i];
    }

    return count;
  }

  public void printCount() {
    for (int i = 0; i <= maxDepth; i++) {
      System.out.println("Depth: " + i + " Nodes: " + nCount[i]);
    }
  }

  public Object visitProgramTree(AST t) {
    count(t);
    return null;
  }

  public Object visitBlockTree(AST t) {
    count(t);
    return null;
  }

  public Object visitFunctionDeclTree(AST t) {
    count(t);
    return null;
  }

  public Object visitCallTree(AST t) {
    count(t);
    return null;
  }

  public Object visitDeclTree(AST t) {
    count(t);
    return null;
  }

  public Object visitIntTypeTree(AST t) {
    count(t);
    return null;
  }

  public Object visitNumberTypeTree(AST t) {
    count(t);
    return null;
  }

  public Object visitScientificTypeTree(AST t) {
    count(t);
    return null;
  }

  public Object visitFloatTypeTree(AST t) {
    count(t);
    return null;
  }

  public Object visitVoidTypeTree(AST t) {
    count(t);
    return null;
  }

  public Object visitBoolTypeTree(AST t) {
    count(t);
    return null;
  }

  public Object visitFormalsTree(AST t) {
    count(t);
    return null;
  }

  public Object visitActualArgsTree(AST t) {
    count(t);
    return null;
  }

  public Object visitIfTree(AST t) {
    count(t);
    return null;
  }

  public Object visitWhileTree(AST t) {
    count(t);
    return null;
  }

  public Object visitForTree(AST t) {
    count(t);
    return null;
  }

  public Object visitReturnTree(AST t) {
    count(t);
    return null;
  }

  public Object visitAssignTree(AST t) {
    count(t);
    return null;
  }

  public Object visitIntTree(AST t) {
    count(t);
    return null;
  }

  public Object visitNumberTree(AST t) {
    count(t);
    return null;
  }

  public Object visitScientificTree(AST t) {
    count(t);
    return null;
  }

  public Object visitFloatTree(AST t) {
    count(t);
    return null;
  }

  public Object visitVoidTree(AST t) {
    count(t);
    return null;
  }

  public Object visitIdTree(AST t) {
    count(t);
    return null;
  }

  public Object visitRelOpTree(AST t) {
    count(t);
    return null;
  }

  public Object visitAddOpTree(AST t) {
    count(t);
    return null;
  }

  public Object visitMultOpTree(AST t) {
    count(t);
    return null;
  }

  // new methods here
  public Object visitStringTypeTree(AST t) {
    count(t);
    return null;
  }

  public Object visitCharTypeTree(AST t) {
    count(t);
    return null;
  }

  public Object visitStringTree(AST t) {
    count(t);
    return null;
  }

  public Object visitCharTree(AST t) {
    count(t);
    return null;
  }

  public Object visitUnlessTree(AST t) {
    count(t);
    return null;
  }

  public Object visitSwitchTree(AST t) {
    count(t);
    return null;
  }

  public Object visitCaseBlockTree(AST t) {
    count(t);
    return null;
  }

  public Object visitCaseTree(AST t) {
    count(t);
    return null;
  }

  public Object visitCaseStatementTree(AST t) {
    count(t);
    return null;
}

  public Object visitDefaultTree(AST t) {
    count(t);
    return null;
  }

  public Object visitTimeStampLitTree(AST t) {
    count(t);
    return null;
  }

  public Object visitTimeStampTypeTree(AST t) {
    count(t);
    return null;
  }

  public Object visitUtf16StringTree(AST t) {
    count(t);
    return null;
  }

  public Object visitUtf16StringLitTree(AST t) {
    count(t);
    return null;
  }
}