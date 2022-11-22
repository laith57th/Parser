package lexer;

/**
 *  The Lexer class is responsible for scanning the source file
 *  which is a stream of characters and returning a stream of
 *  tokens; each token object will contain the string (or access
 *  to the string) that describes the token along with an
 *  indication of its location in the source program to be used
 *  for error reporting; we are tracking line numbers; white spaces
 *  are space, tab, newlines
 */
public class Lexer {
  private boolean atEOF = false;
  // next character to process
  private char ch;
  private SourceReader source;

  // positions in line of current token
  private int startPosition, endPosition;
  private int lineNumber;
  /**
   *  Lexer constructor
   * @param sourceFile is the name of the File to read the program source from
   */
  public Lexer( String sourceFile ) throws Exception {
    // init token table
    new TokenType();
    source = new SourceReader( sourceFile );
    ch = source.read();
  }

  /**
   *  newIdTokens are either ids or reserved words; new id's will be inserted
   *  in the symbol table with an indication that they are id's
   *  @param id is the String just scanned - it's either an id or reserved word
   *  @param startPosition is the column in the source file where the token begins
   *  @param endPosition is the column in the source file where the token ends
   *  @return the Token; either an id or one for the reserved words
   */

   String name = "";
  public Token newIdToken( String id, int startPosition, int endPosition) {
    name = "Int";
    return new Token(
      lineNumber,
      startPosition,
      endPosition,
      Symbol.symbol( id, Tokens.Identifier )
    );
  }

  public int getLineNum(){
    return lineNumber;
  }

  

  /**
   *  number tokens are inserted in the symbol table; we don't convert the
   *  numeric strings to numbers until we load the bytecodes for interpreting;
   *  this ensures that any machine numeric dependencies are deferred
   *  until we actually run the program; i.e. the numeric constraints of the
   *  hardware used to compile the source program are not used
   *  @param number is the int String just scanned
   *  @param startPosition is the column in the source file where the int begins
   *  @param endPosition is the column in the source file where the int ends
   *  @return the int Token
   */
  public Token newNumberToken( String number, int startPosition, int endPosition) {
    name = "Int";
    return new Token(
      lineNumber,
      startPosition,
      endPosition,
      Symbol.symbol( number, Tokens.INTeger )
    );
  }

  public Token newUtf16StringToken(String utf, int startPosition, int endPosition){
    name = "Utf16";
    return new Token(
      lineNumber,
      startPosition,
      endPosition,
      Symbol.symbol(utf, Tokens.Utf16StringLit)
    );
  }

  public Token newtimeStampToken(String time, int startPosition, int endPosition){
    name = "time";
    return new Token(
      lineNumber,
      startPosition,
      endPosition,
      Symbol.symbol(time, Tokens.TimestampLit)
    );
  }

  /**
   *  build the token for operators (+ -) or separators (parens, braces)
   *  filter out comments which begin with two slashes
   *  @param s is the String representing the token
   *  @param startPosition is the column in the source file where the token begins
   *  @param endPosition is the column in the source file where the token ends
   *  @return the Token just found
   */
  public Token makeToken( String s, int startPosition, int endPosition ) {
    // filter comments
    if( s.equals("//") ) {
      try {
        int oldLine = source.getLineno();

        do {
          ch = source.read();
        } while( oldLine == source.getLineno() );
      } catch (Exception e) {
        atEOF = true;
      }

      return nextToken();
    }

    // ensure it's a valid token
    Symbol sym = Symbol.symbol( s, Tokens.BogusToken );

    if( sym == null ) {
      System.out.println( "******** illegal character: " + s );
      atEOF = true;
      return nextToken();
    }

    return new Token( lineNumber, startPosition, endPosition, sym );
  }

  public Token illegalch(String tok){
    System.out.println("*******illegal character: " + tok);
    atEOF = true;
    return nextToken();
  }

  /**
   *  @return the next Token found in the source file
   */

  public void timeCalc(String tok, int index, int from, int to, char sym){
    if(ch != sym){
      illegalch(tok);
    }
    String t = tok.substring(index);
    int p = Integer.parseInt(t);
    if(p < from || p > to){
      illegalch(tok);
    }
  }
  public Token nextToken() {
    // ch is always the next char to process
    int utfLength = 12;
    int timeLength = 19;
    if( atEOF ) {
      if( source != null ) {
        source.close();
        source = null;
      }

      return null;
    }

    try {
      // scan past whitespace
      while( Character.isWhitespace( ch )) {
        ch = source.read();
      }
    } catch( Exception e ) {
      atEOF = true;
      return nextToken();
    }
    lineNumber = source.getLineno();
    startPosition = source.getPosition();
    endPosition = startPosition - 1;
    try{
      if(ch == '\\'){
        String utf = "" + ch + source.read();
        ch = utf.charAt(1);
        if(utf.equals("\\u")){
          while(ch != '\\'){
            ch = source.read();
            if((ch <= 70 && ch >= 65) || (ch >= 97 && ch <= 102) || (ch >= 48 && ch <= 57)){
              utf += ch;
              endPosition++;
              if(utf.length() > 6){
                illegalch(utf);
              }
            } else if(ch == '\\' && utf.length() == 6){
              utf += ch;
              ch = source.read();
              endPosition++;
              if(ch == 'u'){
                utf += ch;
              } else{
                utf += ch;
                illegalch(utf);
              }
              while(utf.length() <= utfLength){
                ch = source.read();
                if((ch <= 70 && ch >= 65) || (ch >= 97 && ch <= 102) || (ch >= 48 && ch <= 57)){
                  utf += ch;
                  endPosition++;
                } else if(utf.length() != utfLength){
                  utf += ch;
                  illegalch(utf);
                } else {
                  return newUtf16StringToken(utf, startPosition, endPosition);
                }
              }
            } else {
              utf += ch;
              illegalch(utf);
            }
          }
        }
      }
    }catch(Exception e){}
    

    if( Character.isJavaIdentifierStart( ch )) {
      // return tokens for ids and reserved words
      String id = "";

      try {
        do {
          endPosition++;
          id += ch;
          ch = source.read();
        } while( Character.isJavaIdentifierPart( ch ));
      } catch( Exception e ) {
        atEOF = true;
      }
      return newIdToken(id, startPosition, endPosition);
    }

    if( Character.isDigit( ch )) {
      // return number tokens
      String number = "";
      String t = "";
      int p = 0;

      try {
        do {
          endPosition++;
          number += ch;
          ch = source.read();
          if(number.length() == 4 && ch == '~'){  //year
            int intValue = Integer.parseInt(number);
            if(intValue <= 0){
              illegalch(number);
            }
            do{
              endPosition++;
              number += ch;
              ch = source.read();
              switch(number.length()){
                case 7: //month
                  timeCalc(number, 5, 1, 12, '~');
                  break;
                case 10:  //day
                  timeCalc(number, 8, 1, 31, '~');
                  break;
                case 13:  //hour
                  timeCalc(number, 11, 0, 23, ':');
                    break;
                case 16:  //minute
                  timeCalc(number, 14, 0, 60, ':');
                    break;
                case 19: //second
                    t = number.substring(17);
                    p = Integer.parseInt(t);
                    if(p < 0 || p > 59){
                      illegalch(number);
                    }
                    break;
              }
            }while(Character.isDigit(ch) || ch == '~' || ch == ':');
            if(number.length() != timeLength){
              illegalch(number);
            }
            return newtimeStampToken(number, startPosition, endPosition);
          }
        } while( Character.isDigit( ch ));
      } catch( Exception e ) {
        atEOF = true;
      }
      return newNumberToken( number, startPosition, endPosition );
    }



    // At this point the only tokens to check for are one or two
    // characters; we must also check for comments that begin with
    // 2 slashes
    String charOld = "" + ch;
    String op = charOld;
    Symbol sym;
    try {
      endPosition++;
      ch = source.read();
      op += ch;

      // check if valid 2 char operator; if it's not in the symbol
      // table then don't insert it since we really have a one char
      // token
      sym = Symbol.symbol( op, Tokens.BogusToken );
      if (sym == null) {
        // it must be a one char token
        return makeToken( charOld, startPosition, endPosition );
      }

      endPosition++;
      ch = source.read();

      return makeToken( op, startPosition, endPosition );
    } catch( Exception e ) { /* no-op */ }
    
    atEOF = true;
    if( startPosition == endPosition ) {
      op = charOld;
    }

    return makeToken( op, startPosition, endPosition );
  }
  /*@Override
  public String toString() {
    Token token;
    String lexicalAnalysis = "";
    String rawCode = "";
    int tokenEndCheck = source.getLineno();
      try {
        while( !atEOF ) {
          token = nextToken();
          if(atEOF){
            return rawCode;
          }
          lexicalAnalysis = String.format("%-15s %-15s %-15s %-15s %-15s",
          token, "Left: " + token.getLeftPosition(),
          "Right: " + token.getRightPosition(), "Line: " +
          getLineNum(), String.valueOf(token.getKind()));
          System.out.println(lexicalAnalysis);
          if(tokenEndCheck == source.getLineno()){
            rawCode += tokenEndCheck + ": " + source.getNextLine() + "\n";
            tokenEndCheck++;
          }
          
        }
      } catch (Exception e) {
      }
      return rawCode;
  }*/
  /*public static void main(String args[]) {
    for ( String arg : args ) {
      try {
        Lexer lex = new Lexer( arg );
        System.out.println(lex);
      } catch (Exception e) {}
    }
  }*/
}
