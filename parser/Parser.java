package parser;

import java.util.*;
import lexer.*;
import ast.*;

/**
 * The Parser class performs recursive-descent parsing; as a by-product it will
 * build the <b>Abstract Syntax Tree</b> representation for the source
 * program<br>
 * Following is the Grammar we are using:<br>
 * 
 * <pre>
 *  PROGRAM -> �program� BLOCK ==> program
 *
 *  BLOCK -> �{� D* S* �}�  ==> block
 *
 *  D -> TYPE NAME                    ==> decl
 *    -> TYPE NAME FUNHEAD BLOCK      ==> functionDecl
 *
 *  TYPE  ->  �int�
 *        ->  �boolean�
 *
 *  FUNHEAD  -> '(' (D list ',')? ')'  ==> formals<br>
 *
 *  S -> �if� E �then� BLOCK �else� BLOCK  ==> if
 *    -> �while� E BLOCK               ==> while
 *    -> �return� E                    ==> return
 *    -> BLOCK
 *    -> NAME �=� E                    ==> assign<br>
 *
 *  E -> SE
 *    -> SE �==� SE   ==> =
 *    -> SE �!=� SE   ==> !=
 *    -> SE �<�  SE   ==> <
 *    -> SE �<=� SE   ==> <=
 *
 *  SE  ->  T
 *      ->  SE �+� T  ==> +
 *      ->  SE �-� T  ==> -
 *      ->  SE �|� T  ==> or
 *
 *  T  -> F
 *     -> T �*� F  ==> *
 *     -> T �/� F  ==> /
 *     -> T �&� F  ==> and
 *
 *  F  -> �(� E �)�
 *     -> NAME
 *     -> <int>
 *     -> NAME '(' (E list ',')? ')' ==> call<br>
 *
 *  NAME  -> <id>
 * </pre>
 */
public class Parser {

  private Token currentToken;
  private Lexer lex;
  private EnumSet<Tokens> relationalOps = EnumSet.of(Tokens.Equal, Tokens.NotEqual, Tokens.Less, Tokens.LessEqual,
      Tokens.Greater, Tokens.GreaterEqual);
  private EnumSet<Tokens> addingOps = EnumSet.of(Tokens.Plus, Tokens.Minus, Tokens.Or);
  private EnumSet<Tokens> multiplyingOps = EnumSet.of(Tokens.Multiply, Tokens.Divide, Tokens.And);

  /**
   * Construct a new Parser;
   *
   * @param sourceProgram - source file name
   * @exception Exception - thrown for any problems at startup (e.g. I/O)
   */
  public Parser(String sourceProgram) throws Exception {
    try {
      lex = new Lexer(sourceProgram);
      scan();
    } catch (Exception e) {
      System.out.println("********exception*******" + e.toString());
      throw e;
    }
    ;
  }

  public Lexer getLex() {
    return lex;
  }

  /**
   * Execute the parse command
   *
   * @return the AST for the source program
   * @exception Exception - pass on any type of exception raised
   */
  public AST execute() throws Exception {
    try {
      return rProgram();
    } catch (SyntaxError e) {
      e.print();
      throw e;
    }
  }

  /**
   * <
   * pre>
   * Program -> 'program' block ==> program
   * </pre>
   *
   * @return the program tree
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rProgram() throws SyntaxError {
    // note that rProgram actually returns a ProgramTree; we use the
    // principle of substitutability to indicate it returns an AST
    AST t = new ProgramTree();
    expect(Tokens.Program);
    t.addKid(rBlock());
    return t;
  }

  /**
   * <
   * pre>
   * block -> '{' d* s* '}' ==> block
   * </pre>
   *
   * @return block tree
   * @exception SyntaxError - thrown for any syntax error e.g. an expected
   *                        left brace isn't found
   */
  public AST rBlock() throws SyntaxError {
    expect(Tokens.LeftBrace);
    AST t = new BlockTree();
    while (startingDecl()) { // get decls
      t.addKid(rDecl());
    }
    while (startingStatement()) { // get statements
      t.addKid(rStatement());
    }
    expect(Tokens.RightBrace);
    return t;
  }

  public AST rCaseBlock() throws SyntaxError {
    expect(Tokens.LeftBrace);
    AST t = new CaseBlockTree(), t1;
    while (isNextTok(Tokens.Case)) {
      t1 = new CaseStatementTree().addKid(rCaseList());
      expect(Tokens.HashDelimeter);
      t1.addKid(rStatement());
      t.addKid(t1);
    }
    if (isNextTok(Tokens.Default)) {
      expect(Tokens.Default);
      t1 = new DefaultTree();
      expect(Tokens.HashDelimeter);
      t1.addKid(rStatement());
      t.addKid(t1);
    }
    return t;
  }

  boolean startingDecl() {
    if (isNextTok(Tokens.Int) || isNextTok(Tokens.BOOLean)
        || isNextTok(Tokens.Utf16String) || isNextTok(Tokens.TimestampType)) {
      return true;
    }
    return false;
  }

  boolean startingStatement() {
    if (isNextTok(Tokens.If) || isNextTok(Tokens.While) || isNextTok(Tokens.Return)
        || isNextTok(Tokens.LeftBrace) || isNextTok(Tokens.Identifier) || isNextTok(Tokens.Switch)) {
      return true;
    }
    return false;
  }

  /**
   * <
   * pre>
   * d -> type name ==> decl -> type name funcHead block ==> functionDecl
   * </pre>
   *
   * @return either the decl tree or the functionDecl tree
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rDecl() throws SyntaxError {
    AST t, t1;
    t = rType();
    t1 = rName();
    if (isNextTok(Tokens.LeftParen)) { // function
      t = (new FunctionDeclTree()).addKid(t).addKid(t1);
      t.addKid(rFunHead());
      t.addKid(rBlock());
      return t;
    }
    t = (new DeclTree()).addKid(t).addKid(t1);
    return t;
  }

  /**
   * <
   * pre>
   * type -> 'int' type -> 'bool'
   * </pre>
   *
   * @return either the intType or boolType tree
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rType() throws SyntaxError {
    AST t;
    if (isNextTok(Tokens.Int)) {
      t = new IntTypeTree();
      scan();
    } else if (isNextTok(Tokens.BOOLean)) {
      expect(Tokens.BOOLean);
      t = new BoolTypeTree();
    } else if (isNextTok(Tokens.Utf16String)) {
      expect(Tokens.Utf16String);
      t = new Utf16StringTree();
    } else {
      expect(Tokens.TimestampType);
      t = new TimeStampTypeTree();
    }
    return t;
  }

  /**
   * <
   * pre>
   * funHead -> '(' (decl list ',')? ')' ==> formals note a funhead is a list
   * of zero or more decl's separated by commas, all in parens
   * </pre>
   *
   * @return the formals tree describing this list of formals
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rFunHead() throws SyntaxError {
    AST t = new FormalsTree();
    expect(Tokens.LeftParen);
    if (!isNextTok(Tokens.RightParen)) {
      do {
        t.addKid(rDecl());
        if (isNextTok(Tokens.Comma)) {
          scan();
        } else {
          break;
        }
      } while (true);
    }
    expect(Tokens.RightParen);
    return t;
  }

  /**
   * <
   * pre>
   * S -> 'if' e 'then' block 'else' block ==> if -> 'while' e block ==> while
   * -> 'return' e ==> return -> block -> name '=' e ==> assign
   * </pre>
   *
   * @return the tree corresponding to the statement found
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rStatement() throws SyntaxError {
    AST t;
    if (isNextTok(Tokens.If)) {
      scan();
      t = new IfTree();
      t.addKid(rExpr());
      expect(Tokens.Then);
      t.addKid(rBlock());
      if (isNextTok(Tokens.Else)) {
        expect(Tokens.Else);
        t.addKid(rBlock());
      }
      return t;
    }
    if (isNextTok(Tokens.While)) {
      scan();
      t = new WhileTree();
      t.addKid(rExpr());
      t.addKid(rBlock());
      return t;
    }
    if (isNextTok(Tokens.Switch)) {
      scan();
      t = new SwitchTree();
      t.addKid(rId());
      t.addKid(rCaseBlock());
      return t;
    }
    if (isNextTok(Tokens.Return)) {
      scan();
      t = new ReturnTree();
      t.addKid(rExpr());
      return t;
    }
    if (isNextTok(Tokens.LeftBrace)) {
      return rBlock();
    }
    t = rName();
    t = (new AssignTree()).addKid(t);
    expect(Tokens.Assign);
    t.addKid(rExpr());
    return t;
  }

  /**
   * <
   * pre>
   * e -> se -> se '==' se ==> = -> se '!=' se ==> != -> se '<' se ==> < -> se
   * '<=' se ==> <=
   * </pre>
   * 
   * @return the tree corresponding to the expression
   *
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rExpr() throws SyntaxError {
    AST t, kid = rSimpleExpr();
    t = getRelationTree();
    if (t == null) {
      return kid;
    }
    t.addKid(kid);
    t.addKid(rSimpleExpr());
    return t;
  }

  public AST rId() throws SyntaxError {
    AST t;
    if (isNextTok(Tokens.LeftParen)) {
      scan();
      if (currentToken.getKind() == Tokens.Identifier) {
        t = new IdTree(currentToken);
        scan();
      } else {
        throw new SyntaxError(currentToken, Tokens.Identifier);
      }
      expect(Tokens.RightParen);
      return t;
    }
    throw new SyntaxError(currentToken, Tokens.LeftParen);
  }

  /**
   * <
   * pre>
   * se -> t -> se '+' t ==> + -> se '-' t ==> - -> se '|' t ==> or This rule
   * indicates we should pick up as many <i>t</i>'s as possible; the
   * <i>t</i>'s will be left associative
   * </pre>
   *
   * @return the tree corresponding to the adding expression
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rSimpleExpr() throws SyntaxError {
    AST t, kid = rTerm();
    while ((t = getAddOperTree()) != null) {
      t.addKid(kid);
      t.addKid(rTerm());
      kid = t;
    }
    return kid;
  }

  /**
   * <
   * pre>
   * t -> f -> t '*' f ==> * -> t '/' f ==> / -> t '&' f ==> and This rule
   * indicates we should pick up as many <i>f</i>'s as possible; the
   * <i>f</i>'s will be left associative
   * </pre>
   *
   * @return the tree corresponding to the multiplying expression
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rTerm() throws SyntaxError {
    AST t, kid = rFactor();
    while ((t = getMultOperTree()) != null) {
      t.addKid(kid);
      t.addKid(rFactor());
      kid = t;
    }
    return kid;
  }

  /**
   * <
   * pre>
   * f -> '(' e ')' -> name -> <int>
   * -> name '(' (e list ',')? ')' ==> call
   * </pre>
   *
   * @return the tree corresponding to the factor expression
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rFactor() throws SyntaxError {
    AST t;
    if (isNextTok(Tokens.LeftParen)) { // -> (e)
      scan();
      t = rExpr();
      expect(Tokens.RightParen);
      return t;
    }
    if (isNextTok(Tokens.INTeger)) { // -> <int>
      t = new IntTree(currentToken);
      scan();
      return t;
    }
    if (isNextTok(Tokens.Utf16StringLit)) {
      t = new Utf16StringLitTree();
      scan();
      return t;
    }
    if (isNextTok(Tokens.TimestampLit)) {
      t = new TimeStampLitTree();
      scan();
      return t;
    }
    t = rName();
    if (!isNextTok(Tokens.LeftParen)) { // -> name
      return t;
    }
    scan(); // -> name '(' (e list ',')? ) ==> call
    t = (new CallTree()).addKid(t);
    if (!isNextTok(Tokens.RightParen)) {
      do {
        t.addKid(rExpr());
        if (isNextTok(Tokens.Comma)) {
          scan();
        } else {
          break;
        }
      } while (true);
    }
    expect(Tokens.RightParen);
    return t;
  }

  public AST rCaseList() throws SyntaxError {
    AST t = rCaseName();
    if (!isNextTok(Tokens.LeftBracket)) {
      return t;
    }
    scan();
    if (!isNextTok(Tokens.RightBracket)) {
      do {
        t.addKid(rExpr());
        if (isNextTok(Tokens.Comma)) {
          scan();
        } else {
          break;
        }
      } while (true);
    }
    expect(Tokens.RightBracket);
    return t;
  }

  /**
   * <
   * pre>
   * name -> <id>
   * </pre>
   *
   * @return the id tree
   * @exception SyntaxError - thrown for any syntax error
   */
  public AST rName() throws SyntaxError {
    AST t;
    if (isNextTok(Tokens.Identifier)) {
      t = new IdTree(currentToken);
      scan();
      return t;
    }
    throw new SyntaxError(currentToken, Tokens.Identifier);
  }

  public AST rCaseName() throws SyntaxError {
    AST t;
    if (isNextTok(Tokens.Case)) {
      t = new CaseListTree();
      scan();
      return t;
    }
    throw new SyntaxError(currentToken, Tokens.Case);
  }

  AST getRelationTree() { // build tree with current token's relation
    Tokens kind = currentToken.getKind();
    if (relationalOps.contains(kind)) {
      AST t = new RelOpTree(currentToken);
      scan();
      return t;
    } else {
      return null;
    }
  }

  AST getIdTree() {
    Tokens kind = currentToken.getKind();
    if (kind == Tokens.Identifier) {
      AST t = new IdTree(currentToken);
      scan();
      return t;
    } else {
      return null;
    }
  }

  private AST getAddOperTree() {
    Tokens kind = currentToken.getKind();
    if (addingOps.contains(kind)) {
      AST t = new AddOpTree(currentToken);
      scan();
      return t;
    } else {
      return null;
    }
  }

  private AST getMultOperTree() {
    Tokens kind = currentToken.getKind();
    if (multiplyingOps.contains(kind)) {
      AST t = new MultOpTree(currentToken);
      scan();
      return t;
    } else {
      return null;
    }
  }

  private boolean isNextTok(Tokens kind) {
    if ((currentToken == null) || (currentToken.getKind() != kind)) {
      return false;
    }
    return true;
  }

  private void expect(Tokens kind) throws SyntaxError {
    if (isNextTok(kind)) {
      scan();
      return;
    }
    System.out.println(currentToken);
    throw new SyntaxError(currentToken, kind);
  }

  private void scan() {
    currentToken = lex.nextToken();
    /*
     * if (currentToken != null) {
     * currentToken.print(); // debug printout
     * }
     */
    return;
  }
}

class SyntaxError extends Exception {

  /**
  *
  */
  private static final long serialVersionUID = 1L;
  /**
  *
  */
  private Token tokenFound;
  private Tokens kindExpected;

  /**
   * record the syntax error just encountered
   *
   * @param tokenFound   is the token just found by the parser
   * @param kindExpected is the token we expected to find based on the current
   *                     context
   */
  public SyntaxError(Token tokenFound, Tokens kindExpected) {
    this.tokenFound = tokenFound;
    this.kindExpected = kindExpected;
  }

  void print() {
    System.out.println("Expected: "
        + kindExpected);
    return;
  }
}
