package vision.spotAnalysis.recursiveSpotAnalysis;

/**
 * Created by Simon Rovder
 */
 //more average maths
public class XYCumulativeAverage {
    private CumulativeAverage x;
    private CumulativeAverage y;

    //constructor
    public XYCumulativeAverage(){
        this.x = new CumulativeAverage();
        this.y = new CumulativeAverage();
    }

    //new point
    public void addPoint(double x, double y){
        this.x.add(x);
        this.y.add(y);
    }

    //average maths
    public double getXAverage(){
        return this.x.getAverage();
    }

    //average maths
    public double getYAverage(){
        return this.y.getAverage();
    }

    //work out if we have any points
    public boolean hasPoints(){
        return this.x.getCount() > 0;
    }

    //return number of points
    public int getCount(){
        return this.x.getCount();
    }

    //reset method
    public void reset(){
        this.x.reset();
        this.y.reset();
    }
}
