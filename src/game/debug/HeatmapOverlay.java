package game.debug;

import com.badlogic.gdx.graphics.Color;

import game.util.Heightmap;
import game.util.colors.Colormap;
import game.util.colors.ColormapConf;
import yarangi.math.Pair;

public class HeatmapOverlay extends TileGridOverlay
{

	private float[][] heatmap;
	private Colormap colormap;

	public HeatmapOverlay(float [][] heatmap, ColormapConf colormap)
	{
		super(heatmap.length, heatmap[0].length);

		this.heatmap = heatmap;

		Pair <Float> minmax = Heightmap.minmax(heatmap);
		this.colormap = new Colormap(minmax, colormap);
	}

	@Override
	protected Color getColor(int x, int y, Color out)
	{
		float value = heatmap[x][y];
		colormap.toColor(value, out);
		return out;
	}

	@Override
	protected float getValue(int x, int y)
	{

		return heatmap[x][y];
	}

}
