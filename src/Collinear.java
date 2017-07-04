import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/** Class: Collinear.java
 *  @author Yury Park
 *  @version 1.0 <p>
 *
 *  Purpose: Given a set of N distinct points on a plane, draw every maximal line segment that
 *  connects a subset of 4 or more of collinear points.
 *
 *  The brute force approach would examine 4 points at a time, leading to N^4 time.
 *  Another, more efficient approach we employ here is as follows:
 *  - For each point P, examine each other point Q and determine the slope it makes with P.
 *  - Sort the points according to the slopes they make with P.
 *  - Then check if 3 or more adjacent points in the sorted order have equal slopes with P.
 *  - If so, these points, together with P, are collinear.
 *  The above algorithm leads to N^2 log N time.
 */
public class Collinear {
	private final static int COUNT = 3;
	/**
	 * Inner class. A line segment consisting of collinear points.
	 */
	private static class LineSegment {
		ArrayList<Point> pointsInLineSegmentAL;	//ArrayList of Point objects. See Point.java.

		/**
		 * no-arg constructor.
		 */
		LineSegment() {
			this.pointsInLineSegmentAL = new ArrayList<>();
		}

		/**
		 * Method: addPoint
		 * @param pt Point to add
		 */
		void addPoint(Point pt) {
			this.pointsInLineSegmentAL.add(pt);
		}

		/**
		 * Method: sortPoints
		 * Sorts the Points from smallest to largest, in terms of location. See the compareTo() method
		 * in the Point class.
		 */
		void sortPoints() {
			Collections.sort(this.pointsInLineSegmentAL);
		}

		/**
		 * Method: draw
		 * Draw a straight line segment from the "smallest" Point (in terms of location) to the "largest" Point.
		 * Assumes that the ArrayList of Point objects is pre-sorted.
		 */
		void draw() {
			Point firstPoint = this.pointsInLineSegmentAL.get(0);	//the first element is the smallest
			Point lastPoint = this.pointsInLineSegmentAL.get(this.pointsInLineSegmentAL.size() - 1);	//last element is the largest
			firstPoint.drawTo(lastPoint);	//custom method in the Point class
		}

		/**
		 * Method: toString
		 * For debugging purposes.
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < this.pointsInLineSegmentAL.size(); i++) {
				sb.append(this.pointsInLineSegmentAL.get(i));
				if (i == this.pointsInLineSegmentAL.size() - 1) {} else sb.append(" -> ");	//omit the arrow if this is the last element
			}
			return sb.toString();
		}

		/**
		 * Method: hashCode
		 */
		@Override
		public int hashCode() {
			/* If hashcode of two objects are the same, Java automatically executes the equals() method to verify
			whether they're equal, which is what we want. So we'll just hack it and say that hashCode is the same
			for EVERY line segment object. This way, we can override the equals() method as below.

			Remember, the hashCode() and equals() contract is as follows:
			Equal objects must produce the same hash code. However, unequal objects need not
			produce distinct hash codes.

			Since we want to be able to treat line segments of different lengths as equal
			for purposes of this class (see below for why), we will assign the same hash codes to
			every line segment object so as to not break the above-mentioned contract.
			*/
			return 0;
		}

		/**
		 * Method: equals
		 * Checks whether two line segments are equal.
		 * Note: two line segments, A and B, are considered equal even if they are of different lengths
		 * as long as they are the same slope and lie in the same location (i.e. one segment
		 * completely overlaps the other). Why do we want to do this? Because we don't need to double-count
		 * line segments of different lengths that overlap each other.
		 */
		@Override
		public boolean equals(Object o) {
			if(this == o) return true;	//if they're strictly the same, return true.
			if(o == null || !(o instanceof LineSegment)) return false;	//check some base cases

			LineSegment ls2 = (LineSegment)o;	//Once we get thru the base cases above, we can safely cast the Object to LineSegment.

			ArrayList<Point> smallerAL, largerAL;
			if (this.pointsInLineSegmentAL.size() < ls2.pointsInLineSegmentAL.size()) {
				smallerAL = this.pointsInLineSegmentAL;
				largerAL = ls2.pointsInLineSegmentAL;
			}
			else {
				smallerAL = ls2.pointsInLineSegmentAL;
				largerAL = this.pointsInLineSegmentAL;
			}

			/* A line segment that's a smaller subset of another longer line segment will be treated as equal.
			 * So as long as the larger segment contains all the points making up the smaller segment,
			 * the two line segments are equal. */
			for (int i = 0; i < smallerAL.size(); i++) {
				if (!largerAL.contains(smallerAL.get(i))) return false;
			}

			return true;
		}
		//end public boolean equals
	}
	//end private static class LineSegment

	/**
	 * Method: main
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		HashSet<LineSegment> hs = new HashSet<>();	//Will save line segments, while auto-pruning any "duplicates".

		/* End user will supply the filename in the command prompt, such as:
		 * mystery10089.txt */
		In in = new In(args[0]);
    	int n = in.readInt();	//Read the first line of the file, which specifies the total no. points in the file
    	ArrayList<Point> pointAL = new ArrayList<>();	//We could've used an array instead of AL. Oh well. No big deal.

    	//Get ready to read in and draw the points on the viewer.
    	StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);

    	/* Read and draw each point from file */
    	for (int i = 0; i < n; i++) {
    		pointAL.add(new Point(in.readInt(), in.readInt()));
    		pointAL.get(i).draw();
    	}
    	//end for i

    	ArrayList<Point> tempPointAL = new ArrayList<>(pointAL);	//initialize an auxiliary copy of the original AL.

    	/* Go thru every Point in the AL. */
    	for (int i = 0; i < n; i++) {
    		Point currentPt = pointAL.get(i);

    		tempPointAL.remove(currentPt);	//Remove this current point from the auxiliary ArrayList before sorting it via SLOPE ORDER.
    		Collections.sort(tempPointAL, currentPt.SLOPE_ORDER);	//SLOPE_ORDER is a custom Comparator in the Point class.

    		//Check to see if 3 or more of the points have the same slope as the current point. If so, we found collinearity of >=4 in a row.
    		int collinearCount = 1;
    		for (int j = 1; j < tempPointAL.size(); j++) {	//begin from j = 1 since we'll compare it with (j-1)th element
    			if (tempPointAL.get(j).slopeTo(currentPt) == tempPointAL.get(j-1).slopeTo(currentPt)) {	//Compare slope with previous element
    				collinearCount++;
    			}
    			else {	//If we do NOT have 2 equal slopes in a row during this iteration...
    				if (collinearCount >= COUNT) { 	//See if, in previous iterations, we have built up 3 or more in a row with equal slopes.
    					LineSegment ls = new LineSegment();	//time to create a line segment

    					//Add up all the points we found that have equal slopes with the current Point
    					for (int k = j - 1; collinearCount > 0; k--, collinearCount--) {
    						ls.addPoint(tempPointAL.get(k));
    					}
    					ls.addPoint(currentPt);	//Finally add the current Point itself to the line segment
    					ls.sortPoints();	//custom method. Sort points from smallest to largest

    					/* Check if the HashSet already contains a duplicate of this line segment.
    					 * Refer to the equals() method in the LineSegment class for how it defines a duplicate.
    					 * If it's not a dupe, add the line segment to the set, and draw it to the GUI,
    					 * and print it out to the console. */
    					if (!hs.contains(ls)) {
    						hs.add(ls);
    						ls.draw();
    						System.out.println(ls);
    					}
    				}
    				//end if (collinearCount >= 3)
    				collinearCount = 1;		//reset count back to 1
    			}
    			//end if / else
    		}
    		//end for j

    		/* Must invoke this if statement one more time after the above for j loop ends, because
    		 * the last 3 elements in the aux ArrayList might have equal slopes, in which case
    		 * the if(collinearCount >= 3) statement never had the chance to be triggered within the j loop. */
    		if (collinearCount >= COUNT) {
    			LineSegment ls = new LineSegment();
				for (int k = tempPointAL.size() - 1; collinearCount > 0; k--, collinearCount--) {
					ls.addPoint(tempPointAL.get(k));
				}
				ls.addPoint(currentPt);
				ls.sortPoints();	//custom method

				if (!hs.contains(ls)) {
					hs.add(ls);			//auto-take care of any duplicates
					ls.draw();
					System.out.println(ls);
				}
    		}
    	}
    	//end for i

    	System.out.println("No. of line segments (minus any duplicates): " + hs.size());
    	System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime));
	}
	//end main
}