package game.systems;

/**
 * Marker interface for systems that need to be run some time before level starts rendering
 * @author fimar
 *
 */
public interface WarmedUpSystem {
	public default void initWarmup() {};
	public void warmup(float deltaTime);
	public default void finishWarmup() {};
}
