package game.util.dist;

import game.util.RandomUtil;

/**

 * @author Fima
 */

public interface Distribution
{
	public float eval();

	public static class Gaussian implements Distribution
	{
		float mean, sigma;

		public Gaussian( float mean, float sigma )
		{
			this.mean = mean;
			this.sigma = sigma;
		}

		@Override
		public float eval()
		{
			return RandomUtil.STD(mean, sigma);
		}

	}

	public static class Uniform implements Distribution
	{
		float mean, span;

		public Uniform( float mean, float span )
		{
			this.mean = mean;
			this.span = span;
		}

		@Override
		public float eval()
		{
			return RandomUtil.U(mean, span);
		}

	}

	public static class Point implements Distribution
	{
		float val;

		public Point( float val )
		{
			this.val = val;
		}

		@Override
		public float eval()
		{
			return val;
		}

	}
}
