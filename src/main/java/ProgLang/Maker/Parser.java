package ProgLang.Maker;
import java.util.ArrayList;
import java.util.List;
public class Parser {
    private List<Token> tokens = new ArrayList<>();
    private int current = 0;
    Parser(List<Token> tokens){
        this.tokens = tokens;
        System.out.println();
    }
    public void parse(){
        try {
          System.out.println("Starting to Parse.");
          expression();
          System.out.println("Parse Successful.");
        } catch (ExpressionException e) {
          System.err.println(e.getMessage());
          e.getStackTrace();
        }          
    }
    
    private void expression() throws ExpressionException{
      while(!tokens.isEmpty()){
        switch(peek().type){
          case TokenType.WHILE -> whileExpr();
          case TokenType.COMMENT -> {continue;}
          case TokenType.EOF -> {return;}

        }
      }
    }
    private void whileExpr() throws ExpressionException{
      
    }
    private void whileCondition() throws ExpressionException{

    }
    private void comparison() throws ExpressionException{
        if(!(match(numbers())||match(TokenType.CALLABLE)));
          complain("Numbers or Function");
        
     }
    private void statement() throws ExpressionException{
      
    }
    //Beyond here are the terminals or a combination of terminals.
    private TokenType[] comparators(){
      return new TokenType[]{
        TokenType.NOT_EQUALS,
        TokenType.GREATERTHAN,
        TokenType.GREATERTHAN_EQUAL,
        TokenType.LESSTHAN_EQUAL,
        TokenType.LESSTHAN};
    }
    private TokenType[] numbers(){
      return new TokenType[]{
        TokenType.FLOATNUM,
        TokenType.INTNUM,
        TokenType.DOUBLENUM,
        TokenType.LONGNUM
      };
    }
    private TokenType[] operators(){
        return new TokenType[]{
          TokenType.PLUS,
          TokenType.MINUS,
          TokenType.SLASH,
          TokenType.MODULO,
          TokenType.STAR};
    }
    private TokenType[] unary(){
      return new TokenType[]{
        TokenType.INCREMENT,
        TokenType.DECREMENT
      };
    }
    private TokenType[] booleanLiteral(){
      return new TokenType[]{
        TokenType.TRUE,
        TokenType.FALSE
      };
    }
    private TokenType[] control(){
      return new TokenType[]{
        TokenType.RETURN,
        TokenType.BREAK,
        TokenType.CONTINUE
      };
    }
    

    //Helper method for token manipulation..
    private boolean match(TokenType... types) throws ExpressionException{
      for (TokenType type : types) {
          if(check(TokenType.COMMENT)/*This case.*/)
            advance();
          if (check(type)) {
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
      private void complain(String message) throws ExpressionException{
        throw new ExpressionException("Expected a "+message+"but found "+peek()+" instead");
      }
}
class ExpressionException extends Exception{
    public ExpressionException(String message) {
      super(message);
    }
}
