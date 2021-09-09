package game.world.saves;

import com.badlogic.gdx.math.Vector2;

import game.util.RandomSeed;
import lombok.Getter;

public class LevelProps
{
	@Getter Vector2 cameraPosition;
	@Getter float cameraZoom;

	@Getter RandomSeed seed;


	public LevelProps(Vector2 cameraPosition, float cameraZoom,int seed)
	{
		this.cameraPosition = cameraPosition;
		this.cameraZoom = cameraZoom;
		this.seed = new RandomSeed(seed); // TODO: load seed
	}


}
