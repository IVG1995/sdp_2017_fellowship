package strategy.points.basicPoints;

import strategy.points.DynamicPointBase;
import strategy.Strategy;
import vision.tools.VectorGeometry;


public class ShootingPoint extends DynamicPointBase {
    private final int KICKING_DISTANCE = 5;
    @Override
    public void recalculate() {
        VectorGeometry ballLocation = Strategy.world.getBall().location;
//        System.out.println("ball: " + ballLocation.toString());
        VectorGeometry ballToEnemyGoal = VectorGeometry.fromTo(ballLocation, new EnemyGoal().toVectorGeometry());
        System.out.println("ballToEnemyGoal: " + ballToEnemyGoal.toString());
        VectorGeometry kickingPoint = ballToEnemyGoal.normaliseToLength(-KICKING_DISTANCE);


        this.x = (int) Math.round(kickingPoint.x + ballLocation.x);
        this.y = (int) Math.round(kickingPoint.y + ballLocation.y);
//        System.out.println("kickingPoint: " + this.x + " " + this.y);
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
