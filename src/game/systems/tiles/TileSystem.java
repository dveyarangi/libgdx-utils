package game.systems.tiles;

import com.badlogic.ashley.core.EntitySystem;

public abstract class TileSystem extends EntitySystem
{
	public abstract ITile getTileAt(float x, float y);
}
