package saka1029.util.declisp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import static saka1029.util.declisp.DecLisp.*;
import static saka1029.util.declisp.Common.*;

public class TestSolver {

    static Expr solver(Expr args, Env env) {
        Expr target = car(cdr(args));
        List<Entry<Symbol, Expr>> vars = new ArrayList<>();
        Set<Symbol> dupCheck = new HashSet<>();
        for (Expr var : car(args)) {
            Symbol v = symbol(car(var));
            if (!dupCheck.add(v))
                throw new DecLispException("solver: duplicated variable '%s'", v);
            vars.add(Map.entry(symbol(car(var)), car(cdr(var)).eval(env)));
        }
        List<Expr[]> result = new ArrayList<>();
        result.add(vars.stream().map(x -> (Expr)x.getKey()).toArray(Expr[]::new));
        new Object() {
            Env nenv = new Env(env);
            void solve(int index) {
                if (index >= vars.size()) {
                    if (bool(target.eval(nenv)))
                        result.add(vars.stream().map(x -> nenv.get(x.getKey())).toArray(Expr[]::new));
                } else {
                    Symbol var = vars.get(index).getKey();
                    for (Expr e : vars.get(index).getValue()) {
                        nenv.define(var, e);
                        solve(index + 1);
                    }
                }
            }
        }.solve(0);
        return list(result.stream().map(x -> list(x)).toList());
    }

    @Test 
    public void testSolver() {
        Env env = defaultEnv();
        String solve = """
            (solve
                ((x (range 3))
                 (y (range 2)))
                (= (+ x y) 4))
        """;
        Expr args = read(solve);
        Expr result = solver(cdr(args), env);
        System.out.println(result);
    }
}
