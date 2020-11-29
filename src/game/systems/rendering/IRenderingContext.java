package game.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

import game.resources.ResourceFactory;
import lombok.AllArgsConstructor;

/**
 * Contexts task is to set up parameters for batch of {@link IRenderingComponent}s
 * @author Fima
 *
 */
public interface IRenderingContext
{
	public static int INVALID_ID = -1;
	
	/**
	 * Context id is used to map rendering components to this context.
	 * Common use is to batch together rendering of entities, 
	 * that use same texture/atlas.
	 * @return
	 */
	public int id();

	/**
	 * Initialize renderer
	 *
	 * @param factory
	 */
	public void init( ResourceFactory factory, IRenderer renderer );

	/**
	 * Setup rendering environment before batch rendering begins.
	 */
	public void begin();

	/**
	 * Finalize batch rendering and restore renderer state.
	 */
	public void end();
	
	/**
	 * If true, no entities are needed for this context to be rendered
	 * @return
	 */
	public boolean isEntityless();

	/**
	 * Dummy context for rendering components that do not specify context id
	 */
	@AllArgsConstructor
	public static class VoidContext implements IRenderingContext
	{
		private int id;
		
		@Override public int id() { return id;}
		@Override public void init(ResourceFactory factory, IRenderer renderer) { }
		@Override public void begin() { }
		@Override public void end() { }
		@Override public String toString() { return "void context"; }
		@Override public boolean isEntityless() { return false; }
	}
	
	public static class DecalContext implements IRenderingContext
	{
		private int id;
		private DecalBatch decals;
		public DecalContext(int id) { this.id = id; }
		@Override public int id() { return id;}
		@Override public void init(ResourceFactory factory, IRenderer renderer) { this.decals = renderer.decals(); }
		@Override public void begin() { 
			Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
			Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);

		}
		@Override public void end() { decals.flush(); }
		@Override public String toString() { return "decals context"; }
		@Override
		public boolean isEntityless() { return false; }
	}

	public default void entityAdded(Entity entity) {};
	public default void entityRemoved(Entity entity) {};
}
