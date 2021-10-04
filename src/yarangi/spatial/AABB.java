package yarangi.spatial;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.Pool;

import yarangi.java.InvokationMapper;
import yarangi.math.IVector2D;
import yarangi.math.Vector2D;

/**
 * Represents an axis-aligned bounding box (square, actually).
 *
 */
public class AABB implements Area
{
	/**
	 * center point
	 */
	private Vector2D ref = Vector2D.ZERO();

	/**
	 * half-width of the square
	 */
	private double rx;
	private double ry;
	private double rmax;
	/**
	 * angle of the square (degrees)
	 */
	private double a;

	private int passId;

	private static InvokationMapper amap = new InvokationMapper();

	private static Pool <AABB> pool = new Pool<AABB>() {
		@Override protected AABB newObject() { return new AABB();}
	};

	private static AABB getAABB()
	{
		return pool.obtain();
	}

	public static void release(AABB aabb)
	{
		pool.free(aabb);
	}

	/**
	 * Initializes this AABB with values of specified AABB
	 * @param aabb
	 */
	public void paste(AABB aabb)
	{
		this.ref.set(aabb.ref);
		this.rx = aabb.rx;
		this.ry = aabb.ry;
		this.rmax = aabb.rmax;
		this.a = aabb.a;
	}

	public static AABB createSquare(double x, double y, double r, double a)
	{
		AABB aabb = getAABB();
		return aabb.update( x, y, r, r, a );
	}
	public static AABB createSquare(Vector2D center, double r, double a)
	{
		AABB aabb = getAABB();
		return aabb.update(center.x(), center.y(), r, r, a);
	}

	public static AABB createFromEdges(double x1, double y1, double x2, double y2, double a)
	{
		double rx = (x2 - x1) / 2.;
		double ry = (y2 - y1) / 2.;

		AABB aabb = getAABB();
		return aabb.update(x1+rx, y1+ry, rx, ry, a);
	}

	public static AABB createFromCenter(double cx, double cy, double rx, double ry, double a)
	{
		AABB aabb = getAABB();
		return aabb.update(cx, cy, rx, ry, a);
	}

	public static AABB createPoint(double x, double y)
	{
		AABB aabb = getAABB();
		return aabb.update(x, y, 0, 0, 0);
	}


	protected AABB()
	{
		//		amap.record();
	}

	/**
	 * C'tor
	 * @param x box center x
	 * @param y box center y
	 * @param r half box width
	 * @param a box orientation (degrees)
	 */

	protected AABB update(double x, double y, double rx, double ry, double a)
	{
		assert !Double.isNaN(x);
		this.ref.setxy( x, y );
		rmax = Math.max(rx, ry);
		this.rx = rx;
		this.ry = ry;
		this.a = a;
		return this;
	}

	/**
	 * Copy ctor.
	 * @param aabb
	 */
	/*	protected AABB(AABB aabb)
	{
		this(aabb.ref.x(), aabb.ref.y(), aabb.getRX(), aabb.getRY(), aabb.getOrientation());
		amap.record();
	}*/

	/**
	 * {@inheritDoc}
	 */

	@Override
	public double getMaxRadius() { return rmax; }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double getOrientation() { return a; }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IVector2D getAnchor() { return ref; }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setOrientation(double a) { this.a = a; }

	/**
	 * {@inheritDoc}
	 */
	@Override
	final public void translate(double dx, double dy) {
		ref.add(dx, dy);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void move(double x, double y) {
		ref.setxy( x, y );
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fitTo(double radius)
	{
		if(radius < 0)
			throw new IllegalArgumentException("AABB radius must be positive.");

		if(rx > ry)
		{
			ry = ry*radius/rx;
			rx = radius;
		}
		else
		{
			rx = rx*radius/ry;
			ry = radius;

		}
		this.rmax = radius;
	}

	public final boolean overlaps(double minx, double miny, double maxx, double maxy)
	{
		return ( (maxx >= ref.x()-rx && maxx <= ref.x()+rx) ||
				(minx >= ref.x()-rx && minx <= ref.x()+rx) ||
				(minx >= ref.x()-rx && maxx <= ref.x()+rx) ||
				(minx <= ref.x()-rx && maxx >= ref.x()+rx)
				) && (
						(maxy >= ref.y()-ry && maxy <= ref.y()+ry) ||
						(miny >= ref.y()-ry && miny <= ref.y()+ry) ||
						(miny >= ref.y()-ry && maxy <= ref.y()+ry) ||
						(miny <= ref.y()-ry && maxy >= ref.y()+ry)
						);

	}
	@Override
	public boolean overlaps(AABB area)
	{
		return overlaps( area.getMinX(), area.getMinY(), area.getMaxX(), area.getMaxY() );
	}

	@Override
	public List<IVector2D> getDarkEdge(Vector2D from)
	{

		// TODO: OPTIMIZE vector allocation
		List <IVector2D> res = new ArrayList <> (2);
		//			System.out.println(entities.size());
		Vector2D distance = ref.minus(from).normalize().multiply(getMaxRadius());

		res.add(distance.left() .add(ref).substract(from));
		res.add(distance.right().add(ref).substract(from));
		return res;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o)
	{
		return o == this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AABB clone() {
		AABB aabb = getAABB();
		return aabb.update(this.ref.x(), this.ref.y(), getRX(), getRY(), getOrientation());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return "AABB [loc:" + ref.x() + ":" + ref.y() + "; r:(" + rx +"," +ry +"); a:" + a + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iterate(int cellsize, IChunkConsumer consumer)
	{

		double minx = Math.floor( (ref.x()-rx)/cellsize ) * cellsize;
		double miny = Math.floor( (ref.y()-ry)/cellsize ) * cellsize;

		double maxx = Math.ceil(  (ref.x()+rx)/cellsize ) * cellsize;
		double maxy = Math.ceil(  (ref.y()+ry)/cellsize ) * cellsize;

		double currx, curry;
		//		System.out.println(minx + " : " + maxx + " : " + miny + " : " + maxy + " " + cellsize);

		for(currx = minx; currx <= maxx; currx += cellsize)
			for(curry = miny; curry <= maxy; curry += cellsize)
			{
				if(consumer.consume( new CellChunk(this, currx, curry, cellsize)))
					return;
			}
	}

	public final double getMinX() { return ref.x() - rx; }
	public final double getMaxX() { return ref.x() + rx; }
	public final double getMinY() { return ref.y() - ry; }
	public final double getMaxY() { return ref.y() + ry; }

	public final double getRX() {return rx; }
	public final double getRY() {return ry; }
	public final double getCenterX()
	{
		return ref.x();
	}
	public final double getCenterY()
	{
		return ref.y();
	}

	@Override
	public final int getPassId() { return passId; }

	@Override
	public final void setPassId(int id) {	this.passId = id; }
	public boolean crosses(double ox, double oy, double dx, double dy)
	{
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean contains(double x, double y)
	{
		return x >= getMinX() && x <= getMaxX() && y >= getMinY() && y <= getMaxY();
	}


}
