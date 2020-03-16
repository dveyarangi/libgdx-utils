package game.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import game.systems.rendering.IRenderer;
import game.util.FastMath;
import game.world.Level;
import game.world.camera.ICameraProvider;

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
public class CoordinateGrid implements IOverlay<Level>
{
	private final float halfWidth;
	private final float halfHeight;

	private final ICameraProvider cameraProvider;

	public CoordinateGrid( float halfWidth, float halfHeight, ICameraProvider cameraProvider )
	{
		this.halfWidth = halfWidth;
		this.halfHeight = halfHeight;
		this.cameraProvider = cameraProvider;
	}

	Color backColor = new Color(0x3D75A00A);
	Color lineColor = new Color(0x5EB2F21A);

	// Color lineColor = new Color( 0x8DA1AA0A );

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

		OrthographicCamera camera = cameraProvider.getCamera();

		// TODO: dont be lazy, use sqrt
		float order;
		for( order = 2048f; order > 0.000001; order /= 2f )
		{
			if( Math.round(order / camera.zoom) == 0 )
			{
				break;
			}
		}

		// lower left screen corner in world coordinates
		float screenMinX = camera.position.x - camera.viewportWidth / 2 * camera.zoom;
		float screenMinY = camera.position.y - camera.viewportHeight / 2 * camera.zoom;
		// higher right screen corner in world coordinates
		float screenMaxX = camera.position.x + camera.viewportWidth / 2 * camera.zoom;
		float screenMaxY = camera.position.y + camera.viewportHeight / 2 * camera.zoom;

		int steps = 8;
		shape.set(ShapeType.Line);
		shape.setColor(lineColor);

		for( int i = 1; i <= steps; i++ )
		{

			int magnitude = (int) Math.pow(2, i);
			float step = order * magnitude * 16;

			float minx = FastMath.toGrid(Math.max(-halfWidth, screenMinX), step);
			float maxx = FastMath.toGrid(Math.min(halfWidth, screenMaxX), step);
			float miny = FastMath.toGrid(Math.max(-halfHeight, screenMinY), step);
			float maxy = FastMath.toGrid(Math.min(halfHeight, screenMaxY), step);

			for( float x = minx; x <= maxx; x += step )
			{
				shape.line(x, miny, x, maxy);
			}
			for( float y = miny; y <= maxy; y += step )
			{
				shape.line(minx, y, maxx, y);
			}
		}

		shape.setColor(1, 0, 1f, 0.5f);
		shape.rect(-halfWidth, -halfHeight, 2 * halfWidth, 2 * halfHeight);

		shape.end();

	}

	@Override
	public boolean isProjected()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
