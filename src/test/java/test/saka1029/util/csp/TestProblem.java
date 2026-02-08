package test.saka1029.util.csp;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.IntStream;

import org.junit.Test;

import saka1029.util.csp.Problem;
import saka1029.util.language.JavaCompilerInMemory.CompileError;

public class TestProblem {

    static int number(int... ds) {
        return IntStream.of(ds).reduce(0, (a, b) -> 10 * a +b);
    }


    @Test
    public void testNumber() {
        assertEquals(1234, number(1,2,3,4));
    }

    @Test
    public void testProblem() {
        Problem problem = new Problem();
        problem.className("SendMoreMoney");
        problem.variable(1, 9, "S", "M");
        problem.variable(0, 9, "E", "N", "D", "O", "R", "Y");
        problem.constraint("number(S, E, N, D) + number(M, O, R, E) == number(M, O, N, E, Y)");
        problem.allDifferent("S", "E", "N", "D", "M", "O", "R", "Y");
        problem.anyCode("import java.util.stream.*;");
        problem.anyCode("static int number(int... ds) {");
        problem.anyCode("return IntStream.of(ds).reduce(0, (a, b) -> 10 * a + b);");
        problem.anyCode("}");
        System.out.println(problem);
    }

    @Test
    public void testGenerate() {
        Problem problem = new Problem();
        problem.className("SendMoreMoney");
        problem.variable(1, 9, "S", "M");
        problem.variable(0, 9, "E", "N", "D", "O", "R", "Y");
        problem.constraint("number(S, E, N, D) + number(M, O, R, E) == number(M, O, N, E, Y)");
        problem.allDifferent("S", "E", "N", "D", "M", "O", "R", "Y");
        problem.anyCode("import java.util.stream.*;");
        problem.anyCode("static int number(int... ds) {");
        problem.anyCode("return IntStream.of(ds).reduce(0, (a, b) -> 10 * a + b);");
        problem.anyCode("}");
        String generated = problem.generate();
        System.out.println(generated);
    }

    @Test
    public void testSolve() throws
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError {
        Problem problem = new Problem();
        problem.className("SendMoreMoney");
        problem.variable(1, 9, "S", "M");
        problem.variable(0, 9, "E", "N", "D", "O", "R", "Y");
        problem.constraint("number(S, E, N, D) + number(M, O, R, E) == number(M, O, N, E, Y)");
        problem.allDifferent("S", "E", "N", "D", "M", "O", "R", "Y");
        problem.anyCode("import java.util.stream.*;");
        problem.anyCode("static int number(int... ds) {");
        problem.anyCode("return IntStream.of(ds).reduce(0, (a, b) -> 10 * a + b);");
        problem.anyCode("}");
        problem.solve(true);
    }
}
