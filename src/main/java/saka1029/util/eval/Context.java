package saka1029.util.eval;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.oer.its.ContributedExtensionBlock;

public class Context {
    private final Context parent;
    private final Map<String, Expression> binding;

    Context(Context parent) {
        this.parent = parent;
        this.binding = new HashMap<>();
    }

    public static Context of() {
        return new Context(null);
    }

    public Context child() {
        return new Context(this);
    }

    public Expression get(String name) {
        Expression e = binding.get(name);
        return e != null ? e : parent != null ? parent.get(name) : null;
    }

    public void put(String name, Expression e) {
        binding.put(name, e);
    }
    
}
