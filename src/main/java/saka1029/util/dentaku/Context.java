package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {
    static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
    final Context parent;
    final Operators operators;
    final Map<String, Str<Expression>> variables = new HashMap<>();

    private Context(Operators operators, Context parent) {
        this.parent = parent;
        this.operators = operators;
    }

    public static Context of(Operators functions) {
        Context context = new Context(functions, null);
        context.initialize();
        return context;
    }

    public Context child() {
        return new Context(operators, this);
    }

    public Operators operators() {
        return operators;
    }
    public Expression variable(String name) {
        Str<Expression> e = variables.get(name);
        return e != null ? e.op : parent != null ? parent.variable(name) : null;
    }

    public String variableString(String name) {
        Str<Expression> e = variables.get(name);
        return e != null ? e.string : parent != null ? parent.variableString(name) : null;
    }

    public List<String> variables() {
        return variables.values().stream()
            .map(s -> s.string)
            .toList();
    }

    public void variable(String name, Expression e, String string) {
        variables.put(name, Str.of(e, string));
    }

    public void variable(String name, Value value) {
        variables.put(name, Str.of(x -> value, "%s = %s".formatted(name, value)));
    }

    public Value eval(String line) {
        return Parser.parse(operators, line).eval(this);
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

    static BigDecimal dec(String s) {
        return new BigDecimal(s);
    }

    static double d(BigDecimal d) {
        return d.doubleValue();
    }

    static boolean b(BigDecimal d) {
        return !d.equals(BigDecimal.ZERO);
    }

    private void initialize() {
        // unary operators
        operators.unary("length", (c, v) -> Value.of(dec(v.size())), "length V -> S : 長さ");
        operators.unary("-", (c, v) -> v.map(BigDecimal::negate), "- V -> V: 符号反転");
        operators.unary("+", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary(BigDecimal::add, r)), "+ V -> S : 和");
        operators.unary("*", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary(BigDecimal::multiply, r)), "* V -> S : 積");
        operators.unary("^", (c, v) -> v.reduce(c, (c1, l, r) -> l.binary(Value::pow, r)), "^ V -> S : べき乗");
        operators.unary("abs", (c, v) -> v.map(x -> x.abs()), "abs V -> V : 絶対値");
        operators.unary("sign", (c, v) -> v.map(x -> dec(x.signum())), "sign V -> V : 符号(-1, 0, 1)");
        operators.unary("int", (c, v) -> v.map(x -> x.setScale(0, RoundingMode.HALF_UP)), "int V -> V : 整数化(四捨五入)");
        operators.unary("trunc", (c, v) -> v.map(x -> x.setScale(0, RoundingMode.DOWN)), "trunc V -> V : 整数化(四捨五入)");
        operators.unary("sqrt", (c, v) -> v.map(x -> x.sqrt(MATH_CONTEXT)), "sqrt V -> V : 平方根");
        operators.unary("sin", (c, v) -> v.map(x -> dec(Math.sin(d(x)))), "sin V -> V : sin値");
        operators.unary("asin", (c, v) -> v.map(x -> dec(Math.asin(d(x)))), "asin V -> V : sin⁻¹値");
        operators.unary("cos", (c, v) -> v.map(x -> dec(Math.cos(d(x)))), "cos V -> V : cos値");
        operators.unary("acos", (c, v) -> v.map(x -> dec(Math.acos(d(x)))), "acos V -> V : cos⁻¹値");
        operators.unary("tan", (c, v) -> v.map(x -> dec(Math.tan(d(x)))), "tan V -> V : tan値");
        operators.unary("atan", (c, v) -> v.map(x -> dec(Math.atan(d(x)))), "atan V -> V : tan⁻¹値");
        operators.unary("log", (c, v) -> v.map(x -> dec(Math.log(d(x)))), "log V -> V : 対数値(底はe)");
        operators.unary("log10", (c, v) -> v.map(x -> dec(Math.log10(d(x)))), "log10 V -> V : 対数値(底は10)");
        operators.unary("not", (c, v) -> v.map(x -> dec(!b(x))), "not V -> V : 否定(0:偽⇔0以外:真)");
        operators.unary("sort", (c, v) -> v.sort(), "sort V -> V : 上昇順にソート");
        operators.unary("reverse", (c, v) -> v.reverse(), "reverse V -> V : 反転");
        operators.unary("shuffle", (c, v) -> v.shuffle(), "shuffle V -> V : シャッフル");
        operators.unary("is-prime", (c, v) -> v.map(x -> dec(Value.isPrime(x))), "is-prime V -> V : 素数の場合1、それ以外の場合0");
        operators.unary("prime", (c, v) -> v.prime(), "prime V -> V : 素数のみを選択");
        operators.unary("factor", (c, v) -> v.factor(), "factor V -> V : 素因数分解");
        operators.unary("year", (c, v) -> v.map(x -> Value.year(Value.date(x))), "year V -> V : YYYYMMDDのYYYY");
        operators.unary("month", (c, v) -> v.map(x -> Value.month(Value.date(x))), "month V -> V : YYYYMMDDのMM");
        operators.unary("day", (c, v) -> v.map(x -> Value.day(Value.date(x))), "day V -> V : YYYYMMDDのDD");
        operators.unary("week", (c, v) -> v.map(x -> Value.week(Value.date(x))), "week V -> V : YYYYMMDDの曜日(1:月曜日..7:日曜日)");
        operators.unary("days", (c, v) -> v.map(x -> Value.days(Value.date(x))), "days V -> V : YYYYMMDDの絶対日(0=1970年1月1日)");
        operators.unary("date", (c, v) -> v.map(x -> Value.dec(Value.dateFromDays(x))), "date V -> V : 絶対日(0=1970年1月1日)から日付(YYYYMMDD)");
        // binary operators
        operators.binary("+", (c, l, r) -> l.binary(BigDecimal::add, r), "V + V -> V : 加算");
        operators.binary("-", (c, l, r) -> l.binary(BigDecimal::subtract, r), "V - V -> V : 減算");
        operators.binary("*", (c, l, r) -> l.binary(BigDecimal::multiply, r), "V * V -> V : 乗算");
        operators.binary("/", (c, l, r) -> l.binary((a, b) -> a.divide(b, MATH_CONTEXT), r), "V / V -> V : 除算");
        operators.binary("%", (c, l, r) -> l.binary((a, b) -> a.remainder(b, MATH_CONTEXT), r), "V % V -> V: 剰余");
        operators.binary("^", (c, l, r) -> l.binary(Value::pow, r), "V ^ V -> V: べき乗");
        operators.binary("gcd", (c, l, r) -> l.binary(Value::gcd, r), "V gcd V -> V: 最大公約数");
        operators.binary("lcm", (c, l, r) -> l.binary(Value::lcm, r), "V lcm V -> V: 最小公倍数");
        operators.binary("P", (c, l, r) -> l.binary(Value::permutation, r), "V P V -> V: 順列");
        operators.binary("C", (c, l, r) -> l.binary(Value::combination, r), "V C V -> V: 組合せ");
        operators.binary("round", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.HALF_UP), r), "V round S -> V : Vを小数点以下S桁に四捨五入");
        operators.binary("ceiling", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.CEILING), r), "V ceiling S -> V : Vを正の無限大方向に向かって小数点以下S桁に切り上げ");
        operators.binary("down", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.DOWN), r), "V down S -> V : Vをゼロに向かって小数点以下S桁に切り捨て");
        operators.binary("floor", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.FLOOR), r), "V floor S -> V : Vを負の無限大方向に向かって小数点以下S桁に切り捨て");
        operators.binary("up", (c, l, r) -> l.binary((a, b) -> a.setScale(b.intValue(), RoundingMode.UP), r), "V up S -> V : Vをゼロの逆に向かって小数点以下S桁に切り上げ");
        operators.binary("==", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) == 0), r), "V == V -> V : 等しい(結果は1,0で返す)");
        operators.binary("~", (c, l, r) -> l.binary((a, b) -> dec(a.subtract(b).abs().compareTo(c.variable("EPSILON").eval(c).oneElement()) <= 0), r), "V ~ V -> V : ほぼ等しい(しきい値はEPSILON)");
        operators.binary("!~", (c, l, r) -> l.binary((a, b) -> dec(a.subtract(b).abs().compareTo(c.variable("EPSILON").eval(c).oneElement()) > 0), r), "V !~ V -> V : ほぼ等しくない(しきい値はEPSILON)");
        operators.binary("!=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) != 0), r), "V != V -> V : 等しくない(結果は1,0で返す)");
        operators.binary("<", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) < 0), r), "V < V -> V : 小さい(結果は1,0で返す)");
        operators.binary("<=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) <= 0), r), "V <= V -> V : 小さいかまたは等しい(結果は1,0で返す)");
        operators.binary(">", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) > 0), r), "V > V -> V : 大きい(結果は1,0で返す)");
        operators.binary(">=", (c, l, r) -> l.binary((a, b) -> dec(a.compareTo(b) >= 0), r), "V >= V -> V : 大きいかまたは等しい(結果は1,0で返す)");
        operators.binary("min", (c, l, r) -> l.binary(BigDecimal::min, r), "V min V -> V : 小さい方");
        operators.binary("max", (c, l, r) -> l.binary(BigDecimal::max, r), "V max V -> V : 大きい方");
        operators.binary("and", (c, l, r) -> l.binary((a, b) -> dec(b(a) & b(b)), r), "V and V -> V : 論理積(ゼロは偽、それ以外は真)");
        operators.binary("or", (c, l, r) -> l.binary((a, b) -> dec(b(a) | b(b)), r), "V or -> V N : 論理和(ゼロは偽、それ以外は真)");
        operators.binary("xor", (c, l, r) -> l.binary((a, b) -> dec(b(a) ^ b(b)), r), "V xor -> V N : 排他的論理和(ゼロは偽、それ以外は真)");
        operators.binary("filter", (c, l, r) -> l.filter(r), "V filter V : 右辺の内、対応する左辺の要素が真のものだけを抽出(ゼロは偽、それ以外は真)");
        operators.binary("to", (c, l, r) -> l.to(r), "S to S -> V : 左辺から右辺までの並び(左辺<右辺のときは下降順)");
        operators.binary("at", (c, l, r) -> l.at(r), "V at S -> V : 右辺番目の要素を取り出す。");
        // high order operations
        operators.high("@", (c, v, b) -> v.reduce(c, b), "@ B V -> S : 二項演算子BでVを簡約(左から右に適用)");
        operators.high("@<", (c, v, b) -> v.reduceRight(c, b), "@< B V -> S : 二項演算子BでVを簡約(右から左に適用)");
        operators.high("@@", (c, v, b) -> v.cumulate(c, b), "@@ B V -> V : 二項演算子BでVを簡約しながら累積(左から右に適用)");
        variable("TODAY", c -> Value.of(Value.dec(LocalDate.now())), "TODAY : 今日(YYYYMMDD)");
        variable("PI", c -> Value.of(dec("3.1415926535897932384626433")), "PI : 円周率");
        variable("E", c -> Value.of(dec("2.7182818284590452353602874")), "E : 自然対数の底");
        variable("EPSILON", c -> Value.of(dec("5E-10")), "EPSILON : ほぼ等しいのしきい値");
        eval("ave x = + x / length x");
        eval("variance x = + (x - ave x ^ 2) / length x");
        eval("sd x = sqrt variance x");
        eval("t-score x = x - ave x / sd x * 10 + 50");
        eval("pascal n = n C (0 to n)");
        eval("c poly x = + (x ^ (length c - 1 to 0) * c)");
        eval("a distance b = sqrt + (a - b ^ 2)");
        eval("a days b = days b - days a");
    }
}
