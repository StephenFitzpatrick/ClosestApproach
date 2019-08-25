package edu.kestrel.fitzpatrick.closestapproach.unittests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.kestrel.fitzpatrick.closestapproach.WayPoint;

public class UnitTestUtils {
	public static final Random random = new Random();

	/**
	 * 
	 * @param min Lower bound
	 * @param max Upper bound
	 * @return A random double in [min, max)
	 */
	public static double randomDouble(double min, double max) {
		return min + (max - min) * random.nextDouble();
	}

	/**
	 * 
	 * @param min Lower bound
	 * @param max Upper bound
	 * @return A random integer in [min, max)
	 */
	public static int randomInt(int min, int max) {
		return min + random.nextInt(max - min);
	}

	/**
	 * Generate a random route.
	 * 
	 * @param startTime  Start time of the route
	 * @param startX     Start position's x coordinate
	 * @param startY     Start position's y coordinate
	 * @param nWayPoints Number of way points in the route
	 * @return A random route
	 */
	public static List<WayPoint> randomRoute(long startTime, double startX, double startY, int nWayPoints) {
		long time = startTime;
		double x = startX;
		double y = startY;
		List<WayPoint> route = new ArrayList<>();
		for (int i = 0; i < nWayPoints; i++) {
			time += randomInt(100, 10_000);
			x += randomDouble(100, 10_000);
			y += randomDouble(100, 10_000);
			route.add(new WayPoint(time, new double[] { x, y }));
		}
		return route;
	}

	/**
	 * Generate a random route using default parameters.
	 * 
	 * @return A random route.
	 */
	public static List<WayPoint> randomRoute() {
		long startTime = randomInt(0, 1000);
		double startX = randomDouble(-1000, 1000);
		double startY = randomDouble(-1000, 1000);
		int size = randomInt(2, 10);
		return randomRoute(startTime, startX, startY, size);
	}

	/**
	 * Find the earliest of the closest approaches between two routes.
	 * 
	 * @param route1 A route
	 * @param route2 A route
	 * @return The earliest, closest approach
	 */
	public static WayPoint[] findClosestApproach(List<WayPoint> route1, List<WayPoint> route2) {
		// The overlap between the two routes time periods.
		long startTime = Math.min(route1.get(0).getTime(), route2.get(0).getTime());
		long endTime = Math.max(route1.get(route1.size() - 1).getTime(), route2.get(route2.size() - 1).getTime());

		// Closest approach found so far.
		double closestDistance = Double.POSITIVE_INFINITY;
		WayPoint closestWayPoint1 = null;
		WayPoint closestWayPoint2 = null;
		// Check the distance at each time in the overlap between the routes' time
		// periods.
		for (long time = startTime; time <= endTime; time++) {
			WayPoint wp1 = interpolate(route1, time);
			WayPoint wp2 = interpolate(route2, time);
			if (wp1 == null || wp2 == null) {
				continue;
			}
			double distance = wp1.distance(wp2);
			if (distance < closestDistance) {
				closestDistance = distance;
				closestWayPoint1 = wp1;
				closestWayPoint2 = wp2;
			}
		}
		if (closestWayPoint1 == null) {
			return null;
		} else {
			return new WayPoint[] { closestWayPoint1, closestWayPoint2 };
		}
	}

	/**
	 * Interpolate a route.
	 * 
	 * @param route A route
	 * @param time  A time
	 * @return The position along the route for the given time, or null if the time
	 *         is outside the route's time period
	 */
	public static WayPoint interpolate(List<WayPoint> route, long time) {
		int n = route.size();
		assert n > 0;

		if (time < route.get(0).getTime()) {
			return null;
		}

		// Find the correct segment of the route for the requested time.
		int wayPointNumber = 0;
		while (wayPointNumber < n - 1) {
			if (route.get(wayPointNumber).getTime() <= time && time < route.get(wayPointNumber + 1).getTime()) {
				break;
			} else {
				wayPointNumber++;
			}
		}

		// If the index is at the last way point, check for an exact match against the
		// way point time.
		if (wayPointNumber == n - 1) {
			if (time == route.get(wayPointNumber).getTime()) {
				return route.get(wayPointNumber);
			} else {
				return null;
			}
		}

		// We have two way points bounding the requested time - interpolate between
		// them.
		WayPoint start = route.get(wayPointNumber);
		WayPoint end = route.get(wayPointNumber + 1);
		WayPoint interpolated = WayPoint.interpolate(start, end, time);
		return interpolated;
	}

}
