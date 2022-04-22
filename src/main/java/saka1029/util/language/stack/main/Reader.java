package saka1029.util.language.stack.main;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import saka1029.util.language.stack.core.Context;
import saka1029.util.language.stack.values.Bool;
import saka1029.util.language.stack.values.Chars;
import saka1029.util.language.stack.values.Int;
import saka1029.util.language.stack.values.List;
import saka1029.util.language.stack.values.Real;
import saka1029.util.language.stack.values.Value;

public class Reader {
    public static final Value END_OF_STREAM = Context.code("End of stream", c -> { throw new RuntimeException(); });
    static final Pattern INTEGER_PAT = Pattern.compile("[-+]?(\\d+|0x[0-9a-f]+|0b[01]+)", Pattern.CASE_INSENSITIVE);
    static final Pattern DOUBLE_PAT = Pattern.compile("[-+]?\\d*\\.?\\d+([e][-+]?\\d+)?", Pattern.CASE_INSENSITIVE);
    static final Map<String, Value> CONSTANTS = Map.of("true", Bool.TRUE, "false", Bool.FALSE);

//    static Value code(Value e) {
//        return new Value() {
//
//            @Override
//            public void execute(Context c) {
//                e.run(c);
//            }
//
//            @Override
//            public String toString() {
//                return e.toString();
//            }
//
//        };
//    }
    static Value read(Context context, java.io.Reader reader) {
        try {
            return new Object() {
                int ch = ' ';

                void skipSpaces() throws IOException {
                    while (Character.isWhitespace(ch))
                        ch = reader.read();
                }

                Value readBlock() throws IOException {
                    ch = reader.read(); // skip '['
                    skipSpaces();
                    List.Builder builder = new List.Builder();
                    while (ch != -1 && ch != ']') {
                        builder.add(read());
                        skipSpaces();
                    }
                    if (ch != ']')
                        throw new RuntimeException("']' expected");
                    ch = reader.read(); // skip ']'
                    return builder.build();
                }

                Value readString() throws IOException {
                    ch = reader.read(); // skip '\"'
                    StringBuilder builder = new StringBuilder();
                    while (ch != -1 && ch != '\"') {
                        builder.append((char)ch);
                        ch = reader.read();
                    }
                    if (ch != '\"')
                        throw new RuntimeException("'\"' expected");
                    ch = reader.read(); // skip '\"'
                    return Chars.of(builder.toString());
                }

                int parseInt(String s) {
                    s = s.toLowerCase();
                    int radix = 10;
                    int start = 0;
                    if (s.startsWith("0b")) {
                        radix = 2;
                        start = 2;
                    } else if (s.startsWith("0x")) {
                        radix = 16;
                        start = 2;
                    }
                    return Integer.parseInt(s.substring(start), radix);
                }

                Value readWord() throws IOException {
                    StringBuilder sb = new StringBuilder();
                    while (ch != -1 && !Character.isWhitespace(ch) && ch != ']') {
                        sb.append((char)ch);
                        ch = reader.read();
                    }
                    String word = sb.toString();
                    if (word.startsWith("\'"))
                        return Int.of(word.codePointAt(1));
                    if (INTEGER_PAT.matcher(word).matches())
                        return Int.of(parseInt(word));
                    if (DOUBLE_PAT.matcher(word).matches())
                        return Real.of(Double.parseDouble(word));
                    if (CONSTANTS.containsKey(word))
                        return CONSTANTS.get(word);
// 早い展開
//                    Value e = context.globals.get(word);
//                    if (e != null)
//                        return code(e);
                    return Context.code(word, c -> {
                        Value x = c.globals.get(word);
                        if (x == null)
                            throw new RuntimeException(word + " is not defined");
                        x.run(c);
                    });
                }

                Value read() throws IOException {
                    skipSpaces();
                    switch (ch) {
                    case -1:
                        return END_OF_STREAM;
                    case ']':
                        throw new RuntimeException("unexpected ']'");
                    case '[':
                        return readBlock();
                    case '\"':
                        return readString();
                    default:
                        return readWord();
                    }
                }
            }.read();
        } catch (IOException e) {
            throw new RuntimeException("IOException throwed", e);
        }
    }


}
