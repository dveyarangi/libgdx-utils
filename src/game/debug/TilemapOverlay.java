package game.debug;

import com.badlogic.gdx.graphics.Color;

import game.util.Heightmap;
import game.util.colors.Colormap;
import game.util.colors.ColormapConf;
import lombok.AllArgsConstructor;
import yarangi.math.Pair;

public class TilemapOverlay extends TileGridOverlay
{
	public interface Provider
	{
		float at(int x, int y);
		int w();
		int h();
		Pair<Float>minmax();
	}

	@AllArgsConstructor
	public static class FloatArray implements Provider
	{
		float [][] array;
		@Override
		public float at(int x, int y){ return array[x][y]; }

		@Override
		public int w() { return array[0].length; }

		@Override
		public int h() { return array.length; }

		@Override
		public Pair<Float> minmax()
		{
			return Heightmap.minmax(array);
		}

	}

	private Provider provider;

	private Colormap colormap;

	public TilemapOverlay(Provider p, ColormapConf colormap)
	{
		super(p.w(), p.h());

		this.provider = p;

		Pair <Float> minmax = p.minmax();
		this.colormap = new Colormap(minmax, colormap);
	}

	public TilemapOverlay(float [][] heatmap, ColormapConf colormap)
	{
		this(new FloatArray(heatmap), colormap);
	}

	@Override
	protected Color getColor(int x, int y, Color out)
	{
		float value = provider.at(x, y);
		colormap.toColor(value, out);
		return out;
	}

	@Override
	protected float getValue(int x, int y)
	{

		return provider.at(x, y);
	}

}
