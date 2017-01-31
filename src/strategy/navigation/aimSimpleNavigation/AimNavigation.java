package strategy.navigation.aimSimpleNavigation;

import strategy.Strategy;
import strategy.navigation.NavigationInterface;
import strategy.navigation.Obstacle;
import strategy.navigation.potentialFieldNavigation.fieldsources.PointSource;
import vision.Robot;
import vision.RobotType;
import vision.tools.VectorGeometry;

import java.util.LinkedList;

/**
 * Created by Simon Rovder
 */
public class AimNavigation implements NavigationInterface {
    private VectorGeometry heading = null;
    private VectorGeometry destination = null;
    private LinkedList<Obstacle> obstacles;

    @Override
    public String toString(){
        return (this.destination != null ? this.destination.getClass().toString() : "NULL") + " - " + (this.heading != null ? this.heading.getClass().toString() : "NULL");
    }

    @Override
    public void setHeading(VectorGeometry heading){
        this.heading = heading;
    }

    @Override
    public void setObstacles(LinkedList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    @Override
    public void clearObstacles() {
        this.obstacles = null;
    }

    @Override
    public void setDestination(VectorGeometry destination) {
        this.destination = destination;
    }

    @Override
    public void draw() {//todo
    }

    @Override
    public VectorGeometry getForce() {
        Robot us = Strategy.world.getRobot(RobotType.FRIEND_2);
        if(us == null) return null;

        return new VectorGeometry(0, 0);

    }
}
