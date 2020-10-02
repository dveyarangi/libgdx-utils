package game.resources;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class NamedTextureAtlas 
{
	@Getter String name;
	@Getter TextureAtlas atlas;
}
