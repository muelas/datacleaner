package at.fhooe.windpuls.ruleengine.rule;

public abstract class AbstractRule implements Rule {
    final String column;
    final String column2;
    final double value;
    final double to;
    final double from;
    int columnNr = -1;
    int columnNr2 = -1;

    protected AbstractRule(String column, double value) {
        this.column = column;
        this.column2 = column;
        this.value = value;
        this.from = value;
        this.to = Double.NEGATIVE_INFINITY;
    }

    public AbstractRule(String column, double from, double to) {
        this.column = column;
        this.column2 = column;
        this.to = to;
        this.from = from;
        this.value = from;
    }

    public AbstractRule(String column, String column2) {
        this.column = column;
        this.column2 = column2;
        this.to = Double.NEGATIVE_INFINITY;
        this.from = Double.NEGATIVE_INFINITY;
        this.value = Double.NEGATIVE_INFINITY;
    }

    @Override
    public int numColumns() {
        return 1;
    }

    @Override
    public int getColumnNr() {
        return this.columnNr;
    }

    @Override
    public void setColumnNr(int colNr) {
        this.columnNr = colNr;
    }

    @Override
    public String getColumn() {
        return this.column;
    }

    @Override
    public int getColumnNr2() {
        return this.columnNr2;
    }

    @Override
    public void setColumnNr2(int colNr) {
        this.columnNr2 = colNr;
    }

    @Override
    public String getColumn2() {
        return this.column2;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public abstract String toString();
}
