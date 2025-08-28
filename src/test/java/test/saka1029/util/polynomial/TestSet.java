package test.saka1029.util.polynomial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class TestSet {

    /**
     * Comparing order
     * 1. Int
     * 2. Var
     * 3. Seq (op順, elementsの辞書的順序)
     */
    interface Expression extends Comparable<Expression> {
        @Override
        default int compareTo(Expression right) {
            return compare(this, right);
        }
    }

    static int compare(Seq left, Seq right) {
        int c = Integer.compare(left.op.ordinal(), right.op.ordinal());
        if (c != 0)
            return c;
        Iterator<Expression> li = left.elements.iterator(),
            ri = right.elements.iterator();
        while (li.hasNext() && ri.hasNext()) {
            c = li.next().compareTo(ri.next());
            if (c != 0)
                return c;
        }
        return li.hasNext() ? 1
            : ri.hasNext() ? -1
            : 0;
    }

    static int compare(Expression left, Expression right) {
        if (left instanceof Int li)
            if (right instanceof Int ri)
                return Integer.compare(li.value, ri.value);
            else
                return -1;
        else if (left instanceof Var lv)
            if (right instanceof Int)
                return 1;
            else if (right instanceof Var rv)
                return lv.name.compareTo(rv.name);
            else
                return -1;
        else if (left instanceof Seq ls)
            if (right instanceof Seq rs)
                return compare(ls, rs);
            else
                return 1;
        else
            throw new RuntimeException("compare");
    }

    static class Int implements Expression {
        final int value;

        Int(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Int i && i.value == value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    static class Var implements Expression {
        final String name;
        Var(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Var v && v.name.equals(name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    enum Op {
        ADD("+"),
        MULT("*"),
        EXP("^");

        public final String name;

        Op(String name) {
            this.name = name;
        }
    }

    static class Seq implements Expression {
        final Op op;
        final NavigableSet<Expression> elements;

        Seq(Op op, Expression... elements) {
            this.op = op;
            this.elements = Stream.of(elements).collect(Collectors.toCollection(TreeSet::new));
        }
        
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Seq s && s.op == op && s.elements.equals(elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(op, elements);
        }

        boolean needParen(Expression e) {
            return e instanceof Seq s
                ? s.op.ordinal() < op.ordinal()
                : false;
        }

        @Override
        public String toString() {
            return elements.stream()
                .map(e -> needParen(e)
                    ? "(%s)".formatted(e) : "" + e)
                .collect(Collectors.joining(op.name));
        }
    }

    static boolean isAtom(Expression e) {
        return e instanceof Var || e instanceof Int;
    }

    static boolean isAdd(Expression e) {
        return e instanceof Seq s && s.op == Op.ADD;
    }

    static boolean isMult(Expression e) {
        return e instanceof Seq s && s.op == Op.MULT;
    }

    static boolean isExp(Expression e) {
        return e instanceof Seq s && s.op == Op.EXP;
    }

    static Var v(String name) { return new Var(name); }
    static Int n(int value) { return new Int(value); }
    static Seq add(Expression... es) { return new Seq(Op.ADD, es); }
    static Seq mult(Expression... es) { return new Seq(Op.MULT, es); }
    static Seq exp(Expression... es) { return new Seq(Op.EXP, es); }

    @Test
    public void testOp() {
        assertTrue(Op.ADD.ordinal() < Op.MULT.ordinal());
    }

    @Test
    public void testToString() {
        Var x = v("x");
        Expression e = add(exp(x, n(2)), mult(n(2), x), n(1)); 
        assertEquals("x^2+2*x+1", e.toString());
    }
}
