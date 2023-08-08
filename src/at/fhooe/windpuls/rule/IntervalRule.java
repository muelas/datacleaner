package at.fhooe.windpuls.rule;

import at.fhooe.windpuls.rule.operation.OperationTernary;

public class IntervalRule extends AbstractRule {
    OperationTernary op;

    public IntervalRule(String column, double from, double to, OperationTernary op) {
        super(column, from, to);
        this.op = op;
    }

    @Override
    public boolean match(double newValue) {
        return op.compare(from, to, newValue);
    }
}
