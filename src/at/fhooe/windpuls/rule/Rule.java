package at.fhooe.windpuls.rule;

public interface Rule {
    boolean match(double newValue);
    int getColumnNr();
    void setColumnNr(int colNr);
    String getColumn();
    double getValue();
}
