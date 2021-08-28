package game.systems.lifecycle;

import com.badlogic.ashley.core.Entity;

import game.systems.EntityPrefab;
import game.systems.IComponentDef;
import game.systems.movement.IMovementDef;
import game.systems.rendering.RendererDef;
import game.systems.spatial.SpatialDef;
import game.util.dist.Distribution;
import game.world.Level;

public class BirthTrailDeathDef implements IComponentDef <LifeAuraComponent>
{

	public static class Aspect
	{
		public Distribution sizeDist, angleDist;
		public EntityPrefab def;

		public Aspect( String type, IMovementDef movementDef,
				RendererDef renderingDef, float lifeDuration, Distribution sizeDist, Distribution angleDist )
		{
			def = new EntityPrefab();
			def.addDef( new LifecycleDef( type, null, lifeDuration ));
			def.addDef( new SpatialDef(0, 0, 0, 0, 0) );
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
	public static final String BIRTH_ID = "birth-effect";
	public void defineBirth( IMovementDef<?> movementDef,
			RendererDef<?> renderingDef, float lifeDuration, Distribution sizeDist, Distribution angleDist )
	{
		birth = new Aspect(BIRTH_ID, movementDef, renderingDef, lifeDuration, sizeDist, angleDist);
	}

	public static final String TRAIL_ID = "trail-effect";
	public void defineTrail( IMovementDef<?> movementDef,
			RendererDef<?> renderingDef, float lifeDuration, Distribution sizeDist, Distribution angleDist,
			float trailInterval,
			float trailTotalDuration )
	{
		trail = new Aspect(TRAIL_ID, movementDef, renderingDef, lifeDuration, sizeDist, angleDist);

		this.trailInterval = trailInterval;
		this.trailTotalDuration = trailTotalDuration;
	}

	public static final String DEATH_ID = "death-effect";
	public void defineDeath( IMovementDef<?> movementDef,
			RendererDef<?> renderingDef, float lifeDuration, Distribution sizeDist, Distribution angleDist )
	{
		death = new Aspect(DEATH_ID, movementDef,
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
