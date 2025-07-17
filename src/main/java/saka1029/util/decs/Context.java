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
    final Map<String, Help<Unary>> builtinUnarys = new HashMap<>();
    final Map<String, Help<Binary>> builtinBinarys = new HashMap<>();
    public Consumer<String> output = System.out::println;

    public Context() {
        init();
    }

    public boolean isVariable(String name) {
        return variables.containsKey(name);
    }

    public Help<Expression> variable(String name) {
        Help<Expression> r = variables.get(name);
        if (r == null)
            throw new UndefException("variable '%s' undefined", name);
        return r;
    }

    public boolean isUnary(String name) {
        return unarys.containsKey(name);
    }

    public Help<Unary> unary(String name) {
        Help<Unary> r = unarys.get(name);
        if (r == null)
            throw new UndefException("unary '%s' undefined", name);
        return r;
    }

    public boolean isBinary(String name) {
        return binarys.containsKey(name);
    }

    public Help<Binary> binary(String name) {
        Help<Binary> r = binarys.get(name);
        if (r == null)
            throw new UndefException("binary '%s' undefined", name);
        return r;
    }

    public boolean isBuiltinUnary(String name) {
        return builtinUnarys.containsKey(name);
    }

    public Help<Unary> builtinUnary(String name) {
        Help<Unary> r = builtinUnarys.get(name);
        if (r == null)
            throw new UndefException("binary '%s' undefined", name);
        return r;
    }

    public boolean isBuiltinBinary(String name) {
        return builtinBinarys.containsKey(name);
    }

    public Help<Binary> builtinBinary(String name) {
        Help<Binary> r = builtinBinarys.get(name);
        if (r == null)
            throw new UndefException("binary '%s' undefined", name);
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

    public void builtinUnary(String name, Unary unary, String help) {
        put(builtinUnarys, name, new Help<>(unary, help));
        put(variables, name, null);
    }

    public void builtinBinary(String name, Binary binary, String help) {
        put(builtinBinarys, name, new Help<>(binary, help));
        put(variables, name, null);
    }

    public int solve(Expression expression) {
        return solve(expression, m -> output.accept(
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
        int[] count = {0};
        try {
            new Object() {
                Map<String, BigDecimal> map = new TreeMap<>();

                void test() {
                    try {
                        BigDecimal[] result = exvar.expression.eval(context);
                        if (result.length < 1 || !Decs.bool(result[0]))
                            return;
                    } catch (ValueException | ArithmeticException e) {
                        return;
                    }
                    ++count[0];
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
            }.solve(0);
        } finally {
            // restore
            IntStream.range(0, length) 
                .forEach(i -> context.variables
                    .put(names.get(i), backup.get(i)));
        }
        return count[0];
    }

    private void init() {
        builtinUnary("!", (c, a) -> Decs.not(a), "! (C) -> (B) : not B");
        unary("+", (c, a) -> Decs.add(a), "+ (A) -> D : +");
        builtinBinary("+", (c, a, b) -> Decs.add(a, b), "(A) + (B) -> (D) : A + B");
        unary("-", (c, a) -> Decs.subtract(a), "- (A) -> D : -");
        builtinBinary("-", (c, a, b) -> Decs.subtract(a, b), "(A) - (B) -> (D) : A - B");
        unary("*", (c, a) -> Decs.multiply(a), "* (A) -> D : *");
        builtinBinary("*", (c, a, b) -> Decs.multiply(a, b), "(A) * (B) -> (D) : A * B");
        unary("/", (c, a) -> Decs.divide(a), "/ (A) -> D : /");
        builtinBinary("/", (c, a, b) -> Decs.divide(a, b), "(A) / (B) -> (D) : A / B");
        builtinBinary("%", (c, a, b) -> Decs.mod(a, b), "(A) % (B) -> (D) : modulo A by B");
        unary("^", (c, a) -> Decs.pow(a), "^ (A) -> D : power");
        unary("^^", (c, a) -> Decs.xor(a), "^^ (M) -> (I) : xor");
        builtinBinary("^^", (c, a, b) -> Decs.xor(a, b), "(M) ^ (N) -> (I) : xor");
        builtinBinary("^", (c, a, b) -> Decs.pow(a, b), "(A) ^ (B) -> (D) : A ^ B");
        unary("|", (c, a) -> Decs.or(a), "| (B) -> B : or");
        builtinBinary("|", (c, a, b) -> Decs.or(a, b), "(A) | (B) -> (D) : bit or A B");
        builtinBinary("||", (c, a, b) -> Decs.cor(a, b), "(A) || (B) -> (D) : conditional or A B");
        builtinBinary("==", (c, a, b) -> Decs.eq(a, b), "(A) == (B) -> (D) : A equal B");
        builtinBinary("!=", (c, a, b) -> Decs.ne(a, b), "(A) != (B) -> (D) : A not equal B");
        builtinBinary("<", (c, a, b) -> Decs.lt(a, b), "(A) < (B) -> (D) : A less than B");
        builtinBinary("<=", (c, a, b) -> Decs.le(a, b), "(A) <= (B) -> (D) : A less than or equal B");
        builtinBinary(">", (c, a, b) -> Decs.gt(a, b), "(A) > (B) -> (D) : A greater than B");
        builtinBinary(">=", (c, a, b) -> Decs.ge(a, b), "(A) >= (B) -> (D) : A greater than or equal B");
        unary("&", (c, a) -> Decs.and(a), "& (M) -> I : bit and");
        builtinBinary("&", (c, a, b) -> Decs.and(a, b), "(M) & (N) -> (I) : bit and");
        builtinBinary("&&", (c, a, b) -> Decs.cand(a, b), "(A) && (B) -> (D) : conditional and A B");
        builtinBinary(",", (c, a, b) -> Decs.concat(a, b), "(A) , (B) -> (D) : concat (A) and (B)");
        unary("abs", (c, a) -> Decs.abs(a), "abs (A) -> (D) : |A|");
        binary("base", (c, a, b) -> Decs.base(a, b), "A base (B) -> (D) : A to base B");
        unary("cos", (c, a) -> Decs.cos(a), "cos (A) -> (D) : cos A");
        unary("count", (c, a) -> Decs.length(a), "count (D) -> (D) : number of elements in (D)");
        unary("cube", (c, a) -> Decs.cube(a), "cube (A) -> (D) : A³");
        unary("date", (c, a) -> Decs.date(a), "date (M) -> (I) : epoch day to YYYYMMDD");
        unary("days", (c, a) -> Decs.days(a), "days (N) -> (I) : YYYYMMDD to epoch day");
        binary("decimal", (c, a, b) -> Decs.decimal(a, b), "(A) decimal (B) -> D : (A) in base (B) to decimal");
        unary("degree", (c, a) -> Decs.degree(a), "degree (A) -> (D) : 180A/π");
        unary("divisor", (c, a) -> Decs.divisor(a), "divisor N -> (I) : divisors of N");
        variable("E", c -> Decs.e(), "E -> D : Euler's number");
        unary("factor", (c, a) -> Decs.factor(a), "factor N -> (I) : factor of N");
        unary("factorial", (c, a) -> Decs.factorial(a), "factorial (D) -> (D) : factorial");
        unary("gcd", (c, a) -> Decs.gcd(a), "gcd (N) -> (I) : GCD");
        binary("gcd", (c, a, b) -> Decs.gcd(a, b), "(M) gcd (N) -> (I) : GCD");
        unary("iota", (c, a) -> Decs.iota(a), "iota N -> (I) : (1..N)");
        unary("iota0", (c, a) -> Decs.iota0(a), "iota0 N -> (I) : (0..N)");
        unary("iotan", (c, a) -> Decs.iotan(a), "iotan N -> (I) : (-N..N)");
        unary("iseven", (c, a) -> Decs.isEven(a), "iseven (N) -> (B) : is even (T:1, F:0)");
        unary("isodd", (c, a) -> Decs.isOdd(a), "isodd (N) -> (B) : is odd (T:1, F:0)");
        // parser.eval("isperfect n = + (divisor n remove n) == n");
        // unary("isperfect", (c, a) -> Decs.eq(Decs.add(Decs.remove(Decs.divisor(a), a)), a),
        //     "isperfect (N) -> (B) : N is perfect number (T:1, F:0)");
        unary("isprime", (c, a) -> Decs.isPrime(a), "isprime (N) -> (B) : is prime (T:1, F:0)");
        unary("lcm", (c, a) -> Decs.lcm(a), "lcm (N) -> (I) : LCM");
        binary("lcm", (c, a, b) -> Decs.lcm(a, b), "(M) lcm (N) -> (I) : LCM");
        unary("ln", (c, a) -> Decs.ln(a), "ln (A) -> (D) : log E A");
        binary("log", (c, a, b) -> Decs.log(a, b), "(A) log (C) -> (D) : log C A");
        unary("log10", (c, a) -> Decs.log10(a), "log10 (A) -> (D) : log 10 A");
        unary("log2", (c, a) -> Decs.log2(a), "log2 (A) -> (D) : log 2 A");
        unary("negate", (c, a) -> Decs.negate(a), "negate (A) -> (D) : -A");
        variable("PI", c -> Decs.pi(), "PI -> D : π");
        unary("pascal", (c, a) -> Decs.pascal(a), "pascal N -> (I) : binomial coefficients for N");
        unary("primes", (c, a) -> Decs.primes(a), "primes (A) -> (D) : primes from 2 to A");
        unary("radian", (c, a) -> Decs.radian(a), "radian (A) -> (D) : A / 180 * π");
        unary("reciprocal", (c, a) -> Decs.reciprocal(a), "reciprocal (A) -> (D) : 1 / A");
        binary("remove", (c, a, b) -> Decs.remove(a, b), "(A) remove (B) -> (D) : remove (B) from (A)");
        unary("reverse", (c, a) -> Decs.reverse(a), "reverse (A) -> (D) : reverse");
        binary("round", (c, a, b) -> Decs.round(a, b), "(A) round (N) -> (D) : truncate A to N decimal places");
        unary("signum", (c, a) -> Decs.signum(a), "signum (A) -> (D) : sign of A");
        unary("sin", (c, a) -> Decs.sin(a), "sin (A) -> (D) : sin A");
        unary("sort", (c, a) -> Decs.sort(a), "sort (A) -> (D) : sort");
        unary("sqrt", (c, a) -> Decs.sqrt(a), "sqrt (A) -> (D) : √A");
        unary("square", (c, a) -> Decs.square(a), "square (A) -> (D) : A²");
        unary("tan", (c, a) -> Decs.tan(a), "tan (A) -> (D) : tan A");
        variable("TODAY", c -> Decs.today(), "TODAY -> I : today (YYYYMMDD)");
        binary("to", (c, a, b) -> Decs.to(a, b), "M to N -> (I) : (M..N)");
        unary("week", (c, a) -> Decs.week(a), "week (N) -> (I) : YYYYMMDD to week (1:Mon, 2:Tue, ... , 7:Sun)");
    }

    void init(Parser parser) {
        parser.eval("isperfect n = + (divisor n remove n) == n");
    }
}
