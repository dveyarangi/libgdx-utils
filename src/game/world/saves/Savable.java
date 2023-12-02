package game.world.saves;

public interface Savable <C>
{
	void save(C def, Props props);
	void load(C def, Props props);
	Class<C> getDefClass();
}
