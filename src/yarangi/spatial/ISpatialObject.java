package yarangi.spatial;

/**
 * Interface for object with shape.
 */
public interface ISpatialObject 
{

	/**
	 * Object volume.
	 * @return
	 */
	public AABB getArea();
	
	public boolean isStatic();


}
