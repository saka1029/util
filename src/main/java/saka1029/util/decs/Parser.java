package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import saka1029.util.decs.Context.Undo;
import saka1029.util.decs.Scanner.Token;
import saka1029.util.decs.Scanner.TokenType;

public class Parser {
    static final String SYNTAX = """
        statement    = expression
                    | id '=' expression
                    | id id '=' expression
                    | id id id '=' expression
                    | 'exit'
                    | 'help' name
                    | 'solve' expression
        expression   = binary { ',' binary }
        binary       = or { bop or }
        or           = and { '|' and }
        and          = comp { '&' comp }
        comp         = add [ cop add ]
        add          = mult { ( '+' | '-' ) mult }
        mult         = power { ( '*' | '/' | '%' ) power }
        power        = unary [ '^' power ]
        unary        = [ '@' ] uop unary | primary
        primary      = '(' [ expression ] ')' | id | num

        name         = ',' | '|' | '&' | cop
                    | '+' | '-' | '*' | '/' | '%' | '^'
        cop          = '==' | '!=' | '>' | '>=' | '<' | '<='
        uop          = id // defined in context
        bop          = id // defined in context
        id           = ALPHABETIC { ALPHABETIC | DIGIT }
                    | SPECIAL { SPECIAL }
        """;
    static final Token END = new Token(TokenType.END, "EOF");

    public final Context context;
    List<Token> tokens;
    Token token;
    int index = 0;
    String input;
    Scanner scanner = new Scanner();
    List<String> variables = new ArrayList<>();

    public Parser(Context context) {
        this.context = context;
    }

    public Parser() {
        this(new Context());
    }

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : END;
    }

    boolean is(TokenType... expects) {
        int length = expects.length;
        if (length > tokens.size() - index + 1)
            return false;
        for (int i = 0, j = index - 1; i < length; ++i, ++j)
            if (expects[i] != tokens.get(j).type)
                return false;
        return true;
    }

    boolean eat(TokenType expected) {
        if (token.type == expected) {
            get();
            return true;
        }
        return false;
    }

    EOFException eofError(String message, Object... args) {
        return new EOFException(message, args);
    }

    SyntaxException syntaxError(String message, Object... args) {
        return new SyntaxException(message, args);
    }

    Expression primary() {
        if (token.type == TokenType.END) {
            throw eofError("Unexpected end");
        } else if (eat(TokenType.LP)) {
            Expression e = expression();
            if (!eat(TokenType.RP))
                throw eofError("')' expected");
            return e;
        } else if (is(TokenType.NUM)) {
            BigDecimal[] decs = Decs.decs(token.string);
            get();  // skip NUM
            return c -> decs;
        } else if (is(TokenType.ID)) {
            String name = token.string;
            get();  // skip ID
            variables.add(name);
            return c -> c.variable(name).expression.eval(c);
        } else {
            throw syntaxError("Unexpected token '%s'", token.string);
        }
    }

    Unary select(Unary unary) {
        return (c, a) -> {
            return Stream.of(a)
                .filter(d -> {
                    BigDecimal[] f = unary.apply(c, new BigDecimal[] {d});
                    return f.length > 0 && f[0].signum() != 0;
                })
                .toArray(BigDecimal[]::new);
        };
    }

    Expression unary() {
        if (eat(TokenType.AT)) {
            String name = token.string;
            if (!context.isUnary(name))
                throw syntaxError("unary expected after '@' but %s", name);
            get();  // skip ID
            Expression arg = unary();
            return c -> select(c.unary(name).expression).apply(c, arg.eval(c));
        } else if (context.isUnary(token.string)) {
            String name = token.string;
            get();  // skip ID
            Expression arg = unary();
            return c -> c.unary(name).expression.apply(c, arg.eval(c));
        } else
            return primary();
    }

    Expression power() {
        Expression e = unary();
        if (eat(TokenType.POW)) {
            Expression left = e, right = power();
            return c -> Decs.pow(left.eval(c), right.eval(c));
        }
        return e;
    }

    Expression mult() {
        Expression e = power();
        while (true) {
            Expression left = e;
            if (eat(TokenType.MULT)) {
                Expression right = power();
                e = c -> Decs.multiply(left.eval(c), right.eval(c));
            } else if (eat(TokenType.DIV)) {
                Expression right = power();
                e = c -> Decs.divide(left.eval(c), right.eval(c));
            } else if (eat(TokenType.MOD)) {
                Expression right = power();
                e = c -> Decs.mod(left.eval(c), right.eval(c));
            } else 
                break;
        }
        return e;
    }

    Expression add() {
        Expression e = mult();
        while (true) {
            Expression left = e;
            if (eat(TokenType.PLUS)) {
                Expression right = mult();
                e = c -> Decs.add(left.eval(c), right.eval(c));
            } else if (eat(TokenType.MINUS)) {
                Expression right = mult();
                e = c -> Decs.subtract(left.eval(c), right.eval(c));
            }
            else 
                break;
        }
        return e;
    }

    Expression comp() {
        Expression e = add();
        if (eat(TokenType.EQ)) {
            Expression right = add();
            return c -> Decs.eq(e.eval(c), right.eval(c));
        } else if (eat(TokenType.NE)) {
            Expression right = add();
            return c -> Decs.ne(e.eval(c), right.eval(c));
        } else if (eat(TokenType.GT)) {
            Expression right = add();
            return c -> Decs.gt(e.eval(c), right.eval(c));
        } else if (eat(TokenType.GE)) {
            Expression right = add();
            return c -> Decs.ge(e.eval(c), right.eval(c));
        } else if (eat(TokenType.LT)) {
            Expression right = add();
            return c -> Decs.lt(e.eval(c), right.eval(c));
        } else if (eat(TokenType.LE)) {
            Expression right = add();
            return c -> Decs.le(e.eval(c), right.eval(c));
        }
        return e;
    }

    Expression and() {
        Expression e = comp();
        while (true) {
            Expression left = e;
            if (eat(TokenType.AND)) {
                Expression right = comp();
                // e = c -> Decs.and(left.eval(c), right.eval(c));
                // conditional AND
                e = c -> {
                    BigDecimal[] l = left.eval(c);
                    return Decs.falses(l) ? l : Decs.and(l, right.eval(c));
                };
            } else
                break;
        }
        return e;
    }

    Expression or() {
        Expression e = and();
        while (true) {
            Expression left = e;
            if (eat(TokenType.OR)) {
                Expression right = add();
                // e = c -> Decs.or(left.eval(c), right.eval(c));
                // conditional OR
                e = c -> {
                    BigDecimal[] l = left.eval(c);
                    return Decs.trues(l) ? l : Decs.or(l, right.eval(c));
                };
            } else
                break;
        }
        return e;
    }

    Expression binary() {
        Expression e = or();
        while (true) {
            Expression left = e;
            if (context.isBinary(token.string)) {
                String name = token.string;
                get();  // skip ID
                Expression right = or();
                e = c -> c.binary(name).expression.apply(c, left.eval(c), right.eval(c));
            } else
                break;
        }
        return e;
    }

    Expression expression() {
        Expression e = binary();
        while (true) {
            Expression left = e;
            if (eat(TokenType.COMMA)) {
                Expression right = binary();
                e = c -> Decs.concat(left.eval(c), right.eval(c));
            } else
                break;
        }
        return new ExpressionWithVariables(e, variables);
    }


    Expression defineVariable() {
        String name = token.string;
        get();   // skip ID (name)
        get();   // skip '='
        Expression e = expression();
        return c -> {
            c.variable(name, e, input);
            return Decs.NO_VALUE;
        };
    }

    Expression defineUnary() {
        String name = token.string;
        get();   // skip ID (name)
        String arg = token.string;
        get();   // skip ID (argument)
        get();   // skip '='
        Expression e = expression();
        return c -> {
            Unary unary = (cc, a) -> {
                try (Undo u = cc.variableTemp(arg, ccc -> a, arg)) {
                    return e.eval(cc);
                }
            };
            c.unary(name, unary, input);
            return Decs.NO_VALUE;
        };
    }

    Expression defineBinary() {
        String left = token.string;
        get();   // skip ID (left)
        String name = token.string;
        get();   // skip ID (name)
        String right = token.string;
        get();   // skip ID (right)
        get();   // skip '='
        Expression e = expression();
        return c -> {
            Binary binary = (cc, l, r) -> {
                try (Undo ul = cc.variableTemp(left, ccc -> l, "local " + left);
                    Undo ur = cc.variableTemp(right, ccc -> r, "local " + right)) {
                    return e.eval(cc);
                }
            };
            c.binary(name, binary, input);
            return Decs.NO_VALUE;
        };
    }

    Expression solve() {
        Expression ev = expression();
        // return number of solutions.
        return c -> Decs.decs(Decs.dec(c.solve(ev)));
    }

    void helpMain() {
        context.output.accept("help syntax");
        context.output.accept("help variable");
        context.output.accept("help unary");
        context.output.accept("help binary");
        context.output.accept("help NAME");
    }

    void helpSyntax() {
        SYNTAX.lines()
            .forEach(context.output::accept);
    }

    void helpVariable() {
        context.variables.entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .map(e -> e.getValue().string)
            .forEach(context.output::accept);
    }

    void helpUnary() {
        context.unarys.entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .map(e -> e.getValue().string)
            .forEach(context.output::accept);
    }

    void helpBinary() {
        context.binarys.entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .map(e -> e.getValue().string)
            .forEach(context.output::accept);
    }

    void helpSolve() {
        context.output.accept("solve EXPRESSION");
    }

    void helpName(String name) {
        boolean found = false;
        if (context.isVariable(name) && (found = true))
            context.output.accept(context.variable(name).string);
        if (context.isUnary(name) && (found = true))
            context.output.accept(context.unary(name).string);
        if (context.isBinary(name) && (found = true))
            context.output.accept(context.binary(name).string);
        if (!found)
            context.output.accept("'%s' not found".formatted(name));
    }

    Expression help() {
        if (token == END)
            helpMain();
        else if (token.string.equals("syntax"))
            helpSyntax();
        else if (token.string.equals("variable"))
            helpVariable();
        else if (token.string.equals("unary"))
            helpUnary();
        else if (token.string.equals("binary"))
            helpBinary();
        else if (token.string.equals("solve"))
            helpSolve();
        else
            helpName(token.string);
        return c -> Decs.NO_VALUE;
    }

    Expression statement() {
        if (is(TokenType.ID, TokenType.ASSIGN))
            return defineVariable();
        else if (is(TokenType.ID, TokenType.ID, TokenType.ASSIGN))
            return defineUnary();
        else if (is(TokenType.ID, TokenType.ID, TokenType.ID, TokenType.ASSIGN))
            return defineBinary();
        else if (eat(TokenType.SOLVE))
            return solve();
        else if (eat(TokenType.HELP))
            return help();
        else
            return expression();
    }

    public Expression parse(String input) {
        this.input = input.trim();
        tokens = scanner.scan(input);
        index = 0;
        get();
        variables.clear();
        Expression result = statement();
        if (token != END)
            throw syntaxError("extra token '%s'", token.string);
        return new ExpressionWithVariables(result, variables);
    }

    public BigDecimal[] eval(String input) {
        return parse(input).eval(context);
    }
}
