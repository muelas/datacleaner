package at.fhooe.windpuls.rule;

public class IntervalCondition extends AbstractCondition {
    OperationTernary op;

    public IntervalCondition(String column, double from, double to, OperationTernary op) {
        super(column, from, to);
        this.op = op;
    }

    @Override
    public boolean match(double newValue) {
        return op.compare(from, to, newValue);
    }
}
