package vision;

import vision.tools.VectorGeometry;

/**
 * Created by Simon Rovder
 */

 //ball object using vector geometry class to describe location and velocity
public class Ball {
    public VectorGeometry location;
    public VectorGeometry velocity;
    public Ball(){ }


    //overides usual clone function to provide exact copy of the ball
    @Override
    public Ball clone(){
        Ball ball = new Ball();
        ball.location = this.location == null ? null : this.location.clone();
        ball.velocity = this.velocity == null ? null : this.velocity.clone();
        return ball;
    }
}
