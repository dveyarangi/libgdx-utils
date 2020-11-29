package game.systems.order;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ControlComponent implements Component, Poolable
{
	private static ComponentMapper<ControlComponent> MAPPER = ComponentMapper.getFor(ControlComponent.class);
	public static ControlComponent get( Entity entity ) { return MAPPER.get( entity ); }

	public IOrder order;
	//public Entity entity;
	public boolean isSelected = false;


	@Override
	public void reset()
	{
		order = null;
	}

	public void setOrder( IOrder order )
	{
		this.order = order;
	}
}
