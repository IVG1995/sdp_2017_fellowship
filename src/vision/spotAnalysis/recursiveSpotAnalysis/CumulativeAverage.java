package vision.spotAnalysis.recursiveSpotAnalysis;

/**
 * Created by Simon Rovder
 */
 //Class with some maths
public class CumulativeAverage {

    private int count;
    private double average;

    //constructor just resets the clas
    public CumulativeAverage(){
        this.reset();
    }

    //Adds and counts
    public void add(double d){
        this.average = (d + this.count*this.average)/(this.count + 1);
        this.count++;
    }

    //return avergae
    public double getAverage(){
        return this.average;
    }

    //return number
    public int getCount(){
        return this.count;
    }

    //reset method
    public void reset(){
        this.count   = 0;
        this.average = 0;
    }
}
