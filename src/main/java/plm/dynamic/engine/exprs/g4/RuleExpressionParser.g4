parser grammar RuleExpressionParser;

options {
  language   = Java;
  tokenVocab = RuleExpressionLexer;
}

@header {
  package plm.dynamic.engine.exprs.g4;
}

ruleExpr :
  nil | bool | string | longs | digits | object | pl ruleExpr pr | not ruleExpr | minus ruleExpr |
  ruleExpr calc ruleExpr |
  ruleExpr logic ruleExpr |
  ruleExpr comps ruleExpr |
  ruleExpr qmark ruleExpr colon ruleExpr |
  function pl (ruleExpr (comma ruleExpr)*)? pr |
  ruleExpr dot method pl (ruleExpr (comma ruleExpr)*)? pr;

object : (referid point)* charact;
calc : ADD | SUB | MUL | DIV | PER;
minus : SUB;
logic : AND | OR;
bool : TRUE | FALSE;
charact : NAME;
function : NAME;
method : NAME;
referid : NAME;
string : STRING;
digits : DIGITS EXPONENT?;
longs : LONG;
pl : PT_L;
pr : PT_R;
point : DOT;
nil : NIL;
dot : DOT;
not : NOT;
colon : COLON;
qmark : QMARK;
comma : COMMA;

comps : EQ | NE | GT | LT | GE | LE;
  
