# dentaku

## syntax

```
statement       = define-variable
                | define-unary
                | define-binary
                | expression
define-variable = ID '=' expression
define-unary    = ID ID '=' expression
define-binary   = ID ID ID '=' expression
expression      = binary { ',' binary }
binary          = comp { BOP comp }
comp            = add { ( '==' | '!=' | '<' | '<=' | '>' | '>=' | '~' | '!~' ) add }
add             = mult { ( '+' | '-' ) mult }
mult            = power { ( '*' | '/' | '%' ) power }
power           = unary [ '^' power ]
unary           = primary | [ '@' ] UOP unary
primary         = '(' expression ')'
                | VAR
                | NUMBER
```


```
ID        = ID-FIRST { ID-REST }
ID-FIRST  = JAVA-ALPHABETIC | '_'
ID-FIRST  = ID-FIRST | JAVA-DIGIT | '.'
COMP      = '==' | '!=' | '<' | '<=' | '>' | '>=' | '~' | '!~'
ADD       = '+' | '-'
MULT      = '*' | '/' | '%'
POWER     = '^'
BOP       = ID | SPECIAL
UOP       = ID | SPECIAL
VAR       = ID
NUMBER    = [ '-' ] DIGITS
            [ '.' DIGITS ]
            [ ( 'e' | 'E' ) [ '+' | '-' ] DIGITS ]
DIGITS    = DIGIT { DIGIT }
DIGIT     = '0' .. '9'
```

## UOP

単項演算子

## BOP

二項演算子

## 評価例

### `+`と`-`

単項の`+`は`+`による簡約です。
```
  + 1 2 3
6
```

単項の`-`は以下のように動作します。
```
  - 1 2
-1
```
```
  - 1 2 3
-4
```
```
  - 1
-1
```

### 負の数値

負の数値を表現するためには数値定数の前に
単項演算子`-`を付与する必要があります。
`-`が二項演算子と解釈されないようにするために、
括弧で囲む必要があります。

```
1 (-2) 3 -> 1 -2 3
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

### 括弧を減らす工夫

$$\sqrt{{11^4+100^4+111^4}\over 2}
$$

```
  sqrt + (11 100 111 ^ 4 / 2)
11221
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
  ave x = + x / count x
  variance x = + (x - ave x ^ 2) / count x
  sd x = sqrt variance x
  standardScore x = x - ave x / sd x * 10 + 50
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
  standardScore kokugo round 2
36.27 46.08 65.69 46.08 55.88
  standardScore sansu round 2
36.45 62.08 41.95 60.25 49.27
```

### 2点間の距離

平面上の2点$(a_x,a_y)$と$(b_x,b_y)$の距離は次式で計算できます。

$$\sqrt{(a_x-b_x)^2+(a_y-b_y)^2}$$

$(0,0)$と$(1,1)$の距離はは以下のように計算できます。

```
  a distance b = sqrt + (a - b ^ 2)
  0 0 distance 1 1
1.414213562373095
```
この`distance`の定義は3次元以上の距離も同様に計算できます。

```
  0 0 0 distance 1 1 1
1.732050807568877
```

### フィボナッチ数列の一般項

WikiPediaによるとフィボナッチ数列の一般項は以下のようになります。
これは以下のように計算できます。

$$F_n
= \frac{1}{\sqrt{5}} \left\{ \left( \frac{1+\sqrt{5}}{2} \right)^n - \left( \frac{1-\sqrt{5}}{2} \right)^n \right\}
$$

```
  fib n = 1 + sqrt 5 / 2 ^ n - (1 - sqrt 5 / 2 ^ n) / sqrt 5
  int fib (0 to 10)
0 1 1 2 3 5 8 13 21 34 55
```

引数に自然数を与えても結果は必ずしも自然数にならないので`int`で結果を丸めています。
`round`を使って丸めることもできます。
`round`の後の`0`は丸めた後の小数点以下の桁数です。

```
  fib (0 to 10) round 0
0 1 1 2 3 5 8 13 21 34 55
```

### πの計算

$tan^{-1}$を求めるライプニッツ (Leibniz)の式は以下のとおりです。

$$arctan\:1 = {\pi \over 4} = \sum_{n=0}^\infin {(-1)^{n} \over {2n+1}}$$

合計する項の数を増やすしながら計算すると以下のようになります。

```
 term n = -1 ^ n / (2 * n + 1)
  pi n = + term (0 to n) * 4
  pi 9
3.04183961892940212
  pi 99
3.131592903558552732
  pi 999
3.1405926538397929396
  pi 9999
3.14149265359004322800
  pi 99999
3.141582653589793475400
```
おおよそ項の数が一桁増えると精度が一桁上がることがわかります。

### シグマ

$$
\sum_{n=1}^{40000}{ 1\over{\sqrt n}}
$$

逆数を求める単項演算子`/`を使って以下のように求めることができる。

```
  + / sqrt (1 to 40000)
398.542145485982080297
```

### ソルバー

問題：$a b c d e f = a+b+c+d+e+f$を満たす自然数の組は何通りか。

`.solve 式`は`式`を満たす変数の値の組を見つけて表示する。

```
  a = 1 to 10
  b = 1 to 10
  c = 1 to 10
  d = 1 to 10
  e = 1 to 10
  f = 1 to 10
  .solve * a b c d e f == + a b c d e f
a=1 b=1 c=1 d=1 e=2 f=6
a=1 b=1 c=1 d=1 e=6 f=2
a=1 b=1 c=1 d=2 e=1 f=6
a=1 b=1 c=1 d=2 e=6 f=1
a=1 b=1 c=1 d=6 e=1 f=2
a=1 b=1 c=1 d=6 e=2 f=1
a=1 b=1 c=2 d=1 e=1 f=6
a=1 b=1 c=2 d=1 e=6 f=1
a=1 b=1 c=2 d=6 e=1 f=1
a=1 b=1 c=6 d=1 e=1 f=2
a=1 b=1 c=6 d=1 e=2 f=1
a=1 b=1 c=6 d=2 e=1 f=1
a=1 b=2 c=1 d=1 e=1 f=6
a=1 b=2 c=1 d=1 e=6 f=1
a=1 b=2 c=1 d=6 e=1 f=1
a=1 b=2 c=6 d=1 e=1 f=1
a=1 b=6 c=1 d=1 e=1 f=2
a=1 b=6 c=1 d=1 e=2 f=1
a=1 b=6 c=1 d=2 e=1 f=1
a=1 b=6 c=2 d=1 e=1 f=1
a=2 b=1 c=1 d=1 e=1 f=6
a=2 b=1 c=1 d=1 e=6 f=1
a=2 b=1 c=1 d=6 e=1 f=1
a=2 b=1 c=6 d=1 e=1 f=1
a=2 b=6 c=1 d=1 e=1 f=1
a=6 b=1 c=1 d=1 e=1 f=2
a=6 b=1 c=1 d=1 e=2 f=1
a=6 b=1 c=1 d=2 e=1 f=1
a=6 b=1 c=2 d=1 e=1 f=1
a=6 b=2 c=1 d=1 e=1 f=1
number of solutions=30
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

# 遅延評価

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

# 機能拡張

## フィルター

### 単項フィルター

`@UNARY V`で`V`にフィルターを掛ける。
`V`のうち`UNARY 要素`がゼロ以外を返したものだけを
選択する。

文法を以下のように変更する。
```
unary           = sequence
                | [ '@' ] UOP unary
```
そもそも`-`は`map -`の省略形であり、
`+`は`reduce +`の省略形であった。
`map +`は意味のない操作であり、
`@ +`も同様に意味のない操作であるため、
規定の操作として`reduce +`を選択するのは
妥当であると思われる。

### 二項フィルター
`V @ BINARY W`で`V`または`W`にフィルターを掛ける。

```
  1 2 3 4 @< 3
1 2
```

```
  3 @> 1 2 3 4
1 2
```


## 二項演算子の単項演算子化（未実装）

以下のように定義すればできるが、
定義無しでできるようにする。

```
positive x = x > 0
```

```
select (> 0) -1 0 1 2
-> 1 2
```

あるいは

```
select (0 <) -1 0 1 2
-> 1 2
```

## ソルバー

### 実行例1
変数`a`を1から5、変数`b`を0から10まで
変えながら式$a ^ 2 + b ^ 2 = 25$を評価し、
真(ゼロ以外)の場合に変数`a`および`b`の値を出力する。
```
  a = 1 to 5
  b = 0 to 10
  .solve (a ^ 2) + (b ^ 2) == 25
a=3 b=4
a=4 b=3
a=5 b=0
number of solutions=3
```

あるいは

```
  a = 1 to 5
  b = 0 to 10
  .solve + (a b ^ 2) == 25
a=3 b=4
a=4 b=3
a=5 b=0
number of solutions=3
```

### 実行例2

$$
{3 \over a} + {5 \over b} + {7 \over c}
$$
が整数となる素数$a, b, c$をすべて求めよ。

```
  isint n = int n == n
  a = @prime (1 to 40)
  b = @prime (1 to 40)
  c = @prime (1 to 40)
  .solve isint + (3 5 7 / a b c)
a=2 b=2 c=7
a=2 b=5 c=2
a=3 b=2 c=2
a=3 b=3 c=3
a=3 b=5 c=7
a=5 b=5 c=5
number of solutions=6
```

### 実行例3

$2^{p-1}-1 = pq^2$を満たす$p, q$をすべて求めよ。
ただし$p,q$は異なる素数とする。

```
  p = @prime (1 to 1000)
  q = @prime (1 to 1000)
  .solve 2 ^ (p - 1) - 1 == (q ^ 2 * p) and (p != q)
p=7 q=3
number of solutions=1
```
`and (p != q)`がなくても同じ結果になる。

### 実行例4

$${_{2n}C_n\over{n+1}}
$$
が素数のとき、
正の整数$n$をすべて求めよ。

```
  n = 1 to 100
  .solve prime (2 * n C n / (n + 1))
n=2
n=3
number of solutions=2
```

### 実行例5

$(x^3-x)^2(y^3-y)=86400$を満たす整数$x, y$の組をすべて求めよ。

```
  x = -100 to 100
  y = -100 to 100
  .solve x ^ 3 - x ^ 2 * (y ^ 3 - y) == 86400
x=-5 y=2
x=-4 y=3
x=4 y=3
x=5 y=2
number of solutions=4
```

あるいは

```
  x = -100 to 100
  y = -100 to 100
  f x = x ^ 3 - x
  .solve f x ^ 2 * f y == 86400
x=-5 y=2
x=-4 y=3
x=4 y=3
x=5 y=2
number of solutions=4
```

単項演算子`f`の定義内で変数`x`を使用している点に注意する。
この変数は`.solve`における式内の`x`とは区別されている。

### 実行例6

以下を満たす素数$p,q,r$を求めよ。

$$
3p^4-5q^4-4r^2=26
$$
```
  p = @prime (1 to 100 )
  q = @prime (1 to 100 )
  r = @prime (1 to 100 )
  .solve + (p q r ^ 4 4 2 * 3 -5 -4) == 26
p=5 q=3 r=19
number of solutions=1
```

### 実行例7

2桁の整数$m$を2乗すると下2桁が36になる。$m$をすべて求めよ。

```
  m = 10 to 99
  .solve m ^ 2 % 100 == 36
m=44
m=56
m=94
number of solutions=3
```

ソルバーを使わずに求めることもできる。

```
  m = 10 to 99
  m ^ 2 % 100 == 36 filter m
44 56 94
```

### 実行例8

以下の式を満たす自然数$a, b, c$を求めよ。

$$
a ^ 2 + b ^ 4 + c ^ 8 = 328
$$

```
  a =  1 to 100
  b =  1 to 100
  c =  1 to 100
  .solve + (2 4 8 ^ a b c) ==328
a=3 b=4 c=2
a=6 b=4 c=1
a=8 b=3 c=1
number of solutions=3
```

### 実行例9

$p$が素数のとき、以下の式を満たす$m, n$を求めよ。

$$
5 ^ n - 3 ^ n = p ^ m
$$
```
  p = @prime (1 to 100)
  n = 1 to 100
  m = 1 to 100
  .solve 5 ^ n - (3 ^ n) == (p ^ m)
m=1 n=1 p=2
m=4 n=2 p=2
number of solutions=2
```
自然数$n$の約数は4個でその和は84である。

### 実行例10

```
  n = 1 to 100
  .solve count divisor n == 4 and (+ divisor n == 84)
n=65
number of solutions=1
```
# Schemeの単項演算子


加算および乗算の場合

```
(+ 3 4)                     =>  7
(+ 3)                       =>  3
(+)                         =>  0
(* 4)                       =>  4
(*)                         =>  1
```

減算および除算の場合
```
(- 3 4)                     =>  -1
(- 3 4 5)                   =>  -6
(- 3)                       =>  -3
(/ 3 4 5)                   =>  3/20
(/ 3)                       =>  1/3
```

# 連結二項演算子の導入

`,`は連結の二項演算子です。
現在、

$$
x^2 - 4x - 15
$$

は以下のように表現できます。

```
1 (-4) (-15) poly x 
```

`,`演算子を使うことによって

```
1, -4, -15 poly x
```

と表現することができるようになります。

`,`は二項演算子なので連結を優先する必要はなくなります。

演算子の優先順位は

unary -> binary

となります。
`,`演算子は二項演算子のひとつとなります。


