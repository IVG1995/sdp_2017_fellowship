package strategy.drives.pid;


import strategy.drives.pid.error.ControlErrorBase;

public abstract class PIDControlBase {

    private double pFactor;
    private double iFactor;
    private double dFactor;
    protected ControlHistory history;

    public PIDControlBase(double pFactor, double iFactor, double dFactor) {
        this.pFactor = pFactor;
        this.iFactor = iFactor;
        this.dFactor = dFactor;
        this.history = new ControlHistory();
    }

    public ControlResult getActuatorInput(ControlErrorBase error) {
        ControlResult actuatorInput =
                getProportional(error).multiply(pFactor).add( // Kp * P(e(t))
                getIntegral(error).multiply(iFactor).add(     // Ki * Integral(e(t)) {in [0, t]}
                getDerivative(error).multiply(dFactor)        // Kd * d(e(t)) / dt
                ));
        history.update(error);
        return actuatorInput;
    }

    abstract ControlResult getProportional(ControlErrorBase error);

    protected ControlResult getIntegral(ControlErrorBase error){
        return getProportional(history.getAccumulated().add(error));
    }

    protected ControlResult getDerivative(ControlErrorBase error) {
        return getProportional(history.getPrevious().subtract(error));
    }

    public ControlHistory getHistory() {
        return history;
    }

    public String toString() {
        return String.format("%.3f, %.3f, %.3f", pFactor, iFactor, dFactor);
    }

}
