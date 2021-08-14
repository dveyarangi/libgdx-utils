package game.world.saves;

public interface Savable
{
	void save(EntityProps props);
	void load(EntityProps props);
}
