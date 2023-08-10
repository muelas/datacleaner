package at.fhooe.windpuls.ruleengine.rule;

public class AndRule extends AbstractRule {

    public AndRule(Rule left, Rule right) {
        super(null, Double.NEGATIVE_INFINITY);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":  ";
    }

    @Override
    public boolean match(double... newValue) {
        return false;
    }
}
