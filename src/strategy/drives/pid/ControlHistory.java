package strategy.drives.pid;


import strategy.drives.pid.error.ControlErrorBase;

public class ControlHistory {

    private ControlErrorBase accumulated;
    private ControlErrorBase previous;
    public ControlHistory() {}

    public void update(ControlErrorBase entry) {
        if (accumulated == null) {
            accumulated = entry;
        } else {
            accumulated = accumulated.add(entry);
        }
        previous = entry;
    }

    public ControlErrorBase getPrevious() {
        return previous;
    }

    public ControlErrorBase getAccumulated() {
        return accumulated;
    }

    public void setAccumulated(ControlErrorBase error) {
        this.accumulated = error;
    }
}
