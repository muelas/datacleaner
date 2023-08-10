package at.fhooe.windpuls.ruleengine.operation.ternary;

import at.fhooe.windpuls.ruleengine.operation.OperationTernary;

public class InInterval implements OperationTernary {
    @Override
    public boolean compare(double from, double to, double current) {
        return from <= current && current >= to;
    }

    @Override
    public String toString() {
        return "∈";
    }
}
