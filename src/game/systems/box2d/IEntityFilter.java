package game.systems.box2d;

import com.badlogic.ashley.core.Entity;

public interface IEntityFilter
{
	public boolean accept(Entity entity);
}
