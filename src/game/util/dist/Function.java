package game.util.dist;

import game.util.RandomUtil;

public interface Function
{
	public float eval( float x );

	public static class Gaussian implements Function
	{
		private float sigma;
		private float sigmaSqr;
		private float mean;

		public Gaussian( float mean, float sigma )
		{
			this.mean = mean;
			this.sigma = sigma;
			this.sigmaSqr = sigma*sigma;
		}

		@Override
		public float eval( float x )
		{
			return mean + (float) (Math.exp( x * x / ( -2 * sigmaSqr ) ) / ( Math.sqrt( 2 * Math.PI ) * sigma ));
		}
	}

	public static class Uniform implements Function
	{
		float span;

		public Uniform( float span )
		{
			this.span = span;
		}

		@Override
		public float eval( float mean )
		{
			return RandomUtil.U(mean, span);
		}

	}

	public static class Constant implements Function
	{
		float val;

		public Constant( float val )
		{
			this.val = val;
		}

		@Override
		public float eval( float x )
		{
			return val;
		}
	}

	public static class Equivalent implements Function
	{
		public Equivalent()
		{
		}

		@Override
		public float eval( float x )
		{
			return x;
		}
	}

	public static class Linear implements Function
	{
		private float a;
		private float b;

		public Linear( float a, float b )
		{
			this.a = a;
			this.b = b;
		}

		@Override
		public float eval( float x )
		{
			return a * x + b;
		}
	}
}
