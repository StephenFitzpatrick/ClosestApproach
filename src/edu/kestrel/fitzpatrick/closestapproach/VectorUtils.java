package edu.kestrel.fitzpatrick.closestapproach;

/**
 * 
 * @author Stephen Fitzpatrick
 * 
 *         Utility functions for treating a double[] as a vector, which may be a
 *         free vector or a positional vector (i.e., anchored at the origin).
 *
 */
public class VectorUtils {

	/**
	 * 
	 * @param u A vector.
	 * @param v A vector, with same dimensions as u.
	 * @return The vector u + v.
	 */
	public static double[] add(double[] u, double[] v) {
		assert u.length == v.length;
		int n = u.length;
		double[] sum = new double[n];
		for (int i = 0; i < n; i++) {
			sum[i] = u[i] + v[i];
		}
		return sum;
	}

	/**
	 * 
	 * @param from A vector.
	 * @param to   A vector, with the same dimensions as from.
	 * @return The vector (to - from).
	 */
	public static double[] subtract(double[] from, double[] to) {
		assert from.length == to.length;
		int n = from.length;
		double[] delta = new double[n];
		for (int i = 0; i < n; i++) {
			delta[i] = from[i] - to[i];
		}
		return delta;
	}

	/**
	 * @param u A vector.
	 * @param v A vector, with the same dimensions as u.
	 * @return The inner (dot) product of u and v. That is, sum over i: u[i] * v[i].
	 */
	public static double innerProduct(double[] u, double[] v) {
		assert u.length == v.length;
		int n = u.length;
		double sum = 0;
		for (int i = 0; i < n; i++) {
			sum += u[i] * v[i];
		}
		return sum;
	}

	/**
	 * 
	 * @param v A vector.
	 * @return The square the vector's length (i.e., the square of its 2-norm).
	 */
	public static double length2(double[] v) {
		return innerProduct(v, v);
	}

	/**
	 * 
	 * @param v A vector.
	 * @return The length of the vector (its 2-norm).
	 */
	public static double length(double[] v) {
		return Math.sqrt(innerProduct(v, v));
	}

	/**
	 * The Euclidean distance between the given positional vectors.
	 * 
	 * @param u A positional vector.
	 * @param v A positional vector, with the same dimensions as v.
	 * @return The distance between the coordinates.
	 */
	public static double distance(double[] u, double[] v) {
		assert u.length == v.length;
		double sumSqrDistance = 0;
		int n = u.length;
		for (int i = 0; i < n; i++) {
			double di = u[i] - v[i];
			sumSqrDistance += di * di;
		}
		return Math.sqrt(sumSqrDistance);
	}

	/**
	 * 
	 * @param scale The factor by which to scale the vector
	 * @param v     The vector to scale
	 * @return The vector scale * v.
	 */
	public static double[] scale(double scale, double[] v) {
		int n = v.length;
		double[] scaled = new double[n];
		for (int i = 0; i < n; i++) {
			scaled[i] = scale * v[i];
		}
		return scaled;
	}

	/**
	 * 
	 * @param v A vector of non-zero length.
	 * @return The unit vector (i.e., length 1) in the same direction as v.
	 */
	public static double[] unitVector(double[] v) {
		assert length(v) > 0;
		return scale(1 / length(v), v);
	}

	/**
	 * Linearly interpolate along the line segment from positional vector start to
	 * positional vector end.
	 * 
	 * For k = 0, returns start.
	 * 
	 * For 0 &lt; k &lt; 1, returns a position along the line segment from start to
	 * end.
	 * 
	 * For k = 1, returns end.
	 * 
	 * @param start The start vector.
	 * @param end   The end vector, with the same dimensions as start.
	 * @param k     The fraction of the interpolation.
	 * @return The interpolated coordinates, assuming uniform motion.
	 */
	public static double[] interpolate(double[] start, double[] end, double k) {
		assert start.length == end.length;
		int n = start.length;
		double[] interpolation = new double[n];
		for (int i = 0; i < n; i++) {
			interpolation[i] = start[i] + k * (end[i] - start[i]);
		}
		return interpolation;
	}

}
