package strategy.drives.pid;

import strategy.drives.pid.error.ControlErrorBase;
import strategy.drives.pid.error.DirectionControlError;


public class PIDDirectionControl extends PIDControlBase {

    public PIDDirectionControl(double pFactor, double iFactor, double dFactor) {
        super(pFactor, iFactor, dFactor);
        history.update(new DirectionControlError());
    }

    protected ControlResult getProportional(ControlErrorBase error) {
        DirectionControlError dirError = (DirectionControlError) error;
        return new ControlResult(
                dirError.getMeasure().y,
                dirError.getMeasure().y,
                dirError.getMeasure().x,
                dirError.getMeasure().x

        );
    }
}
