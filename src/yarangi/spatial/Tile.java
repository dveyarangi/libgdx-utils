package yarangi.spatial;


public final class Tile <O> implements ITile <O>
{
	
	/** 
	 * tile index 
	 */
	private final int i, j;
	private final double minx, miny;
	private final double maxx, maxy ;
	
	private O content;

	public Tile(int i, int j, double x, double y, double width, double height)
	{
		this.i = i;
		this.j = j;
		this.minx = x;
		this.miny = y;
		this.maxx = x + width;
		this.maxy = y + height;
	}
	
	public int i() { return i; }
	public int j() { return j; }
	
/*	public Tile(O content, double x, double y)
	{
		
		this.x = x;
		this.y = y;
		
		this.content = content;
	}*/
	
	@Override
	public boolean put(O content)
	{
		if(this.content != content) {
			this.content = content;
			return true;
		}
		
		return false;
	}
	@Override
	public O get()
	{
		return content;
	}

	@Override
	public double getX() { return minx; }

	@Override
	public double getY() { return miny; }
//	
//	public int i() { return i; }
//	
//	public int j() { return j; }

	@Override
	public <T extends ITile<O>> boolean query(ISpatialSensor<O> sensor, int queryId)
	{
		if(content != null)
			return sensor.objectFound( content );
		return false;
	}
	@Override
	public boolean remove(O object)
	{
		if(this.content != null) 
		{
			content = null;
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean overlaps(double xmin, double ymin, double xmax, double ymax)
	{
		return ( (xmax >= minx && xmax <= maxx) ||
			     (xmin >= minx && xmin <= maxx) ||
			     (xmin >= minx && xmax <= maxx) ||
			     (xmin <= minx && xmax >= maxx)    
			  ) && ( 
			     (ymax >= miny && ymax <= maxy) ||
			     (ymin >= miny && ymin <= maxy) ||
			     (ymin >= miny && ymax <= maxy) ||
			     (ymin <= miny && ymax >= maxy)    
			   );
	}

	@Override
	public double getMinX() { return minx; }
	@Override
	public double getMinY() { return miny; }
	@Override
	public double getMaxX() { return maxx; }
	@Override
	public double getMaxY() { return maxy; }

	@Override
	public Area getArea()
	{
		return null;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("Tile [ idx: (").append( i ).append( "," ).append(j).append(")")
			.append(", minx: ").append(minx)
			.append(", miny: ").append(miny)
			.append(", maxx: ").append(maxx)
			.append(", maxy: ").append(maxy)
			.append("]").toString();
			
	}

}
