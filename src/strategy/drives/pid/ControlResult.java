package strategy.drives.pid;

import java.util.Arrays;
import java.util.List;

public class ControlResult {
    private double front;
    private double back;
    private double left;
    private double right;

    public ControlResult(double front, double back, double left, double right) {
        this.front = front;
        this.back = back;
        this.left = left;
        this.right = right;
    }
    public ControlResult() {
        new ControlResult(0d, 0d, 0d, 0d);
    }

    public double getFront() {
        return front;
    }

    public double getBack() {
        return back;
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public ControlResult add(ControlResult that) {
        return new ControlResult(
                this.front + that.getFront(),
                this.back + that.getBack(),
                this.left + that.getLeft(),
                this.right + that.getRight())
                ;
    }

    public ControlResult multiply(double factor) {
        return new ControlResult(
                this.front * factor,
                this.back * factor,
                this.left * factor,
                this.right * factor)
                ;
    }

    public String toString() {
        return String.format("f: %.3f | b: %.3f | l: %.3f | r: %.3f", front, back, left, right);
    }
}
