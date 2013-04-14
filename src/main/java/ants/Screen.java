package ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roman K.
 */
public class Screen
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(Screen.class);

	private int width;

	private int height;


	public Screen(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	public Point center()
	{
		return new Point((double) width / 2, (double) height / 2);
	}

	/**
	 * @author Roman K.
	 */
	public static enum ScreenSide
	{
		RIGHT, LEFT, BOTTOM
	}
}
