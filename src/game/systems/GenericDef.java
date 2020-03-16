package game.systems;

import game.world.Level;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class GenericDef <C extends Component> implements IComponentDef <C>
{

	private Class <C> clazz;

	public GenericDef( Class <C> clazz)
	{
		this.clazz = clazz;
	}

	@Override
	public Class<C> getComponentClass() { return clazz;	}

	@Override
	public void initComponent( Component component, Entity entity, Level level )
	{
		// TODO Auto-generated method stub

	}

}
