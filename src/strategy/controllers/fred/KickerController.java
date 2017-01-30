package strategy.controllers.fred;

import strategy.controllers.ControllerBase;
import strategy.robots.RobotBase;
/**
 * Created by cole on 1/30/17.
 * A simple wrapper class that sends the kick command through FRED's port.
 * This code is written assuming that the kicker retracts itself automatically after kicking.
 * Note: This class might be simple enough that we could scrap it entirely and just send the
 * command from wherever we would normally call kick().
 */
public class KickerController extends ControllerBase {

    private static boolean kick_ball = false;

    public KickerController(RobotBase robot) {
        super(robot);
    }

    public void kick() {
        this.kick_ball = true;
    }

    public void perform() {
        if (this.kick_ball) {
            // Send kick command here
            this.kick_ball = false;
        }
    }


}
