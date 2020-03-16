package game.util;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.NumberUtils;

public class Colors
{

	static List<Color> colors2;
	static List<Color> colors1;

	public static void init()
	{
		colors2 = readColorFile("colors\\colors2.json");
		colors1 = readColorFile("colors\\colors1.json");

	}

	private static List<Color> readColorFile( String filename )
	{
		FileHandle file = Gdx.files.internal(filename);
		String scores = file.readString();
		Json json = new Json();
		List<Color> colors = json.fromJson(List.class, scores);

		return colors;
	}

	public static Color hsv2rgb1( float h, float s, float v, Color result )
	{
		return hsv2rgb(colors1, h, s, v, result);
	}

	public static Color hsv2rgb2( float h, float s, float v, Color result )
	{
		return hsv2rgb(colors2, h, s, v, result);

	}

	public static Color hsv2rgb( List<Color> colors, float h, float s, float v, Color result )
	{
		while( h < 0 )
			h += 360;
		int hue = ( ( (int) h ) % 360 );

		int idx = hue * colors.size() / 360;

		Color tempColor = colors.get(idx);

		result.set(tempColor.r, tempColor.g, tempColor.b, 1);

		return result;
	}

	public static float toBits( float r, float g, float b, float a )
	{
		int intBits = (int) ( 255 * a ) << 24 | (int) ( 255 * b ) << 16 | (int) ( 255 * g ) << 8 | (int) ( 255 * r );
		return NumberUtils.intToFloatColor(intBits);

	}

	public static float toBits( Color c )
	{
		return toBits(c.r, c.g, c.b, c.a);
	}

	/**
	 * Convert HSV to RGB, writes result into result and returns result
	 * 
	 * @param h
	 *            in range [0. 360]
	 * @param s
	 *            range [0,1]
	 * @param v
	 *            range [0,1]
	 * @param result
	 * @return
	 */
	/*
	 * public static Color hsv2rgb(float h, float s, float v, Color result) {
	 * float hh, p, q, t, ff; int i; float r, g, b;
	 * 
	 * if(s <= 0.0) { // < is bogus, just shuts up warnings r = v; g = v; b = v;
	 * result.set(r, g, b, 1); return result; } if(s > 1) s = 1; if(s < 0) s =
	 * 0; if(v > 1) v = 1; if(v < 0) v = 0; while(h < 0) h += 360; hh = h % 360;
	 * 
	 * 
	 * hh /= 60.0; i = (int)hh; ff = hh - i; p = v * (1.0f - s); q = v * (1.0f -
	 * s * ff); t = v * (1.0f - s * (1.0f - ff));
	 * 
	 * switch(i) { case 0: r = v; g = t; b = p; break; case 1: r = q; g = v; b =
	 * p; break; case 2: r = p; g = v; b = t; break;
	 * 
	 * case 3: r = p; g = q; b = v; break; case 4: r = t; g = p; b = v; break;
	 * case 5: default: r = v; g = p; b = q; break; } result.set(r,g,b,1);
	 * return result; }
	 */

}
