## dentaku

## SYNTAX1

```
expression = unary { BOP unary }
unary      = sequence
           | UOP unary
           | MOP BOP unary
sequence   = primary { primary }
primary    = '(' expression ')'
           | ID
           | NUMBER
```

BOPは二項演算子、UOPは単項演算子である。
MOPは二項演算子から単項演算子を作る高解関数である。

```
insert + iota 4
-> 10
```

```
accumulate + iota 4
-> 1 3 6 10
```

```
accumulate + 1 1 1 1
-> 1 2 3 4
```

```
ones 4
-> 1 1 1 1
```

```
accumulate + ones 4
-> 1 2 3 4
```