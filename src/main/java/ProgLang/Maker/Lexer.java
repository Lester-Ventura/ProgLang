package ProgLang.Maker;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

enum TokenType{
    /*Single Character Tokens */
    PLUS, MINUS, STAR, SLASH, MODULO,//Operators 
    RIGHTPAREN, LEFTPAREN, //Parenthesis
    LEFTBRACE, RIGHTBRACE,
    SEMICOLON, COMMA,
    LEFTBRACKET,RIGHTBRACKET, DOT,
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
            TokenRecognizer();
            System.out.println("Token: ["+tokenList.getLast().toString()+"]");
        }
        }catch(UnrecognizedTokenException e){
            System.out.println(e.getMessage());
        }
        catch (Exception e){
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }
    }
    private void TokenRecognizer() throws UnrecognizedTokenException{
        //Checks for Numbers.
        if(isNumerical(charRead())){
            while(isNumerical(charRead())){
             lexeme += String.valueOf(charRead());   
             forward();
            }
            addIdentifier(TokenType.NUMBER, lexeme);
            lexeme = "";
            return;
        }
        //Checks if it is an alphabet
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
            case '-' -> addToken(TokenType.MINUS);
            case '+' -> addToken(TokenType.PLUS);
            case '*' -> addToken(TokenType.STAR);
            case '%' -> addToken(TokenType.MODULO);
            case '=' -> addToken(TokenType.EQUAL);
            case ',' -> addToken(TokenType.COMMA);
            case '.' -> addToken(TokenType.DOT);
            case '!' -> addToken(TokenType.NOT);
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
            case '"' -> {
                lexeme = "\"";
                forward();
                while(charRead()!= '"'){
                    if(charRead() == '\\'){
                        addIdentifier(TokenType.STRING,lexeme);
                        switch(charLookAhead()){
                            case '\\' -> addToken(TokenType.ESCBACKSLASH);
                            case '\'' -> addToken(TokenType.ESCQUOTE);
                            case '\"' -> addToken(TokenType.ESCDOUBLEQUOTE);
                            case 'n' -> addToken(TokenType.NEWLINE);
                            case 't' -> addToken(TokenType.TAB);
                            case 'f' -> addToken(TokenType.FORMFEED);
                            case 'b' -> addToken(TokenType.BACKSPACE);
                            case 'r' -> addToken(TokenType.CARRIAGERETURN);
                            default -> throw new UnrecognizedTokenException("Unexpected Token! "+ currentLine +" "+charRead());
                            }
                        forward(); forward(); lexeme = ""; }
                    lexeme+= charRead();
                    forward();
                }
                addIdentifier(TokenType.STRINGWORD,lexeme);
                lexeme ="";
            }
            case '|' -> {if(charLookAhead() == '|')
                        addToken(TokenType.BINARYOR);
                        else
                        throw new UnrecognizedTokenException("Unrecognized Token at Line: "+currentLine+" Expected: |");}
            case '&' -> {if(charLookAhead() == '&')
                        addToken(TokenType.BINARYAND);
                        else 
                        throw new UnrecognizedTokenException("Unrecognized Token at Line: "+currentLine+" Expected: &");}
 
            case '/' ->{  
                switch (charLookAhead()) {
                case '/' -> addToken(TokenType.SINGLECOMMENT);
                case '*' -> addToken(TokenType.MULTICOMMENT);
                default -> addToken(TokenType.SLASH); 
                }
                forward();
            }
            
                case ' ' -> {}
                default -> throw new UnrecognizedTokenException("Unrecognized Token! \""+charRead()+"\"");
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