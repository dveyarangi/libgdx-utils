package game.systems.spatial;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import game.systems.DescendantsComponent;
import game.systems.EntityDef;
import game.world.Level;

public class AnchorDef implements ISpatialDef<AnchorComponent>
{
	public float x, y, a, r;

	public EntityDef parent;

	public AnchorDef( final EntityDef parent, final float x, final float y, final float a, final float r )
	{
		super();
		this.x = x;
		this.y = y;
		this.a = a;
		this.r = r;

		this.parent = parent;
	}

	@Override
	public void initComponent( final AnchorComponent anchor, final Entity entity, final Level level )
	{
		ImmutableArray<Entity> entities = level.getEngine().getEntitiesFor(Family.one(DescendantsComponent.class).get());
		for( Entity potentialParent : entities )
		{
			EntityDef parentDef = EntityDef.get(potentialParent);
			if( parentDef == parent )
			{
				anchor.parent = potentialParent;
				anchor.parentSpatial = potentialParent.getComponent(ISpatialComponent.class);
				anchor.x(this.x());
				anchor.y(this.y());
				anchor.a(this.a());
				anchor.r(this.r());
				break;
			}
		}

		if( anchor.parent == null )
			throw new IllegalArgumentException("No parent def for " + entity + ".");

		DescendantsComponent descs = anchor.parent.getComponent(DescendantsComponent.class);
		descs.add(entity);
	}

	@Override
	public float x()
	{
		return x;
	}

	@Override
	public float y()
	{
		return y;
	}

	@Override
	public float a()
	{
		return a;
	}

	@Override
	public float r()
	{
		return r;
	}

	@Override
	public void x( final float x )
	{
		this.x = x;
	}

	@Override
	public void y( final float y )
	{
		this.y = y;
	}

	@Override
	public void a( final float a )
	{
		this.a = a;
	}

	@Override
	public void r( final float r )
	{
		this.r = r;
	}

	@Override
	public Class<AnchorComponent> getComponentClass() { return AnchorComponent.class; }



}
