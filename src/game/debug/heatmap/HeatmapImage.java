package game.debug.heatmap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import game.debug.Debug;
import game.util.Heightmap;
import game.util.colors.Colormap;
import game.util.colors.ColormapConf;
import game.util.colors.Colormaps;
import lombok.extern.slf4j.Slf4j;
import yarangi.math.Pair;

@Slf4j
public class HeatmapImage
{

	static ColormapConf DEFAULT_COLORMAP;
	static {
		try (InputStream in = HeatmapImage.class.getResourceAsStream("Chromatic")){
			DEFAULT_COLORMAP = Colormaps.read(new InputStreamReader(in));
		} catch(IOException e) { throw new RuntimeException(e);	}
	}
	public static void makeImage(float [][] heatmap, int scale, String path, Array<HeatmapLayer> layers )
	{
		makeImage(heatmap, scale, DEFAULT_COLORMAP, Heightmap.minmax(heatmap), path, layers);
	}
	public static void makeImage(float [][] heatmap, int scale, ColormapConf colormap, float min, float max, String path, Array<HeatmapLayer> layers )
	{
		makeImage(heatmap, scale, colormap, new Pair<>(min, max), path, layers);
	}
	public static void makeImage(float [][] heatmap, int scale, ColormapConf colormap, Pair<Float> minmax, String path, Array<HeatmapLayer> layers )
	{

		int w = heatmap[0].length;
		int h = heatmap.length;


		Colormap cm = new Colormap(minmax, colormap);


		BufferedImage image = new BufferedImage(w*scale, h*scale, BufferedImage.TYPE_INT_ARGB);
	
		// draw heatmap
		Color color = new Color();
		for(int x = 0; x < w*scale; x ++)
			for(int y = 0; y < h*scale; y ++)
			{
				cm.toColor(heatmap[x/scale][y/scale], color);
				image.setRGB(x, y, Color.argb8888(color));
			}
	
		// add layers
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		if( layers != null)
			for(HeatmapLayer layer : layers)
				layer.render(g2d, scale);
	
//		BufferedImage image = new BufferedImage(w*scale, h*scale, BufferedImage.TYPE_INT_ARGB);
		// flip vertically
		for(int x = 0; x < w*scale; x ++)
			for(int y = 0; y < h*scale/2; y ++)
			{
				int y2 = h*scale-y-1;
				int rgb = image.getRGB(x,y);
				image.setRGB(x, y, image.getRGB(x,y2));
				image.setRGB(x,y2,rgb);
			}
		
		File outfile =  new File(path);
		try
		{
			Debug.log(2, "DEBUG", "Writing debug heatmap image to " + outfile.getAbsolutePath() + "...");
			ImageIO.write(image, "png", outfile);
		}
		catch (IOException e) { Debug.error("Failed to write heatmap debug image to " + outfile.getAbsolutePath()); }
	}

}
