package strategy.points.basicPoints;

import strategy.points.*;
import strategy.Strategy;
import vision.*;
import vision.tools.VectorGeometry;


/**
 *
 */
public class KickablePoint extends DynamicPointBase{
    private static final int DISTANCE_TO_KICKER = 7;

    @Override
    public void recalculate() {
        Ball b = Strategy.world.getBall();

        if (b == null) {
            //Angry yelling
            System.out.println("cant find ball assholes");
        }

        if(b != null){
            VectorGeometry ball = b.location.clone();
            VectorGeometry goal = new EnemyGoal().toVectorGeometry();
            System.out.println("ball: " + ball.toString());
            System.out.println("goal: " + goal.toString());

            VectorGeometry ballToGoal = goal.minus(ball);
            System.out.println("ballToGoal: " + ballToGoal.toString());

            VectorGeometry kickablePoint = ball.plus(ballToGoal.normaliseToLength(-DISTANCE_TO_KICKER));
            System.out.println("kickablePoint: " + kickablePoint.toString());

            this.x = (int) kickablePoint.x;
            this.y = (int) kickablePoint.y;
        }
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
