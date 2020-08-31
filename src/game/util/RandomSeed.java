package game.util;

import java.util.Random;

import lombok.Getter;

public class RandomSeed
{

	private Random random;
	
	@Getter private int seed;
	
	public RandomSeed(int seed)
	{
		this.random = new Random(seed);
	}
	
	public RandomSeed()
	{
		this.random = new Random();
		this.seed = random.nextInt();
		random.setSeed(seed);
	}

	/*
	 * public static int N() { x ^= (x << 17); x ^= (x >> 21); x ^= (x << 7);
	 *
	 * return x; }
	 */

	/**
	 * @return Random integer in [0,n)
	 */
	public  int N ( final int n ) { return random.nextInt(n); }

	public double getRandomDouble( final double d )
	{
		return d * random.nextDouble();
	}

	public float getRandomFloat( final float d )
	{
		return R(d);
	}

	/**
	 * Normal distribution around mean
	 *
	 * @param mean
	 * @param sigma
	 * @return
	 */
	public float STD( final float mean, final float sigma )
	{
		return mean + sigma * (float) random.nextGaussian();
	}
	
	/**
	 * Normal distribution around mean
	 *
	 * @param mean
	 * @param sigma
	 * @return
	 */
	public double STD( final double mean, final double sigma )
	{
		return mean + sigma * random.nextGaussian();
	}

	/**
	 * Normal distribution around mean, absolute value
	 *
	 * @param mean
	 * @param sigma
	 * @return
	 */
	public float ASTD( final float mean, final float sigma )
	{
		return mean + (float) Math.abs(sigma * random.nextGaussian());
	}


	/**
	 * Uniform distribution around mean
	 *
	 * @param mean
	 * @param span
	 * @return
	 */
	public double U( final double mean, final double span )
	{
		return mean + random.nextDouble() * 2 * span - span;
	}

	public float U( final float mean, final float span )
	{
		return mean + random.nextFloat() * 2 * span - span;
	}


	/**
	 * Uniform random number from 0 to p
	 *
	 * @param p
	 * @return
	 */
	public boolean P( final float p )
	{
		assert p >= 0 && p <= 1;
		return random.nextFloat() <= p;
	}

	public float R( final float d )
	{
		return d * random.nextFloat();
	}

	public float R(final float min, final float max)
	{
		return (max-min) * random.nextFloat() + min;
	}

	/**
	 * 1/num probability of true
	 *
	 * @param num
	 * @return
	 */
	public boolean oneOf( final int num )
	{
		return this.N(num) == 0;
	}

	public int sign()
	{
		return is() ? 1 : -1;
	}

	public  boolean is()
	{
		return this.oneOf(2);
	}

	public <E> E random( Class<E> e )
	{
		E[] values = e.getEnumConstants();
		int pick = this.N(values.length);
		return values[pick];

	}

	public <E> E random( E... e )
	{
		int pick = this.N(e.length);
		return e[pick];

	}

	public <E> E pick(float [] dist, E [] elements)
	{
		assert checkDistribution( dist );

		float P = this.R(1);
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

	private boolean checkDistribution(float [] dist)
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
