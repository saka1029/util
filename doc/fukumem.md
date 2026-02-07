# fukumen-zan

## syntax

```
expression  = or-expr
or-expr     = and-expr { '|' and-expr }
and-expr    = comp-expr { '&' comp-expr }
comp-expr   = add-expr [ COMP add-expr ]
COMP        = '=' | '!=' | '<' | <=' | '>' | '>='
add-expr    = mult-expr { ('+' | '-') mult-expr }
mult-expr   = primary { ('*' | '/') primary }
primary     = '(' expression ')' | VARIABLE
```
