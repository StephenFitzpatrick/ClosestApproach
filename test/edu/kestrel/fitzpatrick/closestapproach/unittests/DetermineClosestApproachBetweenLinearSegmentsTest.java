package edu.kestrel.fitzpatrick.closestapproach.unittests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.kestrel.fitzpatrick.closestapproach.ClosestApproach;
import edu.kestrel.fitzpatrick.closestapproach.DetermineClosestApproachBetweenLinearSegments;
import edu.kestrel.fitzpatrick.closestapproach.VectorUtils;
import edu.kestrel.fitzpatrick.closestapproach.WayPoint;

/**
 * 
 * @author Stephen Fitzpatrick
 *
 *         Unit tests for DetermineClosestApproachBetweenLinearSegments.
 */
class DetermineClosestApproachBetweenLinearSegmentsTest {

	// Number of randomized tests
	private static final int N_RANDOM_TESTS = 1_000_000;

	/*
	 * Test distance using an offset in x, in y, in x and y.
	 */
	@Test
	void testDistanceOffsetX() {
		for (int t = 0; t < N_RANDOM_TESTS; t++) {
			double x = UnitTestUtils.randomDouble(-10, 10);
			double y = UnitTestUtils.randomDouble(-10, 10);
			double d = UnitTestUtils.randomDouble(-10, 10);
			double[] c1 = { x, y };
			double[] c2 = { x + d, y };
			assertEquals(VectorUtils.distance(c1, c2), Math.abs(d), 1e-5);
		}
	}

	@Test
	void testDistanceOffsetY() {
		for (int t = 0; t < N_RANDOM_TESTS; t++) {
			double x = UnitTestUtils.randomDouble(-10, 10);
			double y = UnitTestUtils.randomDouble(-10, 10);
			double d = UnitTestUtils.randomDouble(-10, 10);
			double[] c1 = { x, y };
			double[] c2 = { x, y + d };
			assertEquals(VectorUtils.distance(c1, c2), Math.abs(d), 1e-5);
		}
	}

	@Test
	void testDistanceOffsetXY() {
		for (int t = 0; t < N_RANDOM_TESTS; t++) {
			double x = UnitTestUtils.randomDouble(-10, 10);
			double y = UnitTestUtils.randomDouble(-10, 10);
			double d = UnitTestUtils.randomDouble(-10, 10);
			double[] c1 = { x, y };
			double[] c2 = { x + d, y + d };
			double expected = Math.sqrt(2) * Math.abs(d);
			assertEquals(expected, VectorUtils.distance(c1, c2), 1e-5);
		}
	}


	/*
	 * Check closest approach against known values.
	 */
	@Test
	void testApproach1() {
		long startTime = 0;
		long endTime = 1;
		WayPoint s1 = new WayPoint(startTime, new double[] { -1, 0 });
		WayPoint e1 = new WayPoint(endTime, new double[] { 1, 0 });
		WayPoint s2 = new WayPoint(startTime, new double[] { 0, -1 });
		WayPoint e2 = new WayPoint(endTime, new double[] { 0, 1 });
		DetermineClosestApproachBetweenLinearSegments dca = new DetermineClosestApproachBetweenLinearSegments(s1, e1, s2, e2);
		assertEquals(0.5, dca.closestK());
		ClosestApproach ca = dca.getClosestApproach();
		assertArrayEquals(new double[] { 0, 0 }, ca.wayPoint1().getCoordinates(), 1e-5);
		assertArrayEquals(new double[] { 0, 0 }, ca.wayPoint2().getCoordinates(), 1e-5);
	}

	@Test
	void testApproach2() {
		long startTime = 0;
		long endTime = 1;
		WayPoint s1 = new WayPoint(startTime, new double[] { 1, 0 });
		WayPoint e1 = new WayPoint(endTime, new double[] { 0, 0 });
		WayPoint s2 = new WayPoint(startTime, new double[] { 0, 2 });
		WayPoint e2 = new WayPoint(endTime, new double[] { 0, 1 });
		DetermineClosestApproachBetweenLinearSegments dca = new DetermineClosestApproachBetweenLinearSegments(s1, e1,
				s2, e2);
		assertEquals(1, dca.closestK());
		ClosestApproach ca = dca.getClosestApproach();
		assertArrayEquals(new double[] { 0, 0 }, ca.wayPoint1().getCoordinates(), 1e-5);
		assertArrayEquals(new double[] { 0, 1 }, ca.wayPoint2().getCoordinates(), 1e-5);
	}

	/*
	 * Check closest approach with parallel motions.
	 */
	@Test
	void testApproachParallel() {
		long startTime = 0;
		long endTime = 1;
		WayPoint s1 = new WayPoint(startTime, new double[] { 0, 0 });
		WayPoint e1 = new WayPoint(endTime, new double[] { 2, 0 });
		WayPoint s2 = new WayPoint(startTime, new double[] { 0, 1 });
		WayPoint e2 = new WayPoint(endTime, new double[] { 2, 1 });
		DetermineClosestApproachBetweenLinearSegments dca = new DetermineClosestApproachBetweenLinearSegments(s1, e1,
				s2, e2);
		assertTrue(Double.isNaN(dca.closestK()));
		ClosestApproach ca = dca.getClosestApproach();
		assertEquals(null, ca.wayPoint1());
		assertEquals(null, ca.wayPoint2());
		assertEquals(null, ca.time());
		assertEquals(s1.distance(s2), ca.distance(), 1e-5);
	}

	/*
	 * Check closest approach with one object stationary.
	 */
	@Test
	void testApproachFirstStationary() {
		long startTime = 0;
		long endTime = 1;
		WayPoint s1 = new WayPoint(startTime, new double[] { 0, 0 });
		WayPoint e1 = new WayPoint(endTime, new double[] { 0, 0 });
		WayPoint s2 = new WayPoint(startTime, new double[] { 0, 1 });
		WayPoint e2 = new WayPoint(endTime, new double[] { 2, 1 });
		DetermineClosestApproachBetweenLinearSegments dca = new DetermineClosestApproachBetweenLinearSegments(s1, e1,
				s2, e2);
		assertEquals(0, dca.closestK());
		ClosestApproach ca = dca.getClosestApproach();
		assertArrayEquals(s1.getCoordinates(), ca.wayPoint1().getCoordinates(), 1e-5);
		assertArrayEquals(new double[] { 0, 1 }, ca.wayPoint2().getCoordinates(), 1e-5);
	}

	@Test
	void testApproachSecondStationary() {
		long startTime = 0;
		long endTime = 1;
		WayPoint s1 = new WayPoint(startTime, new double[] { 0, 0 });
		WayPoint e1 = new WayPoint(endTime, new double[] { 2, 0 });
		WayPoint s2 = new WayPoint(startTime, new double[] { 1, 1 });
		WayPoint e2 = new WayPoint(endTime, new double[] { 1, 1 });
		DetermineClosestApproachBetweenLinearSegments dca = new DetermineClosestApproachBetweenLinearSegments(s1, e1,
				s2, e2);
		assertEquals(0.5, dca.closestK());
		ClosestApproach ca = dca.getClosestApproach();
		assertArrayEquals(new double[] { 1, 0 }, ca.wayPoint1().getCoordinates(), 1e-5);
		assertArrayEquals(s2.getCoordinates(), ca.wayPoint2().getCoordinates(), 1e-5);
	}

	/*
	 * Check closest approach with both objects stationary - special case of
	 * parallel motions.
	 */
	@Test
	void testApproachBothStationary() {
		long startTime = 0;
		long endTime = 1;
		double[] p1 = new double[] { UnitTestUtils.randomDouble(-10, 10), UnitTestUtils.randomDouble(-10, 10) };
		double[] p2 = new double[] { UnitTestUtils.randomDouble(-10, 10), UnitTestUtils.randomDouble(-10, 10) };
		WayPoint s1 = new WayPoint(startTime, p1);
		WayPoint e1 = new WayPoint(endTime, p1);
		WayPoint s2 = new WayPoint(startTime, p2);
		WayPoint e2 = new WayPoint(endTime, p2);
		DetermineClosestApproachBetweenLinearSegments dca = new DetermineClosestApproachBetweenLinearSegments(s1, e1,
				s2, e2);
		assertTrue(Double.isNaN(dca.closestK()));
		ClosestApproach ca = dca.getClosestApproach();
		assertEquals(null, ca.wayPoint1());
		assertEquals(null, ca.wayPoint2());
		assertEquals(null, ca.time());
		assertEquals(s1.distance(s2), ca.distance(), 1e-5);
	}

	/*
	 * Check closest approach found by calculation vs sampling.
	 */
	@Test
	void testApproachRandom() {
		long startTime = 0;
		long endTime = 1;
		for (int t = 0; t < N_RANDOM_TESTS; t++) {
			WayPoint s1 = new WayPoint(startTime, new double[] { UnitTestUtils.randomDouble(-10, 10), UnitTestUtils.randomDouble(-10, 10) });
			WayPoint e1 = new WayPoint(endTime, new double[] { UnitTestUtils.randomDouble(-10, 10), UnitTestUtils.randomDouble(-10, 10) });
			WayPoint s2 = new WayPoint(startTime, new double[] { UnitTestUtils.randomDouble(-10, 10), UnitTestUtils.randomDouble(-10, 10) });
			WayPoint e2 = new WayPoint(endTime, new double[] { UnitTestUtils.randomDouble(-10, 10), UnitTestUtils.randomDouble(-10, 10) });
			DetermineClosestApproachBetweenLinearSegments ca = new DetermineClosestApproachBetweenLinearSegments(s1, e1, s2, e2);

			// Sample at fixed interval and record the smallest separation between the two
			// motions.
			int nSamples = 1_001;
			double sampleInterval = 1d / (nSamples - 1);
			double smallestSampleDistance = Double.POSITIVE_INFINITY;
			double smallestSampleK = -1;
			for (int s = 0; s < nSamples; s++) {
				double k = s * sampleInterval;
				double d = ca.distance(k);
				if (d < smallestSampleDistance) {
					smallestSampleDistance = d;
					smallestSampleK = k;
				}
			}
			double closestK = ca.closestK();
			assertEquals(smallestSampleK, closestK, sampleInterval);
		}
	}
}
