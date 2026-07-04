lexer grammar RuleExpressionLexer;

options {
  language = Java;
}

@header {
package plm.dynamic.engine.exprs.g4;
}

TRUE : 'true';
FALSE : 'false';
NIL : 'null';

PT_L : '(';
PT_R : ')';
DOT : '.';
QUOTE : '"';

ADD : '+';
SUB : '-';
MUL : '*';
DIV : '/';
PER : '%';
QMARK : '?';
COMMA : ',';
COLON : ':';

EQ : '==';
NE : '!=';
GT : '>';
LT : '<';
GE : '>=';
LE : '<=';

AND : '&&';

OR : '||';

NOT : '!';

LONG : DIGIT+ L;

NAME : CHAR (CHAR | DIGIT | '_' | '$')*;

DIGITS : DIGIT+ ('.' DIGIT+)?;

STRING : '"' (ESC | .)*? '"';

EXPONENT : E ('+'|'-')? DIGIT+;

fragment ESC : '\\' [btnr"\\]; // \b, \t, \n, \r etc
 
fragment CHAR : [a-zA-Z];

fragment DIGIT : [0-9];

//The following chars will be skipped;
WS : [ \r\t\n]+ -> skip ;

fragment A: ('a'|'A');
fragment B: ('b'|'B');
fragment C: ('c'|'C');
fragment D: ('d'|'D');
fragment E: ('e'|'E');
fragment F: ('f'|'F');
fragment G: ('g'|'G');
fragment H: ('h'|'H');
fragment I: ('i'|'I');
fragment J: ('j'|'J');
fragment K: ('k'|'K');
fragment L: ('l'|'L');
fragment M: ('m'|'M');
fragment N: ('n'|'N');
fragment O: ('o'|'O');
fragment P: ('p'|'P');
fragment Q: ('q'|'Q');
fragment R: ('r'|'R');
fragment S: ('s'|'S');
fragment T: ('t'|'T');
fragment U: ('u'|'U');
fragment V: ('v'|'V');
fragment W: ('w'|'W');
fragment X: ('x'|'X');
fragment Y: ('y'|'Y');
fragment Z: ('z'|'Z');
