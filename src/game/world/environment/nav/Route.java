package game.world.environment.nav;

import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * base class for routes on nav meshes
 * 
 * @author Ni
 *
 */
public abstract class Route<N extends NavNode> implements Poolable
{
	/**
	 * has another node en route?
	 * 
	 * @return
	 */
	public abstract boolean hasNext();

	/**
	 * get next node if available
	 * 
	 * @return
	 */
	public abstract N curr();

	/**
	 * get next node if available
	 * 
	 * @return
	 */
	public abstract N next();

	/**
	 * recycle this route. <b>MAKE SURE NOT TO KEEP THE REF AFTER CALLING</b>
	 */
	public abstract void recycle();
}
