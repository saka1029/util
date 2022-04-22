package saka1029.util.language;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class JavaCompilerInMemory {

    private JavaCompilerInMemory() {
    }

    public static class CompileError extends Exception {
        private static final long serialVersionUID = 1L;

        CompileError(DiagnosticCollector<JavaFileObject> diagnostics) {
            super(diagnostics.getDiagnostics().stream()
                .map(d -> d + System.lineSeparator()).collect(Collectors.joining()));
        }
    }

    public static class Source extends SimpleJavaFileObject {

        CharSequence content;

        public Source(String className, CharSequence content) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension),
                Kind.SOURCE);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    static class ClassFile extends SimpleJavaFileObject {

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ClassFile(String name, Kind kind) {
            super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
        }

        byte[] getBytes() {
            return bos.toByteArray();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return bos;
        }
    }

    static class ClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {

        Map<String, ClassFile> classFiles = new HashMap<>();

        ClassFileManager(StandardJavaFileManager standardManager) {
            super(standardManager);
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return new ClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    ClassFile c = classFiles.get(name);
                    if (c == null)
                        throw new ClassNotFoundException("name");
                    byte[] b = c.getBytes();
                    return super.defineClass(name, b, 0, b.length);
                }
            };
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location,
            String className, Kind kind, FileObject sibling) throws IOException {
            ClassFile classFile = new ClassFile(className, kind);
            classFiles.put(className, classFile);
            return classFile;
        }
    }

    /**
     * 単一のソースをコンパイルします。
     *
     * @param fullName 完全修飾クラス名を指定します。
     * @param sourceCode コンパイルするソースコードを指定します。
     * @return コンパイルした結果のクラスオブジェクトを返します。
     * @throws ClassNotFoundException クラスオブジェクトをロードできなかった時にスローします。
     * @throws CompileError コンパイルに失敗したときにスローします。
     */
    public static Class<?> compile(String fullName, String sourceCode, Iterable<String> options) throws ClassNotFoundException, CompileError {
        return compile(List.of(new Source(fullName, sourceCode)), options).loadClass(fullName);
    }

    /**
     * 複数のソースを同時にコンパイルします。
     *
     * @param sources コンパイルするSourceのコレクションを指定します。
     * @return コンパイルした結果のクラスをロードするためのClassLoaderを返します。
     * @throws CompileError コンパイルに失敗したときにスローします。
     */
    public static ClassLoader compile(Collection<Source> sources, Iterable<String> options) throws CompileError {
        javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileManager fileManager = new ClassFileManager(
            compiler.getStandardFileManager(null, null, null));
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        if (!compiler.getTask(null, fileManager, diagnostics, options, null, sources).call())
            throw new CompileError(diagnostics);
        return fileManager.getClassLoader(null);
    }
}
