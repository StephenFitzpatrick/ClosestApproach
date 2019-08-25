package edu.kestrel.fitzpatrick.closestapproach.tests;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * 
 * @author Stephen Fitzpatrick
 *
 *         Support class for animating simple computations.
 */
public abstract class SimpleAnimation extends JPanel {
	private static final long serialVersionUID = 5353912007880649796L;

	// Delay between frames.
	private int delay;
	// When the animation starts, should it be on pause?
	private final boolean startPaused;
	// Is the animation currently paused.
	private boolean isPaused = false;

	// Timer for doing the animation.
	private Timer timer;

	// Bounds in application coordinate space.
	protected double[] bounds;
	// Factor for scaling application coordinate space to pixel space.
	protected double scale;

	// The drawing environment.
	protected Graphics2D g2d;

	// Space left blank at left/right and top/bottom, in pixels.
	protected final double[] border = { 10, 10 };

	// Offset for x/y to centre drawing.
	protected double[] offset = { 0, 0 };

	// GUI elements.
	private JButton pauseButton;
	private JButton stepButton;
	private JButton fasterButton;
	private JButton slowerButton;
	private JTextPane logTextPane;

	// Frame count - used when saving frames to files, to number the files.
	private int frame = 0;
	// The directory into which frames are saved.
	File frameDirectory = new File("/tmp/frames/");

	// Used for creating random points.
	private static final Random random = new Random();

	/**
	 * Create a new animation.
	 * 
	 * @param delay       Delay between frames (milliseconds).
	 * @param startPaused Should the animation be paused when it starts?
	 */
	public SimpleAnimation(int delay, boolean startPaused) {
		super();
		this.delay = delay;
		this.startPaused = startPaused;
		JFrame frame = new JFrame(getTitle());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(true);
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1000, 800));
		contentPane.add(this, BorderLayout.CENTER);
		contentPane.add(makeControls(), BorderLayout.SOUTH);
		contentPane.add(makeLog(), BorderLayout.EAST);

		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Create a new animation that is running when it is started.
	 * 
	 * @param delay Delay between frames.
	 */
	public SimpleAnimation(int delay) {
		this(delay, false);
	}

	/**
	 * 
	 * @return Title of the animation (appears in the frame's title bar).
	 */
	protected abstract String getTitle();

	/**
	 * Log some text, with a new line appended.
	 * 
	 * @param text The text to log.
	 */
	protected void logln(String text) {
		log(text);
		log("\n");
	}

	/**
	 * Log some text, with the standard formatting features.
	 * 
	 * @param template See String.format.
	 * @param values   See String.format.
	 */
	protected void logf(String template, Object... values) {
		String text = String.format(template, values);
		log(text);
	}

	/**
	 * Log some text.
	 * 
	 * @param text The text to log.
	 */
	protected void log(String text) {
		try {
			StyledDocument doc = logTextPane.getStyledDocument();
			doc.insertString(doc.getLength(), text, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clear the log.
	 */
	protected void clearLog() {
		logTextPane.setText("");
	}

	/**
	 * Create the log GUI element.
	 * 
	 * @return The log GUI element.
	 */
	protected Component makeLog() {
		logTextPane = new JTextPane();
		logTextPane.setFont(new Font("monospaced", Font.PLAIN, 12));
		logTextPane.setEditable(false);
		logTextPane.setPreferredSize(new Dimension(400, 400));
		JScrollPane scroll = new JScrollPane(logTextPane);
		return scroll;
	}

	/**
	 * Create the GUI controls.
	 * 
	 * @return The GUI controls.
	 */
	protected Component makeControls() {
		JPanel controls = new JPanel();
		controls.setPreferredSize(new Dimension(1000, 200));

		slowerButton = new JButton("Slower");
		slowerButton.addActionListener(e -> slowerAnimation());
		controls.add(slowerButton);

		fasterButton = new JButton("Faster");
		fasterButton.addActionListener(e -> fasterAnimation());
		controls.add(fasterButton);

		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(e -> togglePaused());
		controls.add(pauseButton);

		stepButton = new JButton("Step");
		stepButton.addActionListener(e -> {
			stepAnimation();
			repaint();
		});
		stepButton.setEnabled(false);
		controls.add(stepButton);

		return controls;
	}

	/**
	 * Increase the speed of the animation, if possible.
	 */
	protected void fasterAnimation() {
		if (delay > 1) {
			delay /= 2;
			timer.setDelay(delay);
		}
	}

	/**
	 * Reduce the speed of the animation, if possible.
	 */
	protected void slowerAnimation() {
		if (delay < Integer.MAX_VALUE / 2) {
			delay *= 2;
			timer.setDelay(delay);
		}
	}

	/**
	 * Pause the animation.
	 */
	protected void pause() {
		pauseButton.setText("Resume");
		stepButton.setEnabled(true);
		slowerButton.setEnabled(false);
		fasterButton.setEnabled(false);
		isPaused = true;
	}

	/**
	 * Resume the animation.
	 */
	protected void unpause() {
		pauseButton.setText("Pause");
		stepButton.setEnabled(false);
		slowerButton.setEnabled(true);
		fasterButton.setEnabled(true);
		isPaused = false;
	}

	/**
	 * Toggle pause/resume.
	 */
	protected void togglePaused() {
		if (isPaused) {
			unpause();
		} else {
			pause();
		}
	}

	/**
	 * 
	 * @return Whether or not the animation is paused.
	 */
	protected boolean isPaused() {
		return isPaused;
	}

	/**
	 * Begin creating frames for the animation.
	 */
	public void start() {
		if (startPaused) {
			pause();
		}
		timer = new Timer(delay, e -> {
			if (!isPaused()) {
				stepAnimation();
				repaint();
			}
		});
		timer.start();
	}

	/**
	 * Called once per frame to update the animation's state.
	 */
	abstract protected void stepAnimation();

	/**
	 * Subclass this method to draw a frame of the animation.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g2d = (Graphics2D) g;
	}

	/**
	 * Draw a rectangle with coordinates in application coordinate space.
	 * 
	 * @param xMin Minimum x coordinate
	 * @param yMin Minimum y coordinate
	 * @param xMax Maximum x coordinate
	 * @param yMax Maximum y coordinate
	 */
	protected void drawRectangle(double xMin, double yMin, double xMax, double yMax) {
		double[] ll = transform(xMin, yMin);
		double[] ur = transform(xMax, yMax);
		g2d.draw(new Rectangle2D.Double(ll[0], ll[1], ur[0] - ll[0], ur[1] - ll[1]));
	}

	/**
	 * Fill a rectangle with coordinates in application coordinate space.
	 * 
	 * @param xMin Minimum x coordinate
	 * @param yMin Minimum y coordinate
	 * @param xMax Maximum x coordinate
	 * @param yMax Maximum y coordinate
	 */
	protected void fillRectangle(double xMin, double yMin, double xMax, double yMax) {
		double[] ll = transform(xMin, yMin);
		double[] ur = transform(xMax, yMax);
		g2d.fill(new Rectangle2D.Double(ll[0], ll[1], ur[0] - ll[0], ur[1] - ll[1]));
	}

	/**
	 * Draw a circle with coordinates in application coordinate space.
	 * 
	 * @param x        Centre x coordinate
	 * @param y        Centre y coordinate
	 * @param diameter Diameter of the circle
	 */
	protected void drawCircle(double x, double y, int diameter) {
		double[] coords = transform(x, y);
		Shape point = new Ellipse2D.Double(coords[0] - diameter / 2, coords[1] - diameter / 2, diameter, diameter);
		g2d.draw(point);
	}

	/**
	 * Fill a circle with coordinates in application coordinate space.
	 * 
	 * @param x        Centre x coordinate
	 * @param y        Centre y coordinate
	 * @param diameter Diameter of the circle
	 */
	protected void fillCircle(double x, double y, int diameter) {
		double[] coords = transform(x, y);
		Shape point = new Ellipse2D.Double(coords[0] - diameter / 2, coords[1] - diameter / 2, diameter, diameter);
		g2d.fill(point);
	}

	/**
	 * Draw a line with coordinates in application coordinate space.
	 * 
	 * @param x1 Start x coordinate
	 * @param y1 Start y coordinate
	 * @param x2 End x coordinate
	 * @param y2 End y coordinate
	 */
	protected void drawLine(double x1, double y1, double x2, double y2) {
		double[] coords1 = transform(x1, y1);
		double[] coords2 = transform(x2, y2);
		g2d.draw(new Line2D.Double(coords1[0], coords1[1], coords2[0], coords2[1]));
	}

	/**
	 * Draw text with coordinates in application coordinate space.
	 * 
	 * @param x    Start x coordinate
	 * @param y    Start y coordinate
	 * @param text The text to draw
	 */
	protected void drawText(double x, double y, String text) {
		double[] coords = transform(x, y);
		g2d.drawString(text, (float) coords[0], (float) coords[1]);
	}

	/**
	 * Transform from application coordinates to pixel coordinates
	 * 
	 * @param x Application x coordinate
	 * @param y Application y coordinate
	 * @return Array containing pixel x and y coordinates
	 */
	protected double[] transform(double x, double y) {
		double tx = offset[0] + getXBorder() + (x - bounds[0]) * scale;
		double ty = offset[1] + getYBorder() + (y - bounds[1]) * scale;
		return new double[] { tx, ty };
	}

	/**
	 * Set the bounds in application coordinate space
	 * 
	 * @param bounds Array containing minimum x, minimum y, maximum x and maximum y
	 *               application coordinates
	 */
	protected void setBounds(double[] bounds) {
		if (bounds == null) {
			return;
		}
		this.bounds = bounds;
		double dx = bounds[2] - bounds[0];
		double dy = bounds[3] - bounds[1];

		// Update the offsets and scaling to convert from application coordinate to
		// pixel coordinate.
		double xScale = (getWidth() - border[0] * 2) / dx;
		double yScale = (getHeight() - border[1] * 2) / dy;
		scale = Math.min(xScale, yScale);
		double offsetX = (getWidth() - border[0] * 2 - dx * scale) / 2;
		double offsetY = (getHeight() - border[1] * 2 - dy * scale) / 2;
		offset = new double[] { offsetX, offsetY };
	}

	/**
	 * 
	 * @return The x component of the border
	 */
	protected double getXBorder() {
		return border[0];
	}

	/**
	 * 
	 * @return The y component of the border
	 */
	protected double getYBorder() {
		return border[1];
	}

	/**
	 * Save the current frame to file.
	 */
	protected void saveFrame() {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		paint(image.getGraphics());
		File file = new File(frameDirectory, String.format("frame-%03d.png", ++frame));
		file.getParentFile().mkdirs();
		try {
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a random double in [min, max).
	 * 
	 * @param min Lower bound
	 * @param max Upper bound
	 * @return A random double
	 */
	protected static double randomDouble(double min, double max) {
		return min + random.nextDouble() * (max - min);
	}

	/**
	 * Create a random point in the given bounds.
	 * 
	 * @param bounds Minimum x, minimum y, maximum x, maximum y
	 * @return A random point
	 */
	protected static double[] randomPoint(double[] bounds) {
		double x = randomDouble(bounds[0], bounds[2]);
		double y = randomDouble(bounds[1], bounds[3]);
		return new double[] { x, y };
	}

}
