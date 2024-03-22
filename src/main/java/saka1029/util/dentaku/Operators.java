package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operators {
    static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    private Map<String, Str<Unary>> uops = new HashMap<>();
    private Map<String, Str<Binary>> bops = new HashMap<>();
    private Map<String, Str<High>> hops = new HashMap<>();

    private Operators() {
        initialize();
    }

    public static Operators of() {
        return new Operators();
    }

    public Unary unary(String name) {
        Str<Unary> e = uops.get(name);
        return e != null ? e.op : null;
    }

    public String unaryString(String name) {
        Str<Unary> e = uops.get(name);
        return e != null ? e.string : null;
    }

    public void unary(String name, Unary value, String string) {
        uops.put(name, Str.of(value, string));
    }

    public List<String> unarys() {
        return uops.values().stream()
            .map(e -> e.string)
            .toList();
    }

    public Binary binary(String name) {
        Str<Binary> e = bops.get(name);
        return e != null ? e.op : null;
    }

    public List<String> binarys() {
        return bops.values().stream()
            .map(e -> e.string)
            .toList();
    }

    public String binaryString(String name) {
        Str<Binary> e = bops.get(name);
        return e != null ? e.string : null;
    }

    public void binary(String name, Binary value, String string) {
        bops.put(name, Str.of(value, string));
    }

    public High high(String name) {
        Str<High> e = hops.get(name);
        return e != null ? e.op : null;
    }

    public String highString(String name) {
        Str<High> e = hops.get(name);
        return e != null ? e.string : null;
    }

    public List<String> highs() {
        return hops.values().stream()
            .map(e -> e.string)
            .toList();
    }

    public void high(String name, High value, String string) {
        hops.put(name, Str.of(value, string));
    }

    static BigDecimal dec(boolean b) {
        return b ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    static BigDecimal dec(int i) {
        return new BigDecimal(i);
    }

    static BigDecimal dec(double d) {
        return new BigDecimal(d);
    }

    static double d(BigDecimal d) {
        return d.doubleValue();
    }

    static boolean b(BigDecimal d) {
        return !d.equals(BigDecimal.ZERO);
    }

    void initialize() {
        // unary operators
        unary("length", (c, v) -> Value.of(dec(v.size())), "length M : Mの長さ");
        unary("-", (c, v) -> v.map(BigDecimal::negate), "- M : 各要素の符号反転");
        unary("+", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary(BigDecimal::add, r)), "+ M : 全要素の和");
        unary("*", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary(BigDecimal::multiply, r)), "* M : 全要素の積");
        unary("^", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary((a, b) -> dec(Math.pow(d(a), d(b))), r)), "^ M : 全要素のべき乗");
        unary("abs", (c, v) -> v.map(x -> x.abs()), "abs M : 絶対値");
        unary("sign", (c, v) -> v.map(x -> dec(x.signum())), "sign M : 各要素の符号(-1, 0, 1)");
        unary("int", (c, v) -> v.map(x -> x.setScale(0, RoundingMode.HALF_UP)), "int M : 各要素の整数化(四捨五入)");
        unary("sqrt", (c, v) -> v.map(x -> x.sqrt(MATH_CONTEXT)), "sqrt M : 各要素の平方根");
        unary("sin", (c, v) -> v.map(x -> dec(Math.sin(d(x)))), "sin M : 各要素のsin値");
        unary("asin", (c, v) -> v.map(x -> dec(Math.asin(d(x)))), "asin M : 各要素のasin値");
        unary("cos", (c, v) -> v.map(x -> dec(Math.cos(d(x)))), "cos M : 各要素のcos値");
        unary("acos", (c, v) -> v.map(x -> dec(Math.acos(d(x)))), "acos M : 各要素のacos値");
        unary("tan", (c, v) -> v.map(x -> dec(Math.tan(d(x)))), "tan M : 各要素のtan値");
        unary("atan", (c, v) -> v.map(x -> dec(Math.atan(d(x)))), "atan M : 各要素のatan値");
        unary("log", (c, v) -> v.map(x -> dec(Math.log(d(x)))), "log M : 各要素のlog値(底はe)");
        unary("log10", (c, v) -> v.map(x -> dec(Math.log10(d(x)))), "log10 M : 各要素のlog値(底は10)");
        unary("not", (c, v) -> v.map(x -> dec(!b(x))), "not M : 各要素の否定値(0:偽⇔0以外:真)");
        unary("sort", (c, v) -> v.sort(), "sort M : 上昇順にソート");
        unary("reverse", (c, v) -> v.reverse(), "reverse M : Mの逆転");
        unary("shuffle", (c, v) -> v.shuffle(), "shuffle M : Mのシャッフル");
        unary("is-prime", (c, v) -> v.map(x -> dec(Value.isPrime(x))), "is-prime M : 素数の場合1、それ以外の場合0");
        unary("prime", (c, v) -> v.prime(), "prime M : Mから素数のみを選択");
        unary("factor", (c, v) -> v.factor(), "factor M : 素因数分解");
        // binary operators
        binary("+", (c, l, r) -> l.binary(BigDecimal::add, r), "M + N : 加算");
        binary("-", (c, l, r) -> l.binary(BigDecimal::subtract, r), "M - N : 減算");
        binary("*", (c, l, r) -> l.binary(BigDecimal::multiply, r), "M * N : 乗算");
        binary("/", (c, l, r) -> l.binary((a, b) -> a.divide(b, MATH_CONTEXT), r), "M / N : 除算");
        binary("%", (c, l, r) -> l.binary((a, b) -> a.remainder(b, MATH_CONTEXT), r), "M % N : 剰余");
        binary("^", (c, l, r) -> l.binary((a, b) -> dec(Math.pow(d(a), d(b))), r), "M ^ N : べき乗");
        binary("round", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_UP), r), "M round N : Mを小数点以下N桁に四捨五入");
        binary("ceiling", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.CEILING), r), "M ceiling N : Mを正の無限大方向に向かって小数点以下N桁に切り上げ");
        binary("down", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.DOWN), r), "M down N : Mをゼロに向かって小数点以下N桁に切り捨て");
        binary("floor", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.FLOOR), r), "M floor N : Mを負の無限大方向に向かって小数点以下N桁に切り捨て");
        // binary("half-down", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_DOWN), r), "");
        // binary("half-up", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_UP), r), "");
        binary("up", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.UP), r), "M up N : Mをゼロの逆に向かって小数点以下N桁に切り上げ");
        binary("==", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) == 0), r), "M == N : 等しいとき1、そうでないとき0");
        binary("~", (c, l, r) -> l.binary((a, b) -> dec(a.subtract(b).abs().compareTo(Value.EPSILON) < 0), r), "M ~ N : ほぼ等しいとき1、そうでないとき0(差が5E-10以下)");
        binary("!=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) != 0), r), "M != N : 等しくないとき1、等しいとき0");
        binary("<", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) < 0), r), "M < N : より小さいとき1、そうでないとき0");
        binary("<=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) <= 0), r), "M <= N : 小さいかまたは等しいとき1、そうでないとき0");
        binary(">", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) > 0), r), "M > N : より大きいとき1、そうでないとき0");
        binary(">=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) >= 0), r), "M >= N : より大きいかまたは等しいとき1、そうでないとき0");
        binary("min", (c, l, r) -> l.binary(BigDecimal::min, r), "M min N : 小さい方");
        binary("max", (c, l, r) -> l.binary(BigDecimal::max, r), "M max N : 大きい方");
        binary("and", (c, l, r) -> l.binary((a, b) -> dec(b(a) & b(b)), r), "M and N : かつ(ゼロは偽、それ以外は真)");
        binary("or", (c, l, r) -> l.binary((a, b) -> dec(b(a) | b(b)), r), "M or N : または(ゼロは偽、それ以外は真)");
        binary("xor", (c, l, r) -> l.binary((a, b) -> dec(b(a) ^ b(b)), r), "M xor N : 排他的論理和(ゼロは偽、それ以外は真)");
        binary("filter", (c, l, r) -> l.filter(r), "M filter N : Nの内、対応するMの要素が真のものだけを抽出(ゼロは偽、それ以外は真)");
        binary("..", (c, l, r) -> l.to(r), "M .. N : MからNの並び(N>Mのときは下降順、MおよびNはそれぞれ単一の値であること)");
        // high order operations
        high("@", (c, v, b) -> v.reduce(c, b), "@ B M : 二項演算子BでMを簡約(左から右に適用)");
        high("@<", (c, v, b) -> v.reduceRight(c, b), "@ B M : 二項演算子BでMを簡約(右から左に適用)");
        high("@@", (c, v, b) -> v.cumulate(c, b), "@@ B M : 二項演算子BでMを簡約しならが累積(左から右に適用)");
    }

}
