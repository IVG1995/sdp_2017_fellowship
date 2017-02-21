package vision;

/**
 * Created by nlfox on 2/20/17.
 */
public class ShapeObject {
    public double x;
    public double y;
    Integer shape;
    public static Integer SQUARE = 0;
    public static Integer CIRCLE = 1;

    public ShapeObject(Double x, Double y, Integer shape) {
        this.x = x;
        this.y = y;
        this.shape = SQUARE;
    }



}
