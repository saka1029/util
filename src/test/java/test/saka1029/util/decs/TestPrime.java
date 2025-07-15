package test.saka1029.util.decs;

import static org.junit.Assert.assertArrayEquals;
import java.util.BitSet;
import java.util.stream.IntStream;
import org.junit.Test;

public class TestPrime {

    static BitSet primes(int max) {
        int upper = (int) Math.sqrt(max);
        BitSet primes = new BitSet(max);
        primes.set(0);
        primes.set(1);
        new Object() {
            void sieve(int n) {
                // System.out.printf("n=%d bit=%s%n", n, primes.get(n));
                if (primes.get(n))
                    return;
                for (int i = n + n; i < max; i += n)
                    primes.set(i);
            }

            void filter() {
                sieve(2);
                for (int i = 3; i <= upper; i += 2)
                    sieve(i);
            }
        }.filter();
        return primes;
    }

    static void print(BitSet set) {
        for (int i = 0, length = set.length(); i < length; ++i)
            if (!set.get(i))
                System.out.print(" " + i);
    }

    static final int[] PRIME1000 = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59,
            61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151,
            157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241,
            251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349,
            353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449,
            457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569,
            571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661,
            673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787,
            797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907,
            911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997,};

    @Test
    public void testPrimes() {
        BitSet primes = primes(1000);
        int[] array = IntStream.range(0, primes.length())
            .filter(i -> !primes.get(i))
            .toArray();
        assertArrayEquals(PRIME1000, array);
    }

}
