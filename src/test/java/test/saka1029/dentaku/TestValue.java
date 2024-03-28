package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.Collectors;
import org.junit.Test;
import saka1029.util.dentaku.Value;

public class TestValue {

    static BigDecimal dec(double element) {
        return new BigDecimal(element);
    }

    static Value value(double... elements) {
        return Value
                .of(Arrays.stream(elements).mapToObj(BigDecimal::new).toArray(BigDecimal[]::new));
    }

    @Test
    public void testBigDecimal() {
        assertEquals(3, new BigDecimal("123.456").scale());
        assertEquals(5, new BigDecimal("1.23456").scale());
        assertEquals(0, new BigDecimal("123456").scale());
        assertEquals(3, new BigDecimal("-123.456").scale());
        assertEquals(5, new BigDecimal("-1.23456").scale());
        assertEquals(1, new BigDecimal("0.5").scale());
        assertEquals(1, new BigDecimal("0.5").multiply(new BigDecimal(2)).scale());
    }

    @Test
    public void testOf() {
        Value v123 = Value.of(dec(1), dec(2), dec(3));
        assertEquals(value(1, 2, 3), v123);
    }

    @Test
    public void testMap() {
        Value v = value();
        Value v1234 = value(1, 2, 3, 4);
        assertEquals(value(), v.map(BigDecimal::negate));
        assertEquals(value(-1, -2, -3, -4), v1234.map(BigDecimal::negate));
        // assertEquals(value(-1, 0, 1), value(-4, 0, 8).map(Value.SIGN));
        // assertEquals(value(-1, 0, 1), value(-Math.PI/2, 0, Math.PI/2).map(Value.SIN));
        // assertEquals(value(-1, 1, -1), value(-Math.PI, 0, Math.PI).map(Value.COS));
    }

    @Test
    public void testReduce() {
        // Value v = value();
        // Value v1234 = value(1, 2, 3, 4);
        // assertEquals(value(0), v.reduce(BigDecimal::add, BigDecimal.ZERO));
        // assertEquals(value(10), v1234.reduce(BigDecimal::add, BigDecimal.ZERO));
        // assertEquals(value(1), v.reduce(BigDecimal::multiply, BigDecimal.ONE));
        // assertEquals(value(24), v1234.reduce(BigDecimal::multiply, BigDecimal.ONE));
    }

    @Test
    public void testCumulate() {
        // Value v = value();
        // Value v1234 = value(1, 2, 3, 4);
        // assertEquals(value(), v.cumulate(BigDecimal::add, BigDecimal.ZERO));
        // assertEquals(value(1, 3, 6, 10), v1234.cumulate(BigDecimal::add, BigDecimal.ZERO));
        // assertEquals(value(), v.cumulate(BigDecimal::multiply, BigDecimal.ONE));
        // assertEquals(value(1, 2, 6, 24), v1234.cumulate(BigDecimal::multiply, BigDecimal.ONE));
    }

    @Test
    public void testReduceBOP() {
        // Value v = value();
        // Value v1234 = value(1, 2, 3, 4);
        // assertEquals(value(0), v.reduce(Value.ADD));
        // assertEquals(value(10), v1234.reduce(Value.ADD));
        // assertEquals(value(1), v.reduce(Value.MULT));
        // assertEquals(value(24), v1234.reduce(Value.MULT));
        // assertEquals(value(1), v1234.reduce(Value.MIN));
        // assertEquals(value(4), v1234.reduce(Value.MAX));
        // assertEquals(Value.of(Value.MAX_VALUE), v.reduce(Value.MIN));
        // assertEquals(Value.of(Value.MIN_VALUE), v.reduce(Value.MAX));
    }

    @Test
    public void testCumulateBOP() {
        // Value v = value();
        // Value v1234 = value(1, 2, 3, 4);
        // assertEquals(value(), v.cumulate(Value.ADD));
        // assertEquals(value(1, 3, 6, 10), v1234.cumulate(Value.ADD));
        // assertEquals(value(), v.cumulate(Value.MULT));
        // assertEquals(value(1, 2, 6, 24), v1234.cumulate(Value.MULT));
    }

    @Test
    public void testBinary() {
        Value v2 = value(2);
        Value v1234 = value(1, 2, 3, 4);
        Value v5678 = value(5, 6, 7, 8);
        assertEquals(value(3, 4, 5, 6), v1234.binary(BigDecimal::add, v2));
        assertEquals(value(3, 4, 5, 6), v2.binary(BigDecimal::add, v1234));
        assertEquals(value(6, 8, 10, 12), v1234.binary(BigDecimal::add, v5678));
        assertEquals(value(2, 4, 6, 8), v1234.binary(BigDecimal::multiply, v2));
        assertEquals(value(2, 4, 6, 8), v2.binary(BigDecimal::multiply, v1234));
        assertEquals(value(5, 12, 21, 32), v1234.binary(BigDecimal::multiply, v5678));
    }

    @Test
    public void testMapLogical() {
        // Value v1101 = value(1, 1, 0, 1);
        // assertEquals(value(0, 0, 1, 0), v1101.map(Value.NOT));
    }

    @Test
    public void testBinaryCompare() {
        // Value a = value(-1, 0, 1);
        // assertEquals(value(1, 0, 0), a.binary(Value.EQ, value(-1)));
        // assertEquals(value(0, 1, 0), a.binary(Value.EQ, value(0)));
        // assertEquals(value(0, 0, 1), a.binary(Value.EQ, value(1)));
        // assertEquals(value(0, 1, 1), a.binary(Value.NE, value(-1)));
        // assertEquals(value(1, 0, 1), a.binary(Value.NE, value(0)));
        // assertEquals(value(1, 1, 0), a.binary(Value.NE, value(1)));
        // assertEquals(value(0, 0, 0), a.binary(Value.LT, value(-1)));
        // assertEquals(value(1, 0, 0), a.binary(Value.LT, value(0)));
        // assertEquals(value(1, 1, 0), a.binary(Value.LT, value(1)));
        // assertEquals(value(1, 0, 0), a.binary(Value.LE, value(-1)));
        // assertEquals(value(1, 1, 0), a.binary(Value.LE, value(0)));
        // assertEquals(value(1, 1, 1), a.binary(Value.LE, value(1)));
        // assertEquals(value(0, 1, 1), a.binary(Value.GT, value(-1)));
        // assertEquals(value(0, 0, 1), a.binary(Value.GT, value(0)));
        // assertEquals(value(0, 0, 0), a.binary(Value.GT, value(1)));
        // assertEquals(value(1, 1, 1), a.binary(Value.GE, value(-1)));
        // assertEquals(value(0, 1, 1), a.binary(Value.GE, value(0)));
        // assertEquals(value(0, 0, 1), a.binary(Value.GE, value(1)));
    }

    @Test
    public void testBinaryLogical() {
        // Value a = value(1, 1, 0, 0), b = value(1, 0, 1, 0);
        // assertEquals(value(1, 0, 0, 0), a.binary(Value.AND, b));
        // assertEquals(value(1, 1, 1, 0), a.binary(Value.OR, b));
        // assertEquals(value(0, 1, 1, 0), a.binary(Value.XOR, b));
    }

    @Test
    public void testBinaryFilter() {
        assertEquals(value(1, 3), value(1, 0, 1, 0).filter(value(1, 2, 3, 4)));
        assertEquals(value(1, 3, 4), value(1, 0, 1, 1).filter(value(1, 2, 3, 4)));
        assertEquals(value(1, 2, 3, 4), value(1).filter(value(1, 2, 3, 4)));
        assertEquals(value(), value(0).filter(value(1, 2, 3, 4)));
    }

    static void sieve(BitSet set, int n) {
        for (int i = n + n, size = set.size(); i < size; i += n)
            set.clear(i);
    }

    static BitSet primes(int max) {
        BitSet set = new BitSet(max);
        set.set(2, max);
        sieve(set, 2);
        int size = (int) Math.sqrt(max);
        for (int i = 3; i <= size; i += 2)
            if (set.get(i))
                sieve(set, i);
        return set;
    }

    @Test
    public void testPrimes() {
        BitSet set = primes(10000);
        long[] ex = set.toLongArray();
        System.out.println("length=" + ex.length);
        System.out.println("long[] PRIMES_BITS = {");
        System.out.print(Arrays.stream(ex).mapToObj(x -> "0x%XL".formatted(x))
                .collect(Collectors.joining(", ")));
        System.out.println("};");
    }

    public static long[] PRIMES_BITS = {0x28208A20A08A28ACL, 0x800228A202088288L,
            0x8028208820A00A08L, 0x8028228800800A2L, 0x228800200A20A082L, 0x8820808228020800L,
            0x882802802022020L, 0x208808808008A202L, 0xA08200820000A00L, 0x800A2082820802L,
            0x200808220028208AL, 0x220808820808020L, 0x28A00A0020080022L, 0x8A20008A200080L,
            0x208220200808800L, 0x2822002080820880L, 0x800020A00A008280L, 0x8000020820208228L,
            0x2002020820080802L, 0xA00008020020A082L, 0x828008A20A08L, 0x80002020820000L,
            0x8008288082288002L, 0x808008008228A00L, 0x2000880880822080L, 0x8200002008282282L,
            0xA00200A20000028L, 0x2882000082082020L, 0x80008080080200L, 0x208200200A28820L,
            0x8200000A0002800L, 0x8028020808A208L, 0x8008028A00208020L, 0x20200A20A0002L,
            0xA000202088000008L, 0x220820228000808L, 0x208828200000A0L, 0x8208008220A208L,
            0x220808008220L, 0x20A0880802000080L, 0x2202000280080000L, 0x8820808A00800200L,
            0x20200A0082880822L, 0xA20A002008002L, 0x8000220808220008L, 0x800802020820080L,
            0x28000000A082200L, 0x820020220008808L, 0x2802000020880L, 0x88200288000002L,
            0xA28000020220208L, 0x882082800000080L, 0xA00028028008800AL, 0x200020000208000L,
            0x20800808000028A2L, 0xA00800A008282280L, 0x820808220800200L, 0x2022080022800800L,
            0x282000008208080L, 0x200A00008220008L, 0x2022000802880L, 0x800008800A08A088L,
            0x828208A00200000L, 0x28008200020A0000L, 0xA200082280008008L, 0xA08020000020000L,
            0x8028028200A00A0L, 0x200A008000000202L, 0x200208000200820L, 0x8208820000A0L,
            0x8A20800200AL, 0x820808000820028L, 0x820A08A0002000L, 0x220A000080080082L,
            0x8228800000808000L, 0x2000000080022002L, 0x80208002002080L, 0x800228020808228L,
            0x8008000A00A0882L, 0x8208280082200008L, 0x808800208000000L, 0x22880002002020L,
            0x2082000282002000L, 0x8800200800L, 0x20800A2080820820L, 0xA00008A08000020AL,
            0x820000008828008L, 0x80000000802022L, 0x220800200A288280L, 0x20A20000208020L,
            0x2020820080880000L, 0x28028220A088008L, 0x8008800008200L, 0x880000820000002L,
            0x8208002088202000L, 0x8028020200200A00L, 0x8A0000020080080L, 0xA082008000202208L,
            0x208208028008020L, 0x2020082082820082L, 0xA000008200000L, 0x200000008020220L,
            0x220A0802880002L, 0x2000200008008080L, 0x8020A00A00820028L, 0x28000A0000022000L,
            0x202288000080280L, 0x8808000800A08200L, 0x882802800000020L, 0x8008082002208082L,
            0x8000008020020008L, 0x80800002820020L, 0x2008288002080200L, 0x8000000208820L,
            0xA0002802002802L, 0x208000200000200L, 0x8220000A08000200L, 0x2822020820080802L,
            0x8800A082200280L, 0x220000208800020L, 0x2000882000880882L, 0x220000000A200L,
            0x800200200208200L, 0x20808008200008A2L, 0x20000000800A002L, 0x820008000020A00L,
            0x820802022820020L, 0x8008088200080000L, 0x8228028020000L, 0x2080022880022080L,
            0x202080200000200L, 0x8800A08800000028L, 0x20000000082000L, 0x200800008A008202L,
            0x28800200A00020L, 0x822022020800082L, 0x202082208008208L, 0x8028008800008800L,
            0x20800008A00200A0L, 0x80202208000000L, 0x28828000000A08L, 0x808020A0020L,
            0x8002208000208008L, 0x208800820200220L, 0x2000020002800820L, 0xA20008808000200AL,
            0x820800208000020L, 0x8A00000A2802820L, 0x2002008002080082L, 0x8000000008808L,
            0x20020800A0882002L, 0x28000020200A000L, 0x8820028000208820L, 0x800822002080800L,
            0x820008800800AL, 0x20800020200A08L};
    
    @Test
    public void testPrimesBits() {
        BitSet set = BitSet.valueOf(PRIMES_BITS);
        assertEquals(primes(10000), set);
    }

}
