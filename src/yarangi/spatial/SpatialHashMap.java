package yarangi.spatial;

import com.badlogic.gdx.utils.ObjectSet;

import lombok.extern.slf4j.Slf4j;
import yarangi.math.FastMath;

/**
 * Straightforward implementation of spatial hash map.
 * 
 * Note: cannot be used in multi-threaded environment, due to passId optimization (and lack of any type of synchronization).
 * TODO: refactor to extend {@link GridMap}
 * @param <O>
 */
@Slf4j
public class SpatialHashMap <O extends ISpatialObject> extends SpatialRegistry<O>
{
	/**
	 * buckets array.
	 * TODO: hashmap is slow!!!
	 */
	protected ObjectSet <O> [] map;
	
	/**
	 * number of buckets
	 */
	private int size;
	
	/**
	 * dimensions of area this hashmap represents
	 */
	private int width, height;

	/**
	 * size of single hash cell
	 */
	private float cellSize; 
	
	/** 
	 * 1/cellSize, to speed up some calculations
	 */
	private double invCellsize;
	/** 
	 * cellSize/2
	 */
	private double halfCellSize;
	/**
	 * hash cells amounts 
	 */
	private int halfGridWidth, halfGridHeight;
	
	/**
	 * Used by query methods to mark tested objects and avoid result duplication;
	 * thusly permits only single threaded usage. 
	 */
	private int passId;
	
	/**
	 * 
	 * @param size amount of buckets
	 * @param cellSize bucket spatial size
	 * @param width covered area width
	 * @param height covered area height
	 */
	@SuppressWarnings("unchecked")
	public SpatialHashMap(String name, int size, float cellSize, int width, int height)
	{
		
		super(name);
		
		this.size = size;
		
		this.width = width;
		this.height = height;
		this.cellSize = cellSize;
		this.invCellsize = 1. / this.cellSize;
		this.halfGridWidth = (int)(width/2/cellSize);
		this.halfGridHeight = (int)(height/2/cellSize);
		this.halfCellSize = cellSize/2.;
		map = new ObjectSet[size];
		for(int idx = 0; idx < size; idx ++)
		{
			map[idx] = new ObjectSet <O> ();
			map[idx].iterator(); // GC optimization: preallocate iterators
		}
		
		log.trace( "Creating spatial hashmap [" + width + "x" + height + "] with [" + size + "] bins.");
	}
	
	/**
	 * TODO: Optimizing constructor
	 * @param width
	 * @param height
	 * @param averageAmount
	 */
	public SpatialHashMap(String name, int width, int height, int averageAmount)
	{
		super(name);
		throw new IllegalStateException("Not implemented yet.");
	}
	

	/**
	 * @return width of the area, covered by this map
	 */
	@Override
	public final float getHeight() { return height; }

	/**
	 * @return height of the area, covered by this map
	 */
	@Override
	public final float getWidth() { return width; }

	/**
	 * @return size (height and width) of a single cell
	 */
	public final double getCellSize() { return cellSize; }
	
	/**
	 * Retrieves content of the bucket that holds the contents of (x,y) cell.
	 * Result may contain data from other cells as well.
	 * @param x
	 * @param y
	 * @return
	 */
	public final ObjectSet <O> getBucket(int x, int y)
	{
		return map[hash(x, y)];
	}
	
	/**
	 * @return buckets number
	 */
	public final int getBucketCount() { return size; }

	/**
	 * Calculates spatial hash value.
	 * TODO: closure? %)
	 * @param x cell x cell coordinate (can range from -halfWidth to halfWidth)
	 * @param y cell y cell coordinate (can range from -halfHeight to halfHeight)
	 * @return
	 */
	protected final int hash(int x, int y)
	{
		return (x+halfGridWidth)*width + (y+halfGridHeight);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addObject(Area area, O object) 
	{
		if(object == null)
			throw new NullPointerException();

		AABB aabb = (AABB) area;
		iterateOverAABB( aabb.getCenterX(), aabb.getCenterY(), aabb.getRX(), aabb.getRY(), 1, null, object );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected O removeObject(Area area, O object) 
	{
		AABB aabb = (AABB) area;
		iterateOverAABB( aabb.getCenterX(), aabb.getCenterY(), aabb.getRX(), aabb.getRY(), 2, null, object );
		
		return object;
	}

	/**
	 * {@inheritDoc}
	 * TODO: not efficient for large polygons:
	 */
	@Override
	protected void updateObject(Area old, Area area, O object) 
	{
		removeObject(old, object);
		addObject(area, object);
	}

	/**
	 * {@inheritDoc}
	 * TODO: slow
	 */
/*	@Override
	public ISpatialSensor <O> query(ISpatialSensor <O> sensor, Area area)
	{
		if(area == null)
			throw new IllegalArgumentException("Area cannot be null.");
		
		queryingConsumer.setSensor( sensor );
		queryingConsumer.setQueryId(getNextPassId());
		area.iterate( cellSize, queryingConsumer );

		return sensor;
	}*/
	
	public int iterateOverAABB( double cx, double cy, double rx, double ry, int odeToJava, ISpatialSensor <O> sensor, O object)
	{
		double minx = cx - rx;
		double miny = cy - ry;
		double maxx = cx + rx;
		double maxy = cy + ry;
		
		
		int minIdxx = Math.max(toGridIndex(cx-rx), -halfGridWidth);
		int minIdxy = Math.max(toGridIndex(cy-ry), -halfGridHeight);
		int maxIdxx = Math.min(toGridIndex(cx+rx),  halfGridWidth);
		int maxIdxy = Math.min(toGridIndex(cy+ry),  halfGridHeight);
		
		int currx, curry;
		int passId = getNextPassId();
		int hash;
		
		ObjectSet <O> cell;
		AABB objectArea;
		
		found: for(currx = minIdxx; currx <= maxIdxx; currx ++)
			for(curry = minIdxy; curry <= maxIdxy; curry ++)
			{
//				System.out.println(minx + " " + miny + " " + maxx + " " + maxy);
				hash = hash(currx, curry);
				//System.out.println(hash);
				if(hash < 0)
					continue;
				cell = map[hash];
				if(odeToJava == 1) {
					cell.add(object);
					
				} else
				if(odeToJava == 2) {
					cell.remove( object );
				}
				if(odeToJava == 0) {
				
				for(O obj : cell)
				{

					if(!(obj.getArea() instanceof AABB))
						continue; // TODO: too silent.
					
					objectArea = obj.getArea();
					if(objectArea.getPassId() == passId)
						continue;
					if(objectArea.overlaps( minx, miny, maxx, maxy ))
						if(sensor.objectFound(obj))
							break found;
					objectArea.setPassId( passId );
				}			
			}
		}
		
		return 42;
	}
	
	public ISpatialSensor <O> queryAABB(ISpatialSensor <O> sensor, AABB aabb)
	{
		return queryAABB( sensor, (float)aabb.getCenterX(), (float)aabb.getCenterY(), (float)aabb.getRX(), (float)aabb.getRY() );
	}
	@Override
	public ISpatialSensor <O> queryAABB(ISpatialSensor <O> sensor, double cx, double cy, double rx, double ry)
	{
		iterateOverAABB( cx, cy, rx, ry, 0, sensor, null );
		
		return sensor;

	}

	protected final int getNextPassId()
	{
		return ++passId;
	}
	
	public final int toGridIndex(double value)
	{
		return FastMath.round(value * invCellsize);
	}
	
	public final boolean isInvalidIndex(int x, int y)
	{
		return (x < -halfGridWidth || x > halfGridWidth || y < -halfGridHeight || y > halfGridHeight); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ISpatialSensor <O> queryRadius(ISpatialSensor <O> sensor, double x, double y, double radius)
	{
		// TODO: spiral iteration, remove this root calculation:
		double radiusSquare = radius*radius;
		int minx = Math.max(toGridIndex(x-radius), -halfGridWidth);
		int miny = Math.max(toGridIndex(y-radius), -halfGridHeight);
		int maxx = Math.min(toGridIndex(x+radius),  halfGridWidth);
		int maxy = Math.min(toGridIndex(y+radius),  halfGridHeight);
		int passId = getNextPassId();
//		O object;
		
//		System.out.println("dim: " + minx + " " + maxx + " " + miny + " " + maxy + "area size: " + (maxx-minx)*(maxy-miny));
		// removing the object from all overlapping buckets:
		ObjectSet <O> cell;
		for(int tx = minx; tx <= maxx; tx ++)
			for(int ty = miny; ty <= maxy; ty ++)
			{
				cell = map[hash(tx, ty)];
				
				double distanceSquare = FastMath.powOf2(x - tx*cellSize) + FastMath.powOf2(y - ty*cellSize);
				if(radiusSquare < distanceSquare)
					continue;
				
//				System.out.println(aabb.r+radius + " : " + Math.sqrt(distanceSquare));
				
				// TODO: make it strictier:
				for(O object : cell)
				{
					if(object.getArea().getPassId() == passId)
						continue;
					
//					double distanceSquare = FastMath.powOf2(x - chunk.getX()) + FastMath.powOf2(y - chunk.getY());
					
//					System.out.println(aabb.r+radius + " : " + Math.sqrt(distanceSquare));
					
					// TODO: make it strictier:
//					if(radiusSquare >= distanceSquare)
						if(sensor.objectFound(object/*, distanceSquare*/))
							break;
					
					object.getArea().setPassId( passId );
				}

			}
		
		return sensor;
	}
	
	@Override
	public final ISpatialSensor <O> queryLine(ISpatialSensor <O> sensor, double ox, double oy, double dx, double dy)
	{
		int currGridx = toGridIndex(ox);
		int currGridy = toGridIndex(oy);
		double tMaxX, tMaxY;
		double tDeltaX, tDeltaY;
		int stepX, stepY;
		if(dx > 0)
		{
			tMaxX = ((currGridx*cellSize + halfCellSize) - ox) / dx;
			tDeltaX = cellSize / dx;
			stepX = 1;
		}					
		else
		if(dx < 0)
		{
			tMaxX = ((currGridx*cellSize - halfCellSize) - ox) / dx;
			tDeltaX = -cellSize / dx;
			stepX = -1;
		}
		else { tMaxX = Double.MAX_VALUE; tDeltaX = 0; stepX = 0;}
		
		if(dy > 0)
		{
			tMaxY = ((currGridy*cellSize + halfCellSize) - oy) / dy;
			tDeltaY = cellSize / dy;
			stepY = 1;
		}
		else
		if(dy < 0)
		{
			tMaxY = ((currGridy*cellSize - halfCellSize) - oy) / dy;
			tDeltaY = -cellSize / dy;
			stepY = -1;
		}
		else { tMaxY = Double.MAX_VALUE; tDeltaY = 0; stepY = 0;}
		
		// marks entity area to avoid reporting entity multiple times
		int passId = getNextPassId();
		ObjectSet <O> cell;
		while(tMaxX <= 1 || tMaxY <= 1)
		{
			if(tMaxX < tMaxY)
			{
				tMaxX += tDeltaX;
				currGridx += stepX;
			}
			else
			{
				tMaxY += tDeltaY;
				currGridy += stepY;
			}
			cell = map[hash(currGridx, currGridy)];
			for(O object : cell)
			{
				if(object.getArea().getPassId() == passId)
					continue;
				
				AABB aabb = object.getArea();
				if(aabb.crosses(ox, oy, dx, dy))
					if(sensor.objectFound(object))
						break;
				
				object.getArea().setPassId( passId );
			}	
		}		
		
		return sensor;
	}

	private interface IObjectConsumer <T> extends IChunkConsumer
	{
		public void setObject(T object);
	}
	


}
