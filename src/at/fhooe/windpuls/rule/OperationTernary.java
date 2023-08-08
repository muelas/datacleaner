package at.fhooe.windpuls.rule;

public interface OperationTernary {
    boolean compare(double from, double to, double current);
}

class InInterval implements OperationTernary {
    @Override
    public boolean compare(double from, double to, double current) {
        return from<=current && current>=to;
    }
}

class NotInInterval implements OperationTernary {
    @Override
    public boolean compare(double from, double to, double current) {
        return !(from<=current && current>=to);
    }
}