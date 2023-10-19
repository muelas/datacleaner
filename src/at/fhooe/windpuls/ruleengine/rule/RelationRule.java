package at.fhooe.windpuls.ruleengine.rule;

import at.fhooe.windpuls.ruleengine.operation.OperationBinary;

public class RelationRule extends AbstractRule {
    private final OperationBinary op;

    public RelationRule(String column, String column2, OperationBinary op) {
        super(column, column2);
        this.op = op;
    }

    @Override
    public boolean match(double... newValue) {
        if (newValue.length != 2)
            return false;
        return op.compare(newValue[1], newValue[0]);
    }

    @Override
    public int numColumns() {
        return 2;
    }

    @Override
    public String toString() {
        // Condition with two columns
        return this.getClass().getSimpleName() + ": " + this.column + " " + this.op.toString() + " " + this.column2;
    }
}
