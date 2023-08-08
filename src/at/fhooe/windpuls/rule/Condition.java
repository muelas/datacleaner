package at.fhooe.windpuls.rule;

public interface Condition {
    boolean match(double newValue);
    int getColumnNr();
    void setColumnNr(int colNr);
    String getColumn();
    double getValue();
}
