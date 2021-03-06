package communication.ports.interfaces;

/**
 * Abstracted kicker interface. Every robot port for a robot with a grabber should implement this.
 */
public interface KickerEquippedRobotPort {
    public void kick();
    public void startKick();
    public void stopKick();
    public boolean isKicking();
}
