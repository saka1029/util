package test.saka1029.decs;

import org.junit.Test;
import saka1029.util.decs.DecsException;
import saka1029.util.decs.Scanner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static saka1029.util.decs.Scanner.*;
import static saka1029.util.decs.Scanner.TokenType.*;
import java.util.List;

public class TestScanner {

    Token t(TokenType t, String s) {
        return new Token(t, s);
    }

    @Test
    public void testScanner() {
        Scanner s = new Scanner();
        assertEquals(List.of(t(PLUS, "+"), t(NUM, "123.456e-2"), t(NE, "!=")),
            s.scan("  +  123.456e-2 !="));
        assertEquals(List.of(t(EXIT, "exit")), s.scan("  exit  "));
        assertNotEquals(t(PLUS, "+"), t(MINUS, "+"));
        assertNotEquals(t(PLUS, "+"), t(PLUS, "-"));
        assertNotEquals(t(PLUS, "+"), "+");
        assertEquals("Token(PLUS, +)", t(PLUS, "+").toString());
    }

    @Test
    public void testOperator() {
        Scanner scanner = new Scanner();
        List<Token> tokens = scanner.scan(
            "var =  (3 + 4) - (abc321 - 2) ^ 3.5 % X , 0");
        assertEquals(List.of(
            ID, ASSIGN, LP, NUM, PLUS, NUM, RP, MINUS, LP, ID,
            MINUS, NUM, RP, POW, NUM, MOD, ID, COMMA, NUM),
            tokens.stream().map(t -> t.type).toList());
        assertEquals(List.of(
            "var", "=", "(", "3", "+", "4", ")",
            "-", "(", "abc321", "-", "2", ")", "^", "3.5", "%", "X", ",", "0"),
            tokens.stream().map(t -> t.string).toList());
    }

    @Test
    public void testNumber() {
        Scanner scanner = new Scanner();
        List<Token> tokens = scanner.scan(
            "123 1.23 1.23e1 3E-2 4e+33");
        assertEquals(List.of(NUM, NUM, NUM, NUM, NUM),
            tokens.stream().map(t -> t.type).toList());
        assertEquals(List.of(
            "123", "1.23", "1.23e1", "3E-2", "4e+33"),
            tokens.stream().map(t -> t.string).toList());
    }

    @Test
    public void testComp() {
        Scanner scanner = new Scanner();
        List<Token> tokens = scanner.scan(
            "== != > >= < <=");
        assertEquals(List.of(EQ, NE, GT, GE, LT, LE),
            tokens.stream().map(t -> t.type).toList());
        assertEquals(List.of(
            "==", "!=", ">", ">=", "<", "<="),
            tokens.stream().map(t -> t.string).toList());
    }

    @Test
    public void testLog() {
        Scanner scanner = new Scanner();
        List<Token> tokens = scanner.scan(
            "!   &   |  ");
        assertEquals(List.of(ID, AND, OR),
            tokens.stream().map(t -> t.type).toList());
        assertEquals(List.of( "!", "&", "|"),
            tokens.stream().map(t -> t.string).toList());
    }

    @Test
    public void testKeyword() {
        Scanner scanner = new Scanner();
        List<Token> tokens = scanner.scan(
            "help  solve   exit   ");
        assertEquals(List.of(HELP, SOLVE, EXIT),
            tokens.stream().map(t -> t.type).toList());
        assertEquals(List.of( "help", "solve", "exit"),
            tokens.stream().map(t -> t.string).toList());
    }

    @Test
    public void testInvalidChar() {
        Scanner scanner = new Scanner();
        try {
            scanner.scan("#");
        } catch (DecsException e) {
            assertEquals("Unknown char '#'", e.getMessage());
        }
        try {
            scanner.scan("123.e2");
        } catch (DecsException e) {
            assertEquals("Digit expected but 'e'", e.getMessage());
        }
        try {
            scanner.scan("123.");
        } catch (DecsException e) {
            assertEquals("Digit expected but EOF", e.getMessage());
        }
    }

}
