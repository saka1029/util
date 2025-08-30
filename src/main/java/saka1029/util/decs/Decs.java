package saka1029.util.decs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.obermuhlner.math.big.BigDecimalMath;

public class Decs {

    private Decs() {
    }

    public static final BigDecimal[] EMPTY = new BigDecimal[] {};
    public static final BigDecimal[] NO_VALUE = new BigDecimal[] {};
    public static final BigDecimal[] EXIT = new BigDecimal[] {};
    public static final BigDecimal TRUE = BigDecimal.ONE;
    public static final BigDecimal FALSE = BigDecimal.ZERO;
    public static final BigDecimal MINUS_ONE = BigDecimal.valueOf(-1L);
    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal ONE = BigDecimal.ONE;
    public static final BigDecimal TWO = BigDecimal.valueOf(2);
    public static final int PRECISION = 30;
    public static MathContext MATH_CONTEXT = new MathContext(PRECISION);
    public static BigDecimal EPSILON = new BigDecimal("5e-6");

    public static BigDecimal[] precision(BigDecimal[] decs) {
        MATH_CONTEXT = new MathContext(single(decs).intValueExact());
        return NO_VALUE;
    }

    public static BigDecimal[] epsilon(BigDecimal[] decs) {
        EPSILON = single(decs);
        return NO_VALUE;
    }

    static ValueException error(String message, Object... args) {
        return new ValueException(message, args);
    }

    public static Stream<BigDecimal> stream(BigDecimal[] elements) {
        return Arrays.stream(elements);
    }

    public static BigDecimal dec(int i) {
        return BigDecimal.valueOf(i);
    }

    public static BigDecimal dec(long i) {
        return BigDecimal.valueOf(i);
    }

    public static BigDecimal dec(double i) {
        return BigDecimal.valueOf(i);
    }

    static final Pattern BASED_INTEGER = Pattern.compile("(\\d+)[bB](\\w+)");

    public static BigDecimal dec(String s) {
        Matcher m = BASED_INTEGER.matcher(s);
        if (m.matches())
            try {
                return dec(new BigInteger(m.group(2), Integer.parseInt(m.group(1))));
            } catch (NumberFormatException e) {
                throw error("%s '%s'", e.getMessage(), s);
            }
        return new BigDecimal(s);
    }

    public static BigDecimal dec(boolean b) {
        return b ? TRUE : FALSE;
    }

    public static BigDecimal dec(BigInteger i) {
        return new BigDecimal(i);
    }

    public static BigDecimal[] decs() {
        return EMPTY;
    }

    public static BigDecimal[] decs(BigDecimal... elements) {
        return elements.clone();
    }

    public static BigDecimal[] decs(int... values) {
        return decs(IntStream.of(values).mapToObj(i -> dec(i)));
    }

    public static BigDecimal[] decs(List<BigDecimal> list) {
        return list.toArray(BigDecimal[]::new);
    }

    public static BigDecimal[] decs(Deque<BigDecimal> list) {
        return list.toArray(BigDecimal[]::new);
    }

    public static BigDecimal[] decs(Stream<BigDecimal> stream) {
        return stream.toArray(BigDecimal[]::new);
    }

    public static BigDecimal[] decs(String s) {
        s = s.trim();
        return s.equals("") ? EMPTY
            : decs(Stream.of(s.split("\\s+"))
                .map(x -> dec(x)));
    }

    public static int hashCode(BigDecimal[] decs) {
        return java.util.Arrays.hashCode(decs);
    }

    public static boolean equals(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) == 0;
    }

    public static boolean equals(BigDecimal[] left, BigDecimal[] right) {
        return left.length == right.length
            && IntStream.range(0, left.length)
                .allMatch(i -> equals(left[i], right[i]));
    }

    public static String string(BigDecimal dec) {
        return dec.toString().replaceFirst("\\.0+$", "");
    }

    public static String string(BigDecimal[] decs) {
        return decs.length == 1 ?  string(decs[0])
            : stream(decs)
                .map(d -> string(d))
                .collect(Collectors.joining(", ", "(", ")"));
    }

    static boolean bool(BigDecimal d) {
        return !d.equals(ZERO);
    }

    public static BigDecimal[] pi() {
        return decs(BigDecimalMath.pi(MATH_CONTEXT));
    }

    public static BigDecimal[] e() {
        return decs(BigDecimalMath.e(MATH_CONTEXT));
    }

    // unary reduce method

    public static BigDecimal[] reduce(BigDecimal[] decs, BinaryOperator<BigDecimal> op) {
        if (decs.length < 1)
            throw error("Empty argument '%s'", string(decs));
        return decs(stream(decs).reduce(op).get());
    }

    public static BigDecimal[] reduce(BigDecimal[] decs,
            BigDecimal unit, BinaryOperator<BigDecimal> op) {
        return decs(stream(decs).reduce(unit, op));
    }

    public static BigDecimal[] reduce(BigDecimal[] decs,
            BigDecimal unit, UnaryOperator<BigDecimal> one,BinaryOperator<BigDecimal> many) {
        switch (decs.length) {
            case 0: return decs(unit);
            case 1: return decs(one.apply(decs[0]));
            default: return decs(stream(decs).reduce(many).get());
        }
    }

    public static BigDecimal[] add(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    public static BigDecimal[] subtract(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ZERO, BigDecimal::negate, (a, b) -> a.subtract(b));
    }

    public static BigDecimal[] multiply(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ONE, (a, b) -> a.multiply(b));
    }

    public static BigDecimal[] divide(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ONE,
            d -> BigDecimalMath.reciprocal(d, MATH_CONTEXT),
            (a, b) -> a.divide(b, MATH_CONTEXT));
    }

    public static BigDecimal pow(BigDecimal left, BigDecimal right) {
        // System.out.printf("pow(%s, %s)%n", left, right);
        return BigDecimalMath.isLongValue(right)
                ? BigDecimalMath.pow(left, right.longValue(), MATH_CONTEXT)
                : BigDecimalMath.pow(left, right, MATH_CONTEXT);
    }

    /**
     * べき乗を計算する。
     * 右結合である点に注意する。
     * pow(2, 3, 2) = 2 ^ (3 ^ 2) = 512
     * 
     * @param decs
     * @return
     */
    public static BigDecimal[] pow(BigDecimal[] decs) {
        return reduce(reverse(decs), ONE, (a, b) -> pow(b, a));
    }

    public static BigDecimal and(BigDecimal left, BigDecimal right) {
        return dec(left.toBigIntegerExact().and(right.toBigIntegerExact()));
    }

    public static BigDecimal[] and(BigDecimal[] decs) {
        return reduce(decs, TRUE, Decs::and);
    }

    public static BigDecimal[] and(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, Decs::and);
    }

    public static BigDecimal or(BigDecimal left, BigDecimal right) {
        return dec(left.toBigIntegerExact().or(right.toBigIntegerExact()));
    }

    public static BigDecimal[] or(BigDecimal[] decs) {
        return reduce(decs, FALSE, Decs::or);
    }

    public static BigDecimal[] or(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, Decs::or);
    }

    public static BigDecimal xor(BigDecimal left, BigDecimal right) {
        return dec(left.toBigIntegerExact().xor(left.toBigIntegerExact()));
    }

    public static BigDecimal[] xor(BigDecimal[] decs) {
        return reduce(decs, Decs::xor);
    }

    public static BigDecimal[] xor(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, Decs::xor);
    }

    public static BigDecimal[] cand(BigDecimal[] decs) {
        return reduce(decs, ONE, (a, b) -> dec(bool(a) && bool(b)));
    }

    public static BigDecimal[] cor(BigDecimal[] decs) {
        return reduce(decs, ZERO, (a, b) -> dec(bool(a) || bool(b)));
    }

    // unary map method

    public static BigDecimal[] map(BigDecimal[] decs, UnaryOperator<BigDecimal> mapper) {
        return decs(stream(decs).map(mapper));
    }

    public static BigDecimal[] negate(BigDecimal[] decs) {
        return map(decs, BigDecimal::negate);
    }

    public static BigDecimal[] integer(BigDecimal[] decs) {
        return map(decs, d -> d.setScale(0, RoundingMode.DOWN));
    }

    public static BigDecimal[] signum(BigDecimal[] decs) {
        return map(decs, d -> dec(d.signum()));
    }

    public static BigDecimal[] isEven(BigDecimal[] decs) {
        return map(decs, d -> dec(d.toBigIntegerExact().mod(BigInteger.TWO).signum() == 0));
    }

    public static BigDecimal[] isOdd(BigDecimal[] decs) {
        return map(decs, d -> dec(d.toBigIntegerExact().mod(BigInteger.TWO).signum() != 0));
    }

    static boolean isInt(BigDecimal d) {
        try {
            d.toBigIntegerExact();
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    public static BigDecimal[] isInt(BigDecimal[] decs) {
        return map(decs, d -> dec(isInt(d)));
    }

    public static BigDecimal[] abs(BigDecimal[] decs) {
        return map(decs, BigDecimal::abs);
    }

    public static BigDecimal[] reciprocal(BigDecimal[] decs) {
        return map(decs, d -> BigDecimalMath.reciprocal(d, MATH_CONTEXT));
    }

    public static BigDecimal[] not(BigDecimal[] decs) {
        return map(decs, a -> dec(!bool(a)));
    }

    public static BigDecimal[] sqrt(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.sqrt(a, MATH_CONTEXT));
    }

    public static BigDecimal[] square(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.pow(a, 2L, MATH_CONTEXT));
    }

    public static BigDecimal[] cube(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.pow(a, 3L, MATH_CONTEXT));
    }

    public static BigDecimal factorial(BigDecimal dec) {
        BigInteger n = dec.toBigIntegerExact();
        if (n.signum() < 0)
            n = BigInteger.ZERO;
        BigInteger r = BigInteger.ONE;
        for ( ; n.signum() > 0; n = n.subtract(BigInteger.ONE))
            r = r.multiply(n);
        return dec(r);
    }

    public static BigDecimal[] factorial(BigDecimal[] decs) {
        return map(decs, a -> factorial(a));
    }

    public static BigDecimal PI = BigDecimalMath.pi(MATH_CONTEXT);
    public static BigDecimal DEG180 = dec(180);
    public static BigDecimal PI_DIV_DEG180 = PI.divide(DEG180, MATH_CONTEXT);

    public static BigDecimal[] radian(BigDecimal[] decs) {
        return map(decs, a -> a.multiply(PI_DIV_DEG180, MATH_CONTEXT));
    }

    public static BigDecimal[] degree(BigDecimal[] decs) {
        return map(decs, a -> a.divide(PI_DIV_DEG180, MATH_CONTEXT));
    }

    public static BigDecimal[] sin(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.sin(a, MATH_CONTEXT));
    }

    public static BigDecimal[] cos(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.cos(a, MATH_CONTEXT));
    }

    public static BigDecimal[] tan(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.tan(a, MATH_CONTEXT));
    }

    public static BigDecimal[] ln(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.log(a, MATH_CONTEXT));
    }

    public static BigDecimal[] log10(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.log10(a, MATH_CONTEXT));
    }

    public static BigDecimal[] log2(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.log2(a, MATH_CONTEXT));
    }

    static LocalDate date(int yyyymmdd) {
        return LocalDate.of(yyyymmdd / 10000, yyyymmdd / 100 % 100, yyyymmdd % 100);
    }

    static int date(LocalDate date) {
        return date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
    }

    public static BigDecimal[] today() {
        return decs(date(LocalDate.now()));
    }

    public static BigDecimal[] days(BigDecimal[] decs) {
        return map(decs, a -> dec(date(a.intValueExact()).toEpochDay()));
    }

    public static BigDecimal[] date(BigDecimal[] decs) {
        return map(decs, a -> dec(date(LocalDate.ofEpochDay(a.intValueExact()))));
    }

    public static BigDecimal[] week(BigDecimal[] decs) {
        return map(decs, a -> dec(date(a.intValue()).getDayOfWeek().getValue()));
    }

    static BigDecimal isPrime(BigDecimal dec) {
        if (dec.compareTo(TWO) < 0)
            return FALSE;
        BigInteger i = dec.toBigIntegerExact();
        BigInteger max = i.sqrt();
        for (BigInteger d = BigInteger.TWO; d.compareTo(max) <= 0; d = d.add(BigInteger.ONE))
            if (i.remainder(d).equals(BigInteger.ZERO))
                return FALSE;
        return TRUE;
    }

    public static BigDecimal[] isPrime(BigDecimal[] decs) {
        return map(decs, d -> isPrime(d));
    }

    public static BigDecimal[] factor(BigDecimal[] decs) {
        BigInteger num = single(decs).toBigIntegerExact().abs();
        if (num.equals(BigInteger.ZERO))
            throw new ValueException("Cannot factor zero");
        List<BigDecimal> result = new ArrayList<>();
        BigInteger max = num.sqrt();
        for (BigInteger den = BigInteger.TWO; den.compareTo(max) <= 0; den = den.add(BigInteger.ONE)) {
            boolean divided = false;
            while (true) {
                BigInteger[] r = num.divideAndRemainder(den);
                // System.out.printf("%s/%s = %s...%s%n", num, den, r[0], r[1]);
                if (!r[1].equals(BigInteger.ZERO))
                    break;
                divided = true;
                num = r[0];
                result.add(new BigDecimal(den));
            }
            if (divided)
                max = num.sqrt();
        }
        if (!num.equals(BigInteger.ONE))
            result.add(dec(num));
        return decs(result);
    }

    public static BigDecimal[] divisor(BigDecimal[] decs) {
        BigInteger i = single(decs).toBigIntegerExact().abs();
        Set<BigInteger> set = new HashSet<>();
        if (i.equals(BigInteger.ZERO))
            set.add(BigInteger.ZERO);
        else 
            for (BigInteger j = i.sqrt(); j.compareTo(BigInteger.ZERO) > 0; j = j.subtract(BigInteger.ONE)) {
                BigInteger[] x = i.divideAndRemainder(j);
                if (x[1].equals(BigInteger.ZERO)) {
                    set.add(j);
                    set.add(x[0]);
                }
            }
        return decs(set.stream().sorted().map(j -> dec(j)));
    }

    // unary single method

    public static BigDecimal single(BigDecimal[] decs) {
        if (decs.length != 1)
            throw error("Single value expected but '%s'", string(decs));
        return decs[0];
    }

    public static BigDecimal[] iota(BigDecimal[] decs) {
        return decs(IntStream.rangeClosed(1, single(decs).intValue())
            .mapToObj(i -> dec(i)));
    }

    public static BigDecimal[] iota0(BigDecimal[] decs) {
        return decs(IntStream.rangeClosed(0, single(decs).intValue())
            .mapToObj(i -> dec(i)));
    }

    public static BigDecimal[] iotan(BigDecimal[] decs) {
        int n = single(decs).intValue();
        return decs(IntStream.rangeClosed(-n, n)
            .mapToObj(i -> dec(i)));
    }

    static void sieve(boolean[] primes, int n) {
        int size = primes.length;
        for (int i = n + n; i < size; i += n)
            primes[i] = true;
    }

    public static BigDecimal[] primes(BigDecimal[] decs) {
        int size = single(decs).intValue();
        boolean[] primes = new boolean[size];
        primes[0] = primes[1] = true;
        int max = (int)Math.sqrt(size);
        sieve(primes, 2);
        for (int i = 3; i <= max; i += 2)
            sieve(primes, i);
        return decs(IntStream.range(0, size)
            .filter(i -> !primes[i])
            .mapToObj(i -> dec(i)));
    }

    /**
     *        k
     * nCk = Π (n + 1 - i) / i
     *       i=1
     * 
     * or
     * 
     * nC0 = 1
     * nCk = nCk-1 * (n + 1 - k) / k
     * 
     * https://ja.wikipedia.org/wiki/二項係数
     */
    public static BigDecimal[] pascal(BigDecimal[] decs) {
        int n = single(decs).intValueExact();
        if (n < 0)
            throw error("must >= 0 but '%s'", string(decs));
        List<BigDecimal> result = new ArrayList<>();
        result.add(dec(1));
        BigInteger num = BigInteger.ONE, den = BigInteger.ONE;
        for (int i = 1; i <= n; ++i) {
            num = num.multiply(BigInteger.valueOf(n + 1 - i));
            den = den.multiply(BigInteger.valueOf(i));
            result.add(dec(num.divide(den)));
        }
        return decs(result);
    }

    // unary special method

    public static BigDecimal[] length(BigDecimal[] decs) {
        return decs(decs.length);
    }

    public static BigDecimal[] reverse(BigDecimal[] decs) {
        int length = decs.length;
        BigDecimal[] result = new BigDecimal[length];
        for (int i = 0, j = length - 1; i < length; ++i, --j)
            result[j] = decs[i];
        return result;
    }

    public static BigDecimal[] sort(BigDecimal[] decs) {
        return decs(Stream.of(decs).sorted());
    }

    public static BigDecimal[] uniq(BigDecimal[] decs) {
        return decs(Stream.of(decs).distinct());
    }

    public static BigDecimal[] different(BigDecimal[] decs) {
        Set<BigDecimal> map = new HashSet<>();
        for (BigDecimal d : decs)
            if (!map.add(d))
                return decs(FALSE);
        return decs(TRUE);
    }

    // binary zip method

    public static BigDecimal[] zip(BigDecimal[] left, BigDecimal[] right,
            BinaryOperator<BigDecimal> op) {
        int lsize = left.length, rsize = right.length;
        if (lsize == 0)
            return right;
        else if (rsize == 0)
            return left;
        else if (lsize == 1)
            return map(right, d -> op.apply(left[0], d));
        else if (rsize == 1)
            return map(left, d -> op.apply(d, right[0]));
        else if (lsize == rsize)
            return decs(IntStream.range(0, lsize)
                .mapToObj(i -> op.apply(left[i], right[i])));
        else
            throw error("zip: invalid length l='%s' r='%s'", string(left), string(right));
    }

    public static BigDecimal[] add(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, BigDecimal::add);
    }

    public static BigDecimal[] subtract(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, BigDecimal::subtract);
    }

    public static BigDecimal[] multiply(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, BigDecimal::multiply);
    }

    public static BigDecimal[] divide(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> a.divide(b, MATH_CONTEXT));
    }

    public static BigDecimal[] divideInt(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> dec(a.toBigIntegerExact().divide(b.toBigIntegerExact())));
    }

    public static BigDecimal[] mod(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> a.remainder(b, MATH_CONTEXT));
    }

    static BigDecimal permutation(BigDecimal n, BigDecimal r) {
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

    public static BigDecimal[] permutation(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, Decs::permutation);
    }

    static BigDecimal combination(BigDecimal n, BigDecimal r) {
        r = r.min(n.subtract(r));
        BigDecimal den = permutation(n, r);
        BigDecimal num = ONE;
        for (BigDecimal i = r; i.compareTo(ONE) > 0; i = i.subtract(ONE))
            num = num.multiply(i);
        return den.divide(num);
    }

    public static BigDecimal[] combination(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, Decs::combination);
    }

    public static BigDecimal[] pow(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) ->
            BigDecimalMath.isLongValue(b)
                ? BigDecimalMath.pow(a, b.longValue(), MATH_CONTEXT)
                : BigDecimalMath.pow(a, b, MATH_CONTEXT).stripTrailingZeros());
    }

    public static BigDecimal[] round(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> a.setScale(b.intValueExact(), RoundingMode.HALF_UP));
    }

    public static BigDecimal sign(int sign) {
        return sign < 0 ? MINUS_ONE
            : sign == 0 ? ZERO
            : ONE;
    }

    public static BigDecimal[] compare(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> sign(a.compareTo(b)));
    }

    public static BigDecimal[] compare(BigDecimal[] left, BigDecimal[] right, IntPredicate pred) {
        int lsize = left.length, rsize = right.length;
        if (lsize == 0 && rsize == 0)
            return decs(TRUE);
        else if (lsize == 1)
            return decs(dec(Stream.of(right).allMatch(r -> pred.test(left[0].compareTo(r)))));
        else if (rsize == 1)
            return decs(dec(Stream.of(left).allMatch(l -> pred.test(l.compareTo(right[0])))));
        else if (lsize == rsize)
            return decs(dec(IntStream.range(0, lsize).allMatch(i -> pred.test(left[i].compareTo(right[i])))));
        else
            throw error("Cannot compare '%s' and '%s'", string(left), string(right));
    }

    public static BigDecimal[] eq(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> c == 0);
    }

    public static BigDecimal[] ne(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> c != 0);
    }

    public static BigDecimal[] lt(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> c < 0);
    }

    public static BigDecimal[] le(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> c <= 0);
    }

    public static BigDecimal[] gt(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> c > 0);
    }

    public static BigDecimal[] ge(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> c >= 0);
    }

    static boolean nearlyEq(BigDecimal left, BigDecimal right) {
        return left.subtract(right).abs().compareTo(EPSILON) <= 0;
    }

    public static BigDecimal[] nearlyEq(BigDecimal[] left, BigDecimal[] right) {
        int lsize = left.length, rsize = right.length;
        if (lsize == 0 && rsize == 0)
            return decs(TRUE);
        else if (lsize == 1)
            return decs(dec(Stream.of(right).allMatch(r -> nearlyEq(left[0], r))));
        else if (rsize == 1)
            return decs(dec(Stream.of(left).allMatch(l -> nearlyEq(l, right[0]))));
        else if (lsize == rsize)
            return decs(dec(IntStream.range(0, lsize).allMatch(i -> nearlyEq(left[i], right[i]))));
        else
            throw error("Cannot compare '%s' and '%s'", string(left), string(right));
    }

    public static BigDecimal[] nearlyNe(BigDecimal[] left, BigDecimal[] right) {
        return decs(dec(single(nearlyEq(left, right)).equals(ZERO)));
    }

    public static boolean trues(BigDecimal[] decs) {
        return Stream.of(decs).allMatch(d -> !d.equals(ZERO));
    }

    public static boolean falses(BigDecimal[] decs) {
        return Stream.of(decs).allMatch(d -> d.equals(ZERO));
    }

    public static BigDecimal[] cand(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> bool(a) ? b : a);
    }

    public static BigDecimal[] cor(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> bool(a) ? a : b);
    }

    public static BigDecimal[] log(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> BigDecimalMath.log(a, MATH_CONTEXT)
            .divide(BigDecimalMath.log(b, MATH_CONTEXT), MATH_CONTEXT));
    }

    static BigDecimal gcd(BigDecimal a, BigDecimal b) {
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

    public static BigDecimal[] gcd(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, Decs::gcd);
    }

    public static BigDecimal[] gcd(BigDecimal[] decs) {
        return reduce(decs, Decs::gcd);
    }

    static BigDecimal lcm(BigDecimal a, BigDecimal b) {
        return a.multiply(b).divide(gcd(a, b), MATH_CONTEXT);
    }

    public static BigDecimal[] lcm(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, Decs::lcm);
    }

    public static BigDecimal[] lcm(BigDecimal[] decs) {
        return reduce(decs, Decs::lcm);
    }
    
    // binary special method

    public static BigDecimal[] to(BigDecimal[] left, BigDecimal[] right) {
        BigInteger begin = single(left).toBigIntegerExact();
        BigInteger end = single(right).toBigIntegerExact();
        List<BigDecimal> r = new ArrayList<>();
        if (begin.compareTo(end) <= 0)
            for (BigInteger i = begin; i.compareTo(end) <= 0; i = i.add(BigInteger.ONE))
                r.add(dec(i));
        else
            for (BigInteger i = begin; i.compareTo(end) >= 0; i = i.subtract(BigInteger.ONE))
                r.add(dec(i));
        return decs(r);
    }

    public static BigDecimal[] base(BigDecimal[] left, BigDecimal[] right) {
        if (Stream.of(right).anyMatch(d -> d.signum() <= 0))
            throw error("base: right must all positive but %s", string(right));
        BigDecimal m = single(left);
        if (m.signum() < 0)
            throw error("base: left must not negative but %s", string(left));
        Deque<BigDecimal> r = new LinkedList<>();
        BigDecimal[] dr;
        if (right.length == 1) {
            for (BigDecimal base = right[0]; m.signum() > 0; m = dr[0], r.addFirst(dr[1])) 
                dr = m.divideAndRemainder(base);
        } else {
            for (int i = right.length - 1; i >= 0 && m.signum() > 0; --i, m = dr[0], r.addFirst(dr[1])) 
                dr = m.divideAndRemainder(right[i]);
            if (m.signum() != 0)
                r.addFirst(m);
        }
        if (r.size() == 0)
            r.addFirst(ZERO);
        return decs(r);
    }

    /**
     * case: (L0, L1, ... , Ln) decimal (R0)
     *    result = 0
     *    for i=0 to n result = result * R0 + Li
     * case: (L0, L1, ... , Ln) decimal (R0, R1, ... , Rn)
     *    result = 0
     *    for i=0 to n result = result * Ri + Li
     * case: (L0, L1, ... , Ln, Ln+1) decimal (R0, R1, ... , Rn)
     *    result = L0
     *    for i=0 to n result = result * Ri + Li+1
     */
    public static BigDecimal[] decimal(BigDecimal[] left, BigDecimal[] right) {
        if (Stream.of(left).anyMatch(d -> d.signum() < 0))
            throw error("base: left must not negative but %s", string(left));
        if (Stream.of(right).anyMatch(d -> d.signum() <= 0))
            throw error("base: right must all positive but %s", string(right));
        int lsize = left.length, rsize = right.length;
        BigDecimal result = BigDecimal.ZERO;
        if (rsize == 1)
            for (int i = 0; i < lsize; ++i)
                result = result.multiply(right[0]).add(left[i]);
        else if (lsize == rsize)
            for (int i = 0; i < rsize; ++i)
                result = result.multiply(right[i]).add(left[i]);
        else if (lsize == rsize + 1) {
            result = left[0];
            for (int i = 0; i < rsize; ++i)
                result = result.multiply(right[i]).add(left[i + 1]);
        } else
            throw error("Illegal length left=%s right=%s", string(left), string(right));
        return decs(result);
    }

    public static BigDecimal[] concat(BigDecimal[] left, BigDecimal[] right) {
        BigDecimal[] result = new BigDecimal[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    public static BigDecimal[] remove(BigDecimal[] left, BigDecimal[] right) {
        Set<BigDecimal> set = Set.of(right);
        return decs(Stream.of(left).filter(d -> !set.contains(d)));
    }

    public static BigDecimal[] get(BigDecimal[] left, BigDecimal[] right) {
        return decs(Stream.of(right)
            .map(i -> {
                int index = i.intValueExact() - 1;
                if (index < 0 || index >= left.length)
                    throw error("Cannot get %s for '%s'", i, string(left));
                return left[index];
            }));
    }

    public static BigDecimal[] take(BigDecimal[] left, BigDecimal[] right) {
        int length = left.length, sub = single(right).intValueExact();
        return sub >= 0
            ? Arrays.copyOfRange(left, 0, Math.min(sub, length))
            : Arrays.copyOfRange(left, Math.max(length + sub, 0), length);
    }

    public static BigDecimal[] polyAdd(BigDecimal[] left, BigDecimal[] right) {
        int ll = left.length, rl = right.length;
        if (ll < rl)
            return polyAdd(right, left);
        BigDecimal[] result = left.clone();
        for (int i = 0, j = ll - rl; i < rl; ++i, ++j)
            result[j] = result[j].add(right[i], MATH_CONTEXT);
        return result;
    }

    public static BigDecimal[] polyMult(BigDecimal[] left, BigDecimal[] right) {
        int ll = left.length, rl = right.length;
        BigDecimal[] result = new BigDecimal[ll + rl -1];
        Arrays.fill(result, ZERO);
        for (int i = 0; i < ll; ++i)
            for (int j = 0, l = i; j < rl; ++j, ++l)
                result[l] = result[l].add(left[i].multiply(right[j], MATH_CONTEXT), MATH_CONTEXT);
        return result;
    }

    public static BigDecimal[] polyPow(BigDecimal[] left, BigDecimal[] right) {
        int pow = single(right).intValueExact();
        BigDecimal[] result = new BigDecimal[] {ONE};
        for (int i = 0; i < pow; ++i)
            result = polyMult(result, left);
        return result;
    }

    public static BigDecimal[] removeLeadingZeros(BigDecimal[] decs) {
        int len = decs.length, p = 0;
        while (p < len - 1 && decs[p].equals(ZERO))
            ++p;
        return Arrays.copyOfRange(decs, p, len);
    }

    public static BigDecimal[][] polyDivide(BigDecimal[] left, BigDecimal[] right) {
        int ll = left.length, rl = right.length;
        int max = ll - rl + 1;
        BigDecimal[] amari = left.clone();
        BigDecimal[] syo = new BigDecimal[max];
        for (int i = 0; i < max; ++i) {
            BigDecimal d = amari[i].divide(right[0], MATH_CONTEXT);
            syo[i] = d;
            for (int j = 0; j < rl; ++j) {
                System.out.printf("i = %d j = %d i + j = %d%n", i, j, i + j);
                amari[i + j] = amari[i + j].subtract(right[j].multiply(d, MATH_CONTEXT), MATH_CONTEXT);
            }
        }
        return new BigDecimal[][] {syo, removeLeadingZeros(amari)};
    }
}
