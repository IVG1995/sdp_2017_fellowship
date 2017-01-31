package communication.ports.interfaces;

/**
 * Abstracted grabber interface. Every robot port for a robot with a grabber should implement this.
 */
public interface GrabberEquippedRobotPort {
    public void grab();
    public void release();
}
