package game.systems.spatial;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import game.systems.DescendantsComponent;
import game.systems.lifecycle.LifecycleComponent;
import game.world.Level;

public class AnchorDef implements ISpatialDef<AnchorComponent>
{
	public float x, y, z, a, r;

	public int parentId;

	public AnchorDef( int parentId, final float x, final float y, final float z, final float a, final float r )
	{
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
		this.r = r;

		this.parentId = parentId;
	}

	@Override
	public void initComponent( final AnchorComponent anchor, final Entity entity, final Level level )
	{
		ImmutableArray<Entity> entities = level.getEngine().getEntitiesFor(Family.one(DescendantsComponent.class).get());
		for( Entity potentialParent : entities )
		{
			LifecycleComponent parentDef = LifecycleComponent.get(potentialParent);
			if( parentDef.id == parentId )
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
	public float z()
	{
		return z;
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
	public void z( final float z )
	{
		this.z = z;
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
