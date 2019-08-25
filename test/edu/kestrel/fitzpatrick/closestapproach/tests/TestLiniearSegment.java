package edu.kestrel.fitzpatrick.closestapproach.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import edu.kestrel.fitzpatrick.closestapproach.ClosestApproach;
import edu.kestrel.fitzpatrick.closestapproach.DetermineClosestApproachBetweenLinearSegments;
import edu.kestrel.fitzpatrick.closestapproach.DetermineClosestApproachBetweenRoutes;
import edu.kestrel.fitzpatrick.closestapproach.WayPoint;
import edu.kestrel.fitzpatrick.texttable.TextTable;

public class TestLiniearSegment {
	public static void main(String[] argv) {
		int[][] wp1 = { { 0, 0, 0 }, { 10, 2, 2 }, { 20, 4, 2 }, { 60, 0, 0 } };
		List<WayPoint> route1 = new ArrayList<>();
		for (int[] wp : wp1) {
			route1.add(new WayPoint(wp[0], new double[] { wp[1], wp[2] }));
		}

		int[][] wp2 = { { 0, 2, 8 }, { 20, 0, 6 }, { 40, 4, 2 } };
		List<WayPoint> route2 = new ArrayList<>();
		for (int[] wp : wp2) {
			route2.add(new WayPoint(wp[0], new double[] { wp[1], wp[2] }));
		}

		SortedSet<Long> alignedTimes = DetermineClosestApproachBetweenRoutes.alignTimes(route1, route2);
		System.out.println("Aligned times: " + alignedTimes);

		List<WayPoint> aligned1 = DetermineClosestApproachBetweenRoutes.align(route1, alignedTimes);
		List<String> strings1 = aligned1.stream() //
				.map(wp -> String.format("(%.0f, %.0f)", wp.getCoordinates()[0], wp.getCoordinates()[1])) //
				.collect(Collectors.toList());
		System.out.println("Aligned route 1: " + String.join(" ", strings1));

		List<WayPoint> aligned2 = DetermineClosestApproachBetweenRoutes.align(route2, alignedTimes);
		List<String> strings2 = aligned2.stream() //
				.map(wp -> String.format("(%.0f, %.0f)", wp.getCoordinates()[0], wp.getCoordinates()[1])) //
				.collect(Collectors.toList());
		System.out.println("Aligned route 2: " + String.join(" ", strings2));

		TextTable table = new TextTable();
		table.setHeaders("ts", "te", "s1", "e1", "s2", "e2", "k", "t", "p1", "p2", "d");
		List<Long> timeList = new ArrayList<>(alignedTimes);
		for (int t = 0; t < timeList.size() - 1; t++) {
			WayPoint s1 = aligned1.get(t);
			WayPoint e1 = aligned1.get(t + 1);
			WayPoint s2 = aligned2.get(t);
			WayPoint e2 = aligned2.get(t + 1);
			DetermineClosestApproachBetweenLinearSegments dca = new DetermineClosestApproachBetweenLinearSegments(s1,
					e1, s2, e2);
			ClosestApproach approach = dca.getClosestApproach();
			WayPoint cp1 = approach.wayPoint1();
			WayPoint cp2 = approach.wayPoint2();
			table.row(s1.getTime(), e1.getTime(), //
					toString(s1.getCoordinates()), toString(e1.getCoordinates()), //
					toString(s2.getCoordinates()), toString(e2.getCoordinates()), //
					dca.closestK(), cp1.getTime(), //
					toString(cp1.getCoordinates()), toString(cp2.getCoordinates()), //
					String.format("%6.3f", approach.distance()));
		}
		System.out.println(table);

	}

	private static String toString(double[] p) {
		return String.format("(%4.1f, %4.1f)", p[0], p[1]);
	}
}
