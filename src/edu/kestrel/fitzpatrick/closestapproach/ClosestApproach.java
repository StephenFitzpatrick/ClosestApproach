package edu.kestrel.fitzpatrick.closestapproach;

/**
 * 
 * @author Stephen Fitzpatrick
 * 
 *         Represents a closest approach of two objects' motions.
 * 
 *         If the objects are moving in parallel (or effectively so, given
 *         finite precision computation), then the time and way points are null,
 *         but the distance is still defined.
 *
 */
public class ClosestApproach {

	private final Long time;
	private final WayPoint wayPoint1;
	private final WayPoint wayPoint2;
	private final double distance;

	/**
	 * Create a closest approach for two objects moving in parallel - only their
	 * separation, which is constant, is given.
	 * 
	 * @param distance The (constant) distance between the objects.
	 */
	public ClosestApproach(double distance) {
		super();
		assert distance >= 0;

		this.distance = distance;
		time = null;
		wayPoint1 = null;
		wayPoint2 = null;
	}

	/**
	 * Create a closest approach given the two objects' way points at closest
	 * approach. Note that the time and distance of the closest approach are given
	 * by the way points.
	 * 
	 * The two way points must not be null and must have the same time.
	 * 
	 * @param wayPoint1 Way point of object 1 at closest approach.
	 * @param wayPoint2 Way point of object 2 at closest approach.
	 */
	public ClosestApproach(WayPoint wayPoint1, WayPoint wayPoint2) {
		super();
		assert wayPoint1 != null;
		assert wayPoint2 != null;
		assert wayPoint1.getTime() == wayPoint2.getTime();
		assert wayPoint1.getCoordinates().length == wayPoint2.getCoordinates().length;
		this.wayPoint1 = wayPoint1;
		this.wayPoint2 = wayPoint2;
		time = wayPoint1.getTime();
		distance = wayPoint1.distance(wayPoint2);
	}

	/**
	 * 
	 * @return The time of the closest approach. Will be null for parallel motion.
	 */
	public Long time() {
		return time;
	}

	/**
	 * 
	 * @return The way point for object 1 at the closest approach. Will be null for
	 *         parallel motion.
	 */
	public WayPoint wayPoint1() {
		return wayPoint1;
	}

	/**
	 * 
	 * @return The way point for object 2 at the closest approach. Will be null for
	 *         parallel motion.
	 */
	public WayPoint wayPoint2() {
		return wayPoint2;
	}

	/**
	 * 
	 * @return The distance between the two objects at their closest approach.
	 */
	public double distance() {
		return distance;
	}
}
