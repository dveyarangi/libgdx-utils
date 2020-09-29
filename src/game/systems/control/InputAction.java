package game.systems.control;

public interface InputAction
{

	public void execute( InputContext context );

	/**
	 * If true, action will continue firing until deactivated
	 * @return
	 */
	public default boolean isActivable() { return false; }
}
