package game.systems.control;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.world.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PickDef implements IComponentDef <PickComponent> 
{
	
	public static final PickDef FIXTURE_PICK = new PickDef(10);
	public static final PickDef DECORATION_PICK = new PickDef(5);
	public static final PickDef CHARACTER_PICK =  new PickDef(100);
	
	
	//@Getter private int type;
	@Getter private int priority;
	
	@Override
	public Class<PickComponent> getComponentClass() { return PickComponent.class; }

	@Override
	public void initComponent(PickComponent component, Entity entity, Level level)
	{
		component.def = this;
		component.entity = entity;
	}
}
