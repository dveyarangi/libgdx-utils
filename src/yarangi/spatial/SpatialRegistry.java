package yarangi.spatial;

import java.util.HashMap;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;


/**
 * Common interface for spatial indexing data structures.
 * 
 * @param <T> stored element type.
 */
@Slf4j
public abstract class SpatialRegistry <K extends ISpatialObject> implements ISpatialSetIndex <K>
{
	
	/**
	 * Id to location mapping, to keep and eye on object updates.
	 * TODO: switch to primitive int key hash (Trove?)
	 */
	private final HashMap <K, AABB> locations = new HashMap <K, AABB> ();

	protected SpatialRegistry(String name)
	{
	}
	
//	private TIntObjectMap <AABB> locations = new TIntObjectHashMap <AABB> ();
	
	/**
	 * @return Number of managed objects.
	 */
	public int size() { return locations.size(); }

	/**
	 * Adds an {@link AABB} box, containing the object.
	 * @param aabb
	 * @param object
	 */
	public void add(K object)
	{
		AABB area = object.getArea().clone();
		addObject(area, object);
		
		locations.put(object, area);
	}
	
	/**
	 * Removes an {@link AABB} box, containing the object.
	 * @param aabb
	 * @param object
	 */
	public K remove(K object)
	{
		Area oldLocation = locations.remove(object);
		if(oldLocation == null)
			throw new IllegalArgumentException("The object [" + object + "] was not added to the indexer.");
		
		K removedObject = removeObject(oldLocation, object);
		AABB.release( (AABB)oldLocation );
		return removedObject;
	}
	
	/**
	 * Updates an {@link AABB} box location.
	 * TODO:OPTIMIZE - too many AABB cloning
	 * @param aabb
	 * @param object
	 */
	public void update(K object, AABB old)
	{
		AABB oldLocation = locations.get(object);
		if(oldLocation == null)
			throw new IllegalArgumentException("The object [" + object + "] is not updateble by the indexer.");
		
		AABB newLocation = object.getArea().clone();
		
		locations.put(object, newLocation);
		updateObject(oldLocation, newLocation, object);
		
		if( old != null )
			old.paste(oldLocation);
		AABB.release(oldLocation);
	}
	
/*	public void update(K object, IAreaChunk chunk)
	{
		
	}*/
	
	/**
	 * Retrieves set of indexed {@link ISpatialObject}s.
	 * Note: The collection is backed up by the indexer and should not be modified. 
	 * @return
	 */
	@Override
	public Set <K> keySet() { return locations.keySet(); }
	
	/**
	 * Adds an {@link AABB} box, containing the object.
	 * @param aabb
	 * @param object
	 */
	protected abstract void addObject(Area area, K object);
	
	/**
	 * Removes an {@link AABB} box, containing the object.
	 * @param aabb
	 * @param object
	 */
	protected abstract K removeObject(Area area, K object);
	
	/**
	 * Updates an {@link AABB} box, containing the object.
	 * @param aabb
	 * @param object
	 */
	protected abstract void updateObject(Area old, Area area, K object);


}