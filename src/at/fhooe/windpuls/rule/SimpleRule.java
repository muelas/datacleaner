package at.fhooe.windpuls.rule;

import at.fhooe.windpuls.rule.operation.OperationBinary;

public class SimpleRule extends AbstractRule {

    final OperationBinary op;

    public SimpleRule(String column, double value, OperationBinary op) {
        super(column, value);
        this.op = op;
    }

    @Override
    public boolean match(double newValue) {
        return op.compare(value, newValue);
    }
}