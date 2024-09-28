```xml
<while-do> → while (<condition>) <block> | while (condition) <statement>


<boolean_literal> → true | false 


<block> → <left_terminator> [<statement_list>]<right_terminator>


<call> → <callable>{<callable>}

<callable> → <variable_id> | <function> // returns some value


<character> → | /* whitespace */ |<no_whitespace_character> 


<no_whitespace_character> → <letter> | <digit> | _ | $


<char> → ‘<character>’ 

<condition> → <comparison> {<logical_operator> <condition>} | {!}(<comparison>) {<logical_operator> <condition>}


<comparison> → <number> <comparison_operator> <number> | <number> <comparison_operator> <callable> | <callable> <comparison_operator> <number> | <callable> <comparison_operator> <callable> {!}<boolean_literal> | {!}<callable> // <callable> comparisons are not always semantically correct but they are syntactically correct


<comparison_operator> → != | > | < | >= | <=

<control> → return | break | continue



<single_line_comment> → // {<character>}



<multi_line_comment> → /* {<character>} */ 



<digit> → 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9



<double> → <integer>.<integer> | .<integer>



<integer> → <digit>{<digit>} 



<float> → <double>f | <double>F



<number> → <float> | <integer> | <double>



<logical_operator> → && | ||



<left_terminator> → {



<right_terminator> → }[<EOF>] // EOF marks the end of the program



<variable_id> → _{<no_whitespace_character>} | <letter>{<no_whitespace_character>} | ${no_whitespace_character>}



<string> → “{<character>}”



<letter> → a | b | c | d | e | f | g | h | i | j | k | l | m | n | o | p | q | r | s | t | u | v | w | x | y | z | A | B | C | D | E | F | G | H | I | J | K | L | M | N | O | P | Q | R | S | T | U | V | W | X | Y | Z



<statement> → <unary> | <callable> | <control>



<statement_list> → <statement>; [<statement_list>] | <comment> [<statement_list>]



<unary> → <variable_id>++ | <variable_id>--



<function> → <variable_id> ([<args>]) 



<args> → <variable> {, <variable>}
```
