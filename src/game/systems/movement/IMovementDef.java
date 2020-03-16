package game.systems.movement;

import game.systems.IComponentDef;


public interface IMovementDef<C extends IMovementComponent> extends IComponentDef<C>
{
	public float getMaxSpeed();

}
