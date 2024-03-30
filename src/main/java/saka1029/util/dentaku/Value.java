package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Value implements Expression {
    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal ONE = BigDecimal.ONE;
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
    // public static final MathContext MATH_CONTEXT = new MathContext(500, RoundingMode.HALF_EVEN);
    public static final Value NaN = new Value();
    public static final Value EMPTY = new Value();

    private final BigDecimal[] elements;

    Value(BigDecimal... elements) {
        this.elements = elements;
    }

    Value(List<BigDecimal> list) {
        this.elements = list.toArray(BigDecimal[]::new);
    }

    public static Value of(BigDecimal... elements) {
        return new Value(elements.clone());
    }

    public static Value of(List<BigDecimal> list) {
        return new Value(list.toArray(BigDecimal[]::new));
    }

    public static boolean b(BigDecimal d) {
        return d.compareTo(BigDecimal.ZERO) != 0;
    }

    @Override
    public Value eval(Context context) {
        return this;
    }

    public int size() {
        return elements.length;
    }

    public Value append(Value right) {
        int lSize = elements.length, rSize = right.elements.length;
        BigDecimal[] n = new BigDecimal[lSize + rSize];
        System.arraycopy(elements, 0, n, 0, lSize);
        System.arraycopy(right.elements, 0, n, lSize, rSize);
        return new Value(n);
    }

    public BigDecimal oneElement() {
        if (elements.length != 1)
            throw new ValueException("One element expected but '%s'", this);
        return elements[0];
    }

    public Value map(UnaryOperator<BigDecimal> operator) {
        return new Value(Arrays.stream(elements)
            .map(e -> operator.apply(e))
            .toArray(BigDecimal[]::new));
    }

    public Value reduce(Context context, Binary operator) {
        if (elements.length <= 0)
            return EMPTY;
        Value result = Value.of(elements[0]);
        for (int i = 1; i < elements.length; ++i)
            result = operator.apply(context, result, Value.of(elements[i]));
        return result;
    }


    public Value reduceRight(Context context, Binary operator) {
        int length = elements.length;
        if (length <= 0)
            return EMPTY;
        Value result = Value.of(elements[length - 1]);
        for (int i = length - 2; i >= 0; --i)
            result = operator.apply(context, Value.of(elements[i]), result);
        return result;
    }

    public Value cumulate(Context context, Binary operator) {
        if (elements.length <= 0)
            return EMPTY;
            // throw new ValueException("Empty value");
        Value v = Value.of(elements[0]);
        Value result = v;
        for (int i = 1; i < elements.length; ++i)
            result = result.append(v = operator.apply(context, v, Value.of(elements[i])));
        return result;
    }

    public Value binary(BinaryOperator<BigDecimal> operator, Value right) {
        if (elements.length == 1) 
            return new Value(Arrays.stream(right.elements) 
                .map(e -> operator.apply(elements[0], e)) 
                .toArray(BigDecimal[]::new)); 
        else if (right.elements.length == 1) 
            return new Value(Arrays.stream(elements) 
                .map(e -> operator.apply(e, right.elements[0])) 
                .toArray(BigDecimal[]::new)); 
        else if (right.elements.length == elements.length) 
            return new Value(IntStream.range(0, elements.length) 
                .mapToObj(i -> operator.apply(elements[i], right.elements[i])) 
                .toArray(BigDecimal[]::new)); 
        else 
            throw new ValueException("Length mismatch %d and %d", elements.length, right.elements.length);
    }

    public Value to(Value right) {
        BigDecimal start = this.oneElement();
        BigDecimal end = right.oneElement();
        List<BigDecimal> list = new ArrayList<>();
        if (start.compareTo(end) <= 0)
            for (BigDecimal i = start; i.compareTo(end) <= 0; i = i.add(BigDecimal.ONE))
                list.add(i);
        else
            for (BigDecimal i = start; i.compareTo(end) >= 0; i = i.subtract(BigDecimal.ONE))
                list.add(i);
        return new Value(list);
    }

    public Value at(Value right) {
        List<BigDecimal> result = new ArrayList<>();
        for (BigDecimal b : right.elements) {
            int index = b.intValueExact();
            result.add(elements[index >= 0 ? index : elements.length + index]);
        }
        return Value.of(result);
    }

    public Value filter(Value right) {
        if (elements.length == 1)
            return b(elements[0]) ? right : EMPTY;
        else if (elements.length == right.elements.length) {
            List<BigDecimal> result = new ArrayList<>();
            for (int i = 0; i < elements.length; ++i)
                if (b(elements[i]))
                    result.add(right.elements[i]);
            return new Value(result.toArray(BigDecimal[]::new));
        } else
            throw new ValueException("Length mismatch %d and %d", elements.length, right.elements.length);
    }

    public Value sort() {
        return Value.of(Arrays.stream(elements)
            .sorted()
            .toArray(BigDecimal[]::new));
    }

    public Value reverse() {
        BigDecimal[] result = elements.clone();
        Collections.reverse(Arrays.asList(result));
        return Value.of(result);
    }

    public Value shuffle() {
        BigDecimal[] result = elements.clone();
        Collections.shuffle(Arrays.asList(result));
        return Value.of(result);
    }

    public static boolean isPrime(BigDecimal v) {
        BigInteger i = v.toBigIntegerExact();
        int comp2 = i.compareTo(BigInteger.TWO);
        if (comp2 < 0)
            return false;
        else if (comp2 == 0)
            return true;
        BigInteger max = i.sqrt();
        for (BigInteger d = BigInteger.TWO; d.compareTo(max) <= 0; d = d.add(BigInteger.ONE))
            if (i.remainder(d).equals(BigInteger.ZERO))
                return false;
        return true;
    }

    public Value prime() {
        List<BigDecimal> result = new ArrayList<>();
        for (BigDecimal d : elements)
            if (isPrime(d))
                result.add(d);
        return Value.of(result);
    }

    public Value factor() {
        BigDecimal d = oneElement();
        List<BigDecimal> result = new ArrayList<>();
        switch (d.signum()) {
            case 0:
                result.add(BigDecimal.ZERO);
                break;
            case -1:
                result.add(BigDecimal.ONE.negate());
                d = d.negate();
                // thru
            default:
                BigDecimal max = d.sqrt(MATH_CONTEXT);
                for (BigDecimal f = BigDecimal.TWO; f.compareTo(max) <= 0; f = f.add(BigDecimal.ONE)) {
                    while (true) {
                        BigDecimal[] r = d.divideAndRemainder(f);
                        if (!r[1].equals(BigDecimal.ZERO))
                            break;
                        d = r[0];
                        result.add(f);
                    }
                }
                if (!d.equals(BigDecimal.ONE))
                    result.add(d);
        }
        return Value.of(result);
    }

    public static BigDecimal fib(BigDecimal n) {
        BigInteger i = n.toBigIntegerExact();
        if (i.compareTo(BigInteger.ONE) <= 0)
            return n;
        BigInteger x = BigInteger.ZERO, y = BigInteger.ONE;
        while (i.compareTo(BigInteger.ONE) > 0) {
            var t = x.add(y);
            x = y;
            y = t;
            i = i.subtract(BigInteger.ONE);
        }
        return new BigDecimal(y);
    }

    public static BigDecimal permutation(BigDecimal n, BigDecimal r) {
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

    public static BigDecimal combination(BigDecimal n, BigDecimal r) {
        r = r.min(n.subtract(r));
        BigDecimal den = permutation(n, r);
        BigDecimal num = ONE;
        for (BigDecimal i = r; i.compareTo(ONE) > 0; i = i.subtract(ONE))
            num = num.multiply(i);
        return den.divide(num);
    }

    static final BigDecimal POW_MAX = new BigDecimal(Integer.MAX_VALUE);

    public static BigDecimal pow(BigDecimal a, BigDecimal b) {
        if (b.setScale(0, RoundingMode.DOWN).equals(b)
            && b.compareTo(BigDecimal.ZERO) > 0
            && b.compareTo(POW_MAX) <= 0)
            return a.pow(b.intValue());
        else
            return new BigDecimal(Math.pow(a.doubleValue(), b.doubleValue()));
    }

    public static LocalDate date(BigDecimal d) {
        int date = d.intValueExact();
        return LocalDate.of(date / 10000, date / 100 % 100, date % 100);
    }

    public static LocalDate dateFromDays(BigDecimal days) {
        return LocalDate.ofEpochDay(days.intValueExact());
    }

    public static BigDecimal dec(LocalDate date) {
        return BigDecimal.valueOf(
            ((date.getYear() * 100) + date.getMonthValue()) * 100 + date.getDayOfMonth());
    }

    public static BigDecimal year(LocalDate date) {
        return BigDecimal.valueOf(date.getYear());
    }

    public static BigDecimal month(LocalDate date) {
        return BigDecimal.valueOf(date.getMonthValue());
    }

    public static BigDecimal day(LocalDate date) {
        return BigDecimal.valueOf(date.getDayOfMonth());
    }

    public static BigDecimal week(LocalDate date) {
        return BigDecimal.valueOf(date.getDayOfWeek().getValue());
    }

    public static BigDecimal days(LocalDate date) {
        return BigDecimal.valueOf(date.getLong(ChronoField.EPOCH_DAY));
    }

    public static BigDecimal gcd(BigDecimal a, BigDecimal b) {
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

    public static BigDecimal lcm(BigDecimal a, BigDecimal b) {
        return a.multiply(b).divide(gcd(a, b));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Value v && Arrays.equals(elements, v.elements);
    }

    @Override
    public String toString() {
        return elements.length == 0 ? "EMPTY"
            : Arrays.stream(elements)
                .map(d -> d.toString())
                .collect(Collectors.joining(" "));
    }
}
