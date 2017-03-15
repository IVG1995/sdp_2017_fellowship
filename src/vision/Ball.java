package vision;

import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */
public class Ball {
    public VectorGeometry location;
    public VectorGeometry velocity;
    public Ball(){ }

    // FOR STRATEGY TESTING PURPOSES:
    public Ball(VectorGeometry loc, VectorGeometry vel) {
        this.location = loc;
        this.velocity = vel;
    }

    @Override
    public Ball clone(){
        Ball ball = new Ball();
        ball.location = this.location == null ? null : this.location.clone();
        ball.velocity = this.velocity == null ? null : this.velocity.clone();
        return ball;
    }
}
