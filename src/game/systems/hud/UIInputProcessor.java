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

	private IntMap<InputAction> keyActions = new IntMap<InputAction>();

	private InputContext context = new InputContext();
	
	private Array<InputAction> activeActions = new Array<>();

	public void registerAction( final int keycode, final InputAction action )
	{
		this.keyActions.put(keycode, action);
	}

	@Override
	public boolean keyDown( final int keycode )
	{
		InputAction action = keyActions.get(keycode);
		if( action == null )
			return false;

		activeActions.add(action);

		return true;
	}

	@Override
	public boolean keyUp( final int keycode )
	{
		InputAction action = keyActions.get(keycode);
		if( action == null )
			return false;

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
	public boolean mouseMoved( final int screenX, final int screenY )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled( final int amount )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public void update(float dt)
	{
		context.dt = dt;
		for(int i = 0; i < activeActions.size; i ++)
		{
			activeActions.get(i).execute(context);
		}
	}

}
