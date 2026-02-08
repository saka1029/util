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
        problem.variable("S", 1, 9);
        problem.variable("E", 0, 9);
        problem.variable("N", 0, 9);
        problem.variable("D", 0, 9);
        problem.variable("M", 1, 9);
        problem.variable("O", 0, 9);
        problem.variable("R", 0, 9);
        problem.variable("Y", 0, 9);
        problem.constraint("number(S, E, N, D) + number(M, O, R, Y) == number(M, O, N, E, Y)");
        problem.allDifferent("S", "E", "N", "D", "M", "O", "R", "Y");
        problem.anyCode("import java.util.stream.*;");
        problem.anyCode("static int number(int... ds) {");
        problem.anyCode("return IntStream.of(ds).reduce(0, (a, b) -> 10 * a +b);");
        problem.anyCode("}");
        System.out.println(problem);
    }
}
