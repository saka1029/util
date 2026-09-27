package saka1029.util.declisp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ch.obermuhlner.math.big.BigDecimalMath.*;
import static saka1029.util.declisp.Common.*;

/**
 * DecLisp
 * 
 * BigDecimalを数値とするLispの実装。
 * 
 * <ol>
 * <li> Consはイミュータブルである。(car, cdrは更新できない)
 * <li> Consは点対を許容する。
 * <li> 真偽値はT, Fで表現する。
 * <li> 真偽値F以外は真と解釈する。
 * </ol>
 */
public class DecLisp {

    private DecLisp(){}

    static final Env ENV = new Env();

    public static Expr progn(Expr body, Env env) {
        Expr r = Nil.NIL;
        for (Expr c : body)
            r = c.eval(env);
        return r;
    }

    static {
        ENV.define(QUOTE, (Applicable) (args, e) -> car(args));
            // VT.spec, list(sym("value"), sym("a"), sym("b")), "quoteを除外した値を返す。");
        ENV.define(LAMBDA, (Applicable) (args, e) -> {
            Expr parms = car(args), body = cdr(args);
            return (Procedure) a -> {
                Env newEnv = new Env(e);
                parms.pairlis(a, newEnv);
                return progn(body, newEnv);
            };
        }, VT.spec, list(list(sym("var...")), sym("body...")), "varを引数としてbodyを実行する関数を定義する。");
        ENV.define(sym("if"), (Applicable) (args, e) -> {
            boolean p = bool(car(args).eval(e));
            if (p)
                return car(cdr(args)).eval(e);
            else if (!cdr(cdr(args)).equals(Nil.NIL))
                return car(cdr(cdr(args))).eval(e);
            else
                return Nil.NIL;
        }, VT.spec, list(sym("then"), sym("[else]")), "条件が真ならthenを評価し、そうでなければelseを評価する。");
        ENV.define(sym("define"), (Applicable) (args, e) -> {
            return car(args) instanceof Cons head
                ? e.define(symbol(head.car()), cons(LAMBDA, cons(head.cdr(), cdr(args))).eval(e),
                    VT.proc, head.cdr(), "ユーザ定義関数")
                : car(cdr(args)) instanceof Cons && car(car(cdr(args))).equals(LAMBDA)
                    ? e.define(symbol(car(args)), car(cdr(args)).eval(e), VT.proc, car(cdr(car(cdr(args)))), "ユーザ定義関数")
                    : e.define(symbol(car(args)), car(cdr(args)).eval(e), VT.var, Nil.NIL, "");
        }, VT.spec, list(sym("グローバル変数名"), sym("値")), "グローバル変数を定義する。");
        ENV.define(sym("help"), (Applicable) (args, e) -> {
            int n = 0;
            String key = args instanceof Cons c ? sym(car(c)).toLowerCase() : "";
            for (Help h : e.sortedHelp())
                if (h.name.value().toLowerCase().contains(key)) {
                    System.out.println(h);
                    ++n;
                }
            return dec(n);
        },
            VT.spec, list(sym("search")), "searchを含む関数の説明を表示する。");
        ENV.define(sym("set"), (Applicable) (args, e) -> e.set(symbol(car(args)), car(cdr(args)).eval(e)),
            VT.spec, list(sym("グローバル変数"), sym("値")), "グローバル変数に値を代入する。");
    }

    static {
        ENV.define(sym("&&"), (Applicable) (args, e) -> insertArith(args, Bool.T, (x, y) -> bool(x) ? y : x),
            VT.spec, list(sym("{args}")), "argsを左から順に評価して最初のFでないものを返す。");
        ENV.define(sym("||"), (Applicable) (args, e) -> insertArith(args, Bool.F, (x, y) -> bool(x) ? x : y),
            VT.spec, list(sym("{args}")), "argsを左から順に評価して最初のFを返す。");
    }

    static {
        // procedures
        ENV.define(sym("car"), (Procedure) args -> car(car(args)),
            VT.proc, list(sym("arg")), "argのcarを返す。");
        ENV.define(sym("cdr"), (Procedure) args -> cdr(car(args)),
            VT.proc, list(sym("arg")), "argのcdrを返す。");
        ENV.define(sym("cons"), (Procedure) args -> cons(car(args), car(cdr(args))),
            VT.proc, list(sym("a"), sym("b")), "aとbのconsを返す。");
        ENV.define(sym("list"), (Procedure) args -> args,
            VT.proc, sym("r"), "rを返す。");
        ENV.define(sym("reverse"), (Procedure) args -> {
            Expr r = Nil.NIL;
            for (Expr e : car(args))
                r = cons(e, r);
            return r;
        }, VT.proc, list(sym("リスト")), "リストを反転する。");
        ENV.define(sym("append"), (Procedure) args -> {
            Expr result = Nil.NIL;
            List<Expr> lists = args.stream().toList();
            for (int i = lists.size() - 1; i >= 0; --i) {
                List<Expr> list = lists.get(i).stream().toList();
                for (int j = list.size() - 1; j >= 0; --j)
                    result = cons(list.get(j), result);
            }
            return result;
        }, VT.proc, list(sym("{リスト}")), "リストを連結する。");
        ENV.define(sym("apply"), (Procedure) args -> proc(car(args)).apply(car(cdr(args))));
    }

    static {
        ENV.define(sym("not"), (Procedure) args -> bool(!bool(car(args))),
            VT.proc, list(sym("a")), "aがFのときTを返す。それ以外の時Fを返す。");
        ENV.define(sym("!"), (Procedure) args -> bool(!bool(car(args))),
            VT.proc, list(sym("a")), "aがFのときTを返す。それ以外の時Fを返す。");
        ENV.define(sym("abs"), (Procedure) args -> dec(dec(car(args)).abs(MC)),
            VT.proc, list(sym("a")), "a≧0のときaを返す。それ以外の時-aを返す。");
        ENV.define(sym("factorial"), (Procedure) args -> dec(factorial(dec(car(args)), MC)),
            VT.proc, list(sym("n")), "nの階乗を返す。");
    }

    /**
     * gcd / lcm 用insert
     * 
     * 引数の数:
     * 0 -> 1
     * 1 -> arg0
     * default -> 左結合
     * 
     * @param args
     * @param operator
     * @return
     */
    public static Expr insertGcdLcm(Expr args, BinaryOperator<Expr> operator) {
        Expr result = dec(1);
        int count = 0;
        for (Expr a : args)
            result = switch (count++) {
                case 0 -> a;
                default -> operator.apply(result, a);
            };
        return result;
    }

    static BigDecimal gcd(BigDecimal a, BigDecimal b) {
        return bigDec(bigInt(a).gcd(bigInt(b)));
    }

    static BigDecimal lcm(BigDecimal a, BigDecimal b) {
        return a.multiply(b, MC).abs().divide(gcd(a, b), MC); // abs(a * b) / gcd(a, b)
    }

    static {
        ENV.define(sym("gcd"), (Procedure) args -> insertGcdLcm(args,
            (a, b) -> dec(gcd(dec(a), dec(b)))),
            VT.proc, list(sym("n...")), "GCDを求める。");
        ENV.define(sym("lcm"), (Procedure) args -> insertGcdLcm(args, 
            (a, b) -> dec(lcm(dec(a), dec(b)))),
            VT.proc, list(sym("n...")), "LCMを求める。");
    }

    /**
     * 四則演算用insert
     * 引数の数:
     * 0 -> unit
     * 1 -> operator.apply(unit, arg0)
     * default -> 左簡約
     * 
     * @param args
     * @param unit
     * @param operator
     * @return
     */
    public static Expr insertArith(Expr args, Expr unit, BinaryOperator<Expr> operator) {
        Expr result = unit;
        Expr prev = null;
        int count = 0;
        for (Expr a : args) {
            result = operator.apply(count == 1 ? prev : result, a);
            prev = a;
            ++count;
        }
        return result;
    }

    static {
        ENV.define(sym("+"), (Procedure) args -> insertArith(args, dec(0), (x, y) -> dec(dec(x).add(dec(y), MC))),
            VT.proc, list(sym("d...")), "和を求める。");
        ENV.define(sym("-"), (Procedure) args -> insertArith(args, dec(0), (x, y) -> dec(dec(x).subtract(dec(y), MC))),
            VT.proc, list(sym("d...")), "差を求める。");
        ENV.define(sym("*"), (Procedure) args -> insertArith(args, dec(1), (x, y) -> dec(dec(x).multiply(dec(y), MC))),
            VT.proc, list(sym("d...")), "積を求める。");
        ENV.define(sym("/"), (Procedure) args -> insertArith(args, dec(1), (x, y) -> dec(dec(x).divide(dec(y), MC))),
            VT.proc, list(sym("d...")), "除算する。");
        ENV.define(sym("%"), (Procedure) args -> insertArith(args, dec(1), (x, y) -> dec(dec(x).remainder(dec(y), MC))),
            VT.proc, list(sym("d...")), "剰余を求める。");
        ENV.define(sym("pow"), (Procedure) args -> insertArith(args, dec(1), (x, y) -> dec(pow(dec(x), dec(y), MC))),
            VT.proc, list(sym("d...")), "べき乗の計算をする。(左結合)");
        ENV.define(sym("^"), ENV.get(sym("pow")),
            VT.proc, list(sym("d...")), "べき乗の計算をする。(左結合)");
    }

    static {
        ENV.define(sym("and"), (Procedure) args -> insertArith(args, Bool.T, (x, y) -> bool(bool(x) & bool(y))),
            VT.proc, list(sym("d...")), "論理積を求める。");
        ENV.define(sym("or"), (Procedure) args -> insertArith(args, Bool.F, (x, y) -> bool(bool(x) | bool(y))),
            VT.proc, list(sym("d...")), "論理和を求める。");
        ENV.define(sym("xor"), (Procedure) args -> insertArith(args, Bool.F, (x, y) -> bool(bool(x) ^ bool(y))),
            VT.proc, list(sym("d...")), "排他的論理和を求める。");
    }

    /**
     * 比較演算用insert
     * 
     * 引数の数:
     * 0, 1 -> エラー
     * その他 -> 左簡約
     */
    public static Expr insertComp(Expr args, BiPredicate<Expr, Expr> operator) {
        Expr prev = null;
        int count = 0;
        for (Expr a : args) {
            if (count >= 1)
                if (!operator.test(prev, a))
                    return Bool.F;
            prev = a;
            ++count;
        }
        if (count <= 1)
            throw new DecLispException("number of arguments must >= 2 '%s'", args);
        return Bool.T;
    }

    static {
        ENV.define(sym("=="), (Procedure) args -> insertComp(args, (x, y) -> x.compareTo(y) == 0),
            VT.proc, list(sym("d...")), "等しい。");
        ENV.define(sym("="), ENV.get(sym("==")),
            VT.proc, list(sym("d...")), "等しい。");
        ENV.define(sym("!="), (Procedure) args -> insertComp(args, (x, y) -> x.compareTo(y) != 0),
            VT.proc, list(sym("d...")), "等しくない。");
        ENV.define(sym("<>"), ENV.get(sym("!=")),
            VT.proc, list(sym("d...")), "等しくない。");
        ENV.define(sym("<"), (Procedure) args -> insertComp(args, (x, y) -> x.compareTo(y) < 0),
            VT.proc, list(sym("d...")), "より少ない。");
        ENV.define(sym("<="), (Procedure) args -> insertComp(args, (x, y) -> x.compareTo(y) <= 0),
            VT.proc, list(sym("d...")), "より少ないかまたは等しい。");
        ENV.define(sym(">"), (Procedure) args -> insertComp(args, (x, y) -> x.compareTo(y) > 0),
            VT.proc, list(sym("d...")), "より大きい。");
        ENV.define(sym(">="), (Procedure) args -> insertComp(args, (x, y) -> x.compareTo(y) >= 0),
            VT.proc, list(sym("d...")), "より大きいかまたは等しい。");
    }

    static {
        ENV.define(sym("approx"), (Procedure) args ->
            // abs(a - b) <= DELTA
            bool((dec(car(args)).subtract(dec(car(cdr(args))), MC).abs(MC).compareTo(DELTA)) <= 0));
        ENV.define(sym("~"), ENV.get(sym("approx")));
    }

    static Expr[] array(Expr arg) {
        return arg instanceof Nil || arg instanceof Cons
            ? arg.stream().toArray(Expr[]::new)
            : new Expr[] {arg};
    }

    public static Expr[][] matrix(Expr arg) {
        return Stream.of(array(arg))
            .map(row -> array(row))
            .toArray(Expr[][]::new);
    }

    static Expr[][] transpose(Expr[][] origin) {
        int rows = origin.length;
        if (rows <= 0)
            return new Expr[][] {};
        int cols = Stream.of(origin)
            .mapToInt(r -> r.length)
            .max().getAsInt();
        Expr[][] transposed = new Expr[cols][rows];
        for (int r = 0; r < rows; ++r) {
            Expr[] row = origin[r];
            int rowLen = row.length;
            if (rowLen != cols && rowLen != 1)
                throw new DecLispException("Illegal matrix %s", Arrays.deepToString(origin));
            for (int c = 0; c < cols; ++c)
                transposed[c][r] = c >= rowLen ? row[0] : row[c];
        }
        return transposed;
    }

    public static Expr map(Expr arg, Procedure proc) {
        Expr[][] matrix = matrix(arg);
        Expr[][] transposed = transpose(matrix);
        Expr[] lists = Stream.of(transposed)
            .map(row -> proc.apply(list(row)))
            .toArray(Expr[]::new);
        return list(lists);
    }

    static {
        ENV.define(sym("map"), (Procedure) args -> map(cdr(args), proc(car(args))));
    }

    static {
        ENV.define(sym("precision"), (Procedure) args ->
            args.equals(Nil.NIL) ? dec(precision()) : dec(precision(toInt(dec(car(args))))),
            VT.proc, list(sym("[新しい精度]")),
            "現在の精度(有効桁数)を取得(precision)または変更(precision 新しい精度)する。");
        // (delta) -> 現在のデルタ値を返す。
        // (delta d) -> デルタ値にdを設定しdを返す。
        ENV.define(sym("delta"), (Procedure) args ->
            args.equals(Nil.NIL) ? dec(delta()) : dec(delta(dec(car(args)))));
    }

    static {
        ENV.define(sym("sin"), (Procedure) args -> dec(sin(dec(car(args)), MC)));
        ENV.define(sym("cos"), (Procedure) args -> dec(cos(dec(car(args)), MC)));
        ENV.define(sym("tan"), (Procedure) args -> dec(tan(dec(car(args)), MC)));
        ENV.define(sym("asin"), (Procedure) args -> dec(asin(dec(car(args)), MC)));
        ENV.define(sym("acos"), (Procedure) args -> dec(acos(dec(car(args)), MC)));
        ENV.define(sym("atan"), (Procedure) args -> dec(atan(dec(car(args)), MC)));
        ENV.define(sym("log"), (Procedure) args -> dec(log(dec(car(args)), MC)));
        ENV.define(sym("log10"), (Procedure) args -> dec(log10(dec(car(args)), MC)));
        ENV.define(sym("log2"), (Procedure) args -> dec(log2(dec(car(args)), MC)));
        ENV.define(sym("gamma"), (Procedure) args -> dec(gamma(dec(car(args)), MC)));
        ENV.define(sym("exp"), (Procedure) args -> dec(exp(dec(car(args)), MC)));
        ENV.define(sym("sqrt"), (Procedure) args -> dec(sqrt(dec(car(args)), MC)));
        // (root 3 8) -> 8の3乗根
        ENV.define(sym("root"), (Procedure) args -> dec(root(dec(car(cdr(args))), dec(car(args)), MC)));
        ENV.define(sym("pi"), (Procedure) args -> dec(pi(MC)));
        ENV.define(sym("e"), (Procedure) args -> dec(e(MC)));
    }

    static Expr range(BigDecimal start, BigDecimal end, BigDecimal step) {
        int stepSign = step.signum();
        if (stepSign == 0)
            throw new DecLispException("step must != 0");
        List<Expr> elements = new ArrayList<>();
        for (BigDecimal i = start; i.compareTo(end) * stepSign < 0; i = i.add(step))
            elements.add(dec(i));
        return list(elements);
    }

    static Expr range(BigDecimal start, BigDecimal end) {
        return range(start, end, start.compareTo(end) <= 0 ? BigDecimal.ONE : BigDecimal.ONE.negate());
    }

    static {
        ENV.define(sym("range"), (Procedure) args -> {
            BigDecimal[] a = args.stream().map(x -> dec(x)).toArray(BigDecimal[]::new);
            return switch (a.length) {
                case 1 -> range(BigDecimal.ZERO, a[0]);
                case 2 -> range(a[0], a[1]);
                case 3 -> range(a[0], a[1], a[2]);
                default -> throw new DecLispException("Illegal range argument");
            };
        }, VT.proc, list(sym("[start]"), sym("end"), sym("[step]")),
            "指定範囲(start≦x＜endまたはstart≧x＞end)のリストを返す。"
            + "引数省略時(range end)はstart=0、"
            + "(range start end)はstep=1または-1となる。");
    }

    static {
        ENV.define(sym("square"), eval(ENV, "(lambda (x) (* x x))"));
        ENV.define(sym("hypot"), eval(ENV, "(lambda (x y) (sqrt (+ (square x) (square y))))"));
    }
    public static Expr[] removeLeadingZeros(Expr[] d) {
        int length = d.length;
        int start = 0;
        while (start < length && dec(d[start]).compareTo(BigDecimal.ZERO) == 0)
            ++start;
        return start == 0 ? d
            : start == length ? new Expr[] {dec(0)}
            : Arrays.copyOfRange(d, start, length);
    }
    public static Expr[] POLYNOMIAL_ADD_UNIT = new Expr[] {dec(BigDecimal.ZERO)};
    public static Expr[] POLYNOMIAL_MULTIPLY_UNIT = new Expr[] {dec(BigDecimal.ONE)};

    public static BinaryOperator<Expr[]> POLYNOMIAL_ADD = polynomialAddOrSub(true);
    public static BinaryOperator<Expr[]> POLYNOMIAL_SUBTRACT = polynomialAddOrSub(false);
    public static BinaryOperator<Expr[]> polynomialAddOrSub(boolean add) {
        return (a, b) -> {
            int al = a.length, bl = b.length, cl = Math.max(al, bl);
            Expr[] c = new Expr[cl];
            for (int i = al - 1, j = bl - 1, k = cl - 1; k >= 0; --i, --j, --k) {
                BigDecimal d = BigDecimal.ZERO;
                if (i >= 0)
                    d = d.add(dec(a[i]), MC);
                if (j >= 0)
                    d = add ? d.add(dec(b[j])) : d.subtract(dec(b[j]));
                c[k] = dec(d);
            }
            return removeLeadingZeros(c);
        };
    }
    public static BinaryOperator<Expr[]> POLYNOMIAL_MULTIPLY = (a, b) -> {
        int al = a.length, bl = b.length, cl = al + bl - 1;
        Expr[] c = new Expr[cl];
        Arrays.fill(c, dec(BigDecimal.ZERO));
        for (int i = al - 1; i >= 0; --i)
            for (int j = bl - 1; j >= 0; --j)
                c[i + j] = dec(dec(c[i + j]).add(dec(a[i]).multiply(dec(b[j]))));
        return removeLeadingZeros(c);
    };
    public static BinaryOperator<Expr[]> POLYNOMIAL_DIVIDE = (a, b) -> polyDivide(a, b)[0];
    public static BinaryOperator<Expr[]> POLYNOMIAL_MODULO = (a, b) -> polyDivide(a, b)[1];
    /**
     * 係数は最高次のものから降順に指定します。
     * ex) x^3 - 2 -> (1, 0, 0, -2)
     * @param left 割られる1変数多項式の係数を指定します。
     * @param right 割る1変数多項式の係数を指定します。
     * @return 商と余りを返します。(new Expr[][] {商, 余り})
     */
    public static Expr[][] polyDivide(Expr[] left, Expr[] right) {
        int ll = left.length, rl = right.length;
        if (ll < rl)
            return new Expr[][] {POLYNOMIAL_ADD_UNIT, left};
        int max = ll - rl + 1;
        Expr[] amari = left.clone();
        Expr[] syo = new Expr[max];
        for (int i = 0; i < max; ++i) {
            BigDecimal d = dec(amari[i]).divide(dec(right[0]), MC);
            syo[i] = dec(d);
            for (int j = 0; j < rl; ++j)
                amari[i + j] = dec(dec(amari[i + j]).subtract(dec(right[j]).multiply(d, MC), MC));
        }
        return new Expr[][] {syo, removeLeadingZeros(amari)};
    }

    public static Expr polynomial(Expr args, Expr[] unit, BinaryOperator<Expr[]> operator) {
        Expr[][] matrix = matrix(args);
        if (matrix.length == 0)
            return Nil.NIL;
        int count = 0;
        Expr[] result = unit, prev = null;
        for (Expr[] row : matrix) {
            result = operator.apply(count == 1 ? prev : result, row);
            prev = row;
            ++count;
        }
        return list(result);
    }

    static {
        ENV.define(sym("p+"), (Procedure) args -> polynomial(args, POLYNOMIAL_ADD_UNIT, POLYNOMIAL_ADD));
        ENV.define(sym("p-"), (Procedure) args -> polynomial(args, POLYNOMIAL_ADD_UNIT, POLYNOMIAL_SUBTRACT));
        ENV.define(sym("p*"), (Procedure) args -> polynomial(args, POLYNOMIAL_MULTIPLY_UNIT, POLYNOMIAL_MULTIPLY));
        ENV.define(sym("p/"), (Procedure) args -> polynomial(args, POLYNOMIAL_MULTIPLY_UNIT, POLYNOMIAL_DIVIDE));
        ENV.define(sym("p%"), (Procedure) args -> polynomial(args, POLYNOMIAL_MULTIPLY_UNIT, POLYNOMIAL_MODULO));
        ENV.define(sym("p="), (Procedure) args -> {
            Expr[] poly = car(args).array();
            BigDecimal result = BigDecimal.ZERO;
            BigDecimal value = dec(car(cdr(args)));
            for (Expr k : poly)
                result = result.multiply(value, MC).add(dec(k), MC);
            return dec(result);
        });
    }

    static {
        ENV.define(sym("today"), (Procedure) args -> { var d = LocalDate.now();
            return dec(d.getYear() * 10000 + d.getMonthValue() * 100 + d.getDayOfMonth());
        }, VT.proc, Nil.NIL, "今日の日付をYYYYMMDD形式の8桁の数字で返す。");
        ENV.define(sym("days"), (Procedure) args -> {
            int i = toInt(dec(car(args)));
            try {
                LocalDate d = LocalDate.of(i / 10000, i / 100 % 100, i % 100);
                return dec(d.toEpochDay());
            } catch (DateTimeException x) {
                throw new DecLispException(x);
            }
        }, VT.proc, list(sym("YYYYMMDD")), "YYYYMMDD形式で表現された日付のエポック日からの経過日数を返す。");
        ENV.define(sym("date"), (Procedure) args -> {
            long i = toLong(dec(car(args)));
            try {
                LocalDate d = LocalDate.ofEpochDay(i);
                return dec(d.getYear() * 10000 + d.getMonthValue() * 100 + d.getDayOfMonth());
            } catch (DateTimeException x) {
                throw new DecLispException(x);
            }
        }, VT.proc, list(sym("epoc")), "エポック日をYYYYMMDD形式の日付に変換する。");
        ENV.define(sym("week"), (Procedure) args -> {
            int i = toInt(dec(car(args)));
            try {
                LocalDate d = LocalDate.of(i / 10000, i / 100 % 100, i % 100);
                return sym(d.getDayOfWeek().toString());
            } catch (DateTimeException x) {
                throw new DecLispException(x);
            }
        }, VT.proc, list(sym("YYYYMMDD")), "YYYYMMDD形式の日付を曜日に変換する。");
    }

    static List<Entry<Symbol, Expr>> variables(Expr args, Env env) {
        List<Entry<Symbol, Expr>> vars = new ArrayList<>();     // 変数と値の格納領域
        Set<Symbol> dupCheck = new HashSet<>();                 // 変数名の重複チェック集合
        for (Expr var : cdr(args)) {                            // 変数と値の組をvarsに取り出す。
            Symbol v = symbol(car(var));
            if (!dupCheck.add(v))
                throw new DecLispException("variables: duplicated variable '%s'", v);
            vars.add(Map.entry(symbol(car(var)), car(cdr(var)).eval(env)));
        }
        return vars;
    }

    /**
     * (solve
     *      評価式
     *      (変数1 値1)
     *      (変数2 値2)
     *       ...
     * )
     */
    static Expr solve(Expr args, Env env) {
        Expr target = car(args);                                // 式を取り出す。
        List<Entry<Symbol, Expr>> vars = variables(args, env);  // 変数と値の格納領域
        List<Expr[]> result = new ArrayList<>();                // 結果格納領域
        result.add(vars.stream().map(x -> (Expr)x.getKey()).toArray(Expr[]::new));    // 変数名を追加する。
        new Object() {
            Env nenv = new Env(env);                            // 試行錯誤用のEnvを作成。
            void solve(int index) {                             // index番目の変数に値を割り当てる。
                if (index >= vars.size()) {                     // すべての変数に値を割り当てたら
                    if (bool(target.eval(nenv)))                // 式を評価する。
                        result.add(vars.stream()                // 結果を格納する。
                            .map(x -> nenv.get(x.getKey()))
                            .toArray(Expr[]::new));
                } else {
                    Symbol var = vars.get(index).getKey();
                    for (Expr e : vars.get(index).getValue()) { // すべての値について
                        nenv.define(var, e);                    // 値を割り当てる。
                        solve(index + 1);                       // 次の変数に値を割り当てる。
                    }
                }
            }
        }.solve(0);
        return list(result.stream().map(x -> list(x)).toList());
    }

    static {
        ENV.define(sym("solve"), (Applicable) (args, e) -> solve(args, e),
        VT.spec, list(sym("式"), list(list(sym("変数1"), sym("値1")), sym("..."))),
            "それぞれの変数に値を割り当てて式が真となるケースを見つける。");
    }

    /**
     * (min-max
     *      評価式
     *      (変数1 値1)
     *      (変数2 値2)
     *       ...  
     * )
     * 比較式: 2引数の真偽値を返す関数を指定する。
     * 
     * @param args
     * @param env
     * @return
     */
    static Expr minMax(Expr args, Env env) {
        Procedure LT = proc(env.get(sym("<")));
        Procedure GT = proc(env.get(sym(">")));
        Expr 評価式 = car(args);                                // 評価式を取り出す。
        List<Entry<Symbol, Expr>> vars = variables(args, env);  // 変数と値の格納領域
        int varSize = vars.size();
        Expr[] names = new Expr[varSize + 1];
        names[0] = sym("*");
        for (int i = 0; i < varSize; ++i)
            names[i + 1] = vars.get(i).getKey();
        var obj = new Object() {
            Env nenv = new Env(env);                            // 試行錯誤用のEnvを作成。
            Expr 最小評価値 = Nil.NIL;
            Expr[] 最小値 = null;                               // 結果格納領域
            Expr 最大評価値 = Nil.NIL;
            Expr[] 最大値 = null;                               // 結果格納領域

            Expr[] 結果(Expr v) {
                Expr[] r = new Expr[varSize + 1];
                r[0] = v;
                for (int i = 0; i < varSize; ++i)
                    r[i + 1] = nenv.get(vars.get(i).getKey());
                return r;
            }

            void solve(int index) {                             // index番目の変数に値を割り当てる。
                if (index >= vars.size()) {                     // すべての変数に値を割り当てたら
                    Expr ev = 評価式.eval(nenv);                // 式を評価する。
                    if (!ev.isNil()) {
                        if (最小値 == null || bool(LT.apply(list(ev, 最小評価値)))) {
                            最小評価値 = ev;
                            最小値 = 結果(ev);                  // 結果を格納する。
                        }
                        if (最大値 == null || bool(GT.apply(list(ev, 最大評価値)))) {
                            最大評価値 = ev;
                            最大値 = 結果(ev);                  // 結果を格納する。
                        }
                    }
                } else {
                    Symbol var = vars.get(index).getKey();
                    for (Expr e : vars.get(index).getValue()) { // すべての値について
                        nenv.define(var, e);                    // 値を割り当てる。
                        solve(index + 1);                       // 次の変数に値を割り当てる。
                    }
                }
            }
        };
        obj.solve(0);
        return list(list(names), list(obj.最小値), list(obj.最大値));
    }

    static {
        ENV.define(sym("min-max"), (Applicable) (args, e) -> minMax(args, e),
        VT.spec, list(sym("評価式"), list(sym("変数1"), sym("値1")), sym("...")),
            "それぞれの変数に値を割り当てたときに評価式の値が最大および最小となるケースを見つける。");
    }

    static {
        ENV.define(sym("isPrime"), (Procedure) args -> {
            BigInteger i = bigInt(car(args));
            if (i.compareTo(BigInteger.TWO) < 0)
                return Bool.F;
            BigInteger max = i.sqrt();
            for (BigInteger d = BigInteger.TWO; d.compareTo(max) <= 0; d = d.add(BigInteger.ONE))
                if (i.remainder(d).equals(BigInteger.ZERO))
                    return Bool.F;
            return Bool.T;
        }, VT.proc, list(sym("整数")), "整数値が素数かどうかを判定します。");

        ENV.define(sym("primes"), (Procedure) args -> {
            int size = toInt(dec(car(args)));
            boolean[] primes = new boolean[size];
            IntConsumer sieve = n -> {
                for (int i = n + n; i < size; i += n)
                    primes[i] = true;
            };
            primes[0] = primes[1] = true;
            int max = (int)Math.sqrt(size);
            sieve.accept(2);
            for (int i = 3; i <= max; i += 2)
                sieve.accept(i);
            return list(IntStream.range(0, size)
                .filter(i -> !primes[i])
                .mapToObj(i -> dec(i))
                .toArray(Expr[]::new));
        }, VT.proc, list(sym("最大値")),
            "最大値までの素数列を返します。");
    }

    static BigDecimal permutation(BigDecimal n, BigDecimal r) {
        BigInteger x = bigInt(n);
        BigInteger y = bigInt(r);
        if (x.compareTo(BigInteger.ZERO) < 0)
            throw new DecLispException("n must not be negative but %s", x);
        if (y.compareTo(BigInteger.ZERO) < 0)
            throw new DecLispException("r must not be negative but %s", y);
        if (x.compareTo(y) < 0)
            throw new DecLispException("n must be grater than or equals to r but n=%s r=%s", n, r);
        BigInteger result = BigInteger.ONE;
        for (BigInteger i = x.subtract(y).add(BigInteger.ONE); i.compareTo(x) <= 0; i = i.add(BigInteger.ONE)) 
            result = result.multiply(i);
        return new BigDecimal(result);
    }

    static BigDecimal combination(BigDecimal n, BigDecimal r) {
        r = r.min(n.subtract(r, MC));
        BigDecimal den = permutation(n, r);
        BigDecimal num = BigDecimal.ONE;
        for (BigDecimal i = r; i.compareTo(BigDecimal.ONE) > 0; i = i.subtract(BigDecimal.ONE))
            num = num.multiply(i);
        return den.divide(num, MC);
    }

    static {
        ENV.define(sym("P"), (Procedure) args -> dec(permutation(dec(car(args)), dec(car(cdr(args))))),
        VT.proc, list(sym("n"), sym("r")),
            "n個の中からr個選んだ順列の数を返します。");
        ENV.define(sym("C"), (Procedure) args -> dec(combination(dec(car(args)), dec(car(cdr(args))))),
        VT.proc, list(sym("n"), sym("r")),
            "n個の中からr個選んだ組み合わせの数を返します。");

    }

    public static Env defaultEnv() {
        return ENV;
    }
}
