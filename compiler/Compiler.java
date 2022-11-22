package compiler;

import ast.*;
import parser.Parser;
import visitor.*;

import java.io.*;
import javax.imageio.ImageIO;


/**
 * The Compiler class contains the main program for compiling a source program
 * to bytecodes
 */
public class Compiler {

  /**
   * The Compiler class reads and compiles a source program
   */

  String sourceFile;

  public Compiler(String sourceFile) {
    this.sourceFile = sourceFile;
  }

  void compileProgram() {
    try {
      // System.out.println("---------------TOKENS-------------");
      Parser parser = new Parser(sourceFile);
      AST ast = parser.execute();

      //System.out.println(parser.getLex());

      System.out.println("---------------AST-------------");
      PrintVisitor printVisitor = new PrintVisitor();
      ast.accept(printVisitor);

      CountVisitor countVisitor = new CountVisitor();
      ast.accept(countVisitor);

      OffsetVisitor offsetVisitor = new OffsetVisitor();
      ast.accept(offsetVisitor);
      // System.out.println( ov );

      DrawOffsetVisitor drawVisitor = new DrawOffsetVisitor(countVisitor.getCount(), 
      offsetVisitor.getOffset(), offsetVisitor.getMaxOffset());
      ast.accept(drawVisitor);
      drawVisitor.layout();
      try {
        File imagefile = new File(sourceFile + ".png");
        ImageIO.write(drawVisitor.getImage(), "png", imagefile);
      } catch (Exception e) {
        System.out.println("Error in saving image: " + e.getMessage());
      }
    } catch (Exception e) {
      System.out.println("********exception*******" + e.toString());
    };
  }

  public static void main(String args[]) {
    if (args.length == 0) {
      System.out.println("***Incorrect usage, try: java compiler.Compiler <file>");
      System.exit(1);
    }
    (new Compiler(args[0])).compileProgram();
  }
}