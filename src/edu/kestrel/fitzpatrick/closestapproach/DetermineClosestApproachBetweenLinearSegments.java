package edu.kestrel.fitzpatrick.closestapproach;

/**
 * 
 * @author Stephen Fitzpatrick
 * 
 *         <p>
 *         We have one object moving uniformly from way point start1 to a later
 *         way point end1 (note that way points involve time as well as space).
 *         </p>
 * 
 *         <p>
 *         Likewise, we have a second object moving uniformly from way point
 *         start2 to later way point end2.
 *         </p>
 * 
 *         <p>
 *         All of the way points have the same dimensionality for their spatial
 *         coordinates.
 *         </p>
 * 
 *         <p>
 *         Object 1's start time is the same as Object 2's start time.
 *         </p>
 * 
 *         <p>
 *         Object 1's end time is the same as Object 2's end time.
 *         </p>
 * 
 *         <p>
 *         Determine when and where the two objects are closest - i.e., their
 *         closest approach.
 *         </p>
 * 
 *         <p>
 *         If the objects' motions are parallel (or anti-parallel), then they
 *         remain at the same distance apart for the entirety of their common
 *         time period. An arbitrary time is chosen for the closest approach;
 *         the objects' locations at closest approach are determined by the
 *         time.
 *         </p>
 * 
 *         <p>
 *         If one object has the same starting location and ending location,
 *         then it is stationary throughout the time period. If the other object
 *         is also stationary, then their separation is fixed and an arbitrary
 *         time is chosen as the time of closest approach. The objects locations
 *         at closest approach are, of course, determined by their respective
 *         starting and ending locations. (This is a degenerate case of parallel
 *         motion.)
 *         </p>
 * 
 *         <p>
 *         If one object is stationary but the other is not, then the moving
 *         object effectively determines the time of the closest approach.
 *         </p>
 * 
 *         <p>
 *         Because the objects' motions have the same start and end times, their
 *         positions can be given in terms of a single interpolation parameter,
 *         k, such that at k=0 the two objects are at their starting locations,
 *         at k=1 the two objects are at their ending locations, and for 0 &lt;
 *         k &lt; 1 the two objects are between their start and end locations
 *         (linearly interpolated).
 *         </p>
 * 
 *         <p>
 *         The time of closest approach is almost certainly fractional. This
 *         time and the coordinates of the two objects' at closest approach are
 *         consistent, within the limits of double-precision arithmetic.
 *         However, the time is reported as a discrete value, a long. The
 *         fractional time is rounded up or down depending on which gives the
 *         smaller separation. However, the reported coordinates of the objects
 *         remain those for the fractional time. Thus, there is likely a small
 *         discrepancy between the coordinates that would be computed for the
 *         discrete, reported time and the reported coordinates.
 *         </p>
 */
public class DetermineClosestApproachBetweenLinearSegments {
	// The starting way point for object 1.
	private final WayPoint start1;
	// The ending way point for object 1.
	private final WayPoint end1;
	// The starting way point for object 2.
	private final WayPoint start2;
	// The ending way point for object 2.
	private final WayPoint end2;

	// The interpolation parameter at which the closest approach occurs.
	private final double closestK;
	// The closest approach between the two objects.
	private final ClosestApproach closestApproach;

	/**
	 * Determine the closest approach between two objects, with uniform motions,
	 * respectively, from start1 to end1 and from start2 to end2.
	 * 
	 * @param start1 The starting way point of object 1.
	 * @param end1   The ending way point of object 1 (must have a time later than
	 *               the starting time).
	 * @param start2 The starting way point of object 2 (must have a time the same
	 *               as start1).
	 * @param end2   The ending way point of object 2 (must have a time the same as
	 *               end1).
	 */
	public DetermineClosestApproachBetweenLinearSegments(WayPoint start1, WayPoint end1, WayPoint start2,
			WayPoint end2) {
		super();
		double[] s1 = start1.getCoordinates();
		double[] e1 = end1.getCoordinates();
		double[] s2 = start2.getCoordinates();
		double[] e2 = end2.getCoordinates();
		assert s1.length == e1.length;
		assert s2.length == e2.length;
		assert s1.length == s2.length;

		long startTime = start1.getTime();
		long endTime = end1.getTime();
		long deltaTime = endTime - startTime;
		assert startTime == start2.getTime();
		assert endTime == end2.getTime();
		assert startTime < endTime;

		this.start1 = start1;
		this.end1 = end1;
		this.start2 = start2;
		this.end2 = end2;

		// Determine the interpolation parameter for the closest approach.
		closestK = computeClosestK(s1, e1, s2, e2);

		if (Double.isNaN(closestK)) {
			closestApproach = new ClosestApproach(distance(closestK));
		} else {
			double closestTime = startTime + closestK * deltaTime;
			// Make the time discrete - choose the floor or ceiling depending on which gives
			// the closer approach.
			long time1 = Math.max(startTime, (long) Math.floor(closestTime));
			long time2 = Math.min(endTime, time1 + 1);
			long time;
			if (time1 == time2) {
				time = time1;
			} else {
				double k1 = ((double) (time1 - startTime)) / deltaTime;
				double k2 = ((double) (time2 - startTime)) / deltaTime;
				double d1 = distance(k1);
				double d2 = distance(k2);
				if (d1 <= d2) {
					time = time1;
				} else {
					time = time2;
				}
			}

			// Once k is determined, the locations of the closest approach are determined by
			// linear interpolation.
			double[] coords1 = VectorUtils.interpolate(s1, e1, closestK);
			double[] coords2 = VectorUtils.interpolate(s2, e2, closestK);
			closestApproach = new ClosestApproach(new WayPoint(time, coords1), new WayPoint(time, coords2));
		}
	}

	/**
	 * 
	 * @return The start way point for object 1.
	 */
	public WayPoint getStart1() {
		return start1;
	}

	/**
	 * 
	 * @return The end way point for object 1.
	 */
	public WayPoint getEnd1() {
		return end1;
	}

	/**
	 * 
	 * @return The start way point for object 2.
	 */
	public WayPoint getStart2() {
		return start2;
	}

	/**
	 * 
	 * @return The end way point for object 2.
	 */
	public WayPoint getEnd2() {
		return end2;
	}

	/**
	 * 
	 * @return The interpolation parameter for the closest approach. 0 &le; k &le;
	 *         1.
	 */
	public double closestK() {
		return closestK;
	}

	/**
	 * 
	 * @return The closest approach.
	 */
	public ClosestApproach getClosestApproach() {
		return closestApproach;
	}

	/**
	 * <p>
	 * Compute the interpolation parameter for closest approach, assuming that
	 * object 1 is at start1 at the same time as object 2 is at start2, that object
	 * 1 is at end1 at the same time as object 2 is at end2, and that each moves
	 * with constant velocity.
	 * </p>
	 * 
	 * @param start1 The starting location of object 1.
	 * @param end1   The ending location of object 1.
	 * @param start2 The starting location of object 2.
	 * @param end2   The ending location of object 2.
	 * @return The interpolation parameter corresponding to the objects' closest
	 *         approach. Is NaN is the objects' motions are (effectively) parallel.
	 */
	public static double computeClosestK(double[] start1, double[] end1, double[] start2, double[] end2) {
		assert start1.length == end1.length;
		assert start2.length == end2.length;
		assert start1.length == start2.length;
		double[] dS = VectorUtils.subtract(start1, start2);
		double[] dE = VectorUtils.subtract(end1, end2);
		double[] dSdE = VectorUtils.subtract(dS, dE);
		double enumerator = VectorUtils.innerProduct(dS, dSdE);
		double denominator = VectorUtils.length2(dSdE);
		double k = enumerator / denominator;
		if (Double.isInfinite(k) || Double.isNaN(k)) {
			// Degenerate cases - (effectively) parallel or anti-parallel motion.
			return Double.NaN;
		} else {
			// The true closest approach may occur outside the given motion - e.g., if the
			// two objects were to continue in uniform motion. We are interested in the
			// closest approach within the start/end segments we are given. So
			// restrict k to [0, 1].
			return Math.max(0, Math.min(1, k));
		}
	}

	/**
	 * The distance between the two objects at interpolation parameter k.
	 * 
	 * @param k The interpolation parameter.
	 * @return The distance between the two objects at interpolation parameter k. If
	 *         k is NaN, then the objects are moving effectively in parallel -
	 *         returns the distance between their starting positions.
	 */
	public double distance(double k) {
		if (Double.isNaN(k)) {
			return start1.distance(start2);
		} else {
			double[] c1 = VectorUtils.interpolate(start1.getCoordinates(), end1.getCoordinates(), k);
			double[] c2 = VectorUtils.interpolate(start2.getCoordinates(), end2.getCoordinates(), k);
			return VectorUtils.distance(c1, c2);
		}
	}

}
