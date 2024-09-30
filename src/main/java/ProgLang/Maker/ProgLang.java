package ProgLang.Maker;
import java.io.FileReader;
/**
 *
 * @author Ophelia
 */
public class ProgLang {
    public static void main(String[] args) {    
        Lexer lexicon = new Lexer();
        float f = .012f;
        lexicon.addLineCode("-+*/ while(i<=)");
        lexicon.lex();
        lexicon.addLineCode("int pa pa |pregtwe = 025.2341f; \"This is\n a String\" ...");
        lexicon.lex();
    }
}
