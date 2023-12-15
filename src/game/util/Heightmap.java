package game.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import yarangi.math.Pair;

public class Heightmap 
{

	static Vector3 dx = new Vector3();
	static Vector3 dy = new Vector3();

	public static Vector3 normalAt(int x, int y, float [][] heightmap, Vector3 out)
	{
		int w = heightmap.length-1;
		int h = heightmap[0].length-1;
		float above = 10f*heightmap[x][ y > 0 ? y-1 : y];
		float below = 10f*heightmap[x][ y < h ? y+1 : y];
		float right = 10f*heightmap[x < w ? x+1 : x][ y];
		float leftt = 10f*heightmap[x > 0 ? x-1 : x][ y];
		dy.set(0, 1, below - above);
		dx.set(1, 0, right - leftt );
		
		out = out.set(dx).crs(dy).nor();//.scl(-1);
		
		return out;
	}
	public static Vector3 normalAt(int x, int y, float [][] heightmap, float [][] xs, float [][] ys, Vector3 out)
	{
		int w = heightmap.length-1;
		int h = heightmap[0].length-1;
		float above = 10f*heightmap[x][ y > 0 ? y-1 : y];
		float below = 10f*heightmap[x][ y < h ? y+1 : y];
		float right = 10f*heightmap[x < w ? x+1 : x][ y];
		float leftt = 10f*heightmap[x > 0 ? x-1 : x][ y];
		dy.set(0, 1, below - above);
		dx.set(1, 0, right - leftt );
		
		out = out.set(dx).crs(dy).nor();//.scl(-1);
		
		return out;
	}
	
	public static Pair <Float> minmax(float [][] heightmap)
	{
		int w = heightmap.length-1;
		int h = heightmap[0].length-1;
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		for(int x = 0; x < w; x ++)
			for(int y = 0; y < h; y ++)
			{
				float val = heightmap[x][y];
				if( val < min )
					min = val;
				if( val > max )
					max = val;
			}
		
		return new Pair<>(min,max);
	}
	
	public static Pair <Array <Vector2>> findExtrema(float [][] hm, int radius)
	{
		int w = hm.length;
		int h = hm[0].length;
		Array <Vector2> minima = new Array <> ();
		Array <Vector2> maxima = new Array <> ();
		for(int x = 0; x < w; x ++)
			for(int y = 0; y < h; y ++)
			{
				float v = hm[x][y];
				boolean isMinimum = true, isMaximum = true;
				for(int i = -radius; i <= radius; i ++)
					for(int j = -radius; j <= radius; j ++)
					{
						if(i == 0 && j == 0)
							continue;
						if( x+i < 0 || x+i >= w || y+j < 0 || y+j >=h )
							continue;
						if(hm[x+i][y+j] <= v)
							isMinimum = false;
						if(hm[x+i][y+j] >= v)
							isMaximum = false;
					}
				if( isMinimum )
					minima.add(new Vector2(x,y));
				if( isMaximum )
					maxima.add(new Vector2(x,y));
					
			}
		
		return new Pair<>(minima, maxima);
	}
}
