package strategy.navigation.aStarNavigation;

import vision.constants.Constants;
import vision.tools.VectorGeometry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Created by Simon Rovder
 */
public class AStarSearch {

    private HashSet<Integer> scanned;

    /** Contains location of our desired end destination. **/
    private VectorGeometry target;

    private PriorityQueue<Vectorizer> queue;

    private static int JUMP = 10;

    /** A hard limit on how deep into the graph the algorithm will search. **/
    private static int MAX_DEPTH = 50;

    private ObstacleField obstacles;

    private boolean inverted;

    public AStarSearch(){
        this.queue = new PriorityQueue<Vectorizer>();
        this.scanned = new HashSet<Integer>();
    }

    public void setTarget(VectorGeometry target){
        this.target = target.clone();
    }

    /**
     * A method that, given a node/new position "v" from a path "p", determines whether to expand "v" further,
     * disqualify it from further consideration, or return "p" as a successful path.
     * @param v
     * @return null or a Vectorizer containing the optimal path
     */
    public Vectorizer expand(Vectorizer v){
        // Not considering this node as it's too deep (path is too complicated/long)
        if(v.depth > MAX_DEPTH) return null;

        // If this position has been considered before, don't consider it again.
        if(this.scanned.contains(v.x * 1000 + v.y)) return null;
        this.scanned.add(v.x * 1000 + v.y);

        // If this position is close to destination, return the path ending in this node.
        if(target.distance(v.x,v.y) < JUMP) return v;

        // If the position is outside the pitch, don't consider it.
        if(Math.abs(v.x) > Constants.PITCH_WIDTH/2) return null;
        if(Math.abs(v.y) > Constants.PITCH_HEIGHT/2) return null;

        // If the path from previous position in path to here is blocked by an obstacle, don't consider it.
        if(!obstacles.isFree(v.x,v.y, v.previous == null ? v.x : v.previous.x, v.previous == null ? v.y : v.previous.y)) return null;

        // Add v's neighbors to the queue and keep trying nodes.
        // (v's neighbors are four points 10 cm away from it) (JUMP = 10)
        this.queue.add(new Vectorizer().setUp(v.depth + 1, v.x + JUMP, v.y, v));
        this.queue.add(new Vectorizer().setUp(v.depth + 1, v.x - JUMP, v.y, v));
        this.queue.add(new Vectorizer().setUp(v.depth + 1, v.x, v.y + JUMP, v));
        this.queue.add(new Vectorizer().setUp(v.depth + 1, v.x, v.y - JUMP, v));
        return null;
    }


    /**
     * A* search algorithm for deciding what path to take to a destination.
     * @param obstacles
     * @param start
     * @return where to immediately move (or null if there's nowhere to go).
     */
    public VectorGeometry search(ObstacleField obstacles, VectorGeometry start){
        this.obstacles = obstacles;
        this.queue.clear();

        // A "vectorizer" is a node in the A* search graph.
        // Nodes correspond to positions on the pitch.
        this.queue.add(new Vectorizer().setUp(0, (int)start.x, (int)start.y, null));
        Vectorizer goal = null;
        Vectorizer temp;

        // Find the node with lowest estimated cost
        while(!this.queue.peek().expanded){
            // Remove the node of lowest estimated cost.
            temp = this.queue.remove();

            // If goal != null, then the optimal path has been found.
            // If goal == null, then this method has either expanded temp's neighbors and added
            // them to this.queue, or disqualified temp from further consideration.
            goal = this.expand(temp);

            // Mark the node as expanded and add it back to the queue.
            temp.expand();
            this.queue.add(temp);


            if(goal != null) break;
        }

        // If no complete path from start to destination was found, try to moving in the right direction at least.
        // (Path may be temporarily blocked by obstacles like other robots)
        if(goal == null){
            goal = this.queue.peek();
        }

        // If there is no possible direction for the robot to move in, return nothing.
        if(goal == null) return null;
//        System.out.println(target);

        // Get the first step to take in the path.
        while(goal.previous != null && goal.previous.previous != null){
//            System.out.println(goal.x + " " + goal.y + " - " + goal.heuristic());
//            RobotPreview.preview.drawArc(goal.x, goal.y, 2, Color.WHITE);
            goal = goal.previous;
        }
//        System.out.println("=====");
        return VectorGeometry.fromTo(start.x, start.y, goal.x, goal.y);

    }


    private class Vectorizer implements Comparable{

        private int x;
        private int y;
        private Vectorizer previous;
        private boolean expanded;
        private int depth;

        public Vectorizer(){}

        public Vectorizer setUp(int depth, int x, int y, Vectorizer previous){
            this.depth = depth;
            this.x = x;
            this.y = y;
            this.previous = previous;
            this.expanded = false;
            return this;
        }

        public void expand(){
            this.expanded = true;
        }


        // A heuristic based on direct distance from node to goal which also seems to make expanded nodes have really
        // high costs. Perhaps this is a way to ensure all nodes are expanded before returning a path.
        public double heuristic(){
            if(this.expanded){
                return 500000 + VectorGeometry.distance(target.x, target.y, this.x, this.y);
            } else {
                return this.depth*JUMP + VectorGeometry.distance(target.x, target.y, this.x, this.y);
            }
        }


        @Override
        public int compareTo(Object o) {
            if(o instanceof Vectorizer){
                return ((Vectorizer) o).heuristic() < this.heuristic() ? 1 : -1;
            }
            return 0;
        }
    }
}
