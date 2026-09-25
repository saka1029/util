package saka1029.util.declisp;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import static ch.obermuhlner.math.big.BigDecimalMath.*;
import static saka1029.util.declisp.Common.*;
import static saka1029.util.declisp.Operators.POLYNOMIAL_ADD;
import static saka1029.util.declisp.Operators.POLYNOMIAL_ADD_UNIT;
import static saka1029.util.declisp.Operators.POLYNOMIAL_DIVIDE;
import static saka1029.util.declisp.Operators.POLYNOMIAL_MODULO;
import static saka1029.util.declisp.Operators.POLYNOMIAL_MULTIPLY;
import static saka1029.util.declisp.Operators.POLYNOMIAL_MULTIPLY_UNIT;
import static saka1029.util.declisp.Operators.POLYNOMIAL_SUBTRACT;

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

    public static boolean approx(BigDecimal left, BigDecimal right) {
        return left.subtract(right, MC).abs(MC).compareTo(DELTA) < 0;
    }

    public static Expr progn(Expr body, Env env) {
        Expr r = Nil.NIL;
        for (Expr c : body)
            r = c.eval(env);
        return r;
    }

    public static Expr insert(Expr args, Expr unit, BinaryOperator<Expr> operator) {
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

    public static Expr insert(Expr args, BiPredicate<Expr, Expr> operator) {
        Expr prev = null;
        for (Expr a : args) {
            if (prev == null)
                prev = a;
            else if (!operator.test(prev, a))
                return Bool.F;
            prev = a;
        }
        return Bool.T;
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

    public static Expr range(BigDecimal start, BigDecimal end, BigDecimal step) {
        int stepSign = step.signum();
        if (stepSign == 0)
            throw new DecLispException("step must != 0");
        List<Expr> elements = new ArrayList<>();
        for (BigDecimal i = start; i.compareTo(end) * stepSign <= 0; i = i.add(step))
            elements.add(dec(i));
        return list(elements);
    }

    public static Expr range(BigDecimal start, BigDecimal end) {
        return start.compareTo(end) <= 0
            ? range(start, end, BigDecimal.ONE)
            : range(start, end, BigDecimal.ONE.negate());
    }

    public static Expr range(BigDecimal end) {
        return range(BigDecimal.ONE, end);
    }

    public static Env defaultEnv() {
        Env env = new Env();
        env.define(QUOTE, (Applicable) (args, e) -> car(args));
        env.define(LAMBDA, (Applicable) (args, e) -> {
            Expr parms = car(args), body = cdr(args);
            return (Procedure) a -> {
                Env newEnv = new Env(e);
                parms.pairlis(a, newEnv);
                return progn(body, newEnv);
            };
        });
        env.define(sym("if"), (Applicable) (args, e) -> {
            boolean p = bool(car(args).eval(e));
            if (p)
                return car(cdr(args)).eval(e);
            else if (!cdr(cdr(args)).equals(Nil.NIL))
                return car(cdr(cdr(args))).eval(e);
            else
                return Nil.NIL;
        });
        env.define(sym("define"), (Applicable) (args, e) -> {
            return car(args) instanceof Cons head
                ? e.define(symbol(head.car()), cons(LAMBDA, cons(head.cdr(), cdr(args))).eval(e))
                : e.define(symbol(car(args)), car(cdr(args)).eval(e));
        });
        env.define(sym("set"), (Applicable) (args, e) -> e.set(symbol(car(args)), car(cdr(args)).eval(e)));
        env.define(sym("&&"), (Applicable) (args, e) -> insert(args, Bool.T, (x, y) -> bool(x) ? y : x));
        env.define(sym("||"), (Applicable) (args, e) -> insert(args, Bool.F, (x, y) -> bool(x) ? x : y));
        // procedures
        env.define(sym("car"), (Procedure) args -> car(car(args)));
        env.define(sym("cdr"), (Procedure) args -> cdr(car(args)));
        env.define(sym("cons"), (Procedure) args -> cons(car(args), car(cdr(args))));
        env.define(sym("list"), (Procedure) args -> args);
        env.define(sym("not"), (Procedure) args -> bool(!bool(car(args))));
        env.define(sym("!"), (Procedure) args -> bool(!bool(car(args))));
        env.define(sym("abs"), (Procedure) args -> dec(dec(car(args)).abs()));
        env.define(sym("gcd"), (Procedure) args -> insert(args, dec(1), (x, y) -> dec(dec(x).toBigInteger().gcd(dec(y).toBigInteger())))); 
        env.define(sym("+"), (Procedure) args -> insert(args, dec(0), (x, y) -> dec(dec(x).add(dec(y), MC))));
        env.define(sym("-"), (Procedure) args -> insert(args, dec(0), (x, y) -> dec(dec(x).subtract(dec(y), MC))));
        env.define(sym("*"), (Procedure) args -> insert(args, dec(1), (x, y) -> dec(dec(x).multiply(dec(y), MC))));
        env.define(sym("/"), (Procedure) args -> insert(args, dec(1), (x, y) -> dec(dec(x).divide(dec(y), MC))));
        env.define(sym("%"), (Procedure) args -> insert(args, dec(1), (x, y) -> dec(dec(x).remainder(dec(y), MC))));
        env.define(sym("pow"), (Procedure) args -> insert(args, dec(1), (x, y) -> dec(pow(dec(x), dec(y), MC))));
        env.define(sym("^"), env.get(sym("pow")));
        env.define(sym("and"), (Procedure) args -> insert(args, Bool.T, (x, y) -> bool(bool(x) & bool(y))));
        env.define(sym("or"), (Procedure) args -> insert(args, Bool.F, (x, y) -> bool(bool(x) | bool(y))));
        env.define(sym("xor"), (Procedure) args -> insert(args, Bool.F, (x, y) -> bool(bool(x) ^ bool(y))));
        env.define(sym("=="), (Procedure) args -> insert(args, (x, y) -> x.compareTo(y) == 0));
        env.define(sym("="), env.get(sym("==")));
        env.define(sym("!="), (Procedure) args -> insert(args, (x, y) -> x.compareTo(y) != 0));
        env.define(sym("<"), (Procedure) args -> insert(args, (x, y) -> x.compareTo(y) < 0));
        env.define(sym("<="), (Procedure) args -> insert(args, (x, y) -> x.compareTo(y) <= 0));
        env.define(sym(">"), (Procedure) args -> insert(args, (x, y) -> x.compareTo(y) > 0));
        env.define(sym(">="), (Procedure) args -> insert(args, (x, y) -> x.compareTo(y) >= 0));
        env.define(sym("map"), (Procedure) args -> map(cdr(args), proc(car(args))));
        // (precision) -> 現在の精度を返す。
        // (precision n) -> 精度にnを設定しnを返す。
        env.define(sym("precision"), (Procedure) args ->
            args.equals(Nil.NIL) ? dec(precision()) : dec(precision(toInt(dec(car(args))))));
        // (delta) -> 現在のデルタ値を返す。
        // (delta d) -> デルタ値にdを設定しdを返す。
        env.define(sym("delta"), (Procedure) args ->
            args.equals(Nil.NIL) ? dec(delta()) : dec(delta(dec(car(args)))));
        // (~ a b)
        env.define(sym("approx"), (Procedure) args -> bool(approx(dec(car(args)), dec(car(cdr(args))))));
        env.define(sym("~"), env.get(sym("approx")));
        env.define(sym("sin"), (Procedure) args -> dec(sin(dec(car(args)), MC)));
        env.define(sym("cos"), (Procedure) args -> dec(cos(dec(car(args)), MC)));
        env.define(sym("tan"), (Procedure) args -> dec(tan(dec(car(args)), MC)));
        env.define(sym("asin"), (Procedure) args -> dec(asin(dec(car(args)), MC)));
        env.define(sym("acos"), (Procedure) args -> dec(acos(dec(car(args)), MC)));
        env.define(sym("atan"), (Procedure) args -> dec(atan(dec(car(args)), MC)));
        env.define(sym("log"), (Procedure) args -> dec(log(dec(car(args)), MC)));
        env.define(sym("log10"), (Procedure) args -> dec(log10(dec(car(args)), MC)));
        env.define(sym("log2"), (Procedure) args -> dec(log2(dec(car(args)), MC)));
        env.define(sym("gamma"), (Procedure) args -> dec(gamma(dec(car(args)), MC)));
        env.define(sym("exp"), (Procedure) args -> dec(exp(dec(car(args)), MC)));
        env.define(sym("sqrt"), (Procedure) args -> dec(sqrt(dec(car(args)), MC)));
        // (root 3 8) -> 8の3乗根
        env.define(sym("root"), (Procedure) args -> dec(root(dec(car(cdr(args))), dec(car(args)), MC)));
        env.define(sym("pi"), (Procedure) args -> dec(pi(MC)));
        env.define(sym("e"), (Procedure) args -> dec(e(MC)));
        env.define(sym("range"), (Procedure) args -> {
            BigDecimal[] a = args.stream().map(x -> dec(x)).toArray(BigDecimal[]::new);
            return switch (a.length) {
                case 1 -> range(a[0]);
                case 2 -> range(a[0], a[1]);
                case 3 -> range(a[0], a[1], a[2]);
                default -> throw new DecLispException("Illegal range argument");
            };
        });
        env.define(sym("apply"), (Procedure) args -> proc(car(args)).apply(car(cdr(args))));
        env.define(sym("square"), eval(env, "(lambda (x) (* x x))"));
        env.define(sym("hypot"), eval(env, "(lambda (x y) (sqrt (+ (square x) (square y))))"));
        env.define(sym("p+"), (Procedure) args -> polynomial(args, POLYNOMIAL_ADD_UNIT, POLYNOMIAL_ADD));
        env.define(sym("p-"), (Procedure) args -> polynomial(args, POLYNOMIAL_ADD_UNIT, POLYNOMIAL_SUBTRACT));
        env.define(sym("p*"), (Procedure) args -> polynomial(args, POLYNOMIAL_MULTIPLY_UNIT, POLYNOMIAL_MULTIPLY));
        env.define(sym("p/"), (Procedure) args -> polynomial(args, POLYNOMIAL_MULTIPLY_UNIT, POLYNOMIAL_DIVIDE));
        env.define(sym("p%"), (Procedure) args -> polynomial(args, POLYNOMIAL_MULTIPLY_UNIT, POLYNOMIAL_MODULO));
        env.define(sym("today"), (Procedure) args -> { var d = LocalDate.now();
            return dec(d.getYear() * 10000 + d.getMonthValue() * 100 + d.getDayOfMonth());
        });
        env.define(sym("days"), (Procedure) args -> {
            int i = toInt(dec(car(args)));
            try {
                LocalDate d = LocalDate.of(i / 10000, i / 100 % 100, i % 100);
                return dec(d.toEpochDay());
            } catch (DateTimeException x) {
                throw new DecLispException(x);
            }
        });
        env.define(sym("date"), (Procedure) args -> {
            long i = toLong(dec(car(args)));
            try {
                LocalDate d = LocalDate.ofEpochDay(i);
                return dec(d.getYear() * 10000 + d.getMonthValue() * 100 + d.getDayOfMonth());
            } catch (DateTimeException x) {
                throw new DecLispException(x);
            }
        });
        env.define(sym("week"), (Procedure) args -> {
            int i = toInt(dec(car(args)));
            try {
                LocalDate d = LocalDate.of(i / 10000, i / 100 % 100, i % 100);
                return sym(d.getDayOfWeek().toString());
            } catch (DateTimeException x) {
                throw new DecLispException(x);
            }
        });
        env.define(sym("loop"), (Procedure) args -> { while (true); });
        return env;
    }
}
