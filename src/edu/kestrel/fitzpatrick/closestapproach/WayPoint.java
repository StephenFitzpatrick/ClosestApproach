package edu.kestrel.fitzpatrick.closestapproach;

import java.util.Arrays;

/**
 * 
 * @author Stephen Fitzpatrick
 * 
 *         <p>
 *         A way point defines a time and a set of spatial coordinates, the idea
 *         being that some object is to be at the given coordinates at the given
 *         time.
 *         </p>
 * 
 *         <p>
 *         The dimensionality of the spatial coordinates is not fixed - it might
 *         be 1-dimension, 2, 3, etc. Those methods expecting multiple way
 *         points assume that they all have the same dimensionality.
 *         </p>
 */
public class WayPoint {
	// The time. This class is agnostic about the origin and units.
	private final long time;

	// The coordinates. This class is agnostic about the origin and units. The
	// distance between two coordinates is assumed to be Euclidean (square root of
	// the sum of the squares in each dimension).
	private final double[] coordinates;

	// Cache the hash code.
	private final int hash;

	/**
	 * <p>
	 * Create a WayPoint with the given time and coordinates.
	 * </p>
	 * 
	 * <p>
	 * The coordinates are not copied, but should not be changed after the way point
	 * is created.
	 * </p>
	 * 
	 * @param time        The time - arbitrary origin and units.
	 * @param coordinates The coordinates - arbitrary origin and units.
	 */
	public WayPoint(long time, double[] coordinates) {
		super();
		this.time = time;
		this.coordinates = coordinates;
		hash = computeHashCode();
	}

	/**
	 * 
	 * @return The WayPoint's time.
	 */
	public long getTime() {
		return time;
	}

	/**
	 * 
	 * @return The WayPoint's coordinates.
	 */
	public double[] getCoordinates() {
		return coordinates;
	}

	/**
	 * <p>
	 * Interpolate between the start and the end WayPoints, assuming uniform motion
	 * from the start time to the end time.
	 * </p>
	 * 
	 * <p>
	 * If the given interpolation time is outside of [start time, end time] period,
	 * then the interpolation will be as if the object had the same uniform motion
	 * before/after the period.
	 * </p>
	 * 
	 * @param start The starting WayPoint.
	 * @param end   The ending WayPoint. The end time must be later than the start
	 *              time.
	 * @param time  The time to which to interpolate.
	 * @return The way point of an object, moving with uniform motion from the start
	 *         way point to the end way point, at the given interpolation time.
	 */
	public static WayPoint interpolate(WayPoint start, WayPoint end, long time) {
		assert start.getTime() < end.getTime();
		assert start.getCoordinates().length == end.getCoordinates().length;

		// The interpolation time as a fraction of the period [start time, end time].
		double k = ((double) time - start.time) / (end.time - start.time);
		// The coordinates for the interpolated fraction.
		double[] coordinates = VectorUtils.interpolate(start.coordinates, end.coordinates, k);
		return new WayPoint(time, coordinates);
	}

	/**
	 * Linearly interpolate time between the start time and the end time, according
	 * to the interpolation fraction - k=0 results in the start time, k=1 results in
	 * the end time, 0 &lt; k &lt; 1 results in a time in between.
	 * 
	 * @param start The start time.
	 * @param end   The end time.
	 * @param k     The interpolation fraction.
	 * @return The interpolated time.
	 */
	public static long interpolate(long start, long end, double k) {
		return start + Math.round(k * (end - start));
	}

	/**
	 * The Euclidean (spatial) distance between this way point and the given way
	 * point (distance is symmetric and independent of time).
	 * 
	 * @param wp A way point.
	 * @return The distance between this way point and the given way point.
	 */
	public double distance(WayPoint wp) {
		return VectorUtils.distance(coordinates, wp.coordinates);
	}

	@Override
	public int hashCode() {
		return hash;
	}

	/**
	 * Performs the computation of the hash code, which is cached.
	 * 
	 * @return This object's hash code.
	 */
	private int computeHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coordinates);
		result = prime * result + (int) (time ^ (time >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WayPoint other = (WayPoint) obj;
		if (hash != other.hash)
			return false;
		if (time != other.time)
			return false;
		if (!Arrays.equals(coordinates, other.coordinates))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WayPoint [time=" + time + ", coordinates=" + Arrays.toString(coordinates) + "]";
	}

}
