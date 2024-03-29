package game.systems.rendering;

import com.badlogic.gdx.graphics.Texture;

import game.resources.ResourceFactory;
import lombok.Getter;

/**
 * Defines parameters for a texture context.
 *
 * @author Fima
 */
public class TextureRenderingContext implements IRenderingContext
{

	private String textureName;
	@Getter private Texture texture;

	private int contextId;

	private IRenderer renderer;

	public TextureRenderingContext( String textureName, Texture texture )
	{
		this.texture = texture;
		this.textureName = textureName;
		this.contextId = TextureID.genid(texture);
	}

	@Override
	public void init( ResourceFactory factory, IRenderer renderer )
	{
		this.renderer = renderer;
	}

	@Override
	public void begin()
	{
		renderer.sprites().begin();
		// TODO: i rely on SpriteBatch to not reload texture every render call afterward
		// TODO: load or activate texture instead
	}

	@Override
	public void end()
	{
		renderer.sprites().end();
	}

	@Override
	public int id()
	{
		return contextId;
	}
	@Override public boolean isEntityless() { return false; }

	@Override
	public String toString() { return "texture " + textureName; }
}
