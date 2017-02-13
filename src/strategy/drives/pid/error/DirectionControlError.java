package strategy.drives.pid.error;


import vision.tools.VectorGeometry;

public class DirectionControlError extends ControlErrorBase<VectorGeometry> {

    public DirectionControlError(VectorGeometry force) {
        super(force);
    }

    public DirectionControlError() {
        super(new VectorGeometry(0d, 0d));
    }


    public ControlErrorBase add(ControlErrorBase that) {
        DirectionControlError dirError = (DirectionControlError) that;
        return new DirectionControlError(this.getMeasure().add(dirError.getMeasure()));
    }

    public ControlErrorBase subtract(ControlErrorBase that) {
        return new DirectionControlError(this.getMeasure().add(((VectorGeometry) that.getMeasure()).multiply(-1d)));
    }

    public ControlErrorBase zeroError() {
        return new DirectionControlError(new VectorGeometry(0d, 0d));
    }
}
