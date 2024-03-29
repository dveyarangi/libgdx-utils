package game.systems;

import lombok.Getter;
import lombok.Setter;

public abstract class EntityDef
{
	@Getter protected String name;

	/**
	 * Full name of this entity within group
	 */
	@Getter @Setter protected String path;

//	@Getter List <IComponentDef<?>> components;
	
	public abstract <E extends IComponentDef<?>> E getDef(Class <E> class1);
}
