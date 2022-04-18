package util.io;

public class WindowsTreeEntry {

    public static final String PATH_DELIM = "/";
    public final String volume;
    public final String[] path;
    public final boolean isFile;

    public WindowsTreeEntry(String volume, String[] path, boolean isFile) {
        this.volume = volume;
        this.path = path;
        this.isFile = isFile;
    }

    public String path() {
        return PATH_DELIM + String.join(PATH_DELIM, path);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(volume).append(":");
        sb.append(isFile ? "F:" : "D:");
        sb.append(path());
        return sb.toString();
    }

}
