package strategy.points.basicPoints;

import strategy.points.*;
import strategy.Strategy;
import vision.*;
import vision.constants.Constants;
import vision.tools.VectorGeometry;


/**
 *
 */
public class KickablePoint extends DynamicPointBase{

    @Override
    public void recalculate() {
        Ball b = Strategy.world.getBall();

        if (b == null) {
            //Angry yelling
//            System.out.println("cant find ball assholes");
        }

        if(b != null){
            VectorGeometry ball = b.location.clone();
            VectorGeometry goal = new EnemyGoal().toVectorGeometry();
//            System.out.println("ball: " + ball.toString());

            VectorGeometry ballToGoal = goal.minus(ball);

            VectorGeometry kickablePoint = ball.plus(ballToGoal.normaliseToLength(-Constants.distanceToKicker));
//            System.out.println("kickablePoint: " + kickablePoint.toString());

            this.x = (int) kickablePoint.x;
            this.y = (int) kickablePoint.y;
        }
//        System.out.println(this.toVectorGeometry().toString());
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }



}
