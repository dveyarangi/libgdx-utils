/**
 *
 */
package game.debug;


import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

import game.systems.control.GameInputProcessor;
import game.systems.control.Hotkeys;
import game.systems.control.InputAction;
import game.systems.control.InputContext;
import game.systems.fabric.SpatialFabric;
import game.systems.hud.UIInputProcessor;
import game.systems.lifecycle.LifecycleComponent;
import game.systems.rendering.EntityRenderingSystem;
import game.systems.rendering.IRenderer;
import game.systems.rendering.IRenderingComponent;
import game.systems.rendering.OverlayRenderer;
import game.world.Level;
import lombok.Getter;
import lombok.experimental.var;

/**
 * @author dveyarangi
 *
 */
public class Debug
{

	public static final String TAG = "debug";

	public static Debug debug = new Debug();

	public Level level;

	IRenderer levelRenderer;

	IRenderer uiRenderer;

	// private OrthographicCamera camera;

	public static final int [] PROJECTED_SHAPER_ID = new int [] {-1};


	// TODO: turn off in production
	public static final boolean DEBUG_IDS = true;

	public static boolean DEBUG_UI = false;

	static IdentityMap <String, InvokationTimer> timers = new IdentityMap();
	public static boolean registerCallAverageTag(String tag)
	{
		timers.put(tag, new InvokationTimer(tag));
		return true;
	}
	public static boolean callAverage(String tag)
	{
		InvokationTimer timer = timers.get(tag);

		timer.measure();
		return true;
	}


	public static class OverlayBinding
	{
		int keyCode;
		String name;
		@Getter boolean isOn;
		IOverlay overlay;

		Array <ChangeListener> listeners = new Array <> ();

		public OverlayBinding(int keyCode, String name, boolean isOn, IOverlay overlay)
		{
			this.keyCode = keyCode;
			this.name = name;
			this.isOn = isOn;
			this.overlay = overlay;
		}

		public void toggle()
		{
			isOn = !isOn;
			if( isOn )
				overlay.onShow();
			else
				overlay.onHide();

			for(int idx = 0; idx < listeners.size; idx ++)
				listeners.get(idx).changed(null, null);
		}

		public void render( IRenderer renderer )
		{
			if( overlay != null && isOn )
				overlay.draw( renderer );
		}

		public void addToggleListener(ChangeListener changeListener)
		{
			listeners.add(changeListener);
		}
	}

	private IntMap<OverlayBinding> debugOverlays = new IntMap<>();

	private static Map<String, Long> timings = new HashMap<>();

	private int frameCount = 0;
	/**
	 * measures delta time average
	 */
	private static final int SAMPLES = 60;

	/**
	 * Font for debug labels hardloading TODO:set size!
	 */
	public static final BitmapFont FONT;
	static {
		FileHandle font = Gdx.files.internal("fonts/VisOpenSans.ttf");
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(font);
		var param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 12;
		param.color = new Color(1,1,1,1);

		FONT = generator.generateFont(param);
	}

	private float[] deltas = new float[SAMPLES];
	private float deltaPeak = 0;
	private boolean isFirstBatch = true;

	UIInputProcessor uiProcessor;

	// private static InvokationMapper mapper = new InvokationMapper();

	public static void init( final Level level)
	{
		debug.setup(level);
	}

	private Debug(  )
	{

	}

	public void setup(final Level level)
	{

		this.level = level;
		levelRenderer = level.getEngine().getSystem(EntityRenderingSystem.class);

		uiRenderer = new OverlayRenderer();
		//projectedShapeRenderer = new ShapeRenderingContext(PROJECTED_SHAPER_ID[0], levelRenderer.shaper());

		GameInputProcessor inputController = level.getEngine().getSystem(GameInputProcessor.class);

		uiProcessor = inputController.getInputRegistry();

		// coordinate grid:
		addOverlay(Hotkeys.TOGGLE_COORDINATE_GRID, "Coordinate Grid",
				new CoordinateGrid(level.getDef().getCenterX(), level.getDef().getCenterY(), level.def().getWidth()/2, level.def().getHeight()/2)
				);

		// navigation mesh
		/*
		 * addOverlay( uiProcessor, Hotkeys.TOGGLE_NAVMESH, new OverlayBinding(
		 * new NavMeshOverlay( level.getEnvironment().getNavMesh()) ));
		 */

		// spatial faction colors


		/*addOverlay( Hotkeys.TOGGLE_FACTION_COLORS, new
				OverlayBinding(null) {
				 @Override public void toggle() { super.toggle(); for(IUnit unit :
		 debug.level.getUnits()) unit.toggleOverlay( FACTION_OID ); } } );*/

		//this.addOverlay(Hotkeys.TOGGLE_FACTION_COLORS, new UnitSymbol());

		addOverlay(Hotkeys.TOGGLE_BOX2D_DEBUG, "Physics Debug", new WorldOverlay() {

			@Override
			public void draw( IRenderer renderer )
			{
				level.getModules().getEnvironment().debugDraw(cameraProvider.getCamera().combined);
			}

		});
		addOverlay(Hotkeys.TOGGLE_UI_DEBUG, "UI Debug", new IOverlay()
		{
			@Override
			public void onShow() { inputController.getUi().setDebug(true);}
			@Override
			public void onHide() { inputController.getUi().setDebug(false);}
			@Override
			public void draw(IRenderer renderer) { }

		});

		// NOTE: This should be added last to properly display overlay key bindings
		addOverlay(Hotkeys.TOGGLE_DEBUG_INFO, "Debug Info", new DebugInfoOverlay(inputController, debugOverlays));

		
		addOverlay(Keys.G, "Spatial Debug", new SpatialHashMapOverlay(
				((SpatialFabric)level.getModules().getEnvironment()).space));

		
		for(OverlayBinding binding : debugOverlays.values())
		{
			if(binding.overlay instanceof WorldOverlay)
			{
				((WorldOverlay) binding.overlay).setCameraProvider(level.getModules().getCameraProvider());
			}

			uiProcessor.registerAction(binding.keyCode, new InputAction() {
				@Override
				public void execute( final InputContext context )
				{
					binding.toggle();
				}
			});
		}



	}


	public boolean update( final float delta )
	{
		int sampleIdx = frameCount++ % SAMPLES;
		deltas[sampleIdx] = delta;
		if( sampleIdx >= SAMPLES - 1 )
		{
			float maxDelta = Float.MIN_VALUE;
			for( int idx = 0; idx < SAMPLES; idx++ )
			{
				float sample = deltas[idx];

				if( isFirstBatch )
				{
					continue;
				}

				if( sample > maxDelta )
				{
					maxDelta = sample;
				}

				if( sample > deltaPeak )
				{
					deltaPeak = sample;
					// log("New delta maximum: " + deltaPeak);
				}
			}
			// log("Average delta time: " + deltaSum / SAMPLES);
			isFirstBatch = false;
		}
		deltaPeak -= 0.00001f;
		if( !isFirstBatch && delta > deltaPeak * 0.5 )
		{
			// log("Delta peak: " + delta);
			// deltaPeak *= 2;
		}
		return true;
	}

	/**
	 * Debug rendering method
	 *
	 * @param shape
	 */
	public void draw()
	{
		GL20 gl = Gdx.gl;
		gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		// a libgdx helper class that logs the current FPS each second
		// fpsLogger.log();

		for( Entry<OverlayBinding> entry : debugOverlays.entries() )
		{
			OverlayBinding binding = entry.value;
			if( binding.overlay.useWorldCoordinates() )
				binding.render( levelRenderer );
			else
				binding.render(uiRenderer);
		}

	}

	public static void startTiming( final String processName )
	{
		log( DEBUG,"Starting " + processName + "...");
		timings.put(processName, System.currentTimeMillis());
	}

	public static void stopTiming( final String processName )
	{
		if(!timings.containsKey(processName))
		{
			log("Trying to stop missing timing measurement " + processName);
			return;
		}

		long duration = System.currentTimeMillis() - timings.get(processName);
		log( DEBUG, "Finished " + processName + " in " + duration + "ms.");

		timings.remove(processName);
	}


	public static String DEBUG = "DEBUG";
	public static String WARN  = "WARN ";
	public static String ERROR = "ERROR";
	/**
	 * Logs a message.
	 * @param message
	 * @return true, to be used in form [assert Debug.log(...)]
	 */
	public static boolean log( final String message )
	{
		log( DEBUG, message);
		return true;
	}
	public static boolean log( final String fmt, Object ... args )
	{
		log( DEBUG, fmt, args);
		return true;
	}

	public static boolean warn( final String message )
	{
		log( WARN, message);
		return true;
	}
	public static boolean error( final String message )
	{
		log( ERROR, message);
		return true;
	}

	private static String LOG_TEMPLATE = "%s:%d >> %s";

	public static boolean log( int stackDepth, String tag,  final String format, Object ... args )
	{
		return log(stackDepth+1, tag, String.format(format, args));
	}

	public static boolean log( int stackDepth, String tag,  final String message )
	{
		StackTraceElement el = new Exception().getStackTrace()[stackDepth];
		String className = el.getClassName();
		int lineNum = el.getLineNumber();

		Gdx.app.log(tag, String.format(LOG_TEMPLATE, className, lineNum, message));

		return true;
	}

	public static boolean log( String tag,  final String format, Object ... args )
	{
		return log(tag, String.format(format, args));
	}

	public static boolean log(String tag,  final String message )
	{
		StackTraceElement [] els = Thread.currentThread().getStackTrace();
		StackTraceElement el = null;
		for(int idx = 2; idx < els.length; idx ++)
		{
			el = els[idx];
			if(!el.getClassName().equals(Debug.class.getCanonicalName()))
				break;
		}

		String className = el.getClassName();
		int lineNum = el.getLineNumber();

		Gdx.app.log(tag, String.format(LOG_TEMPLATE, className, lineNum, message));

		return true;
	}

	public void addOverlay(  int keyCode, String name, final IOverlay overlay )
	{
		final OverlayBinding binding = new OverlayBinding( keyCode, name, false, overlay );
		debugOverlays.put( keyCode, binding );

	}

	public static final int FACTION_OID = -10;

	/**
	 * Allows to register a stateless rendering components to be toggled by
	 * debug controls.
	 *
	 * @param overlays
	 */
	public static void registerOverlays( final IntMap<IRenderingComponent> overlays )
	{
		overlays.put(FACTION_OID, new UnitSymbol());
	}

	public static String entityToString(Entity entity)
	{
		StringBuilder sb = new StringBuilder();

		for(Component component : entity.getComponents())
			sb.append(component.getClass()).append(": ").append(component.toString()).append("\n");

		return sb.toString();
	}

	public void resize(int screenWidth, int screenHeight)
	{
		uiRenderer.resize(screenWidth, screenHeight);
	}

	public static void printEntity(Entity entity)
	{
		if( entity == null )
		{
			log("Entity: null");
			return;
		}
		LifecycleComponent lifecycle = entity.getComponent(LifecycleComponent.class);
		if( lifecycle == null )
		{
			log("Entity: No id");
			return;
		}

		log("Entity: " + lifecycle.id);
	}
}
