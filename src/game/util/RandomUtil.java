package game.util;

import java.util.Random;

public class RandomUtil
{
	/**
	 * Seedless singleton for static utility usage
	 */
	private static RandomUtil that = new RandomUtil();

	/**
	 * Random seed: TODO: replace
	 */
	private Random random = new Random();

	/**
	 * Random seed: the real one but not ready yet
	 */
	//private int x = random.nextInt();

	/*
	 * public static int N() { x ^= (x << 17); x ^= (x >> 21); x ^= (x << 7);
	 *
	 * return x; }
	 */

	/**
	 * @return Random integer in [0,n)
	 */
	public static int N ( final int n ) { return that.N_( n ); }
	public        int N_( final int n )
	{
		return random.nextInt(n);
	}

	public static double getRandomDouble( final double d )
	{
		return d * that.random.nextDouble();
	}

	public static float getRandomFloat( final float d )
	{
		return that.R_(d);
	}

	/**
	 * Normal distribution around mean
	 *
	 * @param mean
	 * @param sigma
	 * @return
	 */
	public static float STD ( final float mean, final float sigma ) { return that.STD_(mean, sigma); }
	public        float STD_( final float mean, final float sigma )
	{
		return mean + sigma * (float) random.nextGaussian();
	}

	/**
	 * Normal distribution around mean, absolute value
	 *
	 * @param mean
	 * @param sigma
	 * @return
	 */
	public static float ASTD ( final float mean, final float sigma ) { return that.ASTD_(mean, sigma); }
	public        float ASTD_( final float mean, final float sigma )
	{
		return mean + (float) Math.abs(sigma * random.nextGaussian());
	}

	/**
	 * Normal distribution around mean
	 *
	 * @param mean
	 * @param sigma
	 * @return
	 */
	public static double STD( final double mean, final double sigma )
	{
		return mean + sigma * that.random.nextGaussian();
	}

	/**
	 * Uniform distribution around mean
	 *
	 * @param mean
	 * @param span
	 * @return
	 */
	public static double U( final double mean, final double span )
	{
		return mean + that.random.nextDouble() * 2 * span - span;
	}

	public static float U ( final float mean, final float span ) { return that.U_(mean, span); }
	public        float U_( final float mean, final float span )
	{
		return mean + random.nextFloat() * 2 * span - span;
	}


	/**
	 * Uniform random number from 0 to p
	 *
	 * @param p
	 * @return
	 */
	public static boolean P ( final float p ) { return that.P_(p); }
	public        boolean P_( final float p )
	{
		assert p >= 0 && p <= 1;
		return random.nextFloat() <= p;
	}

	public static float R ( final float d ) { return that.R_(d); }
	public        float R_( final float d )
	{
		return d * random.nextFloat();
	}

	public static float R (final float min, final float max) { return that.R_(min, max); }
	public        float R_(final float min, final float max)
	{
		return (max-min) * random.nextFloat() + min;
	}

	/**
	 * 1/num probability of true
	 *
	 * @param num
	 * @return
	 */
	public static boolean oneOf ( final int num ) { return that.oneOf_(num); }
	public        boolean oneOf_( final int num )
	{
		return this.N_(num) == 0;
	}

	public static int sign() { return that.sign_(); }
	public        int sign_()
	{
		return is() ? 1 : -1;
	}

	public static boolean is () { return that.is_(); }
	public        boolean is_()
	{
		return this.oneOf_(2);
	}

	public static <E> E random( Class<E> e )
	{
		E[] values = e.getEnumConstants();
		int pick = RandomUtil.N(values.length);
		return values[pick];

	}

	@SafeVarargs
	public static <E> E random( E... e )
	{
		int pick = RandomUtil.N(e.length);
		return e[pick];

	}

	public static <E> E pick(float [] dist, E [] elements)
	{
		assert checkDistribution( dist );

		float P = RandomUtil.R(1);
		float acc = 0;
		for(int eidx = 0; eidx < elements.length; eidx ++)
		{
			acc += dist[eidx];
			if(P < acc)
			{
				return elements[eidx];
			}
		}

		throw new IllegalStateException("Invalid distribution");
	}

	private static boolean checkDistribution(float [] dist)
	{
		float sum = 0;
		for(float p : dist)
		{
			assert p >= 0 && p <= 1 : "Invalid value in distribution array";
			sum += p;
		}
		assert sum == 1 : "Sum of distribution values != 1";

		return true;
	}


}
