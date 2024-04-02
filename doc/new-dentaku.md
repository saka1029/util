# 新dentaku

## 字句解析

```
LP       = '('
RP       = ')'
ID       = ID-FIRST { ID-REST }
ID-FIRST = JAVA-ALPHABETIC | '_'
ID-REST  = ID-FIRST | JAVA-DIGIT | '-' | '.'
SPECIAL  = '+' | '-' | '*' | '/' | '%' | '^'
         | '==' | '!=' | '<' | '<=' | '>' | '>='
         | '~' | '!~'
         | '@'
NUMBER   = [ '-' ] DIGITS
           [ '.' DIGITS]
           [ ( 'e' | 'E') [ '+' | '-' ] DIGITS ]
DIGITS   = DIGIT { DIGIT }
DIGIT    = '0' | '1' | '2' | '3' | '4'
         | '5' | '6' | '7' | '8' | '9'
```

## 識別子
ユーザー定義の識別子（オペレーターおよび変数）
は基本的に特殊文字を含まない形にする。

```
id = first { rest }
first = 特殊文字数字以外 | '_'
rest  = first | 数字 | '.' | '-' 
```

## 特殊文字のオペレーター

特殊文字のオペレーターは以下に限定する。
ユーザー定義はできない。

`--3`は単項演算子`-`と数値`-3`の並びと解釈する。

|オペレーター|種類|
|-|-|
|`+` `-` `*` `/` `%` `^`|算術演算|
|`==` `!=` `<` `<=` `>` `>=` `~` `!~`|比較演算|

## 単項演算子

特殊文字オペレーターの単項演算子は以下の４種類とする。
'%'と'^'はない点に注意する。

|オペレーター|種類|集約方法|
|-|-|-|
|`+` |和|reduce|
|`-` |符号反転|map|
|`*` |積|reduce|
|`/` |逆数|map|

## ２項演算子

|オペレーター|種類|
|-|-|
|`+` |加算|
|`-` |減算|
|`*` |乗算|
|`/` |除算|
|`%` |剰余|
|`^` |べき乗|
|`==` `!=` `<` `<=` `>` `>=` `~` `!~`|比較演算|

# 数値定数

数値定数の文法は以下の通り。

```
number = [ '-' ]
         digits
         [ '.' digits ]
         [ ( 'e' | 'E' ) [ '+' | '-' ] digits ]
```

# select演算

並びの中から特定の値のみを抽出する。

## 単項演算子の例

偶数を抽出する。

```
  odd n = n % 2
  even n = not odd n
  even (0 to 5)
1 0 1 0 1 0
  @even (0 to 5)
0 2 4
```
## ２項演算子の例

２以上の要素を抽出する。

```
  0 to 5 > 2
0 0 0 1 1 1
  0 to 5 @> 2
3 4 5
  2 < (0 to 5)
0 0 0 1 1 1
  2 @< (0 to 5)
3 4 5
```

## toがネストできない問題の解決

```
  f n = * (1 to n)
  f 10
3628800
  f (1 to 10)
One element expected but '1 2 3 4 5 6 7 8 9 10'
```

`f`の引数として数値の並びが渡されたとき、
エラーとなってしまう。
これは `f`を実行するとき`(1 to 20)`をそのまま
渡しているためである。

`UnaryCall`の`apply()`は以下のようになっている。

```
public Value apply(Context context, Value argument) {
    Context child = context.child();
    child.variable(variable, argument, variable);
    return body.eval(child);
}
```

これはあたかも`f`の引数が変数`n`にバインドされているかのように
評価を行っているに等しい。

単項演算子の定義`f n = * (1 to n)`を
`UnaryOperator<BigDecimal>`に変換できれば
`Value.map(UnaryOperator<BigDecimal>)`を使うことができる。

```
@Override
public Value apply(Context context, Value argument) {
    Context child = context.child();
    return argument.map(a -> {
        child.variable(variable, Value.of(a), variable);
        return body.eval(child).oneElement();
    });
}
```

無駄にValueとBigDecimalの相互変換を行っているが、
とりあえず動作する。

```
  f n = * (1 to n)
  f (1 to 10)
1 2 6 24 120 720 5040 40320 362880 3628800
```

ただし、今度は複数の要素をいっぺんに受け取りたい`ave`
などが動作しなくなる。

```
  ave 1 2 3 4
1 2 3 4
```