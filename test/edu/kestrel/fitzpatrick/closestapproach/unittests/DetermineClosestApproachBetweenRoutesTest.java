package edu.kestrel.fitzpatrick.closestapproach.unittests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.kestrel.fitzpatrick.closestapproach.ClosestApproach;
import edu.kestrel.fitzpatrick.closestapproach.DetermineClosestApproachBetweenRoutes;
import edu.kestrel.fitzpatrick.closestapproach.WayPoint;

/**
 * 
 * @author Stephen Fitzpatrick
 * 
 *         Unit tests for DetermineClosestApproachBetweenRoutes.
 *
 */
class DetermineClosestApproachBetweenRoutesTest {
	// Number of random tests.
	private static final int N_RANDOM_TESTS = 1_000_000;

	// Convenience method to make a way point.
	private static WayPoint wp(long time, double x, double y) {
		return new WayPoint(time, new double[] { x, y });
	}

	// Some routes.
	private static final List<WayPoint> ROUTE_1 = Arrays.asList( //
			wp(0, 0, 0), //
			wp(10, 0, 10), //
			wp(20, 10, 10), //
			wp(30, 10, 0), //
			wp(40, 0, 0) //
	);

	private static final List<WayPoint> ROUTE_2 = Arrays.asList( //
			wp(20, 5, 5), //
			wp(30, 15, 5) //
	);

	/*
	 * Test against known values.
	 */
	@Test
	void test() {
		DetermineClosestApproachBetweenRoutes dca = new DetermineClosestApproachBetweenRoutes(ROUTE_1, ROUTE_2);
		assertEquals(1, dca.getClosestApproaches().size());
		ClosestApproach ca = dca.getClosestApproaches().get(0);
		assertEquals(25, ca.wayPoint1().getTime());
		assertEquals(25, ca.wayPoint2().getTime());
		assertArrayEquals(new double[] { 10, 5 }, ca.wayPoint1().getCoordinates(), 1e-5);
		assertArrayEquals(new double[] { 10, 5 }, ca.wayPoint2().getCoordinates(), 1e-5);
	}

	/*
	 * Test routes that do not overlap in time.
	 */
	@Test
	void testDisjoint() {
		int n1 = UnitTestUtils.randomInt(2, 10);
		List<WayPoint> route1 = UnitTestUtils.randomRoute(0, 0, 0, n1);
		long route2StartTime = route1.get(n1 - 1).getTime() + 5;
		int n2 = UnitTestUtils.randomInt(2, 10);
		List<WayPoint> route2 = UnitTestUtils.randomRoute(route2StartTime, 5, 5, n2);
		DetermineClosestApproachBetweenRoutes ca = new DetermineClosestApproachBetweenRoutes(route1, route2);
		assertEquals(0, ca.getClosestApproaches().size());
	}

	/*
	 * Test random routes - verify computation against sampling.
	 */
	@Test
	void testRandom() {
		for (int r = 0; r < N_RANDOM_TESTS; r++) {
			List<WayPoint> route1 = UnitTestUtils.randomRoute();
			List<WayPoint> route2 = UnitTestUtils.randomRoute();
			DetermineClosestApproachBetweenRoutes ca = new DetermineClosestApproachBetweenRoutes(route1, route2);
			WayPoint[] bySampling = UnitTestUtils.findClosestApproach(route1, route2);

			if (ca.getClosestApproaches().isEmpty()) {
				assertNull(bySampling);
			} else if (bySampling == null) {
				assertEquals(0, ca.getClosestApproaches().size());
			} else {
				double samplingDistance = bySampling[0].distance(bySampling[1]);
				boolean noLonger = ca.getClosestApproaches().stream() //
						.allMatch(c -> c.distance() <= samplingDistance + 1e-5);
				if (!noLonger) {
					for (ClosestApproach bca : ca.getClosestApproaches()) {
						System.out.println("CA:     time=" + bca.time() + "\tdistance=" + bca.distance());
					}
					System.out.println("Sample: time=" + bySampling[0].getTime() + "\tdistance=" + samplingDistance);
					System.out.println("DEBUG");
				}
				assertTrue(noLonger);
			}
		}
	}

}
