package util.simplejson;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    public interface CollectionBuilder {
        Object get();
    }

    public interface ObjectBuilder extends CollectionBuilder {
        void set(String key, Object value);
    }

    public interface ArrayBuilder extends CollectionBuilder {
        void set(double index, Object value);
    }

    public static abstract class CollectionToken {
        public final CollectionToken parent;

        public CollectionToken(CollectionToken parent) {
            this.parent = parent;
        }

        public abstract boolean isObject();
        public abstract boolean isArray();
        public abstract boolean is(String key);
        public abstract boolean is(double index);

    }

    public static class ObjectToken extends CollectionToken {
        String key = "";

        public String key() { return key; }

        public ObjectToken(CollectionToken parent) {
            super(parent);
        }

        @Override
        public String toString() {
            return (parent == ROOT ? "" : parent.toString() + ".") + key;
        }

        @Override public boolean isObject() { return true; }
        @Override public boolean isArray() { return false; }
        @Override public boolean is(String key) { return this.key.equals(key); }
        @Override public boolean is(double index) { return false; }
    }

    public static class ArrayToken extends CollectionToken {
        double index;

        public double index() { return index; }

        public ArrayToken(CollectionToken parent) {
            super(parent);
        }

        @Override
        public String toString() {
            return (parent == ROOT ? "" : parent.toString() + ".") + index;
        }

        @Override public boolean isObject() { return false; }
        @Override public boolean isArray() { return true; }
        @Override public boolean is(String key) { return false; }
        @Override public boolean is(double index) { return this.index == index; }
    }

    public interface Handler {
        ObjectBuilder object(CollectionToken parent);
        ArrayBuilder array(CollectionToken parent);
    }

    public static class DefaultHandler implements Handler {

        @Override
        public ObjectBuilder object(CollectionToken parent) {
            System.out.println("start object parent=" + parent);
            return new ObjectBuilder() {
                Map<String, Object> map = new LinkedHashMap<>();

                @Override
                public Object get() {
                    return map;
                }

                @Override
                public void set(String key, Object value) {
                    map.put(key, value);
                }

            };
        }

        @Override
        public ArrayBuilder array(CollectionToken parent) {
            return new ArrayBuilder() {
                List<Object> list = new ArrayList<>();

                @Override
                public Object get() {
                    return list;
                }

                @Override
                public void set(double index, Object value) {
                    list.add(value);
                    // int i = (int)index;
                    // while (list.size() <= i)
                    // list.add(null);
                    // list.set(i, value);
                }
            };
        }

    }

    public static final Handler DEFAULT_HANDLER = new DefaultHandler();

    public static Object parse(Reader reader) throws IOException {
        return parse(reader, DEFAULT_HANDLER);
    }

    public static final ObjectToken ROOT = new ObjectToken(null) {
        @Override
        public String toString() {
            return "";
        }
    };

    public static Object parse(Reader reader, Handler handler) throws IOException {
        return new Parser(reader, handler).parse(ROOT);
    }

    private static class Parser {

        private static final int EOF = -1;
        private final Reader reader;
        private final Handler handler;
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

        public Parser(Reader reader, Handler handler) {
            this.reader = reader;
            this.handler = handler;
        }

        int get() throws IOException {
            return ch = reader.read();
        }

        void skipSpaces() throws IOException {
            while (Character.isWhitespace(ch))
                get();
        }

        void pair(ObjectToken parent, ObjectBuilder wrapper) throws IOException {
            skipSpaces();
            String key;
            if (ch == '\"')
                key = string();
            else {
                Object keyword = keyword();
                if (!(keyword instanceof String))
                    throw new RuntimeException("Strign expected but " + keyword + " appeared");
                key = (String) keyword;
            }
            skipSpaces();
            if (ch != ':')
                throw new RuntimeException("':' expected but '" + (char) ch + "' appeared");
            get(); // skip ':'
            parent.key = key;
            Object value = parse(parent);
            wrapper.set(key, value);
        }

        /**
         * '{' [ STRING ':' OBJECT { ',' STRING ':' OBJECT } ] '}'
         *
         * @return
         * @throws IOException
         */
        Object object(CollectionToken parent) throws IOException {
            ObjectToken token = new ObjectToken(parent);
            ObjectBuilder wrapper = handler.object(parent);
            get(); // skip '{'
            skipSpaces();
            while (ch != EOF && ch != '}') {
                pair(token, wrapper);
                skipSpaces();
                if (ch != ',')
                    break;
                get(); // skip ','
                skipSpaces();
            }
            if (ch != '}')
                throw new RuntimeException("'}' expected");
            get(); // skip '}'
            return wrapper.get();
        }

        /**
         * '[' [ OBJECT { ',' OBJECT } ] ']'
         *
         * @return
         * @throws IOException
         */
        Object array(CollectionToken parent) throws IOException {
            ArrayToken token = new ArrayToken(parent);
            ArrayBuilder wrapper = handler.array(parent);
            get(); // skip '['
            skipSpaces();
            while (ch != EOF && ch != ']') {
                Object value = parse(token);
                wrapper.set(token.index, value);
                skipSpaces();
                if (ch != ',')
                    break;
                get(); // skip ','
                skipSpaces();
                ++token.index;
            }
            if (ch != ']')
                throw new RuntimeException("']' expected");
            get(); // skip ']'
            return wrapper.get();
        }

        static boolean isHexDigit(int ch) {
            ch = Character.toLowerCase(ch);
            return ch >= 'a' && ch <= 'f' || ch >= '0' && ch <= '9';
        }

        void unicode(StringBuilder sb) throws IOException {
            int start = sb.length();
            sb.append((char) ch); // 'u'
            get();
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 4 && isHexDigit(ch); ++i) {
                sb.append((char) ch);
                hex.append((char) ch);
                get();
            }
            if (hex.length() == 4) {
                sb.setLength(start);
                sb.append((char) Integer.parseInt(hex.toString(), 16));
            }
        }

        String string() throws IOException {
            StringBuilder sb = new StringBuilder();
            get(); // skip '"'
            L: while (true) {
                switch (ch) {
                case EOF:
                    throw new RuntimeException("Untermindated string");
                case '\\':
                    switch (get()) {
                    case EOF:
                        throw new RuntimeException("Unterminated string");
                    case 'b':
                        sb.append('\b');
                        get();
                        break;
                    case 'f':
                        sb.append('\f');
                        get();
                        break;
                    case 'n':
                        sb.append('\n');
                        get();
                        break;
                    case 'r':
                        sb.append('\r');
                        get();
                        break;
                    case 't':
                        sb.append('\t');
                        get();
                        break;
                    case 'u':
                        unicode(sb);
                        break;
                    default:
                        sb.append((char) ch);
                        get();
                        break;
                    }
                    break;
                case '"':
                    get();
                    break L;
                case '\n':
                case '\r':
                    throw new RuntimeException("Untermindated string");
                default:
                    sb.append((char) ch);
                    get();
                    break;
                }
            }
            return sb.toString();
        }

        Number number() throws IOException {
            StringBuilder sb = new StringBuilder();
            // boolean isLong = true;
            do {
                sb.append((char) ch);
                get();
            } while (isDigit(ch));
            if (ch == '.') {
                // isLong = false;
                do {
                    sb.append((char) ch);
                    get();
                } while (isDigit(ch));
            }
            if (Character.toLowerCase(ch) == 'e') {
                // isLong = false;
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
        }

        Object keyword() throws IOException {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append((char) ch);
                get();
            } while (isIdRest(ch));
            String keyword = sb.toString();
            switch (keyword) {
            case "true":
                return Boolean.TRUE;
            case "false":
                return Boolean.FALSE;
            case "null":
                return null;
            default:
                return string();
            }
        }

        Object parse(CollectionToken parent) throws IOException {
            skipSpaces();
            switch (ch) {
            case '{':
                return object(parent);
            case '[':
                return array(parent);
            case '\"':
                return string();
            case '-':
            case '+':
                return number();
            default:
                if (isDigit(ch)) {
                    return number();
                } else if (isIdFirst(ch)) {
                    return keyword();
                } else
                    throw new RuntimeException("Unknown char '" + (char) ch + "'");
            }
        }
    }

}
