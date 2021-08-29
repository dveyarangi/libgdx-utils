package game.systems.tiles;

public interface ITile
{
	int getX();
	int getY();
	float getSpritePriority(float x, float y, float zOffset);
}
