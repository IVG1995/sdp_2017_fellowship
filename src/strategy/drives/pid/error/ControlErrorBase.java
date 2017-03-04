package strategy.drives.pid.error;


public abstract class ControlErrorBase<T> {

    private T measure;

    public ControlErrorBase(T measure) {
        this.measure = measure;
    }

    public abstract ControlErrorBase add(ControlErrorBase that);
    public abstract ControlErrorBase subtract(ControlErrorBase that);

    public T getMeasure() {
        return measure;
    }

    public String toString() {
        return measure.toString();
    }

    abstract public ControlErrorBase zeroError();
}
