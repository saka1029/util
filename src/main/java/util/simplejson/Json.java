package util.simplejson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Json {

    public static final String KEY_ESCAPE = "/";
    public static final String KEY_SEPARATOR = ".";
    static final Pattern DELIMITER = Pattern.compile("(?<!" + KEY_ESCAPE + ")[" + KEY_SEPARATOR + "]");
    public static final String ANY_KEY = "*";
    public static final String ANY_KEYS = "**";
    public static final Object NOTHING = new Object() { public String toString() { return "NOTHING"; }};

    public static boolean isMap(Object value) { return value instanceof Map<?, ?>; }
    public static boolean isList(Object value) { return value instanceof List<?>; }
    public static boolean isBoolean(Object value) { return value instanceof Boolean; }
    public static boolean isString(Object value) { return value instanceof String; }
    public static boolean isDouble(Object value) { return value instanceof Double; }
    public static boolean isInt(Object value) { return value instanceof Integer; }
    public static boolean isLong(Object value) { return value instanceof Long; }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object value) { return (Map<String, Object>) value; }
    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object value) { return (List<Object>) value; }
    public static boolean asBoolean(Object value) { return (boolean) value; }
    public static String asString(Object value) { return (String) value; }
    public static double asDouble(Object value) { return ((Number) value).doubleValue(); }
    public static int asInt(Object value) { return ((Number) value).intValue(); }
    public static long asLong(Object value) { return ((Number) value).longValue(); }

    public static Collection<Object> values(Object value) {
        return isList(value) ? asList(value)
            : isMap(value) ? asMap(value).values()
            : Collections.emptyList();
    }

    public static Object get(Object value, String path) {
        Object result = get(value, 0, parsePath(path));
        if (result == NOTHING)
            throw new IllegalArgumentException("Not found path=" + path);
        return result;
    }

    static Object get(Object value, int index, String... keys) {
//        System.out.println("get: index=" + index + " keys=" + Arrays.toString(keys) +" value=" + value);
        for (int i = index, size = keys.length; i < size; ++i) {
            String key = keys[i];
            if (key == ANY_KEY) {
                for (Object child : values(value)) {
                    Object result = get(child, i + 1, keys);
                    if (result != NOTHING)
                        return result;
                }
                return NOTHING;
            } else if (key == ANY_KEYS)
                return getDeep(value, i + 1, keys);
            else if (isList(value) && key.matches("\\d+"))
                value = asList(value).get(Integer.parseInt(key));
            else if (isMap(value) && asMap(value).containsKey(key))
                value = asMap(value).get(key);
            else
                return NOTHING;
        }
        return value;
    }

    static Object getDeep(Object value, int index, String... keys) {
//        System.out.println("getDeep: index=" + index + " keys=" + Arrays.toString(keys) +" value=" + value);
        if (index >= keys.length)
            return NOTHING;
        Object result = get(value, index, keys);
        if (result != NOTHING)
            return result;
        else if (isList(value))
            for (Object next : asList(value)) {
                Object r = getDeep(next, index, keys);
                if (r != NOTHING)
                    return r;
            }
        else if (isMap(value))
            for (Object next : asMap(value).values()) {
                Object r = getDeep(next, index, keys);
                if (r != NOTHING)
                    return r;
            }
        return NOTHING;
    }

    public static List<Object> select(Object value, String path) {
        List<Object> result = new ArrayList<>();
        select(value, result, 0, parsePath(path));
        return result;
    }

    private static void select(Object value, List<Object> result, int index, String... keys) {
        for (int i = index, size = keys.length; i < size; ++i) {
            String key = keys[i];
            if (key == ANY_KEY) {
                for (Object child : values(value))
                    select(child, result, i + 1, keys);
                return;
            } else if (key == ANY_KEYS)
                selectDeep(value, result, i + 1, keys);
            else if (isList(value) && key.matches("\\d+"))
                value = asList(value).get(Integer.parseInt(key));
            else if (isMap(value) && asMap(value).containsKey(key))
                value = asMap(value).get(key);
            else
                return;
        }
        result.add(value);
    }

    static void selectDeep(Object value, List<Object> results, int index, String... keys) {
        if (index >= keys.length)
            return;
        select(value, results, index, keys);
        if (isList(value))
            for (Object next : asList(value))
                selectDeep(next, results, index, keys);
        else if (isMap(value))
            for (Object next : asMap(value).values())
                selectDeep(next, results, index, keys);
    }

    public static String[] parsePath(String path) {
        String[] keys = DELIMITER.split(path);
        int size = keys.length;
        for (int i = 0; i < size; ++i) {
            if (keys[i].equals(ANY_KEY))
                keys[i] = ANY_KEY;
            else if (keys[i].equals(ANY_KEYS))
                keys[i] = ANY_KEYS;
            else
                keys[i] = keys[i].replaceAll(KEY_ESCAPE + "(.)", "$1");
        }
//        System.out.println("path=" + path + " -> " + Arrays.toString(keys));
        return keys;
    }

    public static List<Object> list(Object... objects) {
        return Stream.of(objects)
            .collect(Collectors.toList());
    }

    public static Map<String, Object> map(Object... objects) {
        int size = objects.length;
        if (size % 2 != 0)
            throw new IllegalArgumentException("Size of objects must be even");
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < objects.length; i += 2) {
            if (!(objects[i] instanceof String))
                throw new IllegalArgumentException("Key must be string: " + objects[i]);
            map.put((String)objects[i], objects[i + 1]);
        }
        return map;
    }

    static void format(String s, StringBuilder sb) {
        sb.append("\"");
        for (int i = 0, size = s.length(); i < size; ++i) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\t': sb.append("\\t"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\b': sb.append("\\b"); break;
                case '\"': sb.append("\\\""); break;
                default: sb.append(ch); break;
            }
        }
        sb.append("\"");
    }

    static void indent(StringBuilder sb, int level) {
        for (int i = 0; i < level; ++i)
            sb.append("  ");
    }

    static final String NL = String.format("%n");

    static void format(List<Object> list, StringBuilder sb, int level) {
        sb.append("[");
        String sep = NL;
        for (Object e : list) {
            sb.append(sep);
            indent(sb, level + 1); format(e, sb, level + 1);
            sep = "," + NL;
        }
        sb.append(NL);
        indent(sb, level); sb.append("]");
    }

    static void format(Map<String, Object> object, StringBuilder sb, int level) {
        sb.append("{");
        String sep = NL;
        for (Entry<String, Object> e : object.entrySet()) {
            sb.append(sep);
            indent(sb, level + 1);
            format(e.getKey(), sb, level + 1);
            sb.append(" : ");
            format(e.getValue(), sb, level + 1);
            sep = "," + NL;
        }
        sb.append(NL);
        indent(sb, level); sb.append("}");
    }

    static void format(Object object, StringBuilder sb, int level) {
        if (isString(object))
            format(asString(object), sb);
        else if (isList(object))
            format(asList(object), sb, level);
        else if (isMap(object))
            format(asMap(object), sb, level);
        else
            sb.append(object);
    }

    public static String format(Object object) {
        StringBuilder sb = new StringBuilder();
        format(object, sb, 0);
        return sb.toString();
    }

    public static Object parse(File file) throws IOException {
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            return new Parser(reader).parse();
        }
    }

    public static Object parse(File file, Charset encoding) throws IOException {
        try (Reader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), encoding))) {
            return new Parser(reader).parse();
        }
    }

    public static Object parse(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            return new Parser(reader).parse();
        }
    }

    public static Object parse(Path path, Charset encoding) throws IOException {
        try (Reader reader = Files.newBufferedReader(path, encoding)) {
            return new Parser(reader).parse();
        }
    }

    public static Object parse(Reader reader) throws IOException {
        return new Parser(reader).parse();
    }

    public static Object parse(String s) {
        try (Reader reader = new StringReader(s)) {
            return parse(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Parser {

        private static final int EOF = -1;
        private final Reader reader;
        private int ch = ' ';

        static boolean isDigit(int ch) {
            return ch >= '0' && ch <= '9';
        }

        static boolean isIdFirst(int ch) {
            return ch >= 'a' && ch <= 'z'
                || ch >= 'A' && ch <= 'Z'
                || ch == '_'
                || ch > 255;
        }

        static boolean isIdRest(int ch) {
            return isIdFirst(ch) || isDigit(ch);
        }

        public Parser(Reader reader) {
            this.reader = reader;
        }

        int get() throws IOException {
            return ch = reader.read();
        }

        void skipSpaces() throws IOException {
            while (Character.isWhitespace(ch))
                get();
        }

        void pair(Map<String, Object> map) throws IOException {
            Object key = parse();
            if (!(key instanceof String))
                throw new RuntimeException("String expected but " + key + " appeared");
            skipSpaces();
            if (ch != ':')
                throw new RuntimeException("':' expected but '" + (char) ch + "' appeared");
            get(); // skip ':'
            Object value = parse();
            map.put((String) key, value);
        }

        /**
         * '{' [ STRING ':' OBJECT { ',' STRING ':' OBJECT } ] '}'
         * @return
         * @throws IOException
         */
        Map<String, Object> object() throws IOException {
            Map<String, Object> map = new LinkedHashMap<>();
            get(); // skip '{'
            skipSpaces();
            while (ch != EOF && ch != '}') {
                pair(map);
                skipSpaces();
                if (ch != ',')
                    break;
                get();  // skip ','
                skipSpaces();
            }
            if (ch != '}')
                throw new RuntimeException("'}' expected");
            get();  // skip '}'
            return map;
        }

        /**
         * '[' [ OBJECT { ',' OBJECT } ] ']'
         *
         * @return
         * @throws IOException
         */
        List<Object> array() throws IOException {
            List<Object> array = new ArrayList<>();
            get(); // skip '['
            skipSpaces();
            while (ch != EOF && ch != ']') {
                array.add(parse());
                skipSpaces();
                if (ch != ',')
                    break;
                get();  // skip ','
                skipSpaces();
            }
            if (ch != ']')
                throw new RuntimeException("']' expected");
            get();  // skip ']'
            return array;
        }

        static boolean isHexDigit(int ch) {
            ch = Character.toLowerCase(ch);
            return ch >= 'a' && ch <= 'f' || ch >= '0' && ch <= '9';
        }

        void unicode(StringBuilder sb) throws IOException {
            int start = sb.length();
            sb.append((char)ch);    // 'u'
            get();
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 4 && isHexDigit(ch); ++i) {
                sb.append((char)ch);
                hex.append((char)ch);
                get();
            }
            if (hex.length() == 4) {
                sb.setLength(start);
                sb.append((char)Integer.parseInt(hex.toString(), 16));
            }
        }

        String string() throws IOException {
            StringBuilder sb = new StringBuilder();
            get(); // skip '"'
            L: while (true) {
                switch (ch) {
                case EOF: throw new RuntimeException("Untermindated string");
                case '\\':
                    switch (get()) {
                    case EOF: throw new RuntimeException("Unterminated string");
                    case 'b': sb.append('\b'); get(); break;
                    case 'f': sb.append('\f'); get(); break;
                    case 'n': sb.append('\n'); get(); break;
                    case 'r': sb.append('\r'); get(); break;
                    case 't': sb.append('\t'); get(); break;
                    case 'u': unicode(sb); break;
                    default: sb.append((char) ch); get(); break;
                    }
                    break;
                case '"': get(); break L;
                case '\n':
                case '\r': throw new RuntimeException("Untermindated string");
                default: sb.append((char) ch); get(); break;
                }
            }
            return sb.toString();
        }

        Number number() throws IOException {
            StringBuilder sb = new StringBuilder();
//            boolean isLong = true;
            do {
                sb.append((char) ch);
                get();
            } while (isDigit(ch));
            if (ch == '.') {
//                isLong = false;
                do {
                    sb.append((char) ch);
                    get();
                } while (isDigit(ch));
            }
            if (Character.toLowerCase(ch) == 'e') {
//                isLong = false;
                sb.append((char) ch);
                get();
                if (ch == '+' || ch == '-') {
                    sb.append((char) ch);
                    get();
                }
                while (isDigit(ch)) {
                    sb.append((char) ch);
                    get();
                }
            }
            String s = sb.toString();
            return Double.parseDouble(s);
//            if (isLong)
//                return Long.parseLong(s);
//            else
//                return Double.parseDouble(s);
        }

        Object keyword() throws IOException {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append((char) ch);
                get();
            } while (isIdRest(ch));
            String keyword = sb.toString();
            switch (keyword) {
            case "true": return Boolean.TRUE;
            case "false": return Boolean.FALSE;
            case "null": return null;
            default: return keyword;
            }
        }

        Object parse() throws IOException {
            skipSpaces();
            switch (ch) {
            case '{': return object();
            case '[': return array();
            case '\"': return string();
            case '-': return number();
            case '+': return number();
            default:
                if (isDigit(ch))
                    return number();
                else if (isIdFirst(ch))
                    return keyword();
                else
                    throw new RuntimeException("Unknown char '" + (char) ch + "'");
            }
        }
    }

}
