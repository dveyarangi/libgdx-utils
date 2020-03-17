package game.world;

import com.badlogic.gdx.math.Matrix4;

import game.systems.box2d.IEntityFilter;

/**
 * This interface for entity interactions with world, user and other entities
 *
 * @author Fima
 */
public interface IFabric
{

	IPickProvider createPickProvider();

	public void debugDraw(Matrix4 proj);

	public boolean hasLineOfSight(float sx, float sy, float tx, float ty, IEntityFilter filter);

}
