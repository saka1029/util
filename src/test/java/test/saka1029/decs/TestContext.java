package test.saka1029.decs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import saka1029.util.decs.Binary;
import saka1029.util.decs.Context;
import saka1029.util.decs.Decs;
import saka1029.util.decs.Expression;
import saka1029.util.decs.Help;
import saka1029.util.decs.Unary;

public class TestContext {

    @Test
    public void testVariable() {
        Expression expression = c -> Decs.EMPTY;
        Unary unary = (c, a) -> Decs.EMPTY;
        Binary binary = (c, l, r) -> Decs.EMPTY;
        Context context = new Context();
        String name = "name";
        context.unary(name, unary, "unary");
        context.binary(name, binary, "binary");
        try (var c = context.variableTemp(name, expression, "variable")) {
            assertFalse(context.isUnary(name));
            assertFalse(context.isBinary(name));
            assertEquals(new Help<>(expression, "variable"), context.variable(name));
        }
        assertFalse(context.isVariable(name));
        assertEquals(new Help<>(unary, "unary"), context.unary(name));
        assertEquals(new Help<>(binary, "binary"), context.binary(name));
    }

}
