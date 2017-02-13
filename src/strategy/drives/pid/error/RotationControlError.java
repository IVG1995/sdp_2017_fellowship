package strategy.drives.pid.error;


public class RotationControlError extends ControlErrorBase<Double> {

    public RotationControlError(double rotation) {
        super(rotation);
    }

    public RotationControlError() {
        super(0d);
    }

    public ControlErrorBase add(ControlErrorBase that) {
        RotationControlError rotError = (RotationControlError) that;
        return new RotationControlError(this.getMeasure() + rotError.getMeasure());
    }

    public ControlErrorBase subtract(ControlErrorBase that) {
        return new RotationControlError(this.getMeasure() - (Double)that.getMeasure());
    }

    public ControlErrorBase zeroError() {
        return new RotationControlError(0d);
    }

}
