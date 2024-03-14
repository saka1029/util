package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

public class Operators {
    final Map<String, UnaryOperator<Expression>> unaryOperators = new HashMap<>();

    public static Operators of() {
        Operators r = new Operators();
        r.init();
        return r;
    }

    public UnaryOperator<Expression> unary(String name) {
        return unaryOperators.get(name);
    }

    public void unary(String name, UnaryOperator<Expression> body) {
        unaryOperators.put(name, body);
    }

    public Set<String> names() {
        return unaryOperators.keySet();
    }

    static Vector evalOne(Expression e, Context c) {
        Vector v = e.eval(c);
        if (v.length() != 1)
            throw new VectorException("Required one argument but %d", v.length());
        return v;
    }

    static UnaryOperator<BigDecimal> unaryDouble(DoubleUnaryOperator operator) {
        return b -> Vector.number(operator.applyAsDouble(b.doubleValue()));
    }

    void init() {
        unaryOperators.put("-", e -> c -> e.eval(c).apply(a -> a.negate()));
        unaryOperators.put("sum", e -> c -> e.eval(c).insert((a, b) -> a.add(b)));
        unaryOperators.put("+", e -> c -> e.eval(c).insert((a, b) -> a.add(b)));
        unaryOperators.put("*", e -> c -> e.eval(c).insert((a, b) -> a.multiply(b)));
        unaryOperators.put("length", e -> c -> Vector.of(e.eval(c).length()));
        unaryOperators.put("reverse", e -> c -> e.eval(c).reverse());
        unaryOperators.put("sort", e -> c -> e.eval(c).sort());
        unaryOperators.put("iota", e -> c -> Vector.iota(evalOne(e, c).get(0).intValue(), 1));
        unaryOperators.put("sqrt", e -> c -> e.eval(c).apply(a -> a.sqrt(Vector.MATH_CONTEXT)));
        unaryOperators.put("abs", e -> c -> e.eval(c).apply(BigDecimal::abs));
        unaryOperators.put("sin", e -> c -> e.eval(c).apply(unaryDouble(Math::sin)));
        unaryOperators.put("asin", e -> c -> e.eval(c).apply(unaryDouble(Math::asin)));
        unaryOperators.put("cos", e -> c -> e.eval(c).apply(unaryDouble(Math::cos)));
        unaryOperators.put("acos", e -> c -> e.eval(c).apply(unaryDouble(Math::acos)));
        unaryOperators.put("tan", e -> c -> e.eval(c).apply(unaryDouble(Math::tan)));
        unaryOperators.put("atan", e -> c -> e.eval(c).apply(unaryDouble(Math::atan)));
        unaryOperators.put("log", e -> c -> e.eval(c).apply(unaryDouble(Math::log)));
        unaryOperators.put("log10", e -> c -> e.eval(c).apply(unaryDouble(Math::log10)));
    }

}
