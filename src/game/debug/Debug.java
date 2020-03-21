/**
 *
 */
package game.debug;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

import game.systems.box2d.InputAction;
import game.systems.box2d.InputContext;
import game.systems.control.GameInputProcessor;
import game.systems.control.Hotkeys;
import game.systems.hud.UIInputProcessor;
import game.systems.rendering.EntityRenderingSystem;
import game.systems.rendering.IRenderer;
import game.systems.rendering.IRenderingComponent;
import game.systems.rendering.ShapeRenderingContext;
import game.world.Level;

/**
 * @author dveyarangi
 *
 */
public class Debug
{

	public static final String TAG = "debug";

	public static Debug debug;

	public Level level;

	IRenderer levelRenderer;

	// private OrthographicCamera camera;

	public static final int [] PROJECTED_SHAPER_ID = new int [] {-1};

	public ShapeRenderingContext projectedShapeRenderer;

	public static class OverlayBinding
	{
		boolean isOn;
		IOverlay overlay;

		public OverlayBinding( IOverlay overlay )
		{
			this(overlay, false);
		}

		public OverlayBinding( IOverlay overlay, boolean isOn )
		{
			this.overlay = overlay;
			this.isOn = isOn;
		}

		public void toggle()
		{
			isOn = !isOn;
		}

		public boolean isOn()
		{
			return isOn;
		}

		public void render( IRenderer renderer )
		{
			if( overlay != null && isOn )
				overlay.draw( renderer );
		}
	}

	private IntMap<OverlayBinding> debugOverlays = new IntMap<OverlayBinding>();

	private static Map<String, Long> timings = new HashMap<String, Long>();

	private int frameCount = 0;
	/**
	 * measures delta time average
	 */
	private static final int SAMPLES = 60;

	/**
	 * Font for debug labels hardloading TODO:set size!
	 */
	public static final BitmapFont FONT = new BitmapFont(Gdx.files.internal("fonts//debug.fnt"));

	private float[] deltas = new float[SAMPLES];
	private float deltaPeak = 0;
	private boolean isFirstBatch = true;

	private FPSLogger fpsLogger = new FPSLogger();

	UIInputProcessor uiProcessor;
	
	public static boolean DEBUG_UI = false;

	// private static InvokationMapper mapper = new InvokationMapper();

	public static void init( final Level level)
	{

		debug = new Debug(level);
		debug.setup();
	}

	private Debug( final Level level )
	{

		this.level = level;
	}

	public void setup()
	{

		levelRenderer = level.getEngine().getSystem(EntityRenderingSystem.class);

		projectedShapeRenderer = new ShapeRenderingContext(PROJECTED_SHAPER_ID[0], levelRenderer.shaper());

		GameInputProcessor  inputController = level.getEngine().getSystem(GameInputProcessor.class);

		uiProcessor = inputController.getInputRegistry();

		// coordinate grid:
		this.addOverlay(Hotkeys.TOGGLE_COORDINATE_GRID,
				new CoordinateGrid(level.def().getHalfWidth(), level.def().getHalfHeight(),
						level.getModules().getCameraProvider())
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

		this.addOverlay(Hotkeys.TOGGLE_BOX2D_DEBUG, new IOverlay() {
			@Override
			public void draw( IRenderer renderer )
			{
				level.getModules().getEnvironment().debugDraw(level.getModules().getCameraProvider().getCamera().combined);
			}

			@Override
			public boolean isProjected()
			{
				return false;
			}

		});

	}


	public boolean update( final float delta )
	{
		fpsLogger.log();
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
			binding.render( levelRenderer );
		}

	}

	public static void startTiming( final String processName )
	{
		log("Starting " + processName + "...");
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
		log("Finished " + processName + " in " + duration + "ms.");
		
		timings.remove(processName);
	}

	/**
	 * Logs a message.
	 * @param message
	 * @return true, to be used in form [assert Debug.log(...)]
	 */
	public static boolean log( final String message )
	{
		log(3, message);
		return true;
	}
	public static boolean log( int stackDepth, final String message )
	{
		StackTraceElement el = new Exception().getStackTrace()[stackDepth];
		String className = el.getClassName();
		int lineNum = el.getLineNumber();

		Gdx.app.log(className + ":" + lineNum, message);

		return true;
	}

	public void addOverlay(  int keyCode, final IOverlay overlay )
	{
		final OverlayBinding binding = new OverlayBinding( overlay );
		debugOverlays.put( keyCode, binding );
		uiProcessor.registerAction(keyCode, new InputAction() {
			@Override
			public void execute( final InputContext context )
			{
				binding.toggle();
			}
		});
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
}
