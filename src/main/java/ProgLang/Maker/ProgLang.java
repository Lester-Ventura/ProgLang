package ProgLang.Maker;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author Ophelia
 */
public class ProgLang {
    public static void main(String[] args) {    
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object to take user input.
        Lexer lexicon = new Lexer();  // Create a Lexer object.
        {{{{{{{{}}}}}}}}
        System.out.println("Enter a line of code (or type 'exit' after the last '}' to quit):");

        StringBuilder lineCode = new StringBuilder("");

        while (true) {
            String inputLine = scanner.nextLine();  // Read a line of input from the user.

            if (inputLine.equalsIgnoreCase("exit")) {  // If the user types 'exit', break the loop.
                System.out.println("Exiting program.");
                break;

            }

            lineCode.append(inputLine);
        }
        lexicon.addLineCode(lineCode.toString());  // Add the input line of code to the lexer.
        lexicon.lex();  // Tokenize the lin5e of code.
        List<Token> tokenList = lexicon.getTokens();
        Parser parse = new Parser(tokenList);
        parse.parse();
        scanner.close();  // Close the scanner when done.
    }
}
