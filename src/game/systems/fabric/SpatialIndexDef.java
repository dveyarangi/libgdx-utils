package game.systems.fabric;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.systems.sensor.SensorCategory;
import game.world.Level;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * If entity has this component,  {@link SpatialFabric} will add it and allow picking and querying
 * @author Fima
 */
@AllArgsConstructor
@NoArgsConstructor
public class SpatialIndexDef implements IComponentDef<SpatialIndexComponent>
{
	public SensorCategory [] categories;
	public boolean isStatic = true;

	@Override
	public Class<SpatialIndexComponent> getComponentClass() { return SpatialIndexComponent.class; }

	@Override
	public void initComponent(SpatialIndexComponent component, Entity entity, Level level)
	{
		component.isStatic = this.isStatic;
		
		var fabric = level.getEngine().getSystem(SpatialFabric.class);
		component.categories = fabric.getCategorySet(categories);
		component.init(entity);
	}
}
