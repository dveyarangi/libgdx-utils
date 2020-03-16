package game.systems.control;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class GameGestureListener implements GestureListener
{
	private final ICameraController camController;

	public GameGestureListener( final ICameraController camController )
	{
		this.camController = camController;
	}

	@Override
	public boolean touchDown( final float x, final float y, final int pointer, final int button )
	{
		return false;
	}

	@Override
	public boolean tap( final float x, final float y, final int count, final int button )
	{
		return false;
	}

	@Override
	public boolean longPress( final float x, final float y )
	{
		return false;
	}

	@Override
	public boolean fling( final float velocityX, final float velocityY, final int button )
	{
		return false;
	}

	@Override
	public boolean pan( final float x, final float y, final float deltaX, final float deltaY )
	{
		return false;
	}

	@Override
	public boolean zoom( final float initialDistance, final float distance )
	{
		camController.zoomTo(initialDistance - distance);
		return true;
	}

	@Override
	public boolean pinch( final Vector2 initialPointer1, final Vector2 initialPointer2,
			final Vector2 pointer1, final Vector2 pointer2 )
	{
		return false;
	}

	@Override
	public boolean panStop( final float x, final float y, final int pointer, final int button )
	{
		return false;
	}

	@Override
	public void pinchStop()
	{
		// TODO Auto-generated method stub
		
	}

}
