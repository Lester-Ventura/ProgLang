package ProgLang.Maker;
import java.util.ArrayList;
import java.util.List;
public class Parser {
    private List<Token> tokens = new ArrayList<>();
    private int current = 0;
    Parser(List<Token> tokens){
        this.tokens = tokens;
        System.out.println("Tokens have been loaded!");
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
          case TokenType.EOF -> {
            System.err.println("Parsing finished!");
            return;
          }
          default -> complain("while");
        }
        tokens.iterator();
      }
    }
    /**
     * <while_do> → while (<condition>) <block> | while (condition) <statement>
     * @throws ExpressionException
     */
    private void whileExpr() throws ExpressionException{
      condition();
    }
    private void condition() throws ExpressionException{
      // gets out of while token
      advance();
      if(!(check(TokenType.LEFTPAREN)))
        complain("Left Parentheses");
      else
        advance();

      comparison();

      if(!(check(TokenType.RIGHTPAREN)))
        complain("Right Parentheses");
      else 
        advance();
    }
    private void comparison() throws ExpressionException {
      if (check(booleanLiteral()))
        advance();
      else if (check(numbers()) || check(TokenType.IDENTIFIER)) {
        advance();
        if (!check(comparators()))
          complain("Comparators");
        else
          advance();
        
        if (!(check(numbers()) || check(TokenType.IDENTIFIER))) 
          complain("Number or Identifier");
        else 
          advance();
      } 
      // TODO: Implement rest of options
      // else if (check(TokenType.NOT)) {
      //   while (check(TokenType.NOT)) {
      //     advance();
      //   }
      //   if(!(check(numbers()) || check(TokenType.IDENTIFIER))) {
      //     complain("Numbers or Function");
      //   }
      // }
      else {
        complain("Comparison");
      }
      
      
      
        
     }
    private void statement() throws ExpressionException{
      if(!(check(unary()) || check(TokenType.CALLABLE) || check(control())));
          complain("Numbers or Function");
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
    
    private boolean check(TokenType... types) {
      if (isAtEnd()) return false;

      for (TokenType type : types) {
        if (peek().type == type) return true;
      }
      return false;
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
      private void complain(String expectation) throws ExpressionException{
        throw new ExpressionException("Expected a "+expectation+" but found "+peek()+" instead");
      }
}
class ExpressionException extends Exception{
    public ExpressionException(String message) {
      super(message);
    }
}
