package at.fhooe.windpuls.rule.operation.binary;

import at.fhooe.windpuls.rule.operation.OperationBinary;

public class NotEqual implements OperationBinary {
    @Override
    public boolean compare(double reference, double current) {
        return current != reference;
    }

    @Override
    public String toString() {
        return "!=";
    }
}
