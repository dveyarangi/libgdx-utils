package yarangi.spatial;

import game.systems.fabric.CategorySet;

public interface ISpatialSensor <O> 
{
	/**
	 * Called on object detection.
	 * @param chunk
	 * @param object
	 * @return true, if the query should stop after this object.
	 */
	public boolean objectFound(O object);
	
	public CategorySet getCategories();
	/**
	 * Resets sensor collections; called by {@link ISpatialIndex} at start of each query.
	 */
	public void clear();
}
