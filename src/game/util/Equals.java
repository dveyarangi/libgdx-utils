package game.util;

import com.badlogic.gdx.graphics.Color;

public class Equals {

	public static final float EPS = 10e-10f;


	public static boolean eq(float a, float b)
	{
		if( a == b ) return true;
		return Math.abs(a-b) <EPS;
	}
	public static boolean eq(double a, double b)
	{
		if( a == b ) return true;
		return Math.abs(a-b) <EPS;
	}

	public static boolean isZero(float a)
	{
		return Math.abs(a) <EPS;
	}

	public static boolean isZero(double a)
	{
		return Math.abs(a) <EPS;
	}

	public static boolean eq(Color a, Color b)
	{
		return a.r == b.r && a.g == b.g && a.b == b.b && a.a == b.a;
	}

}
