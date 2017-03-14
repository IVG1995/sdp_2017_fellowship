package strategy.drives.pid;

import strategy.drives.pid.error.ControlErrorBase;
import strategy.drives.pid.error.RotationControlError;


public class PIDRotationControl extends PIDControlBase {

    public PIDRotationControl(double pFactor, double iFactor, double dFactor) {
        super(pFactor, iFactor, dFactor);
        history.update(new RotationControlError());
    }

    protected ControlResult getProportional(ControlErrorBase error) {
        RotationControlError rotError = (RotationControlError) error;
        return new ControlResult(
                rotError.getMeasure() / Math.PI,
               -rotError.getMeasure() / Math.PI,
               -rotError.getMeasure() / Math.PI,
                rotError.getMeasure() / Math.PI
        );
    }

    @Override
    protected ControlResult getIntegral(ControlErrorBase error) {
        ControlResult motorPowers = getProportional(history.getAccumulated().add(error));
        if (Math.abs(motorPowers.getRight()) > 30){
            return motorPowers.multiply(30d / Math.abs(motorPowers.getRight()));
        } else {
            return motorPowers;
        }
    }

    @Override
    protected ControlResult getDerivative(ControlErrorBase error) {
        return getProportional(history.getPrevious().subtract(error));
    }
}
