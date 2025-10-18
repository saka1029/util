package saka1029.util.stack;

public abstract class Value implements Executable{

    @Override
    public void execute(Context context) {
        context.push(this);
    }

}
