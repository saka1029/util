package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Context {

    final Map<String, Help<Expression>> variables = new HashMap<>();
    final Map<String, Help<Unary>> unarys = new HashMap<>();
    final Map<String, Help<Binary>> binarys = new HashMap<>();
    public Consumer<String> solverOutput = System.out::println;

    public Context() {
        init();
    }

    public boolean isVariable(String name) {
        return variables.containsKey(name);
    }

    public Help<Expression> variable(String name) {
        Help<Expression> r = variables.get(name);
        if (r == null)
            throw new UndefException("variable '%s' undef", name);
        return r;
    }

    public boolean isUnary(String name) {
        return unarys.containsKey(name);
    }

    public Help<Unary> unary(String name) {
        Help<Unary> r = unarys.get(name);
        if (r == null)
            throw new UndefException("unary '%s' undef", name);
        return r;
    }

    public boolean isBinary(String name) {
        return binarys.containsKey(name);
    }

    public Help<Binary> binary(String name) {
        Help<Binary> r = binarys.get(name);
        if (r == null)
            throw new UndefException("binary '%s' undef", name);
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

    public int solve(Expression expression) {
        return solve(expression, m -> solverOutput.accept(
            m.entrySet().stream()
                .map(e -> e.getKey() + "=" + Decs.string(e.getValue()))
                .collect(Collectors.joining(" "))));
    }

    public int solve(Expression expression, Consumer<Map<String, BigDecimal>> out) {
        if (!(expression instanceof ExpressionWithVariables exvar))
            throw new DecsException("Cannot solve");
        Context context = Context.this;
        List<String> names = exvar.variables.stream()
            .distinct().toList();
        int length = names.size();
        List<BigDecimal[]> values = names.stream()
            .map(n -> context.variable(n).expression.eval(context))
            .toList();
        // backup
        List<Help<Expression>> backup = names.stream()
            .map(n -> context.variables.get(n))
            .toList();
        var solver = new Object() {
            int count = 0;
            Map<String, BigDecimal> map = new TreeMap<>();

            void test() {
                BigDecimal[] result = exvar.expression.eval(context);
                if (result.length < 1 || !Decs.bool(result[0]))
                    return;
                ++count;
                map.clear();
                for (String n : names)
                    map.put(n, context.variable(n).expression.eval(context)[0]);
                out.accept(map);
            }

            void solve(int index) {
                if (index >= length)
                    test();
                else {
                    String name = names.get(index);
                    BigDecimal[] decs = values.get(index);
                    for (int i = 0, max = decs.length; i < max; ++i) {
                        BigDecimal[] value = Decs.decs(decs[i]);
                        context.variable(name, c -> value, name);
                        solve(index + 1);
                    }
                }
            }
        };
        solver.solve(0);
        // restore
        IntStream.range(0, length) 
            .forEach(i -> context.variables
                .put(names.get(i), backup.get(i)));
        return solver.count;
    }

    void init() {
        unary("!", (c, a) -> Decs.not(a), "! (B) -> (B) : not");
        unary("+", (c, a) -> Decs.add(a), "+ (A) -> D : +");
        unary("-", (c, a) -> Decs.subtract(a), "- (A) -> D : -");
        unary("*", (c, a) -> Decs.multiply(a), "* (A) -> D : *");
        unary("/", (c, a) -> Decs.divide(a), "/ (A) -> D : /");
        unary("|", (c, a) -> Decs.or(a), "| (B) -> B : or");
        unary("&", (c, a) -> Decs.and(a), "& (B) -> B : and");
        unary("abs", (c, a) -> Decs.abs(a), "abs (A) -> (D) : |A|");
        unary("cos", (c, a) -> Decs.cos(a), "cos (A) -> (D) : cos A");
        unary("degree", (c, a) -> Decs.degree(a), "degree (A) -> (D) : A / π * 180");
        variable("E", c -> Decs.e(), "E -> D : Euler's number");
        unary("factorial", (c, a) -> Decs.factorial(a), "factorial (D) -> (D) : factorial");
        unary("iota", (c, a) -> Decs.iota(a), "iota I -> (I) : (1..I)");
        unary("iota0", (c, a) -> Decs.iota0(a), "iota0 I -> (I) : (0..I)");
        unary("length", (c, a) -> Decs.length(a), "length (D) -> (D) : lenfth of (D)");
        unary("ln", (c, a) -> Decs.ln(a), "ln (D) -> (D) : log E");
        binary("log", (c, a, b) -> Decs.log(a, b), "(A) log (B) -> (D) : log B A");
        unary("log10", (c, a) -> Decs.log10(a), "log10 (A) -> (D) : log 10 A");
        unary("log2", (c, a) -> Decs.log2(a), "log2 (A) -> (D) : log 2 A");
        unary("negate", (c, a) -> Decs.negate(a), "negate (A) -> (D) : -A");
        variable("PI", c -> Decs.pi(), "PI -> D : π");
        unary("pow", (c, a) -> Decs.pow(a), "pow (A) -> D : power");
        binary("pow", (c, a, b) -> Decs.pow(a, b), "(A) pow (B) -> (D) : A ^ B");
        unary("primes", (c, a) -> Decs.primes(a), "primes (A) -> (D) : primes frm 2 to A");
        unary("radian", (c, a) -> Decs.radian(a), "radian (A) -> (D) : A / 180 * π");
        unary("reciprocal", (c, a) -> Decs.reciprocal(a), "reciprocal (A) -> (D) : 1 / A");
        unary("reverse", (c, a) -> Decs.reverse(a), "reverse (A) -> (D) : reverse");
        unary("sin", (c, a) -> Decs.sin(a), "sin (A) -> (D) : sin A");
        unary("sort", (c, a) -> Decs.sort(a), "sort (D) -> (D) : sort");
        unary("tan", (c, a) -> Decs.tan(a), "tan (A) -> (D) : tan A");
    }
}
