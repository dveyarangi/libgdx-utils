package game.systems.control;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.IntSet;

public class Hotkeys
{
	// TODO: allocate keys to check for conflicts
	private static IntSet allocatedKeys = new IntSet();
	
	
	public static final int TIME_FASTER = Keys.PLUS;
	public static final int TIME_SLOWER = Keys.MINUS;

	public static final int NEXT_CONTROL_MODE = Keys.M;

	public static final int TOGGLE_COORDINATE_GRID = Keys.J;
	public static final int TOGGLE_FACTION_COLORS = Keys.L;
	public static final int TOGGLE_BOX2D_DEBUG = Keys.H;
	public static final int TOGGLE_DEBUG_INFO = Keys.GRAVE;
	public static final int TOGGLE_NAVMESH = Keys.K;
	public static final int TOGGLE_TERRAIN = Keys.G;
	public static final int TOGGLE_UI_DEBUG = Keys.U;

}
