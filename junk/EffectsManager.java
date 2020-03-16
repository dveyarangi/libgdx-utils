package game.rendering;

import game.util.ReiterableLinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;

/**
 * Effects loop and collector
 * @author Fima
 *
 */
public class EffectsManager {

	////////////////////////////////////////////////////
	private static Pool<Effect> pool = new Pool<Effect> () {

		@Override
		protected Effect newObject()
		{
			return new Effect();
		}
	};
	
	private final ReiterableLinkedList <Effect> effects;


	public EffectsManager()
	{
		effects = new ReiterableLinkedList <Effect> ();
	}

	public void draw(SpriteBatch batch) 
	{
		effects.reset();
		while(effects.hasNext())
		{
			Effect effect = effects.next();
			effect.draw( batch );
		}
	}
	
	public void update(float delta)
	{
		effects.reset();
		
		while(effects.hasNext())
		{
			Effect effect = effects.next();
			effect.update( delta );
			if(!effect.isAlive())
			{
				effects.remove();
				pool.free( effect );
			}
		}
	}

	public void add(IEffectDef effectDef, float x, float y, float vx, float vy, float angle ) 
	{
		Effect effect = pool.obtain();
		effect.init( effectDef, x, y, vx, vy, angle );
		effects.addLast( effect );
	}

	public void dispose() 
	{
		effects.reset();
		
		while(effects.hasNext())
		{
			pool.free( effects.next() );
		}
		
		effects.clear();
		
		pool.clear();
	}
}
