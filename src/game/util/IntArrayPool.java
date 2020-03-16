package game.util;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;

public class IntArrayPool
{

	IntMap <Pool <int[]>> arraysPool = new IntMap <>();

	//protected abstract int[] newObject(int size);
	
	public int[] obtain(final int size)
	{
		Pool <int[]> sizePool = arraysPool.get(size);
		if( sizePool == null )
		{
			sizePool = new Pool<int[]> () {
				@Override protected int[] newObject()	{ return new int[size];	}
			};
			
			arraysPool.put(size, sizePool);
		}
		
		int [] array = sizePool.obtain();
		return array;
	}
	
	public void free(int [] array)
	{
		Pool <int[]> sizePool = arraysPool.get(array.length);
		sizePool.free(array);
	}
}
