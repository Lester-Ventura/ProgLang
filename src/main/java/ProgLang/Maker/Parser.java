package ProgLang.Maker;
import java.util.ArrayList;
import java.util.List;
public class Parser {
    private String complaint = "";
    private List<Token> tokens = new ArrayList<>();
    private int current = 0;
    Parser(List<Token> tokens){
        this.tokens = tokens;
        System.out.println("Tokens have been loaded");
    }
    public void parse(){
        try {
          System.out.println("Starting to Parse.");
          expression();
          System.out.println("Parse Successful.");
        } catch (ExpressionException e) {
          System.err.println(e.getMessage());
        }          
    }
    private void expression() throws ExpressionException{
      while(!tokens.isEmpty()){
        switch(peek().type){ //Assume while start for this program.
          case TokenType.WHILE -> whileExpr(); 
          case TokenType.EOF -> {return;}
          default -> complain("EOF not reached.");
        }
      } 
    }
    private void whileExpr() throws ExpressionException{
      match(TokenType.WHILE);
      if(!match(TokenType.LEFTPAREN))
        complain("Left Parenthesis");
      whileCondition(); 
      if(!match(TokenType.RIGHTPAREN))
        complain("Right Parenthesis");
      boolean bracePresentFlag = match(TokenType.LEFTBRACE);
        statement();
      if(bracePresentFlag){ 
        whileStatement();
      }
    }
    private void whileCondition() throws ExpressionException{
      if(!booleanLiteral())
          comparison();
      if(match(TokenType.BINARYAND,TokenType.BINARYOR))
        whileCondition();
    }
    private void whileStatement() throws ExpressionException{
      while(!match(TokenType.RIGHTBRACE))
        statement();
    }
    private void statement()throws ExpressionException{
        if(initialization()||assignment()||control()||callable()){
          if(match(TokenType.SEMICOLON)){
            
          }
          else
          complain("SemiColon");
        }
    } 
    

    private void comparison() throws ExpressionException{
        if(match(TokenType.NOT))
          if(!callable()){ //Case: !Func()
            complain("Function");}
        if(!(numberDecimal()||callable())) //Case 2, Func()
            complain("Number Decimal");
        if(!comparators())// All comparators
            complain("Comparison Operator"); 
        if(match(TokenType.NOT)){ // Case: !Func
          if(!callable())
            complain("Function");}
        if(!(numberDecimal()||callable())) //Case 2, Func(), 2 or true
            complain("Numbers, Function or Boolean");
     }    
  
    private boolean callable(){
      if(match(TokenType.IDENTIFIER))
        if(unary())
          return true;
        else if(function())
          return true;
      return false;
    }
    private boolean function(){
      while(match(TokenType.DOT))
        if(!match(TokenType.IDENTIFIER)){
          return false;
        }
      if(match(TokenType.LEFTPAREN)){
        //Insert Argument.
        if(!match(TokenType.RIGHTPAREN))
           return false;}
      return true;
    }
    private boolean numberDecimal(){
      if(match(TokenType.DOT)) // Case: .234[dfl]
        if(match(TokenType.INTNUM,TokenType.FLOATNUM,TokenType.DOUBLENUM,TokenType.LONGNUM))
          return true;
      if(match(TokenType.INTNUM)){ //Case: 1.23[dfl]
        if(match(TokenType.DOT)){
          if(match(TokenType.INTNUM,TokenType.FLOATNUM,TokenType.DOUBLENUM,TokenType.LONGNUM)){
            return true;}}
        else
            return true; //Case: 20 30 10 (int)
      }
      return match(TokenType.FLOATNUM,TokenType.DOUBLENUM,TokenType.LONGNUM);
       //Case: 20f 30l 40d
    }
    private boolean initialization() throws ExpressionException{
        if(types())
          if(match(TokenType.IDENTIFIER)){
            if(match(TokenType.EQUAL)){
              if(match(TokenType.NEW)){
                if(function())
                return true;
                else
                complain("Object/Primitive");
                }
                if(function()||numberDecimal()||match(TokenType.CHARACTER,TokenType.STRINGWORD)){
                return true;
              }
              complain("Empty Assignment.");
            }
            return true;
            }
      return false;
    }
    private boolean assignment() throws ExpressionException{
        if(match(TokenType.IDENTIFIER)){
          if(match(TokenType.EQUAL)){
            if(function()||numberDecimal()||match(TokenType.CHARACTER,TokenType.STRING)){
                return true;
            }
            complain("Empty Assignment.");
          }
          complain("Equal Sign");
        }
        return false;
    }
    //Beyond here are the terminals or a combination of terminals.
    private boolean comparators(){
      return match(
      TokenType.EQUAL_EQUAL,  
      TokenType.NOT_EQUALS,
      TokenType.GREATERTHAN,
      TokenType.GREATERTHAN_EQUAL,
      TokenType.LESSTHAN_EQUAL,
      TokenType.LESSTHAN);
    }
    private boolean operators(){
        return match(TokenType.PLUS,
          TokenType.MINUS,
          TokenType.SLASH,
          TokenType.MODULO,
          TokenType.STAR);
    }
    private boolean unary(){
      return match(
        TokenType.INCREMENT,
        TokenType.DECREMENT
      );
    }
    private boolean booleanLiteral(){
      return match(
        TokenType.TRUE,
        TokenType.FALSE
      );
    }
    private boolean types(){
      return match(
        TokenType.INT,
        TokenType.CHAR,
        TokenType.STRING,
        TokenType.LONG,
        TokenType.DOUBLE,
        TokenType.BOOLEAN,
        TokenType.SHORT,
        TokenType.FLOAT
      );
    }
    private boolean control(){
      if(match(TokenType.RETURN)){
        if(callable()||numberDecimal()||match(TokenType.STRING)||match(TokenType.CHARACTER)){
            return true;
        }
      }
      if(match(TokenType.BREAK, TokenType.CONTINUE))
        return true;
      return false;
    }
    //Helper method for token manipulation..
    private boolean match(TokenType... types){
      for (TokenType type : types) {
          if (check(type)) {
            System.out.println("Matched "+ type);
            advance();
            return true;
          }
        }
        return false;
      }
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
    private Token advance() {
        if (!isAtEnd()) current++;
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
        throw new ExpressionException("Expected "+expectation+" but found "+peek().type);
      }
    }
    class ExpressionException extends Exception{
      public ExpressionException(String complaint) {
      super(complaint);
    }
      
    }
    