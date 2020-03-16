package game.systems.lifecycle;

import game.systems.IComponentDef;
import game.systems.EntityDef;
import game.systems.movement.IMovementDef;
import game.systems.rendering.RendererDef;
import game.systems.spatial.SpatialDef;
import game.util.dist.Distribution;
import game.world.Level;

import com.badlogic.ashley.core.Entity;

public class BirthTrailDeathDef implements IComponentDef <LifeAuraComponent>
{
	public static class Aspect
	{
		public Distribution sizeDist, angleDist;
		public EntityDef def;

		public Aspect( IMovementDef movementDef,
				RendererDef renderingDef, float lifeDuration, Distribution sizeDist, Distribution angleDist )
		{
			def = new EntityDef();
			def.addDef( new LifecycleDef( lifeDuration ));
			def.addDef( new SpatialDef(0, 0, 0, 0) );
			def.addDef( movementDef );
			def.addDef( renderingDef );
			this.sizeDist = sizeDist;
			this.angleDist = angleDist;
		}

	}

	public Aspect birth;
	public Aspect trail;
	public Aspect death;
	public float trailInterval;
	/**
	 * Time for which to spawn trail after entity creation
	 */
	public float trailTotalDuration = Float.POSITIVE_INFINITY;

	public BirthTrailDeathDef()
	{

	}

	public void defineBirth( IMovementDef movementDef,
			RendererDef renderingDef, float lifeDuration, Distribution sizeDist, Distribution angleDist )
	{
		birth = new Aspect(movementDef, renderingDef, lifeDuration, sizeDist, angleDist);
	}

	public void defineTrail( IMovementDef movementDef,
			RendererDef renderingDef, float lifeDuration, Distribution sizeDist, Distribution angleDist,
			float trailInterval,
			float trailTotalDuration )
	{
		trail = new Aspect(movementDef, renderingDef, lifeDuration, sizeDist, angleDist);

		this.trailInterval = trailInterval;
		this.trailTotalDuration = trailTotalDuration;
	}

	public void defineDeath( IMovementDef movementDef,
			RendererDef renderingDef, float lifeDuration, Distribution sizeDist, Distribution angleDist )
	{
		death = new Aspect(movementDef,
				renderingDef, lifeDuration, sizeDist, angleDist);
	}

	@Override
	public Class<LifeAuraComponent> getComponentClass() { return LifeAuraComponent.class; }

	@Override
	public void initComponent( LifeAuraComponent component, Entity entity, Level level )
	{
		component.def = this;
	}

}
