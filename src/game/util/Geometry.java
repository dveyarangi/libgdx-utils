package game.util;

import com.badlogic.gdx.math.Vector2;

public class Geometry
{

	/**
	 * Calculates distance value square.
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static float calcHypotSquare( final Vector2 v1, final Vector2 v2 )
	{
		return calcHypotSquare(v1.x, v1.y, v2.x, v2.y);
	}

	public static float calcHypotSquare( final float x1, final float y1, final float x2, final float y2 )
	{
		float dx = x2 - x1;
		float dy = y2 - y1;
		return dx * dx + dy * dy;
	}

	public static float calcHypot( final Vector2 v1, final Vector2 v2 )
	{
		return calcHypot(v1.x, v1.y, v2.x, v2.y);
	}

	public static float calcHypot( final float x1, final float y1, final float x2, final float y2 )
	{

		return (float) Math.sqrt(( x2 - x1 ) * ( x2 - x1 ) + ( y2 - y1 ) * ( y2 - y1 ));

	}

	/**
	 *
	 *
	 * @param p
	 *            point
	 * @param la
	 *            line anchor
	 * @param d
	 *            line direction
	 * @return
	 */
	/*
	 * public static double calcDistanceToLine(final Vector2 P, final Vector2 Q,
	 * final Vector2 v) { return P.minus(Q).abs() / v.left().abs(); }
	 */

	public static double calcTriangleArea( final double x1, final double y1, final double x2, final double y2, final double x3, final double y3 )
	{
		return ( x1 * y2 - x2 * y1 + x2 * y3 - x3 * y2 - x3 * y1 - x1 * y3 ) / 2;
	}

	public static boolean isLeft( final double px, final double py, final double x1, final double y1, final double x2, final double y2 )
	{
		return calcTriangleArea(x1, y1, x2, y2, px, py) > 0;
	}

	/**
	 * A, B, and C must be ordered clockwise.
	 * 
	 * @param p
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean pointInCircumCirle( final Vector2 p, final Vector2 a, final Vector2 b, final Vector2 c )
	{
		return pointInCircumCirle(p.x, p.y, a.x, a.y, b.x, b.y, c.x, c.y);
	}

	public static boolean pointInCircumCirle( final double px, final double py, final double x1, final double y1, final double x2, final double y2, final double x3, final double y3 )
	{
		double d1 = x1 * x1 + y1 * y1;
		double d2 = x2 * x2 + y2 * y2;
		double d3 = x3 * x3 + y3 * y3;
		double pd = px * px + py * py;
		return determinant(x2, y2, d2, x3, y3, d3, px, py, pd)
				- determinant(x1, y1, d1, x3, y3, d3, px, py, pd)
				+ determinant(x1, y1, d1, x2, y2, d2, px, py, pd)
				- determinant(x1, y1, d1, x2, y2, d2, x3, y3, d3) < 0;
	}

	public static double determinant( final double x1, final double y1, final double x2, final double y2 )
	{
		return x1 * y2 - x2 * y1;
	}

	public static double determinant( final double x1, final double y1, final double z1, final double x2, final double y2, final double z2, final double x3, final double y3, final double z3 )
	{
		return x1 * ( y2 * z3 - y3 * z2 ) - x2 * ( y1 * z3 - y3 * z1 ) + x3 * ( y1 * z2 - y2 * z1 );
	}

	/**
	 * Calculates the intersection point from P + s * u = Q + t * v
	 *
	 * @param P
	 *            - first line anchor point.
	 * @param u
	 *            - first line direction vector.
	 * @param Q
	 *            - second line anchor point.
	 * @param v
	 *            - second line direction vector.
	 * @return intersection point vector, null if parallel TODO: watch for
	 *         failures
	 */
	/*
	 * public static Vector2 calcIntersection(final Vector2 P, final Vector2 u,
	 * final Vector2 Q, final Vector2 v) { Vector2 w = P.minus(Q); double perp =
	 * v.x * u.y - v.y * u.x; if(perp == 0) return null;
	 * 
	 * double s = (v.y * w.x - v.x * w.y) / perp;
	 * 
	 * return Vector2D.R(P.x + u.x * s, P.y + u.y * s); }
	 */
	public static boolean calcIntersection( float o1x, float o1y, float u1x, float u1y, float o2x, float o2y, float u2x, float u2y )
	{
		return calcIntersection(o1x, o1y, u1x, u1y, o2x, o2y, u2x, u2y, null);
	}

	public static boolean calcIntersection( float p0_x, float p0_y, float s1_x, float s1_y, float p2_x, float p2_y, float s2_x, float s2_y, Vector2 result )
	{

		float prop = -s2_x * s1_y + s1_x * s2_y;
		float s, t;
		s = ( -s1_y * ( p0_x - p2_x ) + s1_x * ( p0_y - p2_y ) ) / prop;
		t = ( s2_x * ( p0_y - p2_y ) - s2_y * ( p0_x - p2_x ) ) / prop;

		if( s >= 0 && s <= 1 && t >= 0 && t <= 1 )
		{
			if( result != null )
				result.set(p0_x + ( t * s1_x ), p0_y + ( t * s1_y ));
			return true;
		}

		return false; // No collision
	}

	public static void main( String[] args )
	{
		calcIntersection(1, 1, -2, -2, -1, 1, 2, -2);
	}

	/**
	 * Calculates the intersection coefficients s and t, such as P + s * u = Q +
	 * t * v
	 * 
	 * @param P
	 *            - first line anchor point.
	 * @param u
	 *            - first line direction vector.
	 * @param Q
	 *            - second line anchor point.
	 * @param v
	 *            - second line direction vector.
	 * @return Vector2D object, whose x element is equal to s and y equal to t;
	 *         null if parallel
	 */
	/*
	 * public static Vector2D calcIntersectionParams(final Vector2D P, final
	 * Vector2D u, final Vector2D Q, final Vector2D v) { Vector2D w =
	 * P.minus(Q); double perp = v.x() * u.y() - v.y() * u.x(); if(perp == 0)
	 * return null; // TODO: sort out math: double s = (v.y() * w.x() - v.x() *
	 * w.y()) / perp; double t = (u.y() * w.x() - u.x() * w.y()) / (u.x() *
	 * v.y() - u.y() * v.x());
	 * 
	 * return Vector2D.R(s, t); }
	 */

}
