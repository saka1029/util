# dentaku

## syntax

```
statement       = define-variable
                | define-unary
                | define-binary
                | expression
define-variable = ID '=' expression
define-unary    = ID-SPECIAL ID '=' expression
define-binary   = ID ID-SPECIAL ID '=' expression
expression      = unary { BOP unary }
unary           = sequence
                | UOP unary
                | HOP BOP unary
sequence        = primary { primary }
primary         = '(' expression ')'
                | VAR
                | NUMBER { NUMBER }
```


```
ID              = JAVA-ALPHA { JAVA-ALPHA | JAVA-DIGIT}
SPECAIL         = SP { SP }
ID-SPECIAL      = ID | SPECIAL
BOP             = ID | SPECIAL
UOP             = ID | SPECIAL
HOP             = ID | SPECIAL
VAR             = ID
NUMBER = DIGITS
         [ '.' DIGITS ]
         [ ( 'e' | 'E' ) [ '+' | '-' ] DIGITS ]
DIGITS = DIGIT { DIGIT }
DIGIT  = '0' .. '9'
```

## UOP

単項演算子

## BOP

二項演算子

## HOP

高階演算子

二項演算子と組み合わせて使用する。
`@`は高階演算子の一種で、列に二項演算子を挿入します。

```
@ * 1 2 3 4
-> 1 * 2 * 3 * 4
-> 24
@ + 1 2 3 4
-> 1 + 2 + 3 + 4
-> 10
```

ただし単項演算子`+`および`*`は`@ +`および`@ *`として
定義されているので、以下のように記述できます。

```
* 1 2 3 4
-> 24
+ 1 2 3 4
-> 10
```


## 評価例

```
+ 1 2 3 -> 6
- 1 2 3 -> -1 -2 -3
- 1 -2 3 -> -1 2 -3
* 1 2 3 4 -> 24
1 2 3 + 4 5 6 -> 5 7 9
a = 1 2 3 4 -> NaN
a -> 1 2 3 4
a > 2 filter a -> 3 4
```

## 優先順位

1. 暗黙の連結（左優先）  
  数値、変数、括弧で囲った式またはそれらに
  単項演算子を付与したものの並びは
  連結した列となります。
1. 単項演算子（右優先）  
  単項演算子は右から順に評価されます。
  例えば`sin - PI`は`sin (- PI)`の意味になります。
1. 二項演算子（左優先）  
  二項演算子間に優先順位はありません。
  `1 + 2 * 3`は`(1 + 3) * 2`と解釈されます。

