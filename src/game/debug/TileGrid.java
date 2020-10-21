package game.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import game.systems.rendering.IRenderer;
import game.util.FastMath;

/**
 * Displays grid debug overlay (with powers of 2 steps).
 *
 * The grid automatically adjusts to the zoom level.
 *
 * TODO: print axis values
 *
 *
 * @author dveyarangi
 *
 */
public abstract class TileGrid extends WorldOverlay
{
	private final float width;
	private final float height;
	private final float tileWidth = 1; 
	
	public TileGrid( int width, int height )
	{
		this.width = width;
		this.height = height;
	}

	/**
	 * Provide color for the tile
	 * Returns color.a == 0 if the tile is to be omitted
	 * @param x
	 * @param y
	 * @param out color output buffer
	 * @return
	 */
	protected abstract Color getColor(int x, int y, Color out);

	Color backColor = new Color(0x3D75A00A);
	Color lineColor = new Color(0x5EB2F233);

	// Color lineColor = new Color( 0x8DA1AA0A );

	Color color = new Color();
	
	@Override
	public void draw( final IRenderer renderer )
	{
		ShapeRenderer shape = renderer.shaper();
		shape.begin(ShapeType.Filled);
		/*shape.setColor(backColor);
		for( float r = 10; r < 1000; r += 100 )
			shape.circle(0, 0, r);*/

		GL20 gl = Gdx.gl;
		gl.glEnable(GL20.GL_BLEND);

		Camera camera = cameraProvider.getCamera();


		// lower left screen corner in world coordinates
		float screenMinX = camera.position.x - camera.viewportWidth / 2 * cameraProvider.zoom();
		float screenMinY = camera.position.y - camera.viewportHeight / 2 * cameraProvider.zoom();
		// higher right screen corner in world coordinates
		float screenMaxX = camera.position.x + camera.viewportWidth / 2 * cameraProvider.zoom();
		float screenMaxY = camera.position.y + camera.viewportHeight / 2 * cameraProvider.zoom();
		
		int rminx = FastMath.floor(Math.max(0, screenMinX));
		int rmaxx = FastMath.ceil(Math.min(width, screenMaxX));
		int rminy = FastMath.floor(Math.max(0, screenMinY));
		int rmaxy = FastMath.ceil(Math.min(height, screenMaxY));

		int steps = 8;
		shape.set(ShapeType.Filled);
		shape.setColor(lineColor);
		
		
		for(int x = rminx; x < rmaxx; x ++)
			for(int y = rminy; y < rmaxy; y ++)
			{
				color = getColor(x, y, color);
				if(color.a == 0)
					continue;
				
				shape.setColor(color.r, color.g, color.b, color.a);
				shape.rect(x, y, 1, 1);
			
			}


		shape.end();

	}

}
