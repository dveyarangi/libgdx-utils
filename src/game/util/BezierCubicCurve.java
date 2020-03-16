package game.util;

import com.badlogic.gdx.math.Vector2;

/**
 * @Deprecated TODO: check vector2 compat
 * @author Fima
 *
 */

public class BezierCubicCurve implements ParametricCurve
{
	private final Vector2 p1, p2, p3, p4;

	private final Vector2 at1 = new Vector2();
	private final Vector2 at2 = new Vector2();
	private final Vector2 at3 = new Vector2();
	private final Vector2 at4 = new Vector2();

	//	public BezierCubicCurve( float px1, float py1, float px2, float py2, float px3, float py3, float px4, float py4 )

	/**
	 * Uses references to provided vectors for curve calculation.
	 * The values of the vector2 are controlled by curve creator.
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 */
	public BezierCubicCurve( final Vector2 p1, final Vector2 p2, final Vector2 p3, final Vector2 p4 )
	{
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
	}

	/**
	 * Creates zero curve with all points set to zero.
	 */
	public BezierCubicCurve()
	{
		this(new Vector2(), new Vector2(), new Vector2(), new Vector2());
	}

	@Override
	public Vector2 at( final Vector2 target, final float t )
	{
		float f = 1 - t;

		target.set(at1.set(p1).scl(f * f * f).add(
				at2.set(p2).scl(3 * f * f * t)).add(
						at3.set(p3).scl(3 * f * t * t)).add(
								at4.set(p4).scl(t * t * t)));

		return target;
	}

	public Vector2 p1()
	{
		return p1;
	}

	public Vector2 p2()
	{
		return p2;
	}

	public Vector2 p3()
	{
		return p3;
	}

	public Vector2 p4()
	{
		return p4;
	}
}
