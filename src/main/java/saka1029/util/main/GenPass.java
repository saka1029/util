package saka1029.util.main;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GenPass {
	
	static final Set<Character> EXCLUDE = Set.of('0', 'I', 'O', 'l');

	static final int[] CHARS = Stream.of(
		IntStream.rangeClosed('0', '9'),
		IntStream.rangeClosed('A', 'Z'),
		IntStream.rangeClosed('a', 'z'))
		.flatMapToInt(i -> i)
		.filter(i -> !EXCLUDE.contains((char)i))
		.toArray();

	static final int DEFAULT_LENGTH = 12;
	
	public static void main(String[] args) {
		int length = DEFAULT_LENGTH;
		if (args.length >= 1)
			length = Integer.parseInt(args[0]);
		int[] password = new Random().ints(length, 0, CHARS.length)
			.map(i -> CHARS[i])
			.toArray();
		System.out.println(new String(password, 0, length));
	}
}
