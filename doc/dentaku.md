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

単項の`+`は`+`による簡約です。
```
  + 1 2 3
6
```

単項の`-`はマップです。
```
  - 1 2 3
-1 -2 -3
```

`-2`の中の`-`は演算子ではなく、`2`の符号です。
```
- 1 -2 3 -> -1 2 -3
```

単項の`*`は`*`による簡約です。
```
  * 1 2 3 4
24
```
```
  a = 1 2 3 4
  a
1 2 3 4
  a > 2 filter a
3 4
```

### 偏差値の計算

＜国語のテスト＞
|A|B|C|D|E|
|-:|-:|-:|-:|-:|
|55|60|70|60|65|

＜数学のテスト＞
|A|B|C|D|E|
|-:|-:|-:|-:|-:|
|25|95|40|90|60|

```
  ave x = + x / length x
  variance x = + (x - ave x ^ 2) / length x
  sd x = sqrt variance x
  t-score x = x - ave x / sd x * 10 + 50
  kokugo = 55 60 70 60 65
  sansu = 25 95 40 90 60
  ave kokugo
62
  ave sansu
62
  variance kokugo
26
  variance sansu
746
  sd kokugo
5.099019513592785
  sd sansu
27.31300056749533
  t-score kokugo round 2
36.27 46.08 65.69 46.08 55.88
  t-score sansu round 2
36.45 62.08 41.95 60.25 49.27
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

# Late binding

`g`オペレータが`f`オペレータを参照している場合、
パース時に`f`オペレータの定義を取得するとすれば、
後から`f`オペレータの定義を変更しても
`g`オペレータの定義が変更されることはない。
しかし`g`オペレータは実行時に`"f"`の名前で`f`
オペレータを参照するので問題はない。

```
  f x = x + 1
  g x = f x + 1
  g 0
2
  f x = x + 2
  g 0
3
```
