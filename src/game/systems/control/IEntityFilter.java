package game.systems.control;

import com.badlogic.ashley.core.Entity;

public interface IEntityFilter
{
	public boolean accept(Entity entity);
}
