package at.fhooe.windpuls.rule;

public class TimedCondition extends AbstractCondition {
    final Condition cond;
    final int frame;
    int counter;

    // with side-effects!
    @Override
    public boolean match(double newValue) {
        if (checkMatch(newValue)) {
            this.counter++;
            return this.counter >= this.frame;
        } else {
            this.counter = 0;
            return false;
        }
    }

    // check for match without side-effects
    public boolean checkMatch(double newValue) {
        return cond.match(newValue);
    }

    public TimedCondition(Condition cond, int frame) {
        super(cond.getColumn(), cond.getValue());
        this.cond = cond;
        this.frame = frame;
        this.counter = 0;
    }

    public int getCounter() {
        return this.counter;
    }

    public boolean isNew() {
        return this.frame == this.counter;
    }

    @Override
    public void setColumnNr(int colNr) {
        super.setColumnNr(colNr);
        this.cond.setColumnNr(colNr);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " for frame size " + this.frame + " with Condition: " + cond.toString();
    }
}
