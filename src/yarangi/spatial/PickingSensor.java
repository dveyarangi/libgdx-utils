package yarangi.spatial;

/**
 * Sensor that picks a single object from the indexer.
 * @param <K> indexer ojbects type
 */
public class PickingSensor <K extends ISpatialObject> implements ISpatialSensor <K> 
{
	/**
	 * Spatial filter helper
	 */
	public enum Mode { 
		ANY, // picks first matching  object in queried area (fastest)
		FITTING, // picks first matching object with mouse coordinates inside its area (finest but may be problematic for small objects) 
		CLOSEST, // picks closest matching object (slowest, needs to pass over all objects in range)
	}
	/**
	 * Object filter
	 */
	private ISpatialFilter <K> filter;
	
	/**
	 * Object picked during last indexer query.
	 */
	private K object;
	
	/**
	 * pick center point
	 */
	private double x, y;
	
	private double minDist = Double.MAX_VALUE;
	
	private Mode mode;
	
	public PickingSensor(double x, double y)
	{
		this(x, y, Mode.ANY, null);
	}
	
	public PickingSensor(double x, double y, Mode mode)
	{
		this(x, y, mode, null);
	}
	
	public PickingSensor(double x, double y, Mode mode, ISpatialFilter <K> filter)
	{
		this.x = x;
		this.y = y;
		this.mode = mode;
		this.filter = filter;
	}
	public PickingSensor(double x, double y, ISpatialFilter <K> filter)
	{
		this(x, y, Mode.ANY, filter);
	}

	@Override
	public boolean objectFound(K object) 
	{
		if(filter == null || filter.accept( object ))
		{
			switch(mode) {
			case ANY:
				this.object = object;
				return true; // instantly terminating
			case FITTING:
				if(object.getArea().contains(x, y)) {
					this.object = object;
					return true; // terminating query
				}
				break; 
			case CLOSEST: // must pass over all objects to determine closest one:
				double dist = (object.getArea().getAnchor().x() - x) * (object.getArea().getAnchor().x() - x) +
				  (object.getArea().getAnchor().y() - y) * (object.getArea().getAnchor().y() - y);
				if(minDist > dist) {
					this.minDist = dist;
					this.object = object;
				}
				break;
			}
		}
		
		return false;
	}
	
	/**
	 * @return Object picked in last indexer query with this sensor. 
	 */
	public K getObject() { return object; }

	@Override
	public void clear()
	{
		object = null;
		minDist = Double.MAX_VALUE;
	}

}
