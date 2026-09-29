package saka1029.util.declisp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import static saka1029.util.declisp.DecLisp.*;
import static org.junit.Assert.assertEquals;
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

    /**
     * Iteratorを逆向きに処理するパターン
     * (append (1 2) (3 4))
     * リスト(1 2), (3 4)の順に取り出すIteratorを使って、
     * (3 4)を結果リストに連結、(1 2)を結果リストに連結、
     * というように逆順に処理する。
     */
    public static Expr append(Expr args) {
        Iterator<Expr> it = args.iterator();
        Expr x = new Object() {
            Expr result = Nil.NIL;
            Expr append() {
                if (it.hasNext()) {
                    Expr list = it.next();
                    append();   // 次のリストを先に処理する。
                    List<Expr> elements = list.stream().toList();
                    // 結果リストに逆順に連結する。
                    for (int i = elements.size() - 1; i >= 0; --i)
                        result = cons(elements.get(i), result);
                }
                return result;
            }
        }.append();
        return x;
    }
    public static Expr append2(Expr args) {
        Expr result = Nil.NIL;
        List<Expr> lists = args.stream().toList();
        for (int i = lists.size() - 1; i >= 0; --i) {
            List<Expr> list = lists.get(i).stream().toList();
            for (int j = list.size() - 1; j >= 0; --j)
                result = cons(list.get(j), result);
        }
        return result;
    }

    @Test 
    public void testAppend() {
        Env env = defaultEnv();
        env.define(sym("append"), (Procedure) args -> append(args));
        String a = "(append '(1 2 3) '(4 5) '(6))";
        assertEquals(read("(1 2 3 4 5 6)"), eval(env, a));
        env.define(sym("append2"), (Procedure) args -> append2(args));
        String a2 = "(append2 '(1 2 3) '(4 5) '(6))";
        assertEquals(read("(1 2 3 4 5 6)"), eval(env, a2));
    }

    record Constraint(Expr constraint, Set<Symbol> variables) {
        public Constraint(Expr constraint) {
            this(constraint, new HashSet<>());
        }
    }
    record Variable(Symbol variable, List<Expr> values, List<Expr> constrains) {
        public Variable(Symbol variable) {
            this(variable, new ArrayList<>(), new ArrayList<>());
        }
    }

    static void parseVariables(Expr vlines, List<Variable> variables, Set<Symbol> symbols, Env env) {
        for (Expr v : vlines) {
            Symbol s = symbol(car(v));
            symbols.add(s);
            Variable variable = new Variable(s);
            variables.add(variable);
            for (Expr val : car(cdr(v)).eval(env))
                variable.values.add(val);
        }
    }

    static void parseAllDifferent(Expr cline, List<Constraint> constraints, Set<Symbol> symbols) {
        Expr[] vars = array(cdr(cline));
        for (Expr v : vars) // all-differentの対象変数がすべて変数として定義されていることを確認する
            if (!symbols.contains(v))
                throw new DecLispException("undefined variable '%s'", v);
        for (int i = 0, size = vars.length; i < size; ++i) {
            for (int j = i + 1; j < size; ++j) {
                Constraint diff = new Constraint(list(sym("!="), vars[i], vars[j]));
                constraints.add(diff);
                diff.variables.add(symbol(vars[i]));
                diff.variables.add(symbol(vars[j]));
            }
        }
    }

    static void parseOtherConstraint(Expr cline, List<Constraint> constraints, Set<Symbol> symbols) {
        Constraint constraint = new Constraint(cline);
        constraints.add(constraint);
        new Object() {
            void variable(Expr e) {
                if (e instanceof Symbol s) {
                    if (symbols.contains(s))
                        constraint.variables.add(s);
                } else if (e instanceof Cons c) {
                    variable(c.car());
                    variable(c.cdr());
                }
            }
        }.variable(cline);
    }

    static void parseConstraints(Expr clines, List<Constraint> constraints, Set<Symbol> symbols) {
        for (Expr cline : clines)
            if (car(cline).equals(sym("all-different")))
                parseAllDifferent(cline, constraints, symbols);
            else
                parseOtherConstraint(cline, constraints, symbols);
    }

    static void bind(List<Variable> variables, List<Constraint> constraints) {
        Set<Symbol> bind = new HashSet<>();
        for (Variable variable : variables) {
            bind.add(variable.variable);
            for (Iterator<Constraint> it = constraints.iterator(); it.hasNext(); ) {
                Constraint c = it.next();
                if (c.variables.stream().allMatch(bind::contains)) {
                    variable.constrains.add(c.constraint);
                    it.remove();
                }
            }
        }
        if (!constraints.isEmpty())
            throw new DecLispException("illegal constraints");
    }

    static void solve(Expr args, Env env) {
        List<Variable> variables = new ArrayList<>();
        Set<Symbol> symbols = new HashSet<>();
        List<Constraint> constraints = new ArrayList<>();
        // 変数とその取りうる値を初期化する。
        parseVariables(car(args), variables, symbols, env);
        // 制約とそれに含まれる変数を初期化する。
        parseConstraints(car(cdr(args)), constraints, symbols);
        // 変数に値を割り当てた後実行する制約を設定する。
        bind(variables, constraints);
        for (Variable v : variables)
            System.out.println(v);
    }

    @Test 
    public void testNewSolver() {
        Env env = defaultEnv();
        String solve = """
            (solve
                (   (x (range 10))
                    (y (range 3))
                    (z (-2 -1))  )
                (   (all-different x y z)
                    (<= x y)  ))
            """;
        solve(cdr(read(solve)), env);
    }
}
