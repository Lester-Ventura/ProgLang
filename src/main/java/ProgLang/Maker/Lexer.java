package ProgLang.Maker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

enum TokenType{
    /*Single Character Tokens */
    PLUS, MINUS, STAR, SLASH, MODULO,//Operators 
    RIGHTPAREN, LEFTPAREN, //Parenthesis
    LEFTBRACE, RIGHTBRACE,
    SEMICOLON, COMMA,
    LEFTBRACKET,RIGHTBRACKET, DOT, DOUBLEQUOTE,
    /*Character Tokens for Comparators */
    GREATERTHAN, GREATERTHAN_EQUAL,
    LESSTHAN, LESSTHAN_EQUAL,
    EQUAL, EQUAL_EQUAL,
    BINARYOR, BINARYAND,
    NOT, NOT_EQUALS,
    /* Slash and Backslash */
    COMMENT,  
    /*Literals + Variable*/
    STRINGWORD, INTNUM, IDENTIFIER, FLOATNUM, DOUBLENUM, LONGNUM,
    /*Reserved Keywords*/
    WHILE, TRUE, FALSE, BREAK, CONTINUE, FINAL, EOF, BOOLEAN, BYTE, INT, CHAR, FLOAT, DOUBLE,LONG, SHORT, STRING,RETURN,
    /* Special Functions */
    INCREMENT, DECREMENT,
    /* Callable/Variable Statements */
    CALL, CALLABLE, FUNCTION, ARGS
}
class Token{
    final TokenType type;
    final String lexeme;
    final int line;
    final int column;
    Token(TokenType type,String lexeme,int line, int column){
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }
    Token(TokenType type, int line, int column){
        this.type = type;
        this.lexeme = null;
        this.line = line;
        this.column = column;
    }
    @Override
    public String toString(){
        return "Type: "+type+" |Lexeme: "+lexeme+" |Line: "+line+"|Column: "+column;
    }
}
public class Lexer{
    boolean multiCommentFlag = false;
    private boolean hasEndOfFile = false;
    private final List<Token> tokenList = new ArrayList<>();
    private int stringMarcher;
    private int currentLine = 0;
    private String line;
    private String lexeme = "";
    
    Lexer(){}
    public void addLineCode(String code){
        this.line = code;
        currentLine++;
    }
    public void lex(){
        stringMarcher = 0;
        try{
        while(isNotEnding()){   
            if(multiCommentFlag){
            comment();          
            }
            TokenRecognizer();
        }
        Iterator<Token> tokenIterator = tokenList.iterator();
        while(tokenIterator.hasNext()){
            System.out.println("Token: ["+tokenIterator.next().toString()+"]");
        }
        }catch(UnrecognizedTokenException e){
            System.err.println(e.getMessage());
        }
        catch (Exception e){
            System.err.println("Something went wrong!");
        } 
    }
    private void TokenRecognizer() throws UnrecognizedTokenException{
        //Checks for Numbers.
        if(isNumerical(charRead())){
            while(isNumerical(charRead())){
             lexeme += charRead();   
             forward();
            }
            switch(Character.toLowerCase(charRead())){
               case 'f' ->addIdentifier(TokenType.FLOATNUM,lexeme);
               case 'd' ->addIdentifier(TokenType.DOUBLENUM,lexeme);
               case 'l' ->addIdentifier(TokenType.LONGNUM, lexeme);
               default -> addIdentifier(TokenType.INTNUM,lexeme);
            }
            forward();
            lexeme = "";
            return;
        }
        //Checks if it is an alphabet
        if(isAlphabet(charRead())){
            while(isAlphaNumeric(charRead())){
            lexeme += charRead();
            forward();
            }
            switch(lexeme){
                case "while" -> addToken(TokenType.WHILE);
                case "byte" -> addToken(TokenType.BYTE);
                case "float" -> addToken(TokenType.FLOAT);
                case "double" -> addToken(TokenType.DOUBLE);
                case "char" -> addToken(TokenType.CHAR);
                case "long" -> addToken(TokenType.LONG);
                case "short" -> addToken(TokenType.SHORT);
                case "int" -> addToken(TokenType.INT);
                case "String" -> addToken(TokenType.STRING);
                case "boolean" -> addToken(TokenType.BOOLEAN);
                case "continue" -> addToken(TokenType.CONTINUE);
                case "break" -> addToken(TokenType.BREAK);
                case "true" -> addToken(TokenType.TRUE);
                case "false" -> addToken(TokenType.FALSE);
                case "final" -> addToken(TokenType.FINAL);
                case "return" -> addToken(TokenType.RETURN);
                default -> addIdentifier(TokenType.IDENTIFIER,lexeme);
          }
            // forward();
            lexeme = "";
            return;
        }
        //Recognizes single type tokens.
         switch(charRead()){
            //Closing and Opening Braces
            case '(' -> addToken(TokenType.LEFTPAREN);
            case ')' -> addToken(TokenType.RIGHTPAREN);
            case '[' -> addToken(TokenType.LEFTBRACE);
            case ']' -> addToken(TokenType.RIGHTBRACE);
            case '{' -> addToken(TokenType.LEFTBRACKET);
            case '}' -> addToken(TokenType.RIGHTBRACKET);
            case ';' -> addToken(TokenType.SEMICOLON);
            //Operator beside division
            case '-' -> {
                if (charLookAhead() == '-') {
                    addToken(TokenType.DECREMENT);
                    forward();
                } else 
                    addToken(TokenType.MINUS);
            }
            case '+' -> {
                if (charLookAhead() == '+') {
                    addToken(TokenType.INCREMENT);
                    forward();
                } else 
                    addToken(TokenType.PLUS);
            }
            case '*' -> addToken(TokenType.STAR);
            case '%' -> addToken(TokenType.MODULO);
            case '=' -> {
                if (charLookAhead() == '=') {
                    addToken(TokenType.EQUAL_EQUAL);
                    forward();
                } else 
                  addToken(TokenType.EQUAL);
            }
            case ',' -> addToken(TokenType.COMMA);
            case '.' -> addToken(TokenType.DOT);
            case '!' ->  {
                if (charLookAhead() == '=') {
                    addToken(TokenType.NOT_EQUALS);
                    forward();
                } else 
                addToken(TokenType.NOT); 
            }
            //Comparators 
            case '<' -> {
                if(charLookAhead()=='='){
                    addToken(TokenType.LESSTHAN_EQUAL);
                    forward();
                }
                else
                    addToken(TokenType.LESSTHAN);
            }
            case '>' -> {
                if(charLookAhead()=='='){
                    addToken(TokenType.GREATERTHAN_EQUAL);
                    forward();
                }
                else
                    addToken(TokenType.GREATERTHAN);
            }
            // Beyond here is more special characters.
            // String consumption, if it is a string, it consumes EVERYTHING.
            case '\"' -> {
                do{
                    lexeme+= charRead();
                    forward();
                }while(charRead()!= '"');
                lexeme += charRead();
                forward();
                addIdentifier(TokenType.STRINGWORD,lexeme);
                lexeme ="";
            }
            case '|' -> {if(charLookAhead() == '|'){
                        addToken(TokenType.BINARYOR);
                        forward();}
                        else
                        throw new UnrecognizedTokenException("Unrecognized Token at Line: "+currentLine+" Expected: |");}
            case '&' -> {if(charLookAhead() == '&'){
                        addToken(TokenType.BINARYAND);
                        forward();}
                        else 
                        throw new UnrecognizedTokenException("Unrecognized Token at Line: "+currentLine+" Expected: &");}
 
            case '/' ->{  
                switch (charLookAhead()) {
                case '/' -> addToken(TokenType.COMMENT);
                case '*' -> {
                             multiCommentFlag = true; 
                             comment();
                             return;
                            }
                default -> addToken(TokenType.SLASH); 
                }
                forward();
            }
            
                case ' ' -> {}
                default -> throw new UnrecognizedTokenException("Unrecognized Token! \""+charRead()+"\"");
            }
            forward();
    }
    private void comment(){
        if(multiCommentFlag){
            while(isNotEnding()){
                if(charRead()=='*'){
                    if(charLookAhead()=='/'){
                        multiCommentFlag = false;
                        addIdentifier(TokenType.COMMENT,lexeme);
                        forward(); forward();
                        return;
                        }
                }
                lexeme+= charRead();
                forward();
            }
        }
        else
            while(isNotEnding()){
                lexeme+= charRead();
                forward();
            }
        
            

    }
    public List<Token> getTokens(){
        if (!hasEndOfFile) {
            addToken(TokenType.EOF);
        }
        return tokenList;
    }
    private char charLookAhead(){
        if(stringMarcher>=line.length())
            return ' ';
        return line.charAt(stringMarcher+1);
    }
    private char charRead(){
        if(stringMarcher>=line.length())
            return ' ';
        return line.charAt(stringMarcher);
    }
    private void forward(){
        stringMarcher++;
    }
    private boolean isNotEnding(){
        return stringMarcher < line.length();
    }
    private void addToken(TokenType type){
        this.tokenList.add(new Token(type,this.currentLine,this.stringMarcher));
    }
    private void addIdentifier(TokenType type,String identifier){
        this.tokenList.add(new Token(type,identifier,this.currentLine,this.stringMarcher));
    }
    public boolean isAlphabet(char c){
        return Pattern.compile("[$_a-zA-Z]").matcher(String.valueOf(c)).matches();
    }
    public boolean isNumerical(char c){
        return Pattern.compile("\\d").matcher(String.valueOf(c)).matches();
    }
    public boolean isAlphaNumeric(char c){
        return isNumerical(c) || isAlphabet(c);
    }
}
class UnrecognizedTokenException extends Exception{
    public UnrecognizedTokenException(String message) {
        super(message);
    }
}