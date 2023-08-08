package at.fhooe.windpuls.rule;

public interface OperationBinary {
    boolean compare(double reference, double current);
}

class GreaterThan implements OperationBinary{
    @Override
    public boolean compare(double reference, double current) {
        return current>reference;
    }
}


class GreaterEquals implements OperationBinary{
    @Override
    public boolean compare(double reference, double current) {
        return current>=reference;
    }
}


class LessThan implements OperationBinary{
    @Override
    public boolean compare(double reference, double current) {
        return current<reference;
    }
}


class LessEquals implements OperationBinary{
    @Override
    public boolean compare(double reference, double current) {
        return current<=reference;
    }
}


class Equals implements OperationBinary{
    @Override
    public boolean compare(double reference, double current) {
        return current==reference;
    }
}


class NotEqual implements OperationBinary{
    @Override
    public boolean compare(double reference, double current) {
        return current!=reference;
    }
}