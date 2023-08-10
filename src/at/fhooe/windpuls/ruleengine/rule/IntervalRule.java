package at.fhooe.windpuls.ruleengine.rule;

import at.fhooe.windpuls.ruleengine.operation.OperationTernary;

public class IntervalRule extends AbstractRule {
    OperationTernary op;

    public IntervalRule(String column, double from, double to, OperationTernary op) {
        super(column, from, to);
        this.op = op;
    }

    @Override
    public boolean match(double... newValue) {
        return op.compare(from, to, newValue[0]);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.column + " (#" + this.columnNr + ")" + op.toString() + " " + this.from + "..." + this.to;
    }
}
