package strategy.points.basicPoints;

import strategy.points.DynamicPointBase;
import strategy.Strategy;
import vision.tools.VectorGeometry;


public class ShootingPoint extends DynamicPointBase {
    private final int KICKING_DISTANCE = 5;
    @Override
    public void recalculate() {
        VectorGeometry ballLocation = Strategy.world.getBall().location;
        VectorGeometry ballToEnemyGoal = VectorGeometry.fromTo(ballLocation, new EnemyGoal().toVectorGeometry());
        VectorGeometry kickingPoint = ballToEnemyGoal.normal(-KICKING_DISTANCE);
        this.x = (int) kickingPoint.x;
        this.y = (int) kickingPoint.y;
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
