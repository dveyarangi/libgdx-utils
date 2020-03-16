/**
 *
 */
package game.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * @author dveyarangi
 *
 */
public class Effect implements Component, Poolable
{


	private Animation animation;

	private boolean isAlive = true;

	private Vector2 position;

	private Vector2 velocity;

	private float angle;

	private float size;

	private float stateTime;

	private float timeModifier;

	public Effect()
	{
		position = new Vector2();
		velocity = new Vector2();
	}
	
	public void init( IEffectDef def, float x, float y, float vx, float vy, final float angle)
	{
		this.reset(); // TODO: double call

		this.animation = def.getAnimation();
		this.size = def.getSize();
		this.timeModifier = def.getTimeModifier();
		
		this.position.set( x, y );
		this.velocity.set( vx, vy );
		
		this.angle = angle;
	}
	
	@Override
	public void reset()
	{
		stateTime = 0;
		isAlive = true;
	}
	
	Vector2 tmp = new Vector2();

	public void update( final float delta )
	{
		stateTime += delta*timeModifier;
		if(stateTime > animation.getAnimationDuration())
		{
			isAlive = false;
		}

		
		position.add( tmp.set(velocity).scl( delta ) );
	}

	public boolean isAlive() { return isAlive; }

	public void draw( final SpriteBatch batch )
	{
		TextureRegion region = animation.getKeyFrame( stateTime, true );
		batch.draw( region,
				position.x-region.getRegionWidth()/2, position.y-region.getRegionHeight()/2,
				region.getRegionWidth()/2,region.getRegionHeight()/2,
				region.getRegionWidth(), region.getRegionHeight(),
				size/region.getRegionWidth(),
				size/region.getRegionWidth(), angle);
	}


}
