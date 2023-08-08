package at.fhooe.windpuls.rule;

public abstract class AbstractCondition implements Condition {
    final String column;
    final double value;
    final double to;
    final double from;
    int columnNr = -1;

    protected AbstractCondition(String column, double value) {
        this.column = column;
        this.value = value;
        this.from = value;
        this.to = Double.NEGATIVE_INFINITY;
    }

    public AbstractCondition(String column, double to, double from) {
        this.column = column;
        this.to = to;
        this.from = from;
        this.value = from;
    }

    @Override
    public int getColumnNr() {
        return this.columnNr;
    }

    @Override
    public void setColumnNr(int colNr) {
        this.columnNr=colNr;
    }

    @Override
    public String getColumn() {
        return this.column;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        if (this.to != Double.NEGATIVE_INFINITY) {
            // Condition has from and to
            return this.getClass().getSimpleName() + " with Interval " + this.from + " - " + this.to + " in column " + this.column + " (" + this.columnNr + ")";
        } else {
            // Condition with single value
            return this.getClass().getSimpleName() + " for value " + this.value + " in column " + this.column + " (" + this.columnNr + ")";
        }
    }
}
