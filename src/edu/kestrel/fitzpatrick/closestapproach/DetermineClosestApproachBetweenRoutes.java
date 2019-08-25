package edu.kestrel.fitzpatrick.closestapproach;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 
 * @author Stephen Fitzpatrick
 * 
 *         <p>
 *         We have one object following route1, where route1 is a list of
 *         space-time way points (with increasing times). Between any two
 *         consecutive way points, the object moves uniformly.
 *         </p>
 * 
 *         <p>
 *         Likewise, we have another object, following route2.
 *         </p>
 * 
 *         <p>
 *         Over time, the distance between the objects varies (unless their
 *         routes are parallel). This class finds when and where the objects are
 *         closest, that is, their closest approaches. (In principle, there may
 *         be multiple closest approaches, at different times, with the same
 *         separation between the objects.)
 *         </p>
 * 
 *         <p>
 *         If the two routes have no time period in common, then there is no
 *         closest approach.
 *         </p>
 * 
 *
 */
public class DetermineClosestApproachBetweenRoutes {
	// The route for object 1.
	private final List<WayPoint> route1;
	// The route for object 2.
	private final List<WayPoint> route2;

	// The closest approaches, sorted by time. May be empty (but not null).
	private final List<ClosestApproach> closestApproaches;

	/**
	 * <p>
	 * Determine the closest approaches between object 1, following route 1, and
	 * object 2, following route 2.
	 * </p>
	 * 
	 * <p>
	 * A route must have two or more way points.
	 * </p>
	 * 
	 * <p>
	 * The times of the way points in a route must be increasing.
	 * </p>
	 * 
	 * @param route1 Object 1's route.
	 * @param route2 Object 2's route.
	 */
	public DetermineClosestApproachBetweenRoutes(List<WayPoint> route1, List<WayPoint> route2) {
		super();
		assert route1.size() >= 2;
		assert route2.size() >= 2;
		assert increasingTimes(route1);
		assert increasingTimes(route2);

		this.route1 = route1;
		this.route2 = route2;

		closestApproaches = computeClosestApproaches(route1, route2);
	}

	/**
	 * Check if a the times in a route's way points are increasing.
	 * 
	 * @param route A route.
	 * @return Whether or not the route's way point times are increasing.
	 */
	private static boolean increasingTimes(List<WayPoint> route) {
		for (int i = 0; i < route.size() - 1; i++) {
			if (route.get(i).getTime() >= route.get(i + 1).getTime()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @return The route for object 1.
	 */
	public List<WayPoint> getRoute1() {
		return route1;
	}

	/**
	 * 
	 * @return The route for object 2.
	 */
	public List<WayPoint> getRoute2() {
		return route2;
	}

	/**
	 * <p>
	 * The closest approaches between the two objects, sorted by increasing time.
	 * All of the closest approaches have the same separation between the objects.
	 * </p>
	 * 
	 * <p>
	 * May be empty, but not null.
	 * </p>
	 * 
	 * @return List of the closest approaches, in time order.
	 */
	public List<ClosestApproach> getClosestApproaches() {
		return closestApproaches;
	}

	/**
	 * 
	 * @param route1 The route for object 1.
	 * @param route2 The route for object 2.
	 * @return The (possibly empty) list of closest approaches between object 1 and
	 *         2.
	 */
	public static List<ClosestApproach> computeClosestApproaches(List<WayPoint> route1, List<WayPoint> route2) {
		// First, align the routes in time.
		SortedSet<Long> times = alignTimes(route1, route2);
		List<WayPoint> aligned1 = align(route1, times);
		List<WayPoint> aligned2 = align(route2, times);
		assert aligned1.size() == aligned2.size();
		assert times(aligned1).equals(times(aligned2));

		if (times.isEmpty()) {
			// The two routes do not overlap in time.
			return new ArrayList<>();
		} else if (times.size() == 1) {
			// The end time of one route is the start time of the other.
			List<ClosestApproach> result = new ArrayList<>();
			result.add(new ClosestApproach(aligned1.get(0), aligned2.get(0)));
			return result;
		}

		// Each aligned route has at least two way points. Consider each segment
		// (defined by a pair of consecutive, aligned times) in turn.
		List<ClosestApproach> closestApproachesPerSegment = new ArrayList<>();
		for (int i = 0; i < aligned1.size() - 1; i++) {
			WayPoint start1 = aligned1.get(i);
			WayPoint end1 = aligned1.get(i + 1);
			WayPoint start2 = aligned2.get(i);
			WayPoint end2 = aligned2.get(i + 1);
			DetermineClosestApproachBetweenLinearSegments ca = new DetermineClosestApproachBetweenLinearSegments(start1,
					end1, start2, end2);
			// If the closest approach is exactly at the time of a way point, rather than
			// between consecutive way points, then it could be counted twice, once for the
			// segment that has the way point time at its end, and again for the segment
			// that has the time at its start. Only include in the former case, unless it is
			// the time of the final way point.
			if (i == aligned1.size() - 2 || ca.closestK() < 1) {
				closestApproachesPerSegment.add(ca.getClosestApproach());
			}
		}
		// Extract those segment-wise closest approaches that have the smallest
		// distance.
		double minDistance = closestApproachesPerSegment.stream() //
				.mapToDouble(ca -> ca.distance()) //
				.min().getAsDouble();
		List<ClosestApproach> closestApproaches = closestApproachesPerSegment.stream() //
				.filter(ca -> ca.distance() == minDistance) //
				.collect(Collectors.toList());
		return closestApproaches;
	}

	/**
	 * 
	 * @param route A route.
	 * @return The times of the route's way points, in order.
	 */
	private static List<Long> times(List<WayPoint> route) {
		return route.stream() //
				.map(WayPoint::getTime) //
				.collect(Collectors.toList());
	}

	/**
	 * Given two routes, determine a sequence of times that includes all of the
	 * times of the way points in the two routes that fall within the intersection
	 * of the two routes' time periods.
	 * 
	 * @param route1 The route for object 1.
	 * @param route2 The route for object 2.
	 * @return The union of the way point times in the two routes, in increasing
	 *         order. Excludes any way point that has time before the later of the
	 *         start times of the two routes, or after the earlier of the end times
	 *         of the two routes.
	 */
	public static SortedSet<Long> alignTimes(List<WayPoint> route1, List<WayPoint> route2) {
		// The intersection of the two routes' periods.
		long startTime = Math.max(route1.get(0).getTime(), route2.get(0).getTime());
		long endTime = Math.min(route1.get(route1.size() - 1).getTime(), route2.get(route2.size() - 1).getTime());

		// The aligned times.
		SortedSet<Long> merged = new TreeSet<>();
		for (WayPoint wp : route1) {
			if (startTime <= wp.getTime() && wp.getTime() <= endTime) {
				merged.add(wp.getTime());
			}
		}
		for (WayPoint wp : route2) {
			if (startTime <= wp.getTime() && wp.getTime() <= endTime) {
				merged.add(wp.getTime());
			}
		}
		return merged;
	}

	/**
	 * Creates a route that has a way point for each of the given times (those that
	 * fall within the route's start-end time period). Assuming that the given times
	 * are a superset of the route's way point times, then the new route is
	 * equivalent to the original route.
	 * 
	 * @param route A route.
	 * @param times A set of times (in increasing order).
	 * @return A new route, with one way point for each of the given times. The
	 *         spatial coordinates for time t are given by interpolating the
	 *         original route to time t.
	 */
	public static List<WayPoint> align(List<WayPoint> route, SortedSet<Long> times) {
		List<Long> timeList = new ArrayList<>(times);

		// Index of the next way point in the route;
		int routePos = 0;
		// The start and end way points of the current segment of the route.
		WayPoint start = route.get(routePos);
		WayPoint end = route.get(routePos + 1);

		// The result, the aligned route.
		List<WayPoint> aligned = new ArrayList<>();

		// The next time in the list of times.
		int timePos = 0;

		// Skip any times that precede the route's start time.
		while (timePos < times.size() && timeList.get(timePos) < start.getTime()) {
			timePos++;
		}

		// Create a way point for the aligned route, for each remaining time that does
		// not exceed the route's end time.
		for (; timePos < times.size(); timePos++) {
			long time = timeList.get(timePos);
			while (time > end.getTime()) {
				// Move to the next segment of the route, if any.
				if (routePos == route.size() - 1) {
					// No more segments.
					break;
				} else {
					routePos++;
					start = end;
					end = route.get(routePos);
				}
			}
			// Add a way point for the current time.
			aligned.add(WayPoint.interpolate(start, end, time));
		}

		// Ignore any times that come after the route's end time.

		return aligned;
	}

}
