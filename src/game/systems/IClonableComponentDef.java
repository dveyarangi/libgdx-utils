package game.systems;

import com.badlogic.ashley.core.Component;

public interface IClonableComponentDef<C extends Component> extends IComponentDef <C>
{
	public IClonableComponentDef<C> copy();
}
