package game.systems.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

import game.systems.EntityDef;
import game.world.IPickProvider;
import lombok.Setter;

/**
 * Picks a unit using box2d query
 *
 * @author Fima
 */
public class Box2DPicker implements IPickProvider
{
	private World world;

	private Entity pickedEnitity;
	
	/**
	 * can be set to filter results of picking query
	 */
	@Setter private IEntityFilter entityFilter;

	public Box2DPicker( World world )
	{
		this.world = world;
	}
	

	/** This is callback for mouse picking test */
	QueryCallback pickingCallback = new QueryCallback()
	{
		@Override
		public boolean reportFixture( Fixture fixture )
		{
			if( fixture.isSensor() )
				return true;

			Entity testedEntity = (Entity) fixture.getUserData();
			if(testedEntity == null)
				return true;
			EntityDef def = EntityDef.get( testedEntity );
			if(def.isPickable() && (entityFilter == null || entityFilter.accept(testedEntity)))
			{
				pickedEnitity = testedEntity;
				return false;
			}
			// terminating after first:
			// TODO: retrieve list?
			return true;
		}

	};

	@Override
	public Entity pick( float x, float y, float pickRadius )
	{
		pickedEnitity = null;
		// querying rectangle using box2d
		world.QueryAABB(pickingCallback,
				x - pickRadius, y - pickRadius,
				x + pickRadius, y + pickRadius);
		return pickedEnitity;
	}
}
