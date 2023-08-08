package at.fhooe.windpuls.rule.operation.ternary;

import at.fhooe.windpuls.rule.operation.OperationTernary;

public class NotInInterval implements OperationTernary {
    @Override
    public boolean compare(double from, double to, double current) {
        return !(from <= current && current >= to);
    }
}
