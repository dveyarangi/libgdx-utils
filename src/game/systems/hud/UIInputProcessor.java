package game.systems.hud;

import game.systems.box2d.InputAction;
import game.systems.box2d.InputContext;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntMap;

/**
 * @author Ni
 *
 */
public class UIInputProcessor implements InputProcessor
{

	private IntMap<InputAction> keyActions = new IntMap<InputAction>();

	private InputContext context = new InputContext();

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

		action.execute(context);

		return true;
	}

	@Override
	public boolean keyUp( final int keycode )
	{
		// TODO Auto-generated method stub
		return false;
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

}
