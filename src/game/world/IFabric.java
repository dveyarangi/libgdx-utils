package game.world;

import game.systems.box2d.IEntityFilter;

import com.badlogic.gdx.math.Matrix4;

/**
 * This interface for entity interactions with world, user and other entities
 *
 * @author Fima
 */
public interface IFabric
{

	IPickProvider createPickProvider();

	public void debugDraw(Matrix4 proj);

	public boolean isVisible(float x1, float y1, float x2, float y2, IEntityFilter filter);

}
