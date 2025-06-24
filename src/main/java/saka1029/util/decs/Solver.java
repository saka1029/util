package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import saka1029.util.decs.Context.Undo;

public class Solver {
    final Context context;
    final Expression expression;
    final List<String> variables;
    final Consumer<String> output;
    final List<BigDecimal[]> values;

    private Solver(Context context, Expression expression,
        List<String> variables, List<BigDecimal[]> values,
        Consumer<String> output) {
        this.context = context;
        this.expression = expression;
        this.variables = variables;
        this.values = values;
        this.output = output;
    }

    public static void solve(Context context, Expression ex,
        Consumer<String> output) {
        if (!(ex instanceof ExpressionWithVariables exvar))
            throw new DecsException("Solver: can't solve");
        List<String> variables = exvar.variables.stream()
            .distinct()
            .toList();
        List<BigDecimal[]> values = variables.stream()
            .map(n -> context.variable(n).expression.apply(context))
            .toList();
        new Solver(context, exvar.expression, variables, values, output)
            .solve();
    }

    void eval() {

    }

    void backupVariable(int i) {
        if (i >= variables.size()) {
            eval();
            return;
        }
        try (Undo u = context.variableTemp(variables.get(i), c -> values.get(i), "dummy")) {
            backupVariable(i + 1);
        }
    }

    void solve() {
        backupVariable(0);
    }

}
