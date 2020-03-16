package game.util;

public class Angles
{
	public static final float PI = (float) Math.PI;
	public static final float TAU = (float) Math.PI * 2;
	public static final float PI_div_2 = (float) Math.PI / 2;
	public static final float PI_div_3 = (float) Math.PI / 3;
	public static final float PI_div_4 = (float) Math.PI / 4;
	public static final float PI_div_5 = (float) Math.PI / 5;
	public static final float PI_div_6 = (float) Math.PI / 6;
	public static final float PI_div_12 = (float) Math.PI / 12;
	public static final float PI_div_10 = (float) Math.PI / 10;
	public static final float PI_div_20 = (float) Math.PI / 20;
	public static final float PI_div_40 = (float) Math.PI / 40;

	public static final float TO_RAD = PI / 180.f;
	public static final float TO_DEG = 180.f / PI;

	public static final int FINESSE = 720;

	private static final float[] SIN = new float[FINESSE];
	private static final float[] COS = new float[FINESSE];

	public final static double TRIG_STEP = TAU / FINESSE;
	public final static double INV_TRIG_STEP = FINESSE / TAU;
	static
	{
		double angle = 0;
		for( int idx = 0; idx < FINESSE; idx++ )
		{
			SIN[idx] = (float) Math.sin(angle);
			COS[idx] = (float) Math.cos(angle);
			angle += TRIG_STEP;
		}
	}

	public static float SIN( final double angle )
	{
		return SIN[toTrigoIndex(angle)];
	}

	public static float COS( final double angle )
	{
		return COS[toTrigoIndex(angle)];
	}

	public static double atan2Deg( final double y, final double x )
	{
		return Angles.atan2(y, x) * TO_DEG;
	}

	public static final float atan2DegStrict( final float y, final float x )
	{
		return (float) ( Math.atan2(y, x) * TO_DEG );
	}

	public static final float atan2( double y, double x )
	{
		float add, mul;

		if( x < 0.0f )
		{
			if( y < 0.0f )
			{
				x = -x;
				y = -y;

				mul = 1.0f;
			} else
			{
				x = -x;
				mul = -1.0f;
			}

			add = -3.141592653f;
		} else
		{
			if( y < 0.0f )
			{
				y = -y;
				mul = -1.0f;
			} else
			{
				mul = 1.0f;
			}

			add = 0.0f;
		}

		double invDiv = ATAN2_DIM_MINUS_1 / ( x < y ? y : x );

		int xi = (int) ( x * invDiv );
		int yi = (int) ( y * invDiv );

		return ( atan2[yi * ATAN2_DIM + xi] + add ) * mul;
	}

	private static final int ATAN2_BITS = 7;

	private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
	private static final int ATAN2_MASK = ~( -1 << ATAN2_BITS2 );
	private static final int ATAN2_COUNT = ATAN2_MASK + 1;
	private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);

	private static final float ATAN2_DIM_MINUS_1 = ATAN2_DIM - 1;

	private static final float[] atan2 = new float[ATAN2_COUNT];

	static
	{
		for( int i = 0; i < ATAN2_DIM; i++ )
		{
			for( int j = 0; j < ATAN2_DIM; j++ )
			{
				float x0 = (float) i / ATAN2_DIM;
				float y0 = (float) j / ATAN2_DIM;

				atan2[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
			}
		}
	}

	private final static int toTrigoIndex( final double angle )
	{
		int idx = FastMath.round(angle * INV_TRIG_STEP) % FINESSE;
		if( idx < 0 )
			idx += FINESSE;
		return idx;
	}

	public static double toRadians( final double degrees )
	{
		return degrees * TO_RAD;
	}

	public static double toDegrees( final double radians )
	{
		return radians * TO_DEG;
	}

	/**
	 * Compresses the angle into [-180, 180] range.
	 *
	 * @param angle
	 * @return
	 */
	public static double normalize( final double angle )
	{
		return angle < -PI_div_2 ? angle + TAU : angle > PI_div_2 ? angle - TAU : angle;
	}

	/**
	 * Adjusts the oldAngle in the closest direction to targetAngle by step.
	 *
	 * @param oldAngle
	 * @param targetAngle
	 * @param step
	 * @return
	 */
	public static double stepTo( final double oldAngle, final double targetAngle, final double step )
	{
		double diff = normalize(oldAngle - targetAngle);
		double absDiff = Math.abs(diff);

		return normalize(oldAngle + ( diff < 0 ? 1 : -1 ) * ( absDiff < step ? absDiff : step ));
	}

	/**
	 * Steps the oldAngle towards the targetAngle, making last steps gradually
	 * slowing down, for "softness" effect. A bit slower than the
	 * {@link #stepTo(double, double, double)} operation.
	 *
	 * Reaches target after (abs(targetAngle-oldAngle) - step*softness) + log
	 * b(1/softness) (step*softness)
	 *
	 * @param oldAngle
	 * @param targetAngle
	 * @param step
	 *            initial step size
	 * @param softness
	 * @return
	 */
	public static double stepSoftly( final double oldAngle, final double targetAngle, final double softness )
	{
		double a = normalize(oldAngle);
		double b = normalize(targetAngle);
		double d = b - a;
		if( d < -PI )
		{
			b += TAU;
		}
		if( d > +PI )
		{
			b -= TAU;
		}

		double newAngle = FastMath.ema_smooth(a, b, softness);

		return normalize(newAngle);
	}

	public static double stepSoftlyDeg( final double oldAngle, final double targetAngle, final double softness )
	{
		return stepSoftly(oldAngle * TO_RAD, targetAngle * TO_RAD, softness) * TO_DEG;
	}
}
