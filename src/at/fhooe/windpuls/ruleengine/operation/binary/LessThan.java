package at.fhooe.windpuls.ruleengine.operation.binary;

import at.fhooe.windpuls.ruleengine.operation.OperationBinary;

public class LessThan implements OperationBinary {
    @Override
    public boolean compare(double reference, double current) {
        return current < reference;
    }

    @Override
    public String toString() {
        return "<";
    }
}
