package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Context {

    final Map<String, Help<Expression>> variables = new HashMap<>();
    final Map<String, Help<Unary>> unarys = new HashMap<>();
    final Map<String, Help<Binary>> binarys = new HashMap<>();

    public boolean isVariable(String name) {
        return variables.containsKey(name);
    }

    public Help<Expression> variable(String name) {
        Help<Expression> r = variables.get(name);
        if (r == null)
            throw new DecsException("variable '%s' undef", name);
        return r;
    }

    public boolean isUnary(String name) {
        return unarys.containsKey(name);
    }

    public Help<Unary> unary(String name) {
        Help<Unary> r = unarys.get(name);
        if (r == null)
            throw new DecsException("unary '%s' undef", name);
        return r;
    }

    public boolean isBinary(String name) {
        return binarys.containsKey(name);
    }

    public Help<Binary> binary(String name) {
        Help<Binary> r = binarys.get(name);
        if (r == null)
            throw new DecsException("binary '%s' undef", name);
        return r;
    }

    public static interface Undo extends AutoCloseable {
        void close();
    }

    static <T> void put(Map<String, T> map, String key, T value) {
        if (value == null)
            map.remove(key);
        else
            map.put(key, value);
    }

    public void variable(String name, Expression expression, String help) {
        put(variables, name, new Help<>(expression, help));
        put(unarys, name, null);
        put(binarys, name, null);
    }

    public Undo variableTemp(String name, Expression expression, String help) {
        Help<Expression> oldVariable = variables.get(name);
        Help<Unary> oldUnary = unarys.get(name);
        Help<Binary> oldBinary = binarys.get(name);
        variable(name, expression, help);
        return () -> {
            put(variables, name, oldVariable);
            put(unarys, name, oldUnary);
            put(binarys, name, oldBinary);
        };
    }

    public void unary(String name, Unary unary, String help) {
        put(unarys, name, new Help<>(unary, help));
        put(variables, name, null);
    }

    public void binary(String name, Binary binary, String help) {
        put(binarys, name, new Help<>(binary, help));
        put(variables, name, null);
    }

    public int solve(Expression expression, Consumer<String> output) {
        if (!(expression instanceof ExpressionWithVariables exvar))
            throw new DecsException("Cannot solve");
        Expression ex = exvar.expression;
        List<String> names = exvar.variables.stream()
            .distinct().toList();
        int length = names.size();
        List<BigDecimal[]> values = names.stream()
            .map(n -> variable(n).expression.apply(this))
            .toList();
        // backup
        List<Help<Expression>> backup = names.stream()
            .map(n -> variables.get(n))
            .toList();
        int[] count = {0};
        new Object() {

            void test() {
                BigDecimal[] r = ex.apply(Context.this);
                if (r.length < 1 || !Decs.bool(r[0]))
                    return;
                ++count[0];
                String result = names.stream()
                    .map(n -> n + "=" + Decs.string(variable(n).expression.apply(Context.this)))
                    .collect(Collectors.joining(" "));
                output.accept(result);
            }

            void solve(int index) {
                if (index >= length)
                    test();
                else {
                    BigDecimal[] decs = values.get(index);
                    for (int i = 0, max = decs.length; i < max; ++i) {
                        String name = names.get(index);
                        BigDecimal[] value = Decs.decs(decs[i]);
                        variable(name, c -> value, name);
                        solve(index + 1);
                    }
                }
            }
        }.solve(0);
        // restore
        IntStream.range(0, length) 
            .forEach(i -> variables.put(names.get(i), backup.get(i)));
        return count[0];
    }


}
