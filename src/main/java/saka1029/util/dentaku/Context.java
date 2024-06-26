package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static saka1029.util.dentaku.Value.*;
import ch.obermuhlner.math.big.BigDecimalMath;

public class Context {

    public static final BigDecimal TWO = BigDecimal.valueOf(2);
    private final Context parent;
    private final Map<String, Str<Expression>> variables = new HashMap<>();
    private final Map<String, Str<Unary>> unarys = new HashMap<>();
    private final Map<String, Str<Binary>> builtInBinarys = new HashMap<>();
    private final Map<String, Str<Binary>> binarys = new HashMap<>();
    public MathContext MC = new MathContext(200);
    public String EPSILON = "EPSILON";

    Context(Context parent) {
        this.parent = parent;
    }

    Context() {
        this(null);
    }

    public static Context of() {
        Context context = new Context();
        context.initialize();
        return context;
    }

    public Context child() {
        return new Context(this);
    }

    public boolean isVariable(String name) {
        return variables.containsKey(name) || parent != null && parent.isVariable(name);
    }

    public boolean isBinary(String name) {
        return binarys.containsKey(name) || parent != null && parent.isBinary(name);
    }

    public boolean isBuiltInBinary(String name) {
        return builtInBinarys.containsKey(name) || parent != null && parent.isBuiltInBinary(name);
    }

    public boolean isUnary(String name) {
        return unarys.containsKey(name) || parent != null && parent.isUnary(name);
    }

    public boolean isOperator(String name) {
        return isBinary(name) || isUnary(name) || parent != null && parent.isOperator(name);
    }

    public Stream<Str<String>> variables() {
        return variables.entrySet().stream()
            .map(e -> Str.of(e.getKey(), e.getValue().string));
    }

    public Str<Expression> variable(String name) {
        Str<Expression> e = variables.get(name);
        if (e != null)
            return  e;
        else if (parent != null)
            return parent.variable(name);
        else
            throw new ValueException("Undefined variable '%s'", name);
    }

    public void variable(String name, Expression e, String s) {
        if (isOperator(name))
            throw new ValueException("'%s' is defined as operator", name);
        variables.put(name, Str.of(e, s));
    }

    public Stream<Str<String>> unarys() {
        return unarys.entrySet().stream()
            .map(e -> Str.of(e.getKey(), e.getValue().string));
    }

    public Str<Unary> unary(String name) {
        Str<Unary> u = unarys.get(name);
        if (u != null)
            return u;
        else if (parent != null)
            return parent.unary(name);
        else
            throw new ValueException("Undefined unary '%s'", name);
    }

    public void unary(String name, Unary e, String s) {
        if (isVariable(name))
            throw new ValueException("'%s' is defined as variable", name);
        unarys.put(name, Str.of(e, s));
    }

    public Stream<Str<String>> binarys() {
        return Stream.of(binarys, builtInBinarys)
            .flatMap(m -> m.entrySet().stream())
            .map(e -> Str.of(e.getKey(), e.getValue().string));
    }

    public void builtInBinary(String name, Binary e, String s) {
        builtInBinarys.put(name, Str.of(e, s));
    }

    public Str<Binary> builtInBinary(String name) {
        Str<Binary> b = builtInBinarys.get(name);
        if (b != null)
            return b;
        else if (parent != null)
            return parent.builtInBinary(name);
        else
            throw new ValueException("Undefined built-in binary '%s'", name);
    }

    public void binary(String name, Binary e, String s) {
        if (isVariable(name))
            throw new ValueException("'%s' is defined as variable", name);
        binarys.put(name, Str.of(e, s));
    }

    public Str<Binary> binary(String name) {
        Str<Binary> b = binarys.get(name);
        if (b != null)
            return b;
        else if (parent != null)
            return parent.binary(name);
        else
            throw new ValueException("Undefined binary '%s'", name);
    }

    public BigDecimal[] eval(String input) {
        return Parser.parse(this, input).eval(this);
    }

    public BigDecimal epsilon() {
        return variable(EPSILON).t.eval(this)[0];
    }

    static BigDecimal[] concat(Context c, BigDecimal[] l, BigDecimal[] r) {
        BigDecimal[] result = new BigDecimal[l.length + r.length];
        System.arraycopy(l, 0, result, 0, l.length);
        System.arraycopy(r, 0, result, l.length, r.length);
        return result;
    };

    BigDecimal permutation(BigDecimal n, BigDecimal r) {
        BigInteger x = n.toBigIntegerExact();
        BigInteger y = r.toBigIntegerExact();
        if (x.compareTo(BigInteger.ZERO) < 0)
            throw new ValueException("n must not be negative but %s", x);
        if (y.compareTo(BigInteger.ZERO) < 0)
            throw new ValueException("r must not be negative but %s", y);
        if (x.compareTo(y) < 0)
            throw new ValueException("n must be grater than or equals to r but n=%s r=%s", n, r);
        BigInteger result = BigInteger.ONE;
        for (BigInteger i = x.subtract(y).add(BigInteger.ONE); i.compareTo(x) <= 0; i = i.add(BigInteger.ONE)) 
            result = result.multiply(i);
        return new BigDecimal(result);
    }

    BigDecimal gcd(BigDecimal a, BigDecimal b) {
        BigInteger x = a.toBigIntegerExact().abs();
        BigInteger y = b.toBigIntegerExact().abs();
        if (x.compareTo(y) < 0) {
            var t = x;
            x = y;
            y = t;
        }
        while (y.compareTo(BigInteger.ZERO) != 0) {
            BigInteger t = y;
            y = x.remainder(y);
            x = t;
        }
        return new BigDecimal(x);
    }

    void initialize() {
        variable("PI", c -> new BigDecimal[] {(BigDecimalMath.pi(MC))}, "PI : 円周率");
        variable("E", c -> new BigDecimal[] {(BigDecimalMath.e(MC))}, "E : 自然対数の底");
        variable("EPSILON", c -> new BigDecimal[] {dec("5E-6")}, "EPSILON : 許容誤差");
        unary("+", UnaryInsert.of(BigDecimal::add), "+ (D) -> D : 加算");
        unary("-", UnaryInsert.of(BigDecimal::subtract, BigDecimal::negate), "- (D) -> D : 減算");
        unary("*", UnaryInsert.of(BigDecimal::multiply), "* (D) -> D : 乗算");
        unary("/", UnaryInsert.of((l, r) -> l.divide(r, MC), a -> BigDecimalMath.reciprocal(a, MC)), "/ (D) -> D : 除算");
        unary("min", UnaryInsert.of(BigDecimal::min), "min (D) -> D : 最小値");
        unary("max", UnaryInsert.of(BigDecimal::max), "man (D) -> D : 最大値");
        unary("count", (c, a) -> new BigDecimal[] {dec(a.length)}, "count (D) -> I : 要素数");
        unary("int", UnaryMap.of(a -> a.setScale(0, RoundingMode.HALF_UP)), "int (D) -> (D) : 整数化(四捨五入)");
        unary("trunc", UnaryMap.of(a -> a.setScale(0, RoundingMode.DOWN)), "trunc (D) -> (D) : 整数化(切捨て)");
        unary("ceiling", UnaryMap.of(a -> a.setScale(0, RoundingMode.CEILING)), "ceiling (D) -> (D) : 整数化(無限大に向かって切り上げ)");
        unary("floor", UnaryMap.of(a -> a.setScale(0, RoundingMode.FLOOR)), "floor (D) -> (D) : 整数化(マイナス無限大に向かって切捨て)");
        unary("even?", UnaryMap.of(a -> dec(a.remainder(TWO).equals(BigDecimal.ZERO))), "even? (I) -> (B) : 偶数か？");
        unary("odd?", UnaryMap.of(a -> dec(!a.remainder(TWO).equals(BigDecimal.ZERO))), "odd? (I) -> (B) : 奇数か？");
        unary("fact", UnaryMap.of(a -> {
            BigInteger n = a.toBigIntegerExact(), r = BigInteger.ONE;
            for (BigInteger i = BigInteger.ONE; i.compareTo(n) <= 0; i = i.add(BigInteger.ONE))
                r = r.multiply(i);
            return dec(r);
        }), "fact (I) ->  (I) : 階乗");
        unary("gamma", UnaryMap.of(a -> BigDecimalMath.gamma(a, MC)), "gamma (I) -> (I) : ガンマ関数(自然数のみ計算可)");
        unary("fib", UnaryMap.of(a -> {
            BigInteger i = a.toBigIntegerExact();
            if (i.compareTo(BigInteger.ONE) <= 0)
                return a;
            BigInteger x = BigInteger.ZERO, y = BigInteger.ONE;
            while (i.compareTo(BigInteger.ONE) > 0) {
                BigInteger t = x.add(y);
                x = y;
                y = t;
                i = i.subtract(BigInteger.ONE);
            }
            return dec(y);
        }), "fib (I) -> (I) : フィボナッチ数");
        unary("minus", UnaryMap.of(BigDecimal::negate), "minus (D) -> (D) : 符号反転");
        unary("not", UnaryMap.of(a -> dec(!b(a))), "not (B) -> (B) : 否定");
        unary("reciprocal", UnaryMap.of(a -> BigDecimalMath.reciprocal(a, MC)), "reciprocal (D) -> (D) : 逆数");
        unary("abs", UnaryMap.of(a -> a.abs()), "abs (D) -> (D) : 絶対値");
        unary("sign", UnaryMap.of(a -> dec(a.signum())), "sign (D) -> (D) : 符号(-1,0,1のいずれかを返す)");
        unary("sqrt", UnaryMap.of(a -> BigDecimalMath.sqrt(a, MC).stripTrailingZeros()), "sqrt (D) -> (D) : 平方根");
        unary("square", UnaryMap.of(a -> a.multiply(a)), "square (D) -> (D) : 二乗");
        unary("cube", UnaryMap.of(a -> a.multiply(a).multiply(a)), "cube (D) -> (D) : 三乗");
        unary("sin", UnaryMap.of(a -> BigDecimalMath.sin(a, MC).stripTrailingZeros()), "sin (D) -> (D) : 正弦");
        unary("cos", UnaryMap.of(a -> BigDecimalMath.cos(a, MC).stripTrailingZeros()), "cos (D) -> (D) : 余弦");
        unary("tan", UnaryMap.of(a -> BigDecimalMath.tan(a, MC).stripTrailingZeros()), "tan (D) -> (D) : 正接");
        unary("asin", UnaryMap.of(a -> BigDecimalMath.asin(a, MC).stripTrailingZeros()), "asin (D) -> (D) : 逆正弦");
        unary("acos", UnaryMap.of(a -> BigDecimalMath.acos(a, MC).stripTrailingZeros()), "acos (D) -> (D) : 逆余弦");
        unary("atan", UnaryMap.of(a -> BigDecimalMath.atan(a, MC).stripTrailingZeros()), "log10 (D) -> (D) : 逆正接");
        unary("log10", UnaryMap.of(a -> BigDecimalMath.log10(a, MC).stripTrailingZeros()), "log10 (D) -> (D) : 常用対数");
        unary("log2", UnaryMap.of(a -> BigDecimalMath.log2(a, MC).stripTrailingZeros()), "log2 (D) -> (D) : 対数(底は2)");
        unary("log", UnaryMap.of(a -> BigDecimalMath.log(a, MC).stripTrailingZeros()), "log (D) -> (D) : 自然対数");
        unary("radian", UnaryMap.of(a -> BigDecimalMath.toRadians(a, MC).stripTrailingZeros()), "radian (D) -> (D) : 度→ラジアン");
        unary("degree", UnaryMap.of(a -> BigDecimalMath.toDegrees(a, MC).stripTrailingZeros()), "degree (D) -> (D) : ラジアン→度");
        unary("precision", UnaryMap.of(a -> dec(a.precision())), "precision (D) -> (I) : 精度");
        unary("int_precision", UnaryMap.of(a -> dec(a.precision() - a.scale())), "int_precision (D) -> (I) : 整数部桁数");
        unary("scale", UnaryMap.of(a -> dec(a.scale())), "scale (D) -> (I) : 小数点以下桁数");
        unary("factor", (c, a) -> {
            if (a.length != 1)
                throw new ValueException("Illegal count=%d", a.length);
            BigInteger d = a[0].toBigIntegerExact().abs();
            if (d.equals(BigInteger.ZERO))
                throw new ValueException("Cannot factor zero");
            List<BigDecimal> result = new ArrayList<>();
            BigInteger max = d.sqrt();
            for (BigInteger f = BigInteger.TWO; f.compareTo(max) <= 0; f = f.add(BigInteger.ONE)) {
                while (true) {
                    BigInteger[] r = d.divideAndRemainder(f);
                    if (!r[1].equals(BigInteger.ZERO))
                        break;
                    d = r[0];
                    result.add(new BigDecimal(f));
                }
            }
            if (!d.equals(BigInteger.ONE))
                result.add(new BigDecimal(d));
            return result.toArray(BigDecimal[]::new);
        }, "factor I -> (I) : 素因数");
        unary("divisor", (c, a) -> {
            if (a.length != 1)
                throw new ValueException("Illegal count=%d", a.length);
            BigInteger d = a[0].toBigInteger().abs();
            Set<BigInteger> set = new HashSet<>();
            if (d.equals(BigInteger.ZERO))
                set.add(BigInteger.ZERO);
            else 
                for (BigInteger i = d.sqrt(); i.compareTo(BigInteger.ZERO) > 0; i = i.subtract(BigInteger.ONE)) {
                    BigInteger[] x = d.divideAndRemainder(i);
                    if (x[1].equals(BigInteger.ZERO)) {
                        set.add(i);
                        set.add(x[0]);
                    }
                }
            return set.stream().sorted().map(i -> new BigDecimal(i)).toArray(BigDecimal[]::new);
        }, "divisor I -> (I) : 約数");
        unary("prime?", UnaryMap.of(a -> {
            BigInteger i = a.toBigIntegerExact();
            int comp2 = i.compareTo(BigInteger.TWO);
            if (comp2 < 0)
                return FALSE;
            else if (comp2 == 0)
                return TRUE;
            BigInteger max = i.sqrt();
            for (BigInteger d = BigInteger.TWO; d.compareTo(max) <= 0; d = d.add(BigInteger.ONE))
                if (i.remainder(d).equals(BigInteger.ZERO))
                    return FALSE;
            return TRUE;
        }), "prime (I) -> (B) : 素数判定");
        builtInBinary("+", BinaryMap.of(BigDecimal::add), "(D) + (D) -> (D) : 加算");
        builtInBinary("-", BinaryMap.of(BigDecimal::subtract), "(D) - (D) -> (D) : 減算");
        builtInBinary("*", BinaryMap.of(BigDecimal::multiply), "(D) * (D) -> (D) : 乗算");
        builtInBinary("/", BinaryMap.of((l, r) -> l.divide(r, MC)), "(D) / (D) -> (D) : 除算");
        builtInBinary("%", BinaryMap.of((l, r) -> l.remainder(r, MC)), "(D) % (D) -> (D) : 剰余");
        builtInBinary("^", BinaryMap.of((l, r) -> BigDecimalMath.isIntValue(r) ?
                BigDecimalMath.pow(l, r.longValue(), MC) :
                BigDecimalMath.pow(l, r, MC).stripTrailingZeros())
            , "(D) ^ (D) -> (D) : べき乗");
        builtInBinary("=", BinaryMap.of((l, r) -> dec(l.compareTo(r) == 0)), "(D) == (D) -> (B) : 等しい");
        builtInBinary("!=", BinaryMap.of((l, r) -> dec(l.compareTo(r) != 0)), "(D) != (D) -> (B) : 等しくない");
        builtInBinary("<", BinaryMap.of((l, r) -> dec(l.compareTo(r) < 0)), "(D) < (D) -> (B) : 小さい");
        builtInBinary("<=", BinaryMap.of((l, r) -> dec(l.compareTo(r) <= 0)), "(D) <= (D) -> (B) : 小さいか等しい");
        builtInBinary(">", BinaryMap.of((l, r) -> dec(l.compareTo(r) > 0)), "(D) > (D) -> (B) : 大きい");
        builtInBinary(">=", BinaryMap.of((l, r) -> dec(l.compareTo(r) >= 0)), "(D) >= (D) -> (B) : 大きいか等しい");
        builtInBinary("~", BinaryMap.of((l, r) -> dec(l.subtract(r).abs().compareTo(epsilon()) <= 0)), "(D) ~ (D) -> (B) : ほぼ等しい");
        builtInBinary("!~", BinaryMap.of((l, r) -> dec(l.subtract(r).abs().compareTo(epsilon()) > 0)), "(D) !~ (D) -> (B) : ほぼ等しくない");
        builtInBinary("and", BinaryMap.of((l, r) -> dec(b(l) && b(r))), "(B) and (B) -> (B) : 論理積");
        builtInBinary("or", BinaryMap.of((l, r) -> dec(b(l) || b(r))), "(B) or (B) -> (B) : 論理和");
        builtInBinary("xor", BinaryMap.of((l, r) -> dec(b(l) ^ b(r))), "(B) xor (B) -> (B) : 排他的論理和");
        builtInBinary(",", (c, l, r) -> {
            BigDecimal[] result = new BigDecimal[l.length + r.length];
            System.arraycopy(l, 0, result, 0, l.length);
            System.arraycopy(r, 0, result, l.length, r.length);
            return result;
        }, "(D) , (D) -> (D) : 連結");
        binary("log", BinaryMap.of((l, r) -> BigDecimalMath.log(r, MC).divide(BigDecimalMath.log(l, MC), MC)), "(D) log (D) -> (D) : 対数");
        binary("imply", BinaryMap.of((l, r) -> dec(!b(l) || b(r))), "(B) imply (B) -> (B) : 含意");
        binary("min", BinaryMap.of(BigDecimal::min), "(D) min (D) -> (D) : 小さい方");
        binary("max", BinaryMap.of(BigDecimal::max), "(D) max (D) -> (D) : 大きい方");
        binary("round", BinaryMap.of((l, r) -> l.setScale(i(r), RoundingMode.HALF_UP)), "(D) round I -> (D) : 指定桁数に四捨五入");
        binary("to", (c, l, r) -> {
            if (l.length != 1 || r.length != 1)
                throw new ValueException("Invalid arguments left=%s right=%s", str(l), str(r));
            BigInteger ll = l[0].toBigIntegerExact(), rr = r[0].toBigIntegerExact();
            BigDecimal[] result = array(ll.subtract(rr).abs().intValue() + 1);
            int index = 0;
            if (ll.compareTo(rr) <= 0)
                for (BigInteger i = ll; i.compareTo(rr) <= 0; i = i.add(BigInteger.ONE))
                    result[index++] = dec(i);
            else
                for (BigInteger i = ll; i.compareTo(rr) >= 0; i = i.subtract(BigInteger.ONE))
                    result[index++] = dec(i);
            return result;
        }, "I to I -> (I) : 範囲");
        binary("filter", (c, l, r) -> {
            if (l.length != r.length)
                throw new ValueException("Invalid arguments left=%s right=%s", str(l), str(r));
            List<BigDecimal> result = new ArrayList<>();
            for (int i = 0, max = l.length; i < max; ++i)
                if (b(l[i]))
                    result.add(r[i]);
            return result.toArray(BigDecimal[]::new);
        }, "(B) filter (D) -> (D) : フィルター");
        binary("P", BinaryMap.of((n, r) -> {
            return permutation(n, r);
        }), "(I) P (I) -> (I) : 順列");
        binary("C", BinaryMap.of((n, r) -> {
            r = r.min(n.subtract(r));
            BigDecimal den = permutation(n, r);
            BigDecimal num = BigDecimal.ONE;
            for (BigDecimal i = r; i.compareTo(BigDecimal.ONE) > 0; i = i.subtract(BigDecimal.ONE))
                num = num.multiply(i);
            return den.divide(num);
        }), "(I) C (I) -> (I) : 組合せ");
        binary("gcd", BinaryMap.of((a, b) -> {
            return gcd(a, b);
        }), "(D) gcd (D) -> (D) : 最大公約数");
        binary("lcm", BinaryMap.of((a, b) -> {
            return a.multiply(b).divide(gcd(a, b));
        }), "(D) lcm (D) -> (D) : 最小公倍数");
        binary("base", (c, l, r) -> {
            int lsize = l.length, rsize = r.length;
            if (lsize != 1)
                throw new ValueException("Illegal left length=%d", l.length);
            BigInteger left = l[0].toBigIntegerExact().abs();
            if (rsize == 0)
                throw new ValueException("Illegal right length=%d", rsize);
            List<BigInteger> result = new LinkedList<>();
            if (rsize == 1) {
                BigInteger base = r[0].toBigIntegerExact().abs();
                while (left.compareTo(BigInteger.ZERO) > 0) {
                    BigInteger[] dr = left.divideAndRemainder(base);
                    left = dr[0];
                    result.add(0, dr[1]);
                }
                if (result.size() == 0)
                    result.add(BigInteger.ZERO);
            } else {
                for (int i = rsize - 1; i >= 0; --i) {
                    BigInteger[] dr = left.divideAndRemainder(r[i].toBigIntegerExact().abs());
                    left = dr[0];
                    result.add(0, dr[1]);
                }
                if (left.compareTo(BigInteger.ZERO) > 0)
                    result.add(0, left);
            }
            return result.stream().map(i -> dec(i)).toArray(BigDecimal[]::new);
        }, "I base (I) -> (I) : 進数変換");
        binary("decimal", (c, l, r) -> {
            int lsize = l.length, rsize = r.length;
            BigDecimal result = BigDecimal.ZERO;
            if (rsize == 1) {
                BigDecimal base = r[0].abs();
                for (int i = 0; i < lsize; ++i)
                    result = result.multiply(base).add(l[i].abs());
            } else if (lsize == rsize) {
                for (int i = 0; i < rsize; ++i)
                    result = result.multiply(r[i].abs()).add(l[i].abs());
            } else if (lsize == rsize + 1) {
                result = l[0];
                for (int i = 0; i < rsize; ++i)
                    result = result.multiply(r[i].abs()).add(l[i + 1].abs());
            } else
                throw new ValueException("Illegal length left=%d rigth=%d", lsize, rsize);
            return new BigDecimal[] {result};
        }, "(I) decimal (I) -> I : 10進変換");
        eval("ave x : + (x / count x)");
        eval("variance x : + ((x - ave x) ^ 2) / count x");
        eval("sd x : sqrt variance x");
        eval("standard_score x : (x - ave x) / sd x * 10 + 50");
        eval("pascal n : n C (0 to n)");
        eval("c poly x : + (x ^ (count c - 1 to 0) * c)");
        eval("a distance b : sqrt + ((a - b) ^ 2)");
        // eval("a days b = days b - days a");
        eval("fibonacci n : 1 + sqrt 5 / 2 ^ n - ((1 - sqrt 5 / 2) ^ n) / sqrt 5");
    }

    public int solve(Expression expression, Consumer<String> out) {
        if (!(expression instanceof ExpressionVars ev))
            throw new ValueException("Cannot solve '%s'", expression);
        List<BigDecimal[]> values = ev.variables.stream()
            .map(v -> this.variable(v).t.eval(this))
            .toList();
        Context child = this.child();
        return new Object() {
            int count = 0;

            void test() {
                BigDecimal[] v = expression.eval(child);
                if (v.length < 0 || !b(v[0]))
                    return;
                ++count;
                String result = ev.variables.stream()
                    .map(n -> n + "=" + str(child.variable(n).t.eval(child)))
                    .collect(Collectors.joining(" "));
                out.accept(result);
            }

            int solve(int index) {
                if (index >= ev.variables.size())
                    test();
                else {
                    BigDecimal[] elements = values.get(index);
                    for (int i = 0; i < elements.length; ++i) {
                        int ii = i;
                        String name = ev.variables.get(index);
                        child.variable(name, x -> new BigDecimal[] {elements[ii]}, name);
                        solve(index + 1);
                    }
                }
                return count;
            }
        }.solve(0);
    }
}
