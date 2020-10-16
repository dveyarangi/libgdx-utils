package game.systems.control;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import game.systems.order.ControlComponent;
import game.systems.rendering.IRenderer;
import game.world.Level;

public class ControlModes extends EntitySystem
{
	private Level level;
	private List<IControlMode> controlModes;
	private int controlModeIdx;
	private IControlMode currControlMode;

	public ControlModes()
	{
		controlModes = new ArrayList<IControlMode>();
	}

	public void addMode( IControlMode mode )
	{
		controlModes.add(mode);
	}

	public void init( Level level )
	{
		this.level = level;

		if( !controlModes.isEmpty() )
		{
			currControlMode = controlModes.get(0);
			currControlMode.reset( level );
			controlModeIdx = 0;

			Family family = Family.one( ControlComponent.class ).get();

			for(int cidx = 0; cidx < controlModes.size(); cidx ++)
			{
				IControlMode mode = controlModes.get(0);
				level.getEngine().addEntityListener( family, mode );
			}
		}
	}

	@Override
	public void update( float delta )
	{
		if( !controlModes.isEmpty() )
			currControlMode.update(delta);
	}

	public void setWorldPointer( Vector2 worldPos, float zoom )
	{
		if( !controlModes.isEmpty() )
			currControlMode.setWorldPointer(worldPos, zoom);
	}

	public void objectPicked( Entity pickedObject )
	{
		if( !controlModes.isEmpty() )
			currControlMode.objectPicked(pickedObject);
	}

	public void next()
	{
		if( !controlModes.isEmpty() )
		{
			controlModeIdx = ( controlModeIdx + 1 ) % controlModes.size();
			currControlMode = controlModes.get(controlModeIdx);
			currControlMode.reset( level );
		}
	}

	public void keyDown( int keycode )
	{
		if( !controlModes.isEmpty() )
			currControlMode.keyDown(keycode);
	}

	public void keyUp( int keycode )
	{
		if( !controlModes.isEmpty() )
			currControlMode.keyUp(keycode);
	}

	public void keyTyped( char keycode )
	{
		if( !controlModes.isEmpty() )
			currControlMode.keyTyped(keycode);
	}
	public boolean touchDown( float worldX, float worldY, float scale, Entity pickedObject, int button )
	{
		if( !controlModes.isEmpty() )
			return currControlMode.touchDown(worldX, worldY, scale, pickedObject, button);

		return false;
	}

	public boolean touchUp( float worldX, float worldY, float scale, Entity pickedObject, int button )
	{
		if( !controlModes.isEmpty() )
			return currControlMode.touchUp(worldX, worldY, scale, pickedObject, button);

		return false;
	}

	public void untouch()
	{
		if( !controlModes.isEmpty() )
			currControlMode.untouch();
	}

	public boolean drag( float worldX, float worldY, float scale, Entity pickedObject, int button)
	{
		if( !controlModes.isEmpty() )
			return currControlMode.drag(worldX, worldY, scale, pickedObject, button );

		return false;
	}

	public void render( IRenderer renderer )
	{
		if( !controlModes.isEmpty() )
			currControlMode.render(renderer);
	}
	
	public Table createUI()
	{
		return currControlMode.createUI();
	}

	public IControl control() {
		if( !controlModes.isEmpty() )
			return controlModes.get(0).control();

		return null;
	}


}
