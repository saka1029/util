package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import saka1029.util.decs.Context.Undo;
import saka1029.util.decs.Scanner.Token;
import saka1029.util.decs.Scanner.TokenType;

public class Parser {
    static final String SYNTAX = """
        statement    = expression
                    | ID '=' expression
                    | ID ID '=' expression
                    | ID ID ID '=' expression
                    | 'exit'
                    | 'help' name
                    | 'solve' expression
        expression   = cor { ',' cor }
        cor          = cand { '||' cand }
        cand         = comp { '&&' comp }
        comp         = binary [ COP binary ]
        binary       = or { BOP or }
        or           = and { ( '|' | '^^' ) and }
        and          = add { '&' add }
        add          = mult { ( '+' | '-' ) mult }
        mult         = power { ( '*' | '/' | '%' ) power }
        power        = unary [ '^' power ]
        unary        = [ '@' ] UOP unary | primary
        primary      = '(' [ expression ] ')' | ID | NUMBER
        COP          = '==' | '!=' | '>' | '>=' | '<' | '<=' | '~~' | '!~'
        UOP          = id // defined in context
        BOP          = id // defined in context
        ID           = ALPHABETIC { ALPHABETIC | DIGIT }
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

    private Parser(Context context) {
        this.context = context;
    }

    public static Parser create() {
        Context context = new Context();
        Parser parser = new Parser(context);
        context.init(parser);
        return parser;
    }

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : END;
    }

    Token prev() {
        return tokens.get(index - 1);
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
            throw eofError("unexpected end");
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
            throw syntaxError("unexpected token '%s'", token.string);
        }
    }

    static boolean isTrue(BigDecimal[] decs) {
        return decs.length > 0 && decs[0].signum() != 0;
    }

    Unary select(Unary unary) {
        return (c, a) -> Decs.decs(Stream.of(a)
            .filter(d -> isTrue(unary.apply(c, Decs.decs(d)))));
    }

    Expression unary() {
        if (eat(TokenType.AT)) {
            String name = token.string;
            if (!context.isUnary(name))
                throw syntaxError("unary expected after '@' but '%s'", name);
            get();  // skip ID
            Expression arg = unary();
            return c -> select(c.unary(name).expression).apply(c, arg.eval(c));
        } else if (context.isBuiltinUnary(token.string)) {
            Unary unary = context.builtinUnary(token.string).expression;
            get();  // skip ID
            Expression arg = unary();
            return c -> unary.apply(c, arg.eval(c));
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
        if (token.type == TokenType.POW) {
            Binary pow = context.builtinBinary(token.string).expression;
            get();
            Expression left = e, right = power();
            return c -> pow.apply(c, left.eval(c), right.eval(c));
        }
        return e;
    }

    Expression mult() {
        Expression e = power();
        L: while (true) {
            Expression left = e;
            switch (token.type) {
                case MULT: case DIV: case MOD:
                    Binary mult = context.builtinBinary(token.string).expression;
                    get();
                    Expression right = power();
                    e = c -> mult.apply(c, left.eval(c), right.eval(c));
                    break;
                default:
                    break L;
            }
        }
        return e;
    }

    Expression add() {
        Expression e = mult();
        L: while (true) {
            Expression left = e;
            switch (token.type) {
                case PLUS: case MINUS:
                    Binary add = context.builtinBinary(token.string).expression;
                    get();
                    Expression right = mult();
                    e = c -> add.apply(c, left.eval(c), right.eval(c));
                    break;
                default:
                    break L;
            }
        }
        return e;
    }

    Expression and() {
        Expression e = add();
        while (true) {
            Expression left = e;
            if (token.type == TokenType.AND) {
                Binary op = context.builtinBinary(token.string).expression;
                get();
                Expression right = add();
                e = c -> op.apply(c, left.eval(c), right.eval(c));
            } else
                break;
        }
        return e;
    }

    Expression or() {
        Expression e = and();
        L: while (true) {
            Expression left = e;
            switch (token.type) {
                case OR: case XOR:
                    Binary op = context.builtinBinary(token.string).expression;
                    get();
                    Expression right = and();
                    e = c -> op.apply(c, left.eval(c), right.eval(c));
                    break;
                default:
                    break L;
            }
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

    Expression comp() {
        Expression e = binary();
        switch (token.type) {
            case EQ: case NE:
            case LT: case LE:
            case GT: case GE:
            case NEARLY_EQ: case NEARLY_NE:
                Binary op = context.builtinBinary(token.string).expression;
                get();
                Expression right = binary();
                return c -> op.apply(c, e.eval(c), right.eval(c));
            default:
                return e;
        }
    }

    Expression cand() {
        Expression e = comp();
        while (true) {
            Expression left = e;
            if (token.type == TokenType.CAND) {
                Binary and = context.builtinBinary(token.string).expression;
                get();
                Expression right = comp();
                e = c -> {
                    BigDecimal[] l = left.eval(c);
                    return Decs.falses(l) ? l : and.apply(c, l, right.eval(c));
                };
            } else
                break;
        }
        return e;
    }

    Expression cor() {
        Expression e = cand();
        while (true) {
            Expression left = e;
            if (token.type == TokenType.COR) {
                Binary or = context.builtinBinary(token.string).expression;
                get();
                Expression right = cand();
                e = c -> {
                    BigDecimal[] l = left.eval(c);
                    return Decs.trues(l) ? l : or.apply(c, l, right.eval(c));
                };
            } else
                break;
        }
        return e;
    }

    Expression expression() {
        Expression e = cor();
        while (true) {
            Expression left = e;
            if (token.type == TokenType.COMMA) {
                Binary concat = context.builtinBinary(token.string).expression;
                get();
                Expression right = cor();
                e = c -> concat.apply(c, left.eval(c), right.eval(c));
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
        if (context.isSetter(name)) {
            Unary setter = context.setter(name);
            return c -> {
                setter.apply(c, e.eval(c));
                return Decs.NO_VALUE;
            };
        } else
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
        return c -> Decs.decs(c.solve(ev));
    }

    Expression helpMain() {
        return c -> {
            c.output.accept("help syntax");
            c.output.accept("help variable");
            c.output.accept("help unary");
            c.output.accept("help binary");
            c.output.accept("help NAME");
            return Decs.NO_VALUE;
        };
    }

    Expression helpSyntax() {
        return c -> {
            SYNTAX.lines()
                .forEach(c.output::accept);
            return Decs.NO_VALUE;
        };
    }

    Expression helpVariable() {
        return c -> {
            c.variables.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> e.getValue().string)
                .forEach(c.output::accept);
            return Decs.NO_VALUE;
        };
    }

    Expression helpUnary() {
        return c -> {
            Stream.of(c.builtinUnarys, c.unarys)
                .map(map -> map.entrySet().stream())
                .flatMap(Function.identity())
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> e.getValue().string)
                .forEach(c.output::accept);
            return Decs.NO_VALUE;
        };
    }

    Expression helpBinary() {
        return c -> {
            Stream.of(c.builtinBinarys, c.binarys)
                .map(map -> map.entrySet().stream())
                .flatMap(Function.identity())
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> e.getValue().string)
                .forEach(c.output::accept);
            return Decs.NO_VALUE;
        };
    }

    Expression helpSolve() {
        return c -> {
            c.output.accept("solve EXPRESSION");
            return Decs.NO_VALUE;
        };
    }

    Expression helpName(String name) {
        return c -> {
            boolean found = false;
            if (c.isBuiltinUnary(name) && (found = true))
                c.output.accept(c.builtinUnary(name).string);
            if (c.isBuiltinBinary(name) && (found = true))
                c.output.accept(c.builtinBinary(name).string);
            if (c.isVariable(name) && (found = true))
                c.output.accept(c.variable(name).string);
            if (c.isUnary(name) && (found = true))
                c.output.accept(c.unary(name).string);
            if (c.isBinary(name) && (found = true))
                c.output.accept(c.binary(name).string);
            if (!found)
                c.output.accept("'%s' not found".formatted(name));
            return Decs.NO_VALUE;
        };
    }

    Expression help() {
        if (token == END)
            return helpMain();
        String operand = token.string;
        get(); // skip operand
        return switch (operand) {
            case "syntax" -> helpSyntax();
            case "variable" -> helpVariable();
            case "unary" -> helpUnary();
            case "binary" -> helpBinary();
            case "solve" -> helpSolve();
            default-> helpName(operand);
        };
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
        else if (eat(TokenType.EXIT))
            return c -> Decs.EXIT;
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
