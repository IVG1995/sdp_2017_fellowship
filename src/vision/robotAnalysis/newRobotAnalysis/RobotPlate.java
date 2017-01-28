package vision.robotAnalysis.newRobotAnalysis;

import vision.Robot;
import vision.RobotAlias;
import vision.colorAnalysis.SDPColor;
import vision.gui.MiscellaneousSettings;
import vision.robotAnalysis.RobotColorSettings;
import vision.spotAnalysis.approximatedSpotAnalysis.Spot;
import vision.spotAnalysis.recursiveSpotAnalysis.XYCumulativeAverage;
import vision.tools.DirectedPoint;
import vision.tools.VectorGeometry;

import java.util.ArrayList;
/**
 * Created by Simon Rovder
 */
 //class for the plates (basically stores the information about bots)
public class RobotPlate {

    //What?
    private static double MAGIC_ANGLE_NUMBER_PLEASE_CHANGE_ME = 0.6;


    private XYCumulativeAverage location = new XYCumulativeAverage();
    private VectorGeometry expectedDeterminer;
    private SDPColor mainColor;
    private VectorGeometry actualDeterminer = null;
    private SDPColor teamColor = null;
    private VectorGeometry center = null;
    private ArrayList<Spot> spots = new ArrayList<Spot>();

    //constructor plates have 3 spots which determine what the bots are
    public RobotPlate(Spot s1, Spot s2, Spot s3) {
        this.mainColor = s1.color;
        this.addSpot(s1);
        this.addSpot(s2);
        this.addSpot(s3);
        double angle1 = VectorGeometry.angle(s2, s1, s3);
        double angle2 = VectorGeometry.angle(s1, s2, s3);
        double angle3 = VectorGeometry.angle(s2, s3, s1);
        if(angle1 > angle2){
            if(angle1 > angle3){
                findExpectedDeterminer(s1);
            } else {
                findExpectedDeterminer(s3);
            }
        } else {
            if(angle2 > angle3){
                findExpectedDeterminer(s2);
            } else {
                findExpectedDeterminer(s3);
            }
        }
    }

    //facing for the VectorGeometry
    private void findExpectedDeterminer(Spot s){
        VectorGeometry centre = new VectorGeometry(this.location.getXAverage(), this.location.getYAverage());
        VectorGeometry dir = VectorGeometry.fromTo(s, centre);
        dir.multiply(0.7);
        dir.add(centre);
        this.expectedDeterminer = dir;
    }

    //validate the spot
    public boolean validate(Spot s){
        if(s.color != this.mainColor){
            if(VectorGeometry.distance(this.expectedDeterminer, s) < 10){
                this.addSpot(s);
                this.center = new VectorGeometry(this.location.getXAverage(), this.location.getYAverage());
                this.actualDeterminer = VectorGeometry.fromTo(center, s);
                return true;
            }
        }
        return false;
    }

    //turn the plate data into a real robot
    public Robot toRobot(){
        Robot r = new Robot();
        r.type = RobotColorSettings.getRobotType(this.teamColor, this.mainColor);
        r.location = this.toDirectedPoint();
        r.alias = (RobotAlias)(MiscellaneousSettings.aliases.get(r.type).getSelectedItem());
        return r;
    }

    //return a point
    private DirectedPoint toDirectedPoint(){
        return new DirectedPoint((int)this.center.x, (int)this.center.y, this.getHeading());
    }

    //get heading of the plate
    private double getHeading(){
        return this.actualDeterminer.angle() - Math.PI + MAGIC_ANGLE_NUMBER_PLEASE_CHANGE_ME;
    }

    //add a new spot
    private void addSpot(Spot s){
        this.spots.add(s);
        this.location.addPoint(s.x, s.y);
    }

    //what does this do?
    public boolean isValid(){
        return this.actualDeterminer != null;
    }

    //determine if plate has a team
    public boolean hasTeam(){
        return this.teamColor != null;
    }

    //try to add the plate to a team
    public void tryAddTeam(Spot s){
        if(this.teamColor == null && VectorGeometry.distance(this.center, s) < 5){
            this.teamColor = s.color;
            this.addSpot(s);
        }
    }

    //manually set the team
    public void setTeam(SDPColor c){
        this.teamColor = c;
    }

    //determine if the spot is part of a robot
    public boolean isBotPart(Spot spot){
        for(Spot s : this.spots){
            if(VectorGeometry.distance(s, spot) < 5){
                return true;
            }
        }
        return false;
    }
}
