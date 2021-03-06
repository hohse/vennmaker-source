/**
 * 
 */
package data;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * VennMaker-Coordinatesystem.
 * 
 * This system is an unlimited cartesian coordinate system with (0,0) as the
 * center of the view plane (unlike the Java2D-coordinate system).
 * 
 * This class contains information about mapping from Java2D's system to
 * VennMaker's system and vice versa. Every instance of
 * <code>VennMakerView</code> should have a pointer to an instance of this
 * class.
 * 
 * 
 * 
 */
public class VennMakerCoordinateSystem
{
	/**
	 * The scaling of pixels in java2d to pixels in VMCS Example: A value of 2
	 * indicates that 1 pixel in vmcs corresponds to 2 pixels in java2d
	 */
	private float		scale;

	/**
	 * The point in java2d space that maps to vmcs (0,0) point.
	 */
	private Point2D	mid;

	/**
	 * Creates a standard mapping of the VennMakerCoordinateSystem to Java2D with
	 * an initial scaling of 1.0 and an initial center in the mid of the defined
	 * view.
	 * 
	 * @param mid
	 *           The current mid point in java2d space (usually the current ego's
	 *           center)
	 */
	public VennMakerCoordinateSystem(Point2D mid)
	{
		this.scale = 1.0f;
		this.mid = mid;
	}

	/**
	 * Returns a rectangle in java2d space that corresponds to the given
	 * rectangle in vmcs space.
	 * 
	 * @param rect
	 *           A rectangle in vmcs space.
	 * @return A rectangle in java2d space.
	 */
	public Rectangle2D toJava2D(Rectangle2D rect)
	{
		if (rect != null)
		{
			Point2D newP = toJava2D(new Point2D.Double(rect.getX(), rect.getY()));
			return new Rectangle2D.Double(newP.getX(), newP.getY(), rect
					.getWidth()
					* scale, rect.getHeight() * scale);
		}
		return null;
	}

	/**
	 * Returns the correspondent point in java2D coordinate space according to
	 * the given vmcs point.
	 * 
	 * @param pt
	 *           A point in vmcs space.
	 * @return A point in java2d space
	 */
	public Point2D toJava2D(Point2D pt)
	{
		if (pt != null)
		{
			return new Point2D.Double(this.mid.getX() + pt.getX() * this.scale,
					this.mid.getY() + pt.getY() * this.scale);
		}
		return null;
	}

	public float xToJava2D(float x)
	{
		if (this.mid != null) return (float) this.mid.getX() + x * this.scale;
		return 0;
	}

	public float yToJava2D(float y)
	{
		if (this.mid != null) return (float) this.mid.getY() + y * this.scale;
		return 0;
	}

	public float xToJava2D(double x)
	{
		if (this.mid != null) return (float) this.mid.getX() + (float) x * this.scale;
		return 0;		
	}

	public float yToJava2D(double y)
	{
		if (this.mid != null) return (float) this.mid.getY() + (float) y * this.scale;
		return 0;
	}

	/**
	 * Scales a float value in java2D coordinate space.
	 * 
	 * @param value
	 *           A float value in vmcs space.
	 * @return A float value in java2d space
	 */
	public float toJava2D(float value)
	{
		return value * (float) this.scale;
	}

	public float toJava2D(double value)
	{
		return (float) value * (float) this.scale;
	}

	/**
	 * Scales a float value in vmcs coordinate space.
	 * 
	 * @param value
	 *           A float value in java2d space.
	 * @return A float value in vmcs space
	 */
	public float toVMCS(float value)
	{
		return value / (float) this.scale;
	}

	/**
	 * Returns the point in vmcs space from java2d space.
	 * 
	 * @param pt
	 *           A point in java2d space.
	 * @return A point in vmcs space
	 */
	public Point2D toVMCS(Point2D pt)
	{
		if (pt != null)
		{
			return new Point2D.Double((pt.getX() - this.mid.getX()) / this.scale,
					(pt.getY() - this.mid.getY()) / this.scale);
		}
		return null;

	}

	/**
	 * @return the scale
	 */
	public final float getScale()
	{
		return scale;
	}

	/**
	 * @param scale
	 *           the scale to set
	 */
	public final void setScale(float scale)
	{
		this.scale = scale;
	}

	/**
	 * @return the mid
	 */
	public final Point2D getMid()
	{
		return mid;
	}

	/**
	 * @param mid
	 *           the mid to set
	 */
	public final void setMid(Point2D mid)
	{
		this.mid = mid;
	}
}
