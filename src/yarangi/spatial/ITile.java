package yarangi.spatial;

public interface ITile <O> extends IAreaChunk
{
	/**
	 * Adds an object to tile
	 * @param cell
	 * @param object
	 * @return true, if the tile contents were changed.
	 */	
	public boolean put(O o);
	
	public O get();
	
	/**
	 * Query this tile and feeds the results to the sensor.
	 * 
	 * @param cell
	 * @param sensor
	 * @param queryId may be used to mark objects during query execution.
	 * @return true, if the query should stop after this tile
	 */
	public abstract <T extends ITile<O>> boolean query(ISpatialSensor<O> sensor, int queryId);
//	public abstract boolean query(ISpatialSensor<ITile<O>, O> sensor, int queryId);
	
	/**
	 * Removes an object from this tile
	 * @param cell
	 * @param object
	 * @return
	 */
	public abstract boolean remove(O object);

}
