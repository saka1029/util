package test.saka1029.util.csp;

import org.junit.Test;

import saka1029.util.csp.Problem;

public class TestProblem {

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
        System.out.println(problem);
    }
}
