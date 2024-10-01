```xml
<while_do> → while (<condition>) <block> | while (<condition>) <statement>


<block> → <left_terminator> [<statement_list>]<right_terminator>


<call> → <callable>{<callable>}

<callable> → <variable_id> | <function> // returns some value


<character> → | /* whitespace */ |<no_whitespace_character> 


<no_whitespace_character> → <letter> | <digit> | _ | $


<char> → ‘<character>’ 

<condition> → <comparison> {<logical_operator> <condition>} | {!}(<comparison>) {<logical_operator> <condition>}


<comparison> → <number> <comparison_operator> <number> | <number> <comparison_operator> <callable> | <callable> <comparison_operator> <number> | <callable> <comparison_operator> <callable> {!}<boolean_literal> | {!}<callable> // <callable> comparisons are not always semantically correct but they are syntactically correct



<logical_operator> → && | ||



<left_terminator> → {



<right_terminator> → }[<EOF>] // EOF marks the end of the program


<string> → “{<character>}”


<statement> → <unary> | <callable> | <control>



<statement_list> → <statement>; [<statement_list>]



<unary> → <variable_id>++ | <variable_id>--



<function> → <variable_id> ([<args>]) 



<args> → <variable> {, <variable>}
```
