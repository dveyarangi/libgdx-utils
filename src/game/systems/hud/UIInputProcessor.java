package game.systems.hud;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

import game.systems.control.InputAction;
import game.systems.control.InputContext;

/**
 * @author Ni
 *
 */
public class UIInputProcessor implements InputProcessor
{

	private IntMap<InputAction> keyActions = new IntMap<>();

	private InputContext context = new InputContext();

	private Array<InputAction> activeActions = new Array<>();

	public void registerAction( final int keycode, final InputAction action )
	{
		this.keyActions.put(keycode, action);
	}
	

	public void update(float dt)
	{
		context.dt = dt;
		for(int i = 0; i < activeActions.size; i ++)
		{
			activeActions.get(i).execute(context);
		}
	}


	@Override
	public boolean keyDown( final int keycode )
	{
		InputAction action = keyActions.get(keycode);
		if( action == null )
			return false;

		action.execute(context);
		if( action.isActivable() )
			activeActions.add(action);

		return true;
	}

	@Override
	public boolean keyUp( final int keycode )
	{
		InputAction action = keyActions.get(keycode);
		if( action == null )
			return false;

		if( action.isActivable() )
			activeActions.removeValue(action, true);
		return true;
	}

	@Override
	public boolean keyTyped( final char character )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown( final int screenX, final int screenY, final int pointer, final int button )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp( final int screenX, final int screenY, final int pointer, final int button )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged( final int screenX, final int screenY, final int pointer )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean mouseMoved( final int screenX, final int screenY )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled (float amountX, float amountY)
	{
		// TODO Auto-generated method stub
		return false;
	}


}
