package game.systems.control;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import game.systems.hud.HUD;
import game.systems.hud.UIInputProcessor;
import game.systems.rendering.IRenderer;
import game.world.IPickProvider;
import game.world.Level;
import game.world.LevelInitialSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * handles input for game
 *
 * @author Ni
 *
 */
public class GameInputProcessor extends EntitySystem implements InputProcessor
{
	private Level level;

	/**
	 * Input processors chain
	 */
	private final InputMultiplexer inputMultiplexer;

	/**
	 * Current and previous position of mouse pointer in screen coordinates
	 */
	private int currx, curry;

	// private final Vector2 worldPos = new Vector2();
	/**
	 * Current world position of mouse cursor
	 */
	private final Vector2 worldPos = new Vector2();
	/**
	 * Previous world position of mouse cursor
	 */
	private final Vector2 prevWorldPos = new Vector2();

	/**
	 * Camera controller
	 */
	private ICameraController camController;

	@Getter private ControlModes controlModes;


	/**
	 * Provides information about game entity under mouse cursor
	 */
	protected IPickProvider picker;

	private Entity pickedObject;

	private Entity touchedObject;

	private TimeController timeController;

	private UIInputProcessor uiProcessor;

	private boolean debugPick = false;

	protected float BASE_PICK_RADIUS = 1;
	protected float pickRadius = BASE_PICK_RADIUS;

	protected HUD ui;

	protected int lastButton;
	// private boolean pointerChanged = true;

	public static float ZOOM_SPEED_COEF = 0.025f;

	public GameInputProcessor( HUD ui )
	{
		this.controlModes = new ControlModes();

		inputMultiplexer = new InputMultiplexer();

		this.uiProcessor = new UIInputProcessor();

		this.timeController = new TimeController();

		uiProcessor.registerAction(Hotkeys.TIME_FASTER, new InputAction() {
			@Override
			public void execute( final InputContext context )
			{
				timeController.setTarget(timeController.getTargetModifier() * 2f);
			}
		});
		uiProcessor.registerAction(Hotkeys.TIME_SLOWER, new InputAction() {
			@Override
			public void execute( final InputContext context )
			{
				timeController.setTarget(timeController.getTargetModifier() / 2f);
			}
		});

		this.ui = ui;
		
	}
	/**
	 * Called when this EntitySystem is added to an {@link Engine}.
	 * @param engine The {@link Engine} this system was added to.
	 */
	@Override
	public void addedToEngine (Engine engine)
	{
		this.level = engine.getSystem(Level.class);

		
		controlModes.init( level );
		

		this.ui.init( this );
		
		
		inputMultiplexer.addProcessor(uiProcessor);
		if( ui != null)
			inputMultiplexer.addProcessor(ui.getInputProcessor());
		inputMultiplexer.addProcessor(new GestureDetector(new GameGestureListener(camController)));
		inputMultiplexer.addProcessor(this);

		
		LevelInitialSettings settings = level.getDef().getInitialSettings();
		camController = new FreeCameraController( level.getModules().getCameraProvider(), settings.getInitZoom(), settings.getMinZoom(), settings.getMaxZoom() );

		this.picker = level.getModules().getEnvironment().createPickProvider();

		/**
		 * TODO: check if this really should be located here:
		 */
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	public void setPickFilter(IEntityFilter filter)
	{
		picker.setEntityFilter(filter);
	}

	/**
	 * Updates controller state, - recalculate camera position - check units
	 * under mouse cursor - update time controller - update control mode
	 *
	 * @param delta
	 */
	@Override
	public void update( final float delta )
	{
	
		setPickFilter(controlModes.getPickFilter());

		uiProcessor.update(delta);
		
		// adjusting camera:
		camController.update(delta);

		// TODO: this may be called too much:
		this.toggleCursorMoved(currx, curry, true);

		timeController.update(delta);

		controlModes.update(delta);
		


	}

	/**
	 * Called when this EntitySystem is removed from an {@link Engine}.
	 * @param engine The {@link Engine} the system was removed from.
	 */
	@Override
	public void removedFromEngine (Engine engine) {
	}


	/** temporal storage for camera un-projection operation */
	private final Vector2 cursorPos = new Vector2();


	private void toggleCursorMoved( int screenX, int screenY, boolean forceUpdate )
	{
		if( !forceUpdate && screenX == currx && screenY == curry )
		{
			return;
		}
		// pushing previous position:
		currx = screenX;
		curry = screenY;

		float zoom = camController.zoom();

		// determining pick radius:
		pickRadius = BASE_PICK_RADIUS * zoom;

		prevWorldPos.set(worldPos);

		// calculating mouse position in world coordinates:
		// camController.camera().viewportWidth/2;
		camController.unproject(currx, curry, cursorPos);
		worldPos.x = cursorPos.x;
		worldPos.y = cursorPos.y;

		// System.out.println(worldPos);

		controlModes.setWorldPointer(worldPos, zoom);

		// testing for units under mouse cursor:
		Entity newPickedObject = picker.pick(worldPos.x, worldPos.y, pickRadius);
		//if( newPickedObject != null && pickedObject == null)
		//	System.out.println(newPickedObject);
		//if( newPickedObject == null && pickedObject != null)
		//	System.out.println("unpicked");
		// informing controllers of change of object picking:
		if( newPickedObject != pickedObject )
		{
			//Debug.printEntity(newPickedObject);
			controlModes.objectPicked(newPickedObject);
			pickedObject = newPickedObject;
		}

	}

	@Override
	public boolean keyDown( final int keycode )
	{
		uiProcessor.keyDown(keycode);
		switch( keycode )
		{

			/*
			 * case Input.Keys.SPACE: if(camController == freeController) {
			 * autoController.zoomTarget = camController.getCamera().zoom;
			 * camController = autoController; } else { camController =
			 * freeController; } break;
			 */
		default:

			controlModes.keyDown(keycode);
			return false;
		}
	}

	@Override
	public boolean keyUp( final int keycode )
	{
		uiProcessor.keyUp(keycode);
		switch( keycode )
		{
		default:
			controlModes.keyUp(keycode);
			return false;
		}
		// return false;
	}

	@Override
	public boolean keyTyped( final char keycode )
	{

		uiProcessor.keyTyped(keycode);
		switch( keycode )
		{
		default:
			controlModes.keyTyped(keycode);
			return false;
		}
	}

	@Override
	public boolean touchDown( final int screenX, final int screenY, final int pointer, final int button )
	{
		this.toggleCursorMoved(screenX, screenY, true);

		boolean consumed = controlModes.touchDown(worldPos.x, worldPos.y, camController.zoom(), pickedObject, button);

		lastButton = button;

		if( pickedObject != null )
		{
			if( touchedObject == null )
			{
				touchedObject = pickedObject;
			}
		}


		return consumed;
	}

	public IControl control() { return controlModes.control(); }

	@Override
	public boolean touchUp( final int screenX, final int screenY, final int pointer, final int button )
	{
		this.toggleCursorMoved(screenX, screenY, true);

		boolean consumed = controlModes.touchUp(worldPos.x, worldPos.y, camController.zoom(), pickedObject, button);

		if(!consumed)
			if(button == Input.Buttons.RIGHT)
			{
				touchedObject = null;
				controlModes.untouch();
				lastButton = 0;
			}
		return true;
	}

	@Override
	public boolean touchDragged( final int screenX, final int screenY, final int pointer )
	{
		this.toggleCursorMoved(screenX, screenY, false);

		boolean consumed = false;

		consumed = controlModes.drag(worldPos.x, worldPos.y, camController.zoom(), pickedObject, lastButton);

		/*if(! consumed && lastButton == Input.Buttons.RIGHT)
		{
			camController.moveBy(prevWorldPos.x - worldPos.x, prevWorldPos.y - worldPos.y);
		}*/

		return consumed;
	}
	
	@AllArgsConstructor
	public class MoveCameraAction implements InputAction
	{
		float dx, dy;
		@Override
		public void execute(InputContext context)
		{
			camController.moveBy(dx*context.dt*camController.zoom(), dy*context.dt*camController.zoom());
		}
		public boolean isActivable() { return true; }
	}
	
	@Override
	public boolean mouseMoved( final int screenX, final int screenY )
	{
		this.toggleCursorMoved(screenX, screenY, false);
		return true;
	}

	@Override
	public boolean scrolled( final int amount )
	{
		this.toggleCursorMoved(currx, curry, true);
		camController.zoomTo(worldPos.x, worldPos.y, amount * ZOOM_SPEED_COEF);
		return true;
	}


	public void render( final IRenderer renderer )
	{
		final ShapeRenderer shape = renderer.shaper();

		if( debugPick )
		{
			shape.setColor(0, 0, 1, 0.5f);
			shape.begin(ShapeType.Line);
			shape.rect(worldPos.x - pickRadius, worldPos.y - pickRadius, 2 * pickRadius, 2 * pickRadius);
			shape.end();

			/*
			 * if(pickedObject != null) { shape.begin(ShapeType.Line);
			 * shape.rect(pickedObject.getArea().getMinX(),
			 * pickedObject.getArea().getMinX(),
			 * pickedObject.getArea().getHalfWidth()*2,
			 * 2*pickedObject.getArea().getHalfHeight()); shape.end(); }
			 */
		}

		/*
		 * batch.begin(); TextureRegion crossHairregion = crosshair.getKeyFrame(
		 * lifeTime, true ); batch.draw( crossHairregion,
		 * pointerPosition2.x-crossHairregion.getRegionWidth()/2,
		 * pointerPosition2.y-crossHairregion.getRegionHeight()/2,
		 * crossHairregion
		 * .getRegionWidth()/2,crossHairregion.getRegionHeight()/2,
		 * crossHairregion.getRegionWidth(), crossHairregion.getRegionHeight(),
		 * 5f/crossHairregion.getRegionWidth(),
		 * 5f/crossHairregion.getRegionWidth(), 0); batch.end();
		 */

		/*
		 * shape.setColor( 0, 1, 0, 0.1f ); shape.begin( ShapeType.Line );
		 * shape.line( level.getControlledUnit().getBody().getAnchor().x,
		 * level.getControlledUnit().getBody().getAnchor().y,
		 * pointerPosition2.x, pointerPosition2.y ); shape.end();
		 */

		controlModes.render(renderer);
		
		ui.render();
	}

	/**
	 * @param width
	 * @param height
	 */
	public void resize( final int width, final int height )
	{
		// autoController.resize(width, height);
		ui.resize(width, height);

		camController.resize(width, height);

	}

	public Vector2 getCrosshairPosition()
	{
		return worldPos;
	}

	public UIInputProcessor getInputRegistry()
	{
		return uiProcessor;
	}

	public float getTimeModifier()
	{
		return timeController.getModifier();
	}


	public void dispose()
	{
		ui.dispose();
	}
}
