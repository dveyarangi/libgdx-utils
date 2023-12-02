package game.world.saves;

import game.world.Level;

public interface SavableSystem
{
	SavedSystem save();
	void load(Level level, SavedSystem data);
	

}
 