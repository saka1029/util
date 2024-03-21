package saka1029.util.dentaku;

/**
 * ユーザー定義の二項演算子を呼び出します。
 */
public record BinaryCall(
    String leftVariable,
    String rightVariable,
    Expression body) implements Binary {

    @Override
    public Value apply(Context context, Value left, Value right) {
        Context child = context.child();
        child.variable(leftVariable, left, leftVariable);
        child.variable(rightVariable, right, rightVariable);
        return body.eval(child);
    }
}
