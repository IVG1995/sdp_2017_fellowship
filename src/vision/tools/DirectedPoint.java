package vision.tools;
/**
 * Created by Simon Rovder
 */
 //gives a point a direction (for bots etc)
public class DirectedPoint extends VectorGeometry{

	public double direction;

	public DirectedPoint(double x, double y, double d) {
		super(x,y);
		this.direction = d;
	}

	public DirectedPoint clone(){
		return new DirectedPoint(this.x, this.y, this.direction);
	}


	@Override
	public String toString(){
		return "[ " + this.x + " , " + this.y + " ] - " + this.direction;
	}

}
