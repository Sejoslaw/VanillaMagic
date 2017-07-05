package seia.vanillamagic.api.util;

/**
 * Simple presentation of 2D Point. <br>
 */
public class Point 
{
	protected int x, y;
	
	public Point()
	{
		this(0, 0);
	}
	
	public Point(int x, int y)
	{
		this.setPosition(x, y);
	}
	
	/**
	 * Set the position of this point.
	 */
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return Returns the X-Coordinate of this point.
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * @return Returns the Y-Coordintate of this point.
	 */
	public int getY()
	{
		return y;
	}
}