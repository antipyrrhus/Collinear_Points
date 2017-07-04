/*************************************************************************
 * Compilation:  javac Point.java
 * Execution:
 * Dependencies: StdDraw.java
 *
 * Description: An immutable data type for points in the plane.
 *
 *************************************************************************/
import java.util.ArrayList;
import java.util.Comparator;

public class Point implements Comparable<Point> {

    // compare points by slope
	/* The SLOPE_ORDER comparator should compare points by the slopes they make with the invoking point (x0, y0).
	 * Formally, the point (x1, y1) has a smaller slope than the point (x2, y2) IFF:
	 * the slope (y1 - y0) / (x1 - x0) is less than the slope (y2 - y0) / (x2 - x0).
	 * Treat horizontal, vertical, and degenerate line segments as in the slopeTo() method. */
    public final Comparator<Point> SLOPE_ORDER = new Comparator<Point>() {
		@Override
		public int compare(Point pt1, Point pt2) {
			if (pt1 == null || pt2 == null) throw new java.lang.NullPointerException();

			if (pt1.slopeTo(Point.this) < pt2.slopeTo(Point.this)) return -1;
			if (pt1.slopeTo(Point.this) > pt2.slopeTo(Point.this)) return 1;
			return 0;
		}
    };

    private final int x;                              // x coordinate
    private final int y;                              // y coordinate

    // create the point (x, y)
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // plot this point to standard drawing
    public void draw() {
        StdDraw.point(x, y);
    }

    // draw line between this point and that point to standard drawing
    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    // slope between this point and that point
    public double slopeTo(Point pt2) {
    	/* Slope = rise / run = (y1-y0) / (x1-x0) */
    	if (pt2.x == this.x && pt2.y == this.y) return Double.NEGATIVE_INFINITY;		//these points are one and the same.
    	if (pt2.x - this.x == 0) return Double.POSITIVE_INFINITY;	//slope is a vertical line (undefined) in this case
    	if (pt2.y - this.y == 0) return 0;							//slope is a horizontal line in this case
    	return (double)(pt2.y - this.y) / (double)(pt2.x - this.x);			//slope is neither vertical nor horizontal in this case
    }

    // is this point lexicographically smaller than that one?
    // comparing y-coordinates and breaking ties by x-coordinates
    @Override
    public int compareTo(Point pt2) {
    	if (this.y < pt2.y) return -1;
    	if (this.y > pt2.y) return 1;
    	return this.x - pt2.x;
    }

    // return string representation of this point
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    // Optional unit test
    public static void main(String[] args) {
    	/* Testing slopeTo() method... */
    	ArrayList<Point> pointAL = new ArrayList<>();
    	/* When using console, press ctrl-z to signify to the StdIn class that there is no more data. */
    	while (!StdIn.isEmpty()) {
    		/* End user should type in integer points in pairs, such as:
    		 * 0 0
    		 * 1 1
    		 * -2 3
    		 * 0 5
    		 * 1 0
    		 * (ctrl-z) to end input */
    		Point pt = new Point(StdIn.readInt(), StdIn.readInt());
    		pointAL.add(pt);
    	}
    	//end while

    	/* Print out every possible combination of slopes between each pair of points */
    	for (int i = 0; i < pointAL.size() - 1; i++) {
    		for (int j = i+1; j < pointAL.size(); j++) {
    			Point pt1 = pointAL.get(i);
    			Point pt2 = pointAL.get(j);
    			System.out.printf("Slope from %s to %s: %s\n", pt1, pt2, pt1.slopeTo(pt2));
    		}
    	}
    }
    //end main
}
