package ProgLang.Maker;

import java.util.ArrayList;
import java.util.List;

public class Parser {
  private List<Token> tokens = new ArrayList<>();
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
    System.out.println("Tokens have been loaded");
  }

  public void parse() {
    
    try {
      System.out.println("Starting to Parse.");
      expression();
      System.out.println("Parse Successful.");
    } catch (ExpressionException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  private void expression() throws ExpressionException {
    while (!tokens.isEmpty()) {
      switch (peek().type) { // Assume while start for this program.
        case TokenType.WHILE -> whileExpr();
        case TokenType.EOF -> {
          return;
        }
        default -> complain("EOF not reached.");
      }
    }
  }

  private void whileExpr() throws ExpressionException {
    match(TokenType.WHILE);
    whileCondition();
    boolean bracePresentFlag = match(TokenType.LEFTBRACE);
    statement();
    if (bracePresentFlag) {
      whileStatement();
    }
  }

  private void whileCondition() throws ExpressionException {
    boolean ParenFlag = false;
    if (match(TokenType.LEFTPAREN)) {
      ParenFlag = true;
    }
    if (check(TokenType.LEFTPAREN))
      whileCondition();
    if (!booleanLiteral() && !check(TokenType.RIGHTPAREN) && !check(TokenType.BINARYAND) && !check(TokenType.BINARYOR))
      comparison();
    if (match(TokenType.BINARYAND, TokenType.BINARYOR))
      whileCondition();
    if (ParenFlag) {
      if (match(TokenType.RIGHTPAREN))
        return;
      else if (match(TokenType.BINARYAND, TokenType.BINARYOR))
        comparison();
      else
        complain("Right Parenthesis");
    }
  }

  private void whileStatement() throws ExpressionException {
    while (!match(TokenType.RIGHTBRACE)) {
      {
        int lastConsume = current;
        if (TokenType.EOF == peek().type) {
          complain("Right curly brace");
        }
        if (check(TokenType.WHILE)) 
          whileExpr();
        else
          statement();
        if(match(TokenType.LEFTBRACE)){
          whileStatement();
        }
        if(lastConsume == current){
          complain("Statement or While");
        }
      }
    }
  }

  private boolean statement() throws ExpressionException {
    if (control() || initialization() || assignment()) {
    } else if (function())
      unary();
    else
      return false;

    if (match(TokenType.SEMICOLON)) {
      System.out.println("Matched STATEMENT");
      return true;
    } else
      complain("SemiColon");
    return false;
  }

  private void comparison() throws ExpressionException {
    if (match(TokenType.NOT)) {
      if (!(callable() || booleanLiteral())) // Case: !Func()
        complain("Callable or Boolean Literal Expected");
    } else if (booleanLiteral()) {
      if (!(match(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUALS)))
        complain("== or !=");
      if (!(booleanLiteral() || callable()))
        complain("Callable Value or Boolean Literal Expected");
    }

    else if (callable()) {
      while (operators()) { // Special Condtiion to Check
        if (numberDecimal() || callable()) {
          continue;
        }
      }
      // == or != comparison
      if (match(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUALS))
        if (!(operation() || booleanLiteral())) // Case 2, Func(), 2 or true
          complain("Numbers, Callable Statement, or Boolean");

      // the rest of the logical operators
      if (comparators()) // for simplicity's sake
        if (!(operation() || booleanLiteral())) // Case 2, Func(), 2 or true
          complain("Numbers, Callable Statement, or Boolean");

      // could also be a standalone statement
    }

    // number operations, other data types are disregarded for brevity
    else {
      if (!operation()) // Case 2, Func()
        complain("Number, Callable Statement or Boolean"); // error throw for all the conditions
      if (!comparators())// All comparators
        complain("Comparison Operator");
      if (!(operation())) // Case 2, Func(), 2 or true
        complain("Numbers, Callable Statement");
    }
  }

  private boolean operation() throws ExpressionException {
    if (!(numberDecimal() || callable())) {
      return false;
    }

    while (operators()) {
      if (numberDecimal() || callable()) {
        continue;
      }
      complain("Expected Number,Callable Statement");
    }
    return true;
  }

  private boolean callable() {
    if (match(TokenType.IDENTIFIER)) {
      unary();
      return true;
    }
    return false;
  }

  private boolean function() throws ExpressionException {
    if (!match(TokenType.IDENTIFIER)) {
      return false;
    }
    if (match(TokenType.DOT)) {
      function();
    }
    boolean parenFlag = false;
    if (match(TokenType.LEFTPAREN)) {
      parenFlag = true;
      while (match(TokenType.STRINGWORD) || operation()) {
        if (!match(TokenType.COMMA)) {
          break;
        }
      }
      if (parenFlag)
        return match(TokenType.RIGHTPAREN);
    }
    return true;
  }

  private boolean numberDecimal() {
    if (match(TokenType.DOT)) // Case: .234[dfl]
      if (match(TokenType.INTNUM, TokenType.FLOATNUM, TokenType.DOUBLENUM, TokenType.LONGNUM))
        return true;
    if (match(TokenType.INTNUM)) { // Case: 1.23[dfl]
      if (match(TokenType.DOT)) {
        if (match(TokenType.INTNUM, TokenType.FLOATNUM, TokenType.DOUBLENUM, TokenType.LONGNUM)) {
          return true;
        }
      } else
        return true; // Case: 20 30 10 (int)
    }
    return match(TokenType.FLOATNUM, TokenType.DOUBLENUM, TokenType.LONGNUM);
    // Case: 20f 30l 40d
  }

  private boolean initialization() throws ExpressionException {
    if (types()||check(TokenType.IDENTIFIER))
      if (check(TokenType.IDENTIFIER)) {
          match(TokenType.IDENTIFIER);
          match(TokenType.IDENTIFIER);
        if (match(TokenType.EQUAL)) {
          if (match(TokenType.NEW)) {
            if (function())
              return true;
            else
              complain("Object/Primitive");
          }
          if (function() || operation() || match(TokenType.CHARACTER, TokenType.STRINGWORD)) {
            return true;
          }
          complain("Empty Assignment.");
        }
        return true;
      }
    return false;
  }

  private boolean assignment() throws ExpressionException {
    if (check(TokenType.IDENTIFIER)) {
      if (match(TokenType.EQUAL)) {
        match(TokenType.IDENTIFIER);
        if (function() || operation() || match(TokenType.CHARACTER, TokenType.STRING)) {
          return true;
        }
        complain("Empty Assignment.");
      }
    }
    return false;
  }

  // Beyond here are the terminals or a combination of terminals.
  private boolean comparators() {
    return match(
        TokenType.EQUAL_EQUAL,
        TokenType.NOT_EQUALS,
        TokenType.GREATERTHAN,
        TokenType.GREATERTHAN_EQUAL,
        TokenType.LESSTHAN_EQUAL,
        TokenType.LESSTHAN);
  }

  private boolean operators() {
    return match(TokenType.PLUS,
        TokenType.MINUS,
        TokenType.SLASH,
        TokenType.MODULO,
        TokenType.STAR);
  }

  private boolean unary() {
    return match(
        TokenType.INCREMENT,
        TokenType.DECREMENT);
  }

  private boolean booleanLiteral() {
    return match(
        TokenType.TRUE,
        TokenType.FALSE);
  }

  private boolean types() {
    return match(
        TokenType.INT,
        TokenType.CHAR,
        TokenType.STRING,
        TokenType.LONG,
        TokenType.DOUBLE,
        TokenType.BOOLEAN,
        TokenType.SHORT,
        TokenType.FLOAT);
  }

  private boolean control() throws ExpressionException {
    if (match(TokenType.RETURN)) {
      if (operation() || match(TokenType.STRING) || match(TokenType.CHARACTER) || true)
        return true; // empty return statements do exist
    }
    if (match(TokenType.BREAK, TokenType.CONTINUE))
      return true;
    return false;
  }

  // Helper method for token manipulation..
  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        System.out.println("Matched " + type);
        advance();
        return true;
      }
    }
    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd())
      current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == TokenType.EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private boolean complain(String expectation) throws ExpressionException {
    throw new ExpressionException("Expected " + expectation + " but found " + peek().type);
  }
}

class ExpressionException extends Exception {
  public ExpressionException(String complaint) {
    super(complaint);
  }

}
