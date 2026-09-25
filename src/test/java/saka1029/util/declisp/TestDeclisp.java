package saka1029.util.declisp;

import static org.junit.Assert.*;
import static saka1029.util.declisp.DecLisp.*;

import java.time.DateTimeException;
import java.time.LocalDate;

import static saka1029.util.declisp.Common.*;

import org.junit.Test;

public class TestDeclisp {

    @Test 
    public void testAndOr() {
        Env env = defaultEnv();
        assertEquals(Bool.F, eval(env,"(not T)"));
        assertEquals(Bool.T, eval(env,"(not F)"));
        assertEquals(Bool.F, eval(env,"(! T)"));
        assertEquals(Bool.T, eval(env,"(! F)"));
        assertEquals(Bool.T, eval(env,"(&&)"));
        assertEquals(Bool.T, eval(env,"(&& T)"));
        assertEquals(Bool.T, eval(env,"(&& T T)"));
        assertEquals(Bool.F, eval(env,"(&& T F)"));
        assertEquals(Bool.F, eval(env,"(&& F T)"));
        assertEquals(Bool.F, eval(env,"(&& F F)"));
        assertEquals(Bool.F, eval(env,"(&& 3 F)"));
        assertEquals(Bool.F, eval(env,"(&& 1 2 F)"));
        assertEquals(dec(3), eval(env,"(&& 1 2 3)"));
        assertEquals(list(dec(1), Bool.F, dec(3)), eval(env,"(&& (1 F 3))"));
        assertEquals(Bool.F, eval(env,"(||)"));
        assertEquals(Bool.T, eval(env,"(|| T)"));
        assertEquals(Bool.T, eval(env,"(|| T T)"));
        assertEquals(Bool.T, eval(env,"(|| T F)"));
        assertEquals(Bool.T, eval(env,"(|| F T)"));
        assertEquals(Bool.F, eval(env,"(|| F F)"));
        assertEquals(dec(3), eval(env,"(|| 3 F)"));
        assertEquals(dec(3), eval(env,"(|| F 3 F)"));
        assertEquals(dec(1), eval(env,"(|| 1 2 3)"));
    }

    @Test
    public void testEvalRead() {
        Env env = defaultEnv();
        assertEquals(sym("F"), sym("F"));
        assertEquals(list(dec(0), dec(3)), eval(env, "(list '0 3)"));
        System.out.println(list(sym("F"), dec(3)));
        System.out.println(eval(env, "(list 'F 3)"));
        assertEquals(list(Bool.F, dec(3)), eval(env, "(list 'F 3)"));
        assertEquals(dec(2), eval(env, "(if F 1 2)"));
        assertEquals(Nil.NIL, eval(env, "(if F 1)"));
        assertEquals(dec(1), eval(env, "(car '(1 a))"));
        assertEquals(sym("a"), eval(env, "(cdr '(1 . a))"));
        assertEquals(list(dec(1), dec(2)), eval(env, "(cons 1 '(2))"));
        // Cannot cons any and ATOM
        // assertEquals(cons(d(1), d(2)), evalRead("(cons 1 2)", env));
        assertEquals(Bool.F, eval(env, "(not (== 0 0))"));
        assertEquals(sym("a"), eval(env, "((lambda (a) (car a)) '(a b))"));
        assertEquals(dec(6), eval(env, "(+ 1 2 3)"));
        assertEquals(dec(6), eval(env, "(+ 1 2 (+ 1 2))"));
        // System.out.println(print(env, evalRead("(-)")));
        assertEquals(dec(0), eval(env, "(-)"));
        assertEquals(dec(-1), eval(env, "(- 1)"));
        assertEquals(dec(-4), eval(env, "(- 1 2 3)"));
        assertEquals(Bool.T, eval(env, "(== 2 2)"));
        assertEquals(Bool.F, eval(env, "(== 0 2)"));
        env.define(sym("fact"), eval(env, "(lambda (n) (if (<= n 0) 1 (* n (fact (- n 1)))))"));
        assertEquals(dec(1), eval(env, "(fact 0)"));
        assertEquals(dec(1), eval(env, "(fact 1)"));
        assertEquals(dec(2), eval(env, "(fact 2)"));
        assertEquals(dec(6), eval(env, "(fact 3)"));
        assertEquals(sym("fact2"), eval(env, "(define fact2 (lambda (n) (if (<= n 0) 1 (* n (fact (- n 1))))))"));
        assertEquals(dec(1), eval(env, "(fact2 0)"));
        assertEquals(dec(1), eval(env, "(fact2 1)"));
        assertEquals(dec(2), eval(env, "(fact2 2)"));
        assertEquals(dec(6), eval(env, "(fact2 3)"));
        assertEquals(Bool.T, eval(env, "(&&)"));
        assertEquals(dec(3), eval(env, "(&& 2 3)"));
        assertEquals(Bool.F, eval(env, "(&& F 3)"));
        assertEquals(Bool.F, eval(env, "(||)"));
        assertEquals(dec(2), eval(env, "(|| 2 3)"));
        assertEquals(dec(3), eval(env, "(|| F 3)"));
        assertEquals(sym("listx"), eval(env, "(define listx (lambda x x))))"));
        assertEquals(read("(1 2 3)"), eval(env, "(listx 1 2 3)"));
        assertEquals(sym("foo"), eval(env, "(define foo (lambda (first . rest) (listx first rest)))))"));
        assertEquals(read("(1 (2 3))"), eval(env, "(foo 1 2 3)"));
    }

    @Test 
    public void testArithmetic() {
        Env env = defaultEnv();
        assertEquals(dec(0), eval(env, "(+)"));
        assertEquals(dec(2), eval(env, "(+ 2)"));
        assertEquals(dec(3), eval(env, "(+ 1 2)"));
        assertEquals(dec(6), eval(env, "(+ 1 2 3)"));
        assertEquals(dec(0), eval(env, "(-)"));
        assertEquals(dec(-2), eval(env, "(- 2)"));
        assertEquals(dec(-1), eval(env, "(- 1 2)"));
        assertEquals(dec(-4), eval(env, "(- 1 2 3)"));
        assertEquals(dec(1), eval(env, "(*)"));
        assertEquals(dec(2), eval(env, "(* 2)"));
        assertEquals(dec(2), eval(env, "(* 1 2)"));
        assertEquals(dec(8), eval(env, "(* 1 2 4)"));
        assertEquals(dec(1), eval(env, "(/)"));
        assertEquals(dec(0.5), eval(env, "(/ 2)"));
        assertEquals(dec(0.25), eval(env, "(/ 1 4)"));
        assertEquals(dec(4), eval(env, "(/ 24 2 3)"));
        assertEquals(dec(1), eval(env, "(%)"));
        assertEquals(dec(1), eval(env, "(% 2)"));
        assertEquals(dec(1), eval(env, "(% 1 4)"));
        assertEquals(dec(0), eval(env, "(% 24 2 3)"));
        assertEquals(dec(679), eval(env, "(% 1679 1000)"));
    }

    @Test 
    public void testCompare() {
        Env env = defaultEnv();
        assertEquals(Bool.F, eval(env, "(== 1 0)"));
        assertEquals(Bool.T, eval(env, "(== 0 0)"));
        assertEquals(Bool.F, eval(env, "(== 0 1)"));
        assertEquals(Bool.F, eval(env, "(= 1 0)"));
        assertEquals(Bool.T, eval(env, "(= 0 0)"));
        assertEquals(Bool.F, eval(env, "(= 0 1)"));
        assertEquals(Bool.T, eval(env, "(!= 1 0)"));
        assertEquals(Bool.F, eval(env, "(!= 0 0)"));
        assertEquals(Bool.T, eval(env, "(!= 0 1)"));
        assertEquals(Bool.F, eval(env, "(< 1 0)"));
        assertEquals(Bool.F, eval(env, "(< 0 0)"));
        assertEquals(Bool.T, eval(env, "(< 0 1)"));
        assertEquals(Bool.F, eval(env, "(<= 1 0)"));
        assertEquals(Bool.T, eval(env, "(<= 0 0)"));
        assertEquals(Bool.T, eval(env, "(<= 0 1)"));
        assertEquals(Bool.T, eval(env, "(> 1 0)"));
        assertEquals(Bool.F, eval(env, "(> 0 0)"));
        assertEquals(Bool.F, eval(env, "(> 0 1)"));
        assertEquals(Bool.T, eval(env, "(>= 1 0)"));
        assertEquals(Bool.T, eval(env, "(>= 0 0)"));
        assertEquals(Bool.F, eval(env, "(>= 0 1)"));
        assertEquals(Bool.T, eval(env, "(== 1 1 1 1)"));
        assertEquals(Bool.F, eval(env, "(== 1 1 2 1)"));
        assertEquals(Bool.T, eval(env, "(<= 0 1 1 2)"));
        assertEquals(Bool.F, eval(env, "(< 0 1 1 2)"));
    }

    @Test 
    public void testCompareBool() {
        Env env = defaultEnv();
        assertEquals(Bool.T, eval(env, "(== T T)"));
        assertEquals(Bool.F, eval(env, "(== T F)"));
        assertEquals(Bool.F, eval(env, "(== F T)"));
        assertEquals(Bool.T, eval(env, "(== F F)"));
        assertEquals(Bool.T, eval(env, "(= T T)"));
        assertEquals(Bool.F, eval(env, "(= T F)"));
        assertEquals(Bool.F, eval(env, "(= F T)"));
        assertEquals(Bool.T, eval(env, "(= F F)"));
        assertEquals(Bool.F, eval(env, "(< T T)"));
        assertEquals(Bool.F, eval(env, "(< T F)"));
        assertEquals(Bool.T, eval(env, "(< F T)"));
        assertEquals(Bool.F, eval(env, "(< F F)"));
        assertEquals(Bool.T, eval(env, "(<= T T)"));
        assertEquals(Bool.F, eval(env, "(<= T F)"));
        assertEquals(Bool.T, eval(env, "(<= F T)"));
        assertEquals(Bool.T, eval(env, "(<= F F)"));
        assertEquals(Bool.F, eval(env, "(> T T)"));
        assertEquals(Bool.T, eval(env, "(> T F)"));
        assertEquals(Bool.F, eval(env, "(> F T)"));
        assertEquals(Bool.F, eval(env, "(> F F)"));
        assertEquals(Bool.T, eval(env, "(>= T T)"));
        assertEquals(Bool.T, eval(env, "(>= T F)"));
        assertEquals(Bool.F, eval(env, "(>= F T)"));
        assertEquals(Bool.T, eval(env, "(>= F F)"));
    }

    @Test 
    public void testLogical() {
        Env env = defaultEnv();
        assertEquals(Bool.T, eval(env, "(and)"));
        assertEquals(Bool.T, eval(env, "(and T)"));
        assertEquals(Bool.F, eval(env, "(and F)"));
        assertEquals(Bool.T, eval(env, "(and T T)"));
        assertEquals(Bool.F, eval(env, "(and T F)"));
        assertEquals(Bool.F, eval(env, "(and F T)"));
        assertEquals(Bool.F, eval(env, "(and F F)"));
        assertEquals(Bool.F, eval(env, "(or)"));
        assertEquals(Bool.T, eval(env, "(or T)"));
        assertEquals(Bool.F, eval(env, "(or F)"));
        assertEquals(Bool.T, eval(env, "(or T T)"));
        assertEquals(Bool.T, eval(env, "(or T F)"));
        assertEquals(Bool.T, eval(env, "(or F T)"));
        assertEquals(Bool.F, eval(env, "(or F F)"));
        assertEquals(Bool.F, eval(env, "(xor)"));
        assertEquals(Bool.T, eval(env, "(xor T)"));
        assertEquals(Bool.F, eval(env, "(xor F)"));
        assertEquals(Bool.F, eval(env, "(xor T T)"));
        assertEquals(Bool.T, eval(env, "(xor T F)"));
        assertEquals(Bool.T, eval(env, "(xor F T)"));
        assertEquals(Bool.F, eval(env, "(xor F F)"));
    }

    @Test 
    public void testGet() {
        Env env = defaultEnv();
        assertEquals(sym("var"), eval(env, "(define var 3)"));
        assertEquals(dec(3), eval(env, "var"));
        try {
            eval(env, "foo");
        } catch (DecLispException x) {
            assertEquals("Env.get(): symbol 'foo' not found", x.getMessage());
        }
    }

    @Test 
    public void testSet() {
        Env env = defaultEnv();
        assertEquals(sym("var"), eval(env, "(define var 3)"));
        assertEquals(dec(6), eval(env, "(set var (+ 1 2 3))"));
        try {
            eval(env, "(set foo (+ 1 2 3))");
        } catch (DecLispException x) {
            assertEquals("Env.set(): symbol 'foo' not found", x.getMessage());
        }
    }

    @Test 
    public void testDefine() {
        Env env = defaultEnv();
        eval(env, "(define var (+ 1 2 3))");
        assertEquals(dec(6), eval(env, "var"));
        eval(env, "(define fact (lambda (n) (if (<= n 0) 1 (* n (fact (- n 1))))))");
        assertEquals(dec(120), eval(env, "(fact 5)"));
        eval(env, "(define (fact2 n) (if (<= n 0) 1 (* n (fact2 (- n 1)))))");
        assertEquals(dec(1), eval(env, "(fact2 0)"));
        assertEquals(dec(1), eval(env, "(fact2 1)"));
        assertEquals(dec(2), eval(env, "(fact2 2)"));
        assertEquals(dec(6), eval(env, "(fact2 3)"));
        assertEquals(dec(24), eval(env, "(fact2 4)"));
        assertEquals(dec(120), eval(env, "(fact2 5)"));
        eval(env, "(define (foo h . t) (list h t))");
        assertEquals(read("(1 (x))"), eval(env, "(foo 1 'x)"));
        eval(env, "(define (bar . r) r)");
        assertEquals(read("(1 x)"), eval(env, "(bar 1 'x)"));
    } 

    @Test 
    public void testMatrixTranspose() {
        Expr[][] matrix = {
            {dec(1), dec(2), dec(3)},
            {dec(4)},
            {dec(5), dec(6)}};
        assertArrayEquals(matrix, matrix(read("((1 2 3) 4 (5 6))")));
        assertArrayEquals(matrix, matrix(read("((1 2 3) (4) (5 6))")));
        try {
            // 行の長さは最大値(=3)または1でなければならない。
            // (5 6)はこの条件を満たさない。
            transpose(matrix(read("((1 2 3) 4 (5 6))")));
            fail();
        } catch (DecLispException x) {
            assertEquals("Illegal matrix [[1, 2, 3], [4], [5, 6]]", x.getMessage());
        }
        // 長さが1だった行は最大値(=3)まで増幅される。
        Expr[][] transposed = {
            {dec(1), dec(4), dec(5)},
            {dec(2), dec(4), dec(6)},
            {dec(3), dec(4), dec(7)}};
        assertArrayEquals(transposed, transpose(matrix(read("((1 2 3) 4 (5 6 7))"))));
    }

    @Test 
    public void testMap() {
        Env env = defaultEnv();
        assertEquals(read("-1"), eval(env, "(- 1)"));
        assertEquals(read("(-1 -2)"), eval(env, "(map - '(1 2))"));
        assertEquals(read("(-1 -2)"), eval(env, "(map - (1 2))"));
        assertEquals(read("(2 4)"), eval(env, "(map (lambda (x) (+ x x)) '(1 2))"));
        assertEquals(read("(2 4)"), eval(env, "(map (lambda (x) (+ x x)) '(1 2) (3 4))"));
        assertEquals(read("(-2 -2)"), eval(env, "(map - (1 2) (3 4))"));
        assertEquals(read("(-7 -7)"), eval(env, "(map - (1 2) (3 4) 5)"));
        assertEquals(read("()"), eval(env, "(map - )"));
        try {
            eval(env, "(map - (1 2) () 5)");
        } catch (DecLispException x) {
            assertEquals("Illegal matrix [[1, 2], [], [5]]", x.getMessage());
        }
        assertEquals(read("(F T)"), eval(env, "(map not (T F))"));
        assertEquals(read("(T F F F)"), eval(env, "(map and (T T F F) (T F T F))"));
        assertEquals(read("(T T T F)"), eval(env, "(map or (T T F F) (T F T F))"));
        assertEquals(read("(F T T F)"), eval(env, "(map xor (T T F F) (T F T F))"));
        assertEquals(read("fact"), eval(env, "(define (fact n) (if (<= n 0) 1 (* n (fact (- n 1)))))"));
        assertEquals(read("(1 1 2 6 24 120 720)"), eval(env, "(map fact (range 0 6))"));
    }

    @Test 
    public void testAbs() {
        Env env = defaultEnv();
        assertEquals(dec(9), eval(env, "(abs 9)"));
        assertEquals(dec(9), eval(env, "(abs -9)"));
    }

    @Test 
    public void testGcd() {
        Env env = defaultEnv();
        assertEquals(dec(1), eval(env, "(gcd)"));
        assertEquals(dec(1), eval(env, "(gcd 9)"));
        assertEquals(dec(3), eval(env, "(gcd 9 15)"));
        assertEquals(dec(3), eval(env, "(gcd -9 15)"));
        assertEquals(dec(3), eval(env, "(gcd 9 -15)"));
        assertEquals(dec(12), eval(env, "(gcd 120 84 48)"));
    }

    @Test 
    public void testDelta() {
        Env env = defaultEnv();
        assertEquals(dec(5e-9), eval(env, "(delta 5e-9)"));
        assertTrue(bool(eval(env, "(~ 12.1234567890 12.1234567899)")));
        assertEquals(dec(5e-20), eval(env, "(delta 5e-20)"));
        assertEquals(dec(5e-20), eval(env, "(delta)"));
        assertFalse(bool(eval(env, "(~ 12.1234567890 12.1234567899)")));
    }

    @Test 
    public void testConstant() {
        Env env = defaultEnv();
        assertEquals(dec(34), eval(env, "(precision 34)"));
        assertEquals(dec(34), eval(env, "(precision)"));
        assertEquals(dec(bigdec("3.141592653589793238462643383279503")), eval(env, "(pi)"));
        assertEquals(dec(bigdec("2.718281828459045235360287471352662")), eval(env, "(e)"));
        assertEquals(dec(8), eval(env, "(precision 8)"));
        assertEquals(dec(bigdec("3.1415927")), eval(env, "(pi)"));
        assertEquals(dec(bigdec("2.7182818")), eval(env, "(e)"));
    }

    @Test 
    public void testTriangle() {
        Env env = defaultEnv();
        eval(env, "(define d30 (/ (pi) 6))");
        eval(env, "(define d45 (/ (pi) 4))");
        eval(env, "(define d90 (/ (pi) 2))");
        assertTrue(bool(eval(env, "(approx (sin 0) 0)")));
        assertTrue(bool(eval(env, "(approx (sin 0) 0)")));
        assertTrue(bool(eval(env, "(approx (sin d90) 1)")));
        assertTrue(bool(eval(env, "(approx (cos 0) 1)")));
        assertTrue(bool(eval(env, "(approx (cos d90) 0)")));
        assertTrue(bool(eval(env, "(approx (tan 0) 0)")));
        assertTrue(bool(eval(env, "(approx (tan d45) 1)")));
        assertTrue(bool(eval(env, "(approx (asin 0.5) d30)")));
        assertTrue(bool(eval(env, "(approx (asin -1) (- d90))")));
        assertTrue(bool(eval(env, "(~ (acos 0) d90)")));
        assertTrue(bool(eval(env, "(~ (atan 1) d45)")));
    }

    @Test 
    public void testLog() {
        Env env = defaultEnv();
        assertEquals(dec(4), eval(env, "(log10 10000)"));
        assertEquals(dec(10), eval(env, "(log2 1024)"));
        assertTrue(bool(eval(env, "(approx (log (pow (e) 7)) 7)")));
        assertTrue(bool(eval(env, "(~ (log (^ (e) 7)) 7)")));
    }

    @Test 
    public void testGamma() {
        Env env = defaultEnv();
        assertTrue(bool(eval(env, "(approx (gamma (/ 2)) (sqrt (pi)))")));
    }

    @Test 
    public void testExp() {
        Env env = defaultEnv();
        assertTrue(bool(eval(env, "(approx (exp 0) 1)")));
        assertTrue(bool(eval(env, "(approx (exp 1) (e))")));
        assertTrue(bool(eval(env, "(approx (exp 2) (pow (e) 2))")));
    }

    @Test 
    public void testRoot() {
        Env env = defaultEnv();
        assertTrue(bool(eval(env, "(approx (sqrt 65536) 256)")));
        assertTrue(bool(eval(env, "(approx (root 4 65536) 16)")));
    }

    @Test 
    public void testRange() {
        Env env = defaultEnv();
        assertEquals(read("(1 2 3)"), eval(env, "(range 3)"));
        assertEquals(read("(0 1 2 3)"), eval(env, "(range 0 3)"));
        assertEquals(read("(0 -1 -2 -3)"), eval(env, "(range 0 -3)"));
        assertEquals(read("(0 0.5 1 1.5 2)"), eval(env, "(range 0 2 0.5)"));
        assertEquals(read("()"), eval(env, "(range 0 -2 0.5)"));
        assertEquals(read("(0 -0.2 -0.4 -0.6 -0.8 -1)"), eval(env, "(range 0 -1 -0.2)"));
        try {
            eval(env, "(range 0 -1 0)");
        } catch (DecLispException x) {
            assertEquals("step must != 0", x.getMessage());
        }
        try {
            eval(env, "(range 0 -1 -0.2 0)");
        } catch (DecLispException x) {
            assertEquals("Illegal range argument", x.getMessage());
        }
        assertEquals(read("(2 4 6)"), eval(env, "(map (lambda (n) (* 2 n)) (range 3))"));
    }

    @Test 
    public void testApply() {
        Env env = defaultEnv();
        assertEquals(read("6"), eval(env, "(apply + (1 2 3))"));
        assertEquals(read("5050"), eval(env, "(apply + (range 100))"));
    }

    @Test 
    public void testHypot() {
        Env env = defaultEnv();
        assertEquals(read("25"), eval(env, "(square 5)"));
        assertEquals(read("5"), eval(env, "(hypot 3 4)"));
    }

    @Test 
    public void testPolynomial() {
        Env env = defaultEnv();
        assertEquals(read("()"), eval(env, "(p+)"));
        assertEquals(read("(1 1)"), eval(env, "(p+ (1 1))"));
        assertEquals(read("(1 2 1)"), eval(env, "(p+ (1 0 0) (2 0) (1))"));
        assertEquals(read("()"), eval(env, "(p-)"));
        assertEquals(read("(-1 -1)"), eval(env, "(p- (1 1))"));
        assertEquals(read("(1 -2 -1)"), eval(env, "(p- (1 0 0) (2 0) (1))"));
        assertEquals(read("()"), eval(env, "(p*)"));
        assertEquals(read("(1 1)"), eval(env, "(p* (1 1))"));
        assertEquals(read("(1 3 3 1)"), eval(env, "(p* (1 1) (1 1) (1 1))"));
        assertEquals(read("()"), eval(env, "(p/)"));
        assertEquals(read("(0)"), eval(env, "(p/ (1 1))"));
        assertEquals(read("(1)"), eval(env, "(p/ (1 1) (1 1))"));
        assertEquals(read("(1 1)"), eval(env, "(p/ (1 3 3 1) (1 1) (1 1))"));
        assertEquals(read("()"), eval(env, "(p%)"));
        assertEquals(read("(1)"), eval(env, "(p% (1 1))"));
        assertEquals(read("(0)"), eval(env, "(p% (1 1) (1 1))"));
        assertEquals(read("(0)"), eval(env, "(p% (1 3 3 1) (1 1) (1 1))"));
        assertEquals(read("(1 -2)"), eval(env, "(p/ (1 -1 -6) (1 1))"));
        assertEquals(read("(-4)"), eval(env, "(p% (1 -1 -6) (1 1))"));
        assertEquals(read("(1 -1 -6)"), eval(env, "(p+ (p* (1 1) (1 -2)) (-4))"));
    }

    @Test 
    public void testDate() {
        Env env = defaultEnv();
        var d = LocalDate.now();
        int today = d.getYear() * 10000 + d.getMonthValue() * 100 + d.getDayOfMonth();
        assertEquals(dec(today), eval(env, "(today)"));
        assertEquals(dec(-4447), eval(env, "(days 19571029)"));
        try {
            eval(env, "(days 19579999)");
            fail();
        } catch (DecLispException e) {
            assertEquals(DateTimeException.class, e.getCause().getClass());
        }
        assertEquals(dec(0), eval(env, "(days 19700101)"));
        assertEquals(dec(19571029), eval(env, "(date -4447)"));
        assertEquals(dec(19700101), eval(env, "(date 0)"));
        try {
            eval(env, "(date " + Long.MAX_VALUE + ")");
            fail();
        } catch (DecLispException e) {
            assertEquals(DateTimeException.class, e.getCause().getClass());
        }
        assertEquals(sym("THURSDAY"), eval(env, "(week 19700101)"));
        assertEquals(sym("TUESDAY"), eval(env, "(week 19571029)"));
        try {
            eval(env, "(week 19579999)");
            fail();
        } catch (DecLispException e) {
            assertEquals(DateTimeException.class, e.getCause().getClass());
        }
    }
}
