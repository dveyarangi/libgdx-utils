/**
 * 
 */
package game.world.environment.nav;

/**
 * Additional info to tie on the navnode
 *
 */
public class NavNodeDescriptor
{
	public final Object object;

	public final int index;

	/**
	 * @param object
	 * @param index
	 */
	public NavNodeDescriptor( Object object, int index )
	{
		super();
		this.object = object;
		this.index = index;
	}

	/**
	 * @return the object
	 */
	@SuppressWarnings( "unchecked" )
	public <E> E getObject()
	{
		return (E) object;
	}

	/**
	 * @return the index
	 */
	public int getIndex()
	{
		return index;
	}

}
