package edu.kestrel.fitzpatrick.closestapproach.tests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;

import edu.kestrel.fitzpatrick.closestapproach.DetermineClosestApproachBetweenLinearSegments;
import edu.kestrel.fitzpatrick.closestapproach.VectorUtils;

/**
 * 
 * @author Stephen Fitzpatrick
 *
 *         Animate the distance between two line segments for various values of
 *         the interpolation parameter, k.
 */
public class AnimateTwoSegmentsApp extends SimpleAnimation {
	private static final long serialVersionUID = 6687985025097433466L;

	// The current start and end points of the line segments.
	private double[] s1;
	private double[] e1;
	private double[] s2;
	private double[] e2;

	// The amount by which to increase/decrease k per frame.
	private double stepK = 0.001;

	// The current value of k.
	private double k = -1;

	// The position on each line segment for the current k.
	private double[] p1;
	private double[] p2;
	// The distance between the two positions.
	private double distance;

	// The minimum distance found.
	private double minDistance = Double.POSITIVE_INFINITY;

	// The predicted closest approach.
	private double predictedClosestK;
	private double[] predictedClosestPoint1;
	private double[] predictedClosestPoint2;
	private double predictedClosestDistance;

	/**
	 * Create a new animation.
	 */
	public AnimateTwoSegmentsApp() {
		super(5, false);
		setBounds(new double[] { 0, 0, 1, 1 });
	}

	@Override
	protected String getTitle() {
		return "Closest Approach between Two Segments";
	}

	@Override
	protected void stepAnimation() {
		// In each frame of the animation, step from 0 to 1 in increments of stepK, or
		// step back from 1 to 0. Then create a new pair of line segments.
		k += stepK;
		if (k <= 0) {
			stepK = -stepK;
			k = 0;
			generateRandomLineSegments();
		} else if (k >= 1) {
			stepK = -stepK;
			k = 1;
		}

		// Update the points and distance for the updated k.
		p1 = VectorUtils.interpolate(s1, e1, k);
		p2 = VectorUtils.interpolate(s2, e2, k);
		distance = VectorUtils.distance(p1, p2);

		// Have we found a new minimum?
		if (distance < minDistance) {
			minDistance = distance;
			logf("k=%5.3f  distance=%7.4f  predicted=%7.4f\n", k, distance, predictedClosestDistance);
		}
	}

	/*
	 * Generate random start/end points for the two lines segments, and update the
	 * predicted closest approach.
	 */
	private void generateRandomLineSegments() {
		s1 = randomPoint(bounds);
		e1 = randomPoint(bounds);
		s2 = randomPoint(bounds);
		e2 = randomPoint(bounds);
		predictedClosestK = DetermineClosestApproachBetweenLinearSegments.computeClosestK(s1, e1, s2, e2);
		predictedClosestPoint1 = VectorUtils.interpolate(s1, e1, predictedClosestK);
		predictedClosestPoint2 = VectorUtils.interpolate(s2, e2, predictedClosestK);
		predictedClosestDistance = VectorUtils.distance(predictedClosestPoint1, predictedClosestPoint2);
		minDistance = Double.POSITIVE_INFINITY;
		clearLog();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// Draw the bounds.
		if (bounds != null) {
			g2d.setPaint(Color.BLACK);
			drawRectangle(bounds[0], bounds[1], bounds[2], bounds[3]);
		}

		// Draw the line segments.
		if (s1 != null && e1 != null) {
			g2d.setPaint(Color.BLACK);
			drawLine(s1[0], s1[1], e1[0], e1[1]);
		}
		if (s2 != null && e2 != null) {
			g2d.setPaint(Color.BLACK);
			drawLine(s2[0], s2[1], e2[0], e2[1]);
		}

		// Draw the interpolated positions.
		if (p1 != null) {
			g2d.setPaint(Color.MAGENTA);
			fillCircle(p1[0], p1[1], 5);
		}
		if (p2 != null) {
			g2d.setPaint(Color.MAGENTA);
			fillCircle(p2[0], p2[1], 5);
		}
		// Draw the line between the interpolated positions and show their distance.
		if (p1 != null && p2 != null) {
			g2d.setPaint(Color.MAGENTA);
			drawLine(p1[0], p1[1], p2[0], p2[1]);

			double midX = (p1[0] + p2[0]) / 2;
			double midY = (p1[1] + p2[1]) / 2;
			String text = String.format("%6.4f", distance);
			drawText(midX, midY, text);
		}

		// Draw the predicted closest approach.
		if (predictedClosestPoint1 != null && predictedClosestPoint2 != null) {
			g2d.setPaint(new Color(0f, 0f, 1f, 0.5f));
			fillCircle(predictedClosestPoint1[0], predictedClosestPoint1[1], 7);
			fillCircle(predictedClosestPoint2[0], predictedClosestPoint2[1], 7);
			float dash[] = { 10, 20 };
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 0, dash, 0));
			drawLine(predictedClosestPoint1[0], predictedClosestPoint1[1], predictedClosestPoint2[0],
					predictedClosestPoint2[1]);

			double midX = (predictedClosestPoint1[0] + predictedClosestPoint2[0]) / 2;
			double midY = (predictedClosestPoint1[1] + predictedClosestPoint2[1]) / 2;
			String text = String.format("%6.4f", predictedClosestDistance);
			drawText(midX, midY, text);
		}
	}

	public static void main(String[] args) {
		AnimateTwoSegmentsApp animation = new AnimateTwoSegmentsApp();
		animation.start();
	}

}
