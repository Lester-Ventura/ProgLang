
package ProgLang.Maker;

/**
 *
 * @author Ophelia
 */
public class ProgLang {

    public static void main(String[] args) {    
        Lexer lexicon = new Lexer();
        lexicon.addLineCode("-+*/ while()");
        lexicon.addLineCode("int i = 0");
        lexicon.lex();
    }
}
