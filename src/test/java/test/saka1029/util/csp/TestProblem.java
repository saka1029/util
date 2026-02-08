package test.saka1029.util.csp;

import java.util.stream.IntStream;

import org.junit.Test;

import saka1029.util.csp.Problem;

public class TestProblem {

    static int number(int... ds) {
        return IntStream.of(ds).reduce(0, (a, b) -> 10 * a +b);
    }
    @Test
    public void testProblem() {
        Problem problem = new Problem();
        problem.className("SendMoreMoney");
        problem.variable(1, 9, "S", "M");
        problem.variable(0, 9, "E", "N", "D", "O", "R", "Y");
        problem.constraint("number(S, E, N, D) + number(M, O, R, Y) == number(M, O, N, E, Y)");
        problem.allDifferent("S", "E", "N", "D", "M", "O", "R", "Y");
        problem.anyCode("import java.util.stream.*;");
        problem.anyCode("static int number(int... ds) {");
        problem.anyCode("return IntStream.of(ds).reduce(0, (a, b) -> 10 * a +b);");
        problem.anyCode("}");
        System.out.println(problem);
    }
}
