package edu.kestrel.fitzpatrick.closestapproach.unittests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.kestrel.fitzpatrick.closestapproach.VectorUtils;

/**
 * 
 * @author Stephen Fitzpatrick
 *
 *         Unit tests for VectorUtils.
 */
class VectorUtilsTest {
	// Number of randomized tests
	private static final int N_RANDOM_TESTS = 1_000_000;

	/*
	 * Interpolate at start, half-way and end.
	 */
	@Test
	void testInterpolate1() {
		double[] start = { 1, 1 };
		double[] end = { 5, 9 };
		double k = 0;
		double[] expected = { 1, 1 };
		assertArrayEquals(expected, VectorUtils.interpolate(start, end, k), 1e-5);
	}

	@Test
	void testInterpolate2() {
		double[] start = { 1, 1 };
		double[] end = { 5, 9 };
		double k = 0.5;
		double[] expected = { 3, 5 };
		assertArrayEquals(expected, VectorUtils.interpolate(start, end, k), 1e-5);
	}

	@Test
	void testInterpolate3() {
		double[] start = { 1, 1 };
		double[] end = { 5, 9 };
		double k = 1;
		double[] expected = { 5, 9 };
		assertArrayEquals(expected, VectorUtils.interpolate(start, end, k), 1e-5);
	}

	/*
	 * Interpolate at random fractions - check results in each dimension.
	 */
	@Test
	void testInterpolateRandom() {
		double[] start = { 1, 1 };
		double[] end = { 5, 9 };
		for (int t = 0; t < N_RANDOM_TESTS; t++) {
			double k = UnitTestUtils.random.nextDouble();
			double[] interpolated = VectorUtils.interpolate(start, end, k);
			double actualXDistance = interpolated[0] - start[0];
			double expectedXDistance = k * (end[0] - start[0]);
			assertEquals(expectedXDistance, actualXDistance, 1e-5);
			double actualYDistance = interpolated[1] - start[1];
			double expectedYDistance = k * (end[1] - start[1]);
			assertEquals(expectedYDistance, actualYDistance, 1e-5);
		}
	}

}
