package yarangi.spatial;

import yarangi.math.FastMath;

/**
 * Encapsulates the functionality shared between various grid structures.
 * 
 * Note: cannot be used in multi-threaded environment, due to passId optimization (and lack of any type of synchronization).
 *
 * @param <K>
 */
public abstract class GridMap <T extends ITile<O>, O> implements IGrid <T>
{
	/**
	 * data array. It is references using {@link #at(int, int)} method.
	 */
	protected T [] map;
	
	/**
	 * dimensions of area this grid represents
	 */
	private final float width, height;
	
	/**
	 * edges of represented area
	 */
	private final float minX, minY, maxX, maxY;

	/**
	 * size of single grid cell
	 */
	private final int cellSize; 
	
	/** 
	 * 1/cellSize, to speed up some calculations
	 */
	private final float invCellsize;
	
	/** 
	 * cellSize/2
	 */
	private final float halfCellSize;
	
	/**
	 * grid dimensions 
	 */
	private final int gridWidth, gridHeight;
	
	/**
	 * grid dimensions 
	 */
	private final int halfGridWidth, halfGridHeight;
	
	/**
	 * Used by query methods to mark tested objects and avoid result duplication;
	 * thusly permits only single threaded usage. 
	 * TODO: still can be made synchronous by introducing a racing condition semaphore
	 */
	private int passId;
	
	/**
	 * Customizable handler for tiles that had been messed with.
	 */
	protected IGridListener <T> listener;
	
	/**
	 * 
	 * @param size amount of buckets
	 * @param cellSize bucket spatial size
	 * @param width covered area width
	 * @param height covered area height
	 */
	public GridMap(int cellSize, float width, float height)
	{
		
		this.width = width;
		this.height = height;
		this.cellSize = cellSize;
		this.invCellsize = 1f / this.cellSize;
		this.gridWidth = FastMath.round(width/cellSize);
		this.gridHeight = FastMath.round(height/cellSize);
		this.halfGridWidth = gridWidth/2;
		this.halfGridHeight = gridHeight/2;
		
		this.halfCellSize = cellSize/2f;
		

		minX = (float)Math.ceil( -width/2f);
		maxX = (float)Math.floor( width/2f);
		minY = (float)Math.ceil(-height/2f);
		maxY = (float)Math.floor(height/2f);
		
		map = createMap( cellSize, gridWidth, gridHeight );

	}

	/**
	 * Creates an empty grid cell.
	 * @param x - cell reference x
	 * @param y - cell reference y
	 * @return
	 */
	protected abstract T createEmptyCell(int i, int j, double x, double y);
	
	/**
	 * Create a grid array. Array indexes must be consistent with
	 * {@link #indexAtCell(int, int)} implementation.
	 * @param cellSize
	 * @param width
	 * @param height
	 * @return
	 */
	protected abstract T [] createMap(int cellSize, int width, int height);
	
	/**
	 * Calculates {@link GridMap#map} index for cell index.
	 * @param i cell x cell coordinate (from 0 to gridWidth-1)
	 * @param j cell y cell coordinate (from 0 to gridHeight-1)
	 * @return
	 */
	protected abstract int indexAtTile(int i, int j);
	
	/**
	 * Converts model coordinates to grid array index.
	 * @param x
	 * @param y
	 * @return
	 */
	protected int indexAtCoord(double x, double y)
	{
		int i = toGridXIndex(x);
		int j = toGridYIndex(y);
		if(i < 0 || i >= gridWidth || j < 0 || j >= gridHeight)
			throw new IllegalArgumentException("Invalid grid coordinate [" + x + ", " + y + "].");
		return indexAtTile(i, j);
	}

	/**
	 * Converts a model x coordinate to cell dimensional index.
	 * @param value
	 * @return
	 */
	public final int toGridXIndex(double x)
	{
		return FastMath.floor(invCellsize * (float)x ) + halfGridWidth;
	}
	
	/**
	 * Converts a model x coordinate to cell dimensional index.
	 * @param value
	 * @return
	 */
	public final int toLowerGridXIndex(double x)
	{
		return FastMath.floor(invCellsize * (float)x ) + halfGridWidth;
	}
	/**
	 * Converts a model x coordinate to cell dimensional index.
	 * @param value
	 * @return
	 */
	public final int toHigherGridXIndex(double x)
	{
		return FastMath.ceil(invCellsize * (float)x ) + halfGridWidth;
	}
	
	/**
	 * Converts x grid index to model coordinate
	 * @param i
	 * @return
	 */
	public final double toXCoord(int i)
	{
		return (i-halfGridWidth) * cellSize;
	}
	/**
	 * Converts a model y coordinate to cell dimensional index.
	 * @param value
	 * @return
	 */
	public final int toGridYIndex(double y)
	{
//		System.out.println(x + " " + x*invCellsize + " " + FastMath.round(x * invCellsize) + halfGridHeight);
		return FastMath.floor(invCellsize * (float)y ) + halfGridHeight;
	}
	/**
	 * Converts a model x coordinate to cell dimensional index.
	 * @param value
	 * @return
	 */
	public final int toLowerGridYIndex(double y)
	{
		return FastMath.floor(invCellsize * (float)y ) + halfGridHeight;
	}
	/**
	 * Converts a model x coordinate to cell dimensional index.
	 * @param value
	 * @return
	 */
	public final int toHigherGridYIndex(double y)
	{
		return FastMath.ceil(invCellsize * (float)y ) + halfGridHeight;
	}
	
	/**
	 * Converts y grid index to model coordinate
	 * @param i
	 * @return
	 */
	public final double toYCoord(int j)
	{
		return (j - halfGridHeight) * cellSize;
	}
	
	/** 
	 * calculates lower coord value for tile row that contains specified value 
	 */
	public final double toLowerCoord(double x)
	{
		return FastMath.toGrid( x, cellSize );
	}
	
	/**
	 * Creates an id for query procedure. 
	 * @return
	 */
	protected final int createNextQueryId() { return ++passId; }
	
	/**
	 * @return width of the area, covered by this map
	 */
	public final float getHeight() { return height; }

	/**
	 * @return height of the area, covered by this map
	 */
	public final float getWidth() { return width; }

	
	@Override
	public float getMaxX() { return maxX; }
	@Override
	public float getMinX() { return minX; }
	@Override
	public float getMaxY() { return maxY; }
	@Override
	public float getMinY() { return minY; }
	
	/**
	 * @return size (height and width) of a single cell
	 */
	@Override
	public final int getCellSize() { return cellSize; }
	protected final float getHalfCellSize() { return halfCellSize; }
	@Override
	public final int getGridWidth() { return gridWidth; }
	@Override
	public final int getGridHeight() { return gridHeight; }
	
	/**
	 * Retrieves content of the bucket that holds the contents of (x,y) cell.
	 * @param x
	 * @param y
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public final T getTileByCoord(double x, double y)
	{
		int idx = indexAtCoord(x,y);
		if(idx < 0 || idx >= map.length)
			throw new IllegalArgumentException("Specified coordinate [" + x + "," + y + "] is not inside the grid.");
		return map[idx];
	}
	
	/**
	 * Retrieve contents of cell at specified grid index.
	 * @param x
	 * @param y
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public final T getTileByIndex(int i, int j)
	{
		int idx = indexAtTile( i, j );
		if(idx < 0 || idx >= map.length)
			return null;//throw new IllegalArgumentException( "Invalid tile index [" + i + "," + j + "].");
		T tile = map[idx];
		if(tile == null)
		{
			double x = toXCoord( i );
			double y = toYCoord( j );
			tile = createEmptyCell( i, j, x, y );
			map[idx] = tile;
			setModifiedByIndex(i, j);
		}
		
		return tile;
	}
		
	/**
	 * Retrieves contents of tile at specified index.
	 * @param i
	 * @param j
	 * @return
	 */
	public final O getContentByIndex(int i, int j)
	{
		return getTileByIndex( i, j ).get();
	}
	
	/**
	 * Retrieves contents of tile at specified coordinate.
	 * @param x
	 * @param y
	 * @return
	 */
	public final O getContentByCoord(double x, double y)
	{
		return getTileByCoord( x, y ).get();
	}

	/**
	 * Puts tile contents.
	 * @param x
	 * @param y
	 * @param object
	 */
	public final void put(double x, double y, O object)
	{
		// TODO: dissolve, if hitting not in the cell center?
//		System.out.println(x + " : " + y + " : " + at(x,y));
		int i = toGridXIndex(x);
		int j = toGridYIndex(y);
		int idx = indexAtTile( i, j );
		T tile = map[idx];
		if(tile == null)
		{
			tile = createEmptyCell( i, j, toXCoord( i ), toYCoord( j ) );
			map[idx] = tile;
			setModifiedByIndex(i, j);
		}
		if(tile.put( object ))
			setModifiedByIndex(i, j);
				
	}
	
	public final void remove(double x, double y, O object)
	{
		// TODO: dissolve, if hitting not in the cell center?
//		System.out.println(x + " : " + y + " : " + at(x,y));
		T tile = map[indexAtCoord(x,y)];
		if(tile == null)
			return;
		
		if(tile.remove( object ))
			setModifiedByCoord(x, y);
				
	}
	
	protected final void putAtIndex(int i, int j, T tile)
	{
		// TODO: dissolve, if hitting not in the cell center?
//		System.out.println(x + " : " + y + " : " + at(x,y));
		map[indexAtTile( i, j )] = tile;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addObject(Area area, O object) 
	{
		if(object == null)
			throw new NullPointerException();
		addingConsumer.setObject( object );
		area.iterate( cellSize, addingConsumer );
	}

	/**
	 * {@inheritDoc}
	 */
	public O removeObject(Area area, O object) 
	{
		removingConsumer.setObject( object );
		
		area.iterate( cellSize, removingConsumer );
		
		return object;
	}

	/**
	 * {@inheritDoc}
	 * TODO: not efficient for large polygons:
	 */
	protected void updateObject(Area old, Area area, O object) 
	{
		removeObject(old, object);
		addObject(area, object);
	}
	

	@Override
	public boolean isEmptyAtCoord(float x, float y)
	{
		T tile = getTileByCoord( x, y );
		if(tile == null || tile.get() == null)
			return true;
		return false;
	}

	/**
	 * Adds a cell to modified cells queue.
	 * @param cell
	 */
	public void setModifiedByCoord(double x, double y)
	{
		T tile = getTileByCoord( x, y );
		if(tile == null)
			throw new IllegalArgumentException("No tile at coords [" + x + "," + y + "].");
		fireGridModified( tile );
	}
	
	/**
	 * 
	 * 
	 * @param i
	 * @param j
	 */
	public void setModifiedByIndex(int i, int j)
	{
		T tile = getTileByIndex( i, j );
		if(tile == null)
			throw new IllegalArgumentException("No tile at index [" + i + "," + j + "].");
		fireGridModified( tile );
	}

	/**
	 * Defines a handler for modified tiles; only one is allowed
	 */
	@Override
	public void setModificationListener(IGridListener <T> l) 
	{
		if(listener != null)
			throw new IllegalStateException("Grid modification listener is already set.");
		listener = l; 
	}
	
	/**
	 * Fires cell modification event to listeners and clears modified cells queue.
	 */
	public void fireGridModified(T tile)
	{
		if(tile == null)
			throw new NullPointerException( "Tile cannot be null." );
		if(listener != null)
			listener.tilesModified( tile );
		// resetting modified cells queue:
	}
	
	/**
	 * Queries the specified area for objects; found objects are fed to {@link ISpatialSensor#objectFound(Object)} method.	
	 * @param sensor
	 * @param aabb
	 * @return the same sensor
	 */
	public ISpatialSensor <O> queryAABB(ISpatialSensor <O> sensor, AABB aabb)
	{
		return queryAABB( sensor, aabb.getCenterX(), aabb.getCenterY(), aabb.getRX(), aabb.getRY() );
	}
	
	/**
	 * Queries the specified rectangle for objects; found objects are fed to {@link ISpatialSensor#objectFound(Object)} method.	
	 * @param sensor
	 * @param aabb
	 * @return the same sensor
	 */
	public ISpatialSensor <O> queryAABB(ISpatialSensor <O> sensor, double cx, double cy, double rx, double ry)
	{

		int minIdxx = Math.max(toGridXIndex(cx-rx), 0);
		int minIdxy = Math.max(toGridYIndex(cy-ry), 0);
		int maxIdxx = Math.min(toGridXIndex(cx+rx), gridWidth);
		int maxIdxy = Math.min(toGridYIndex(cy+ry), gridHeight);		
		int tx, ty;
		
		T tile;
		
		int passId = createNextQueryId();
	
		for(tx = minIdxx; tx <= maxIdxx; tx ++)
			for(ty = minIdxy; ty <= maxIdxy; ty ++)
			{
				
				tile = getTileByIndex(tx, ty);

				if(tile != null)
				{
					if(tile.query(sensor, passId))
						return sensor;
				}
			}
		
		return sensor;

	}

	
	/**
	 * Queries the specified circle for objects; found objects are fed to {@link ISpatialSensor#objectFound(Object)} method.	
	 * @param x - circle center x coord
	 * @param y - circle center y coord
	 * @param radius - circle radius
	 * @param aabb
	 * @return the same sensor
	 */
	public final ISpatialSensor <O> queryRadius(ISpatialSensor <O> sensor, double x, double y, double radius)
	{
		// TODO: spiral iteration?
		double radiusSquare = radius * radius;
		int minx = Math.max(toLowerGridXIndex(x-radius-cellSize), 0);
		int miny = Math.max(toLowerGridYIndex(y-radius-cellSize), 0);
		int maxx = Math.min(toLowerGridXIndex(x+radius), gridWidth);
		int maxy = Math.min(toLowerGridYIndex(y+radius), gridHeight);
		int passId = createNextQueryId();
		T tile;
		for(int tx = minx; tx <= maxx; tx ++)
			for(int ty = miny; ty <= maxy; ty ++)
			{
				tile = getTileByIndex(tx, ty);
				
				double distanceSquare0 = FastMath.powOf2(x - toXCoord( tx )) + FastMath.powOf2(y - toYCoord( ty ));
				double distanceSquare1 = FastMath.powOf2(x - toXCoord( tx+1 )) + FastMath.powOf2(y - toYCoord( ty ));
				double distanceSquare2 = FastMath.powOf2(x - toXCoord( tx+1 )) + FastMath.powOf2(y - toYCoord( ty+1 ));
				double distanceSquare3 = FastMath.powOf2(x - toXCoord( tx )) + FastMath.powOf2(y - toYCoord( ty+1 ));
				if(radiusSquare < distanceSquare0 && radiusSquare < distanceSquare1 && radiusSquare < distanceSquare2 && radiusSquare < distanceSquare3)
					continue;
				
				if(tile != null)
					if(tile.query(sensor, passId))
						return sensor;
			}
		
		return sensor;
	}

	public final ISpatialSensor <O> queryLine(ISpatialSensor <O> sensor, double ox, double oy, double dx, double dy)
	{
		int currGridI = toGridXIndex(ox);
		int currGridJ = toGridYIndex(oy);
		double tMaxX, tMaxY;
		double tDeltaX, tDeltaY;
		int stepI, stepJ;
		if(dx > 0)
		{
			tMaxX = ((FastMath.toGrid( ox, cellSize ) + halfCellSize) - ox) / dx;
			tDeltaX = cellSize / dx;
			stepI = 1;
		}					
		else
		if(dx < 0)
		{
			tMaxX = ((FastMath.toGrid( ox, cellSize ) - halfCellSize) - ox) / dx;
			tDeltaX = -cellSize / dx;
			stepI = -1;
		}
		else { tMaxX = Double.MAX_VALUE; tDeltaX = 0; stepI = 0;}
		
		if(dy > 0)
		{
			tMaxY = ((FastMath.toGrid( oy, cellSize ) + halfCellSize) - oy) / dy;
			tDeltaY = cellSize / dy;
			stepJ = 1;
		}
		else
		if(dy < 0)
		{
			tMaxY = ((FastMath.toGrid( oy, cellSize ) - halfCellSize) - oy) / dy;
			tDeltaY = -cellSize / dy;
			stepJ = -1;
		}
		else { tMaxY = Double.MAX_VALUE; tDeltaY = 0; stepJ = 0;}
		
		int passId = createNextQueryId();

//		System.out.println(currGridx + " : " + currGridy + " : " + tMaxX + " : " + tMaxY);
		T tile;
		while(tMaxX <= 1 || tMaxY <= 1)
		{
//			System.out.println("   * " + tMaxX + " : " + tMaxY);
			if(tMaxX < tMaxY)
			{
				tMaxX += tDeltaX;
				currGridI += stepI;
			}
			else
			{
				tMaxY += tDeltaY;
				currGridJ += stepJ;
			}
			tile = getTileByIndex(currGridI, currGridJ);
			if(tile != null)
			{
				if(tile.query(sensor, passId))
					return sensor;
			}
		}		
		
		return sensor;
	}

	private interface IObjectConsumer <T> extends IChunkConsumer
	{
		public void setObject(T object);
	}
	
	private final IObjectConsumer <O> addingConsumer = new IObjectConsumer <O>()
	{
		private O object;
		
		@Override
		public void setObject(O object)
		{
			this.object = object;
		}
		
		@Override
		public boolean consume(IAreaChunk chunk)
		{
			put( chunk.getX(), chunk.getY(), object );
			
			return false;
		}
	};
	
	private final IObjectConsumer <O> removingConsumer = new IObjectConsumer <O>()
	{
		private O object;
		
		@Override
		public void setObject(O object)
		{
			this.object = object;
		}
		
		@Override
		public boolean consume(IAreaChunk chunk)
		{
			remove(chunk.getX(), chunk.getY(), object);
			
			return false;
		}
	};
	
/*	private interface IQueryingConsumer <T, O> extends IChunkConsumer
	{
		public void setSensor(ISpatialSensor <O> sensor);

		public void setQueryId(int nextPassId);
	}*/
	
/*	private final IQueryingConsumer <T, O> queryingConsumer = new IQueryingConsumer <T, O>()
	{
		private ISpatialSensor <O> sensor;
		private int passId;
		
		@Override
		public void setSensor(ISpatialSensor <O> sensor)
		{
			this.sensor = sensor;
		}
		
		@Override
		public void setQueryId(int passId)
		{
			this.passId = passId;
		}

		@Override
		public boolean consume(IAreaChunk chunk)
		{
			T tile = getTileByCoord(chunk.getX(), chunk.getY());
			if(tile != null)
			{
//				System.out.println(chunk.getX() + " : " + chunk.getY());
				return tile.query(sensor, passId);
			}
			return false;
		}
	};*/

}
