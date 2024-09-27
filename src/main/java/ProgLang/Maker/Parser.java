package ProgLang.Maker;
import java.util.ArrayList;
import java.util.List;
public class Parser {
    List<Token> tokens = new ArrayList<>();
    int current = 0;
    Parser(List<Token> tokens){
        this.tokens = tokens;
    }
    public void parse(){
        List<TokenType> currentSentence = new ArrayList<>();
        for(Token currentToken:tokens){
            
        }
        
    }
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
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
}
