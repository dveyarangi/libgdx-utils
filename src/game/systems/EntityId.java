package game.systems;

public class EntityId
{
	/**
	 * Component ID generating counter
	 */
	public static int COMPONENT_ID = 0;
	/** some invalid id value */
	public static final int INVALID_ID = -100;

	public static int UID() { return ++COMPONENT_ID; }

}
