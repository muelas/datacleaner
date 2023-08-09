package at.fhooe.windpuls.ruleengine.rule;

public interface Rule {
    boolean match(double... newValue);

    int getColumnNr();

    void setColumnNr(int colNr);

    String getColumn();

    int getColumnNr2();

    void setColumnNr2(int colNr);

    String getColumn2();

    double getValue();

    int numColumns();
}
