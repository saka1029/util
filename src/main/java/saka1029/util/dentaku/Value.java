package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Value implements Expression {
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

    static boolean bool(BigDecimal d) {
        return !d.equals(BigDecimal.ZERO);
    }

    public Value filter(Value right) {
        if (elements.length == 1)
            return bool(elements[0]) ? right : EMPTY;
        else if (elements.length == right.elements.length) {
            List<BigDecimal> result = new ArrayList<>();
            for (int i = 0; i < elements.length; ++i)
                if (bool(elements[i]))
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
        if (v.compareTo(BigDecimal.ONE) <= 0)
            return false;
        if (v.compareTo(BigDecimal.TWO) == 0)
            return true;
        BigDecimal max = v.sqrt(MATH_CONTEXT);
        for (BigDecimal d = BigDecimal.TWO; d.compareTo(max) <= 0; d = d.add(BigDecimal.ONE))
            if (v.remainder(d, MATH_CONTEXT).equals(BigDecimal.ZERO))
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
                            BigDecimal[] r = d.divideAndRemainder(f, MATH_CONTEXT);
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
