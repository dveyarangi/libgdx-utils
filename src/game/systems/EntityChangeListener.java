package game.systems;

import com.badlogic.ashley.core.Entity;

public interface EntityChangeListener
{
	public void entityChanged(Entity entity);
}
