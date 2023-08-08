package at.fhooe.windpuls.rule;

public class SimpleCondition extends AbstractCondition {

    final OperationBinary op;

    public SimpleCondition(String column, double value, OperationBinary op) {
        super(column, value);
        this.op = op;
    }

    @Override
    public boolean match(double newValue) {
        return op.compare(value, newValue);
    }
}