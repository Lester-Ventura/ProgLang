package ProgLang.Maker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

enum TokenType {
    /* Single Character Tokens */
    PLUS, MINUS, STAR, SLASH, MODULO, // Operators
    RIGHTPAREN, LEFTPAREN, // Parenthesis
    LEFTBRACE, RIGHTBRACE,
    SEMICOLON, COMMA,
    LEFTBRACKET, RIGHTBRACKET, DOT, DOUBLEQUOTE,
    /* Character Tokens for Comparators */
    GREATERTHAN, GREATERTHAN_EQUAL,
    LESSTHAN, LESSTHAN_EQUAL,
    EQUAL, EQUAL_EQUAL, NOT_EQUALS,
    /* Logical Operators */
    BINARYOR, BINARYAND,
    NOT, // special logical operator
    /* Literals + Variable */
    STRINGWORD, INTNUM, IDENTIFIER, FLOATNUM, DOUBLENUM, LONGNUM, CHARACTER,
    /* Reserved Keywords */
    WHILE, NEW, TRUE, FALSE, BREAK, CONTINUE, FINAL, EOF, BOOLEAN, INT, CHAR, FLOAT, DOUBLE, LONG, SHORT, STRING,
    RETURN,
    /* Special Functions */
    INCREMENT, DECREMENT,
}

class Token {
    final TokenType type;
    final String lexeme;
    final int line;
    final int column;

    Token(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    Token(TokenType type, int line, int column) {
        this.type = type;
        this.lexeme = null;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Type: " + type + " |Lexeme: " + lexeme + " |Line: " + line + "|Column: " + column;
    }
}

public class Lexer {
    boolean multiCommentFlag = false;
    private boolean foundEOF = false;
    private final List<Token> tokenList = new ArrayList<>();
    private int stringMarcher;
    private int currentLine = 0;
    private String line;
    private String lexeme = "";

    Lexer() {
    }

    public void addLineCode(String code) {
        this.line = code;
        currentLine++;
    }

    public void lex() {
        stringMarcher = 0;
        try {
            while (isNotEnding()) {
                if (multiCommentFlag)
                    comment();
                TokenRecognizer();
            }
            Iterator<Token> tokenIterator = tokenList.iterator();
            while (tokenIterator.hasNext()) {
                System.out.println("Token: [" + tokenIterator.next().toString() + "]");
            }
        } catch (UnrecognizedTokenException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Something went wrong!");
        }
    }

    private void TokenRecognizer() throws UnrecognizedTokenException {
        // Checks for Numbers.
        if (isNumerical(charRead())) {
            for (; isNumerical(charRead()); forward()) {
                lexeme += charRead();
            }
            stringMarcher--; // Special Case to save column, bad practice, avoid using if possible
            switch (Character.toLowerCase(charRead())) {
                case 'f' -> addIdentifier(TokenType.FLOATNUM, lexeme);
                case 'd' -> addIdentifier(TokenType.DOUBLENUM, lexeme);
                case 'l' -> addIdentifier(TokenType.LONGNUM, lexeme);
                default -> addIdentifier(TokenType.INTNUM, lexeme);
            }
            forward();
            lexeme = "";
            return;
        }
        // Checks if it is an alphabet
        if (isAlphabet(charRead())) {
            for (; isAlphaNumeric(charRead()); forward()) {
                lexeme += charRead();
            }
            stringMarcher--; // Special Case to save column, bad practice, avoid using if possible
            switch (lexeme) {
                case "while" -> addToken(TokenType.WHILE);
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
                case "new" -> addToken(TokenType.NEW);
                default -> addIdentifier(TokenType.IDENTIFIER, lexeme);
            }
            forward();
            lexeme = "";
            return;
        }
        // Recognizes other type tokens.
        switch (charRead()) {
            // Closing and Opening Braces
            case '(' -> addToken(TokenType.LEFTPAREN);
            case ')' -> addToken(TokenType.RIGHTPAREN);
            case '[' -> addToken(TokenType.LEFTBRACKET);
            case ']' -> addToken(TokenType.RIGHTBRACKET);
            case '{' -> addToken(TokenType.LEFTBRACE);
            case '}' -> addToken(TokenType.RIGHTBRACE);
            case ';' -> addToken(TokenType.SEMICOLON);
            // Operator beside division
            case '-' -> doubleCharacter(TokenType.PLUS, '-', TokenType.DECREMENT);
            case '+' -> doubleCharacter(TokenType.PLUS, '+', TokenType.INCREMENT);
            case '*' -> addToken(TokenType.STAR);
            case '%' -> addToken(TokenType.MODULO);
            case '=' -> doubleCharacter(TokenType.EQUAL, '=', TokenType.EQUAL_EQUAL);
            case ',' -> addToken(TokenType.COMMA);
            case '.' -> addToken(TokenType.DOT);
            case '!' -> doubleCharacter(TokenType.NOT, '=', TokenType.NOT_EQUALS);
            case '<' -> doubleCharacter(TokenType.LESSTHAN, '=', TokenType.LESSTHAN_EQUAL);
            case '>' -> doubleCharacter(TokenType.GREATERTHAN, '=', TokenType.GREATERTHAN_EQUAL);
            case '|' -> doubleCharacter(null, '|', TokenType.BINARYOR);
            case '&' -> doubleCharacter(null, '&', TokenType.BINARYAND);
            case '\"' -> string();
            case '\'' -> character();
            case '/' -> {
                switch (charLookAhead()) {
                    case '/' -> comment();
                    case '*' -> {
                        multiCommentFlag = true;
                        comment();
                        return;
                    }
                    default -> addToken(TokenType.SLASH);
                }
                forward();
            }
            case ' ', '\t' -> {
            }
            default -> throw new UnrecognizedTokenException("Unrecognized Token! \"" + charRead() + "\"");
        }
        forward();
    }

    private void character() throws UnrecognizedTokenException {
        lexeme += charRead();
        forward();
        lexeme += charRead();
        forward();
        if (charRead() == '\'') {
            lexeme += charRead();
            addIdentifier(TokenType.CHARACTER, lexeme);
            forward();
        } else
            throw new UnrecognizedTokenException("Expected ' but found" + charRead());
    }

    // Handling for Comments
    private void comment() {
        if (multiCommentFlag) {
            while (isNotEnding()) {
                if (charRead() == '*') {
                    if (charLookAhead() == '/') {
                        multiCommentFlag = false;
                        forward();
                        forward();
                        return;
                    }
                }
                forward();
            }
        } else
            stringMarcher = line.length();
    }

    private void doubleCharacter(TokenType singleToken, char characterDouble, TokenType doubleToken)
            throws UnrecognizedTokenException {
        if (charLookAhead() == characterDouble) {
            forward();
            addToken(doubleToken);
        } else if (singleToken == null)
            throw new UnrecognizedTokenException(
                    "Unrecognized Token at Line: " + currentLine + " Expected: " + characterDouble);
        else
            addToken(singleToken);
    }

    private void string() {
        lexeme += charRead();
        do {
            forward();
            lexeme += charRead();
        } while (charRead() != '"');
        addIdentifier(TokenType.STRINGWORD, lexeme);
        lexeme = "";
    }

    public List<Token> getTokens() {
        if (!foundEOF) {
            addToken(TokenType.EOF);
        }
        return tokenList;
    }

    private char charLookAhead() {
        if (stringMarcher >= line.length())
            return ' ';
        return line.charAt(stringMarcher + 1);
    }

    private char charRead() {
        if (stringMarcher >= line.length())
            return ' ';
        return line.charAt(stringMarcher);
    }

    private void forward() {
        stringMarcher++;
    }

    private boolean isNotEnding() {
        return stringMarcher < line.length();
    }

    private void addToken(TokenType type) {
        this.tokenList.add(new Token(type, this.currentLine, this.stringMarcher + 1));
    }

    private void addIdentifier(TokenType type, String identifier) {
        this.tokenList.add(new Token(type, identifier, this.currentLine, this.stringMarcher + 1));
    }

    public boolean isAlphabet(char c) {
        return Pattern.compile("[$_a-zA-Z]").matcher(String.valueOf(c)).matches();
    }

    public boolean isNumerical(char c) {
        return Pattern.compile("\\d").matcher(String.valueOf(c)).matches();
    }

    public boolean isAlphaNumeric(char c) {
        return isNumerical(c) || isAlphabet(c);
    }
}

class UnrecognizedTokenException extends Exception {
    public UnrecognizedTokenException(String message) {
        super(message);
    }
}