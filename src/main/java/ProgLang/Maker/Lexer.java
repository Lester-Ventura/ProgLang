package ProgLang.Maker;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

enum TokenType{
    /*Single Character Tokens */
    PLUS, MINUS, STAR, SLASH, //Operators 
    RIGHTPAREN, LEFTPAREN, //Parenthesis
    LEFTBRACE, RIGHTBRACE,
    SEMICOLON, COMMA,
    LEFTBRACKET,RIGHTBRACKET, DOUBLEQUOTE, DOT,
    /*Character Tokens for Comparators */
    GREATERTHAN, GREATERTHAN_EQUAL,
    LESSTHAN, LESSTHAN_EQUAL,
    EQUAL, EQUAL_EQUAL,
    BINARYOR, BINARYAND,
    NOT,
    /* Slash and Backslash */
    SINGLECOMMENT, MULTICOMMENT, ESCBACKSLASH, ESCQUOTE, ESCDOUBLEQUOTE,
    /*Literals + Variable*/
    STRINGWORD, NUMBER, IDENTIFIER, 
    /*Reserved Keywords*/
    WHILE, TRUE, FALSE, BREAK, CONTINUE, FINAL, EOF, BOOLEAN, BYTE, INT, CHAR, FLOAT, DOUBLE,LONG, SHORT, STRING,
    /*Special Characters */
    DOLLAR, NEWLINE, TAB, CARRIAGERETURN, BACKSPACE, FORMFEED
}
class Token{
    final TokenType type;
    final String lexeme;
    final int line;
    Token(TokenType type,String lexeme,int line){
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
    }
    Token(TokenType type, int line){
        this.type = type;
        this.lexeme = null;
        this.line = line;
    }
    @Override
    public String toString(){
        return "Type: "+type+" |Lexeme: "+lexeme+" |Line: "+line;
    }
}
public class Lexer{
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
        while(stringMarcher<line.length()){
            TokenRecognizer();
            System.out.println("Token: ["+tokenList.getLast().toString()+"]");
        }
        }catch (Exception e){
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }
    }
    private void TokenRecognizer() throws UnrecognizedTokenException{
        if(isNumerical(charRead())){
            while(isNumerical(charRead())){
             lexeme += String.valueOf(charRead());   
             forward();
            }
        }
        if(isAlphabet(charRead())){
            while(isAlphaNumeric(charRead())){
            lexeme += String.valueOf(charRead());
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
                default -> addIdentifier(TokenType.IDENTIFIER,lexeme);
          }
            lexeme = "";
        }
        //Recognizes single type tokens.
         switch(charRead()){
            case '.' -> addToken(TokenType.DOT);
            case '!' -> addToken(TokenType.NOT);
            case '(' -> addToken(TokenType.LEFTPAREN);
            case ')' -> addToken(TokenType.RIGHTPAREN);
            case '[' -> addToken(TokenType.LEFTBRACE);
            case ']' -> addToken(TokenType.RIGHTBRACE);
            case '{' -> addToken(TokenType.LEFTBRACKET);
            case '}' -> addToken(TokenType.RIGHTBRACKET);
            case ';' -> addToken(TokenType.SEMICOLON);
            case '-' -> addToken(TokenType.MINUS);
            case '+' -> addToken(TokenType.PLUS);
            case '*' -> addToken(TokenType.STAR);
            case ',' -> addToken(TokenType.COMMA);
            case '"' -> addToken(TokenType.DOUBLEQUOTE);
            case '|' -> {if(charLookAhead() == '|')
                        addToken(TokenType.BINARYOR);
                        else
                        throw new UnrecognizedTokenException("Unrecognized Token at Line: "+currentLine+" Expected: |");}
            case '&' -> {if(charLookAhead() == '&')
                        addToken(TokenType.BINARYAND);
                        else 
                        throw new UnrecognizedTokenException("Unrecognized Token at Line: "+currentLine+" Expected: &");}
            // Beyond here is more special characters.
            case '/' ->{  
                switch (charLookAhead()) {
                case '/' -> addToken(TokenType.SINGLECOMMENT);
                case '*' -> addToken(TokenType.MULTICOMMENT);
                default -> addToken(TokenType.SLASH); 
                }
                forward();
            }
            case '\\' -> {
                switch(charLookAhead()){
                    case '\\' -> addToken(TokenType.ESCBACKSLASH);
                    case '\'' -> addToken(TokenType.ESCQUOTE);
                    case '\"' -> addToken(TokenType.ESCDOUBLEQUOTE);
                    case 'n' -> addToken(TokenType.NEWLINE);
                    case 't' -> addToken(TokenType.TAB);
                    }
                forward();}
                case ' ' -> {}
                default -> throw new UnrecognizedTokenException("Unrecognized Token!"+charRead());
            }
            forward();
    }

    public List<Token> getTokens(){
        addToken(TokenType.EOF);
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
    private boolean ending(){
        return stringMarcher >= line.length();
    }
    private void addToken(TokenType type){
        this.tokenList.add(new Token(type,currentLine));
    }
    private void addIdentifier(TokenType type,String identifier){
        this.tokenList.add(new Token(type,identifier,currentLine));
    }
    public boolean isAlphabet(char c){
        return Pattern.compile("[a-zA-Z]").matcher(String.valueOf(c)).matches();
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