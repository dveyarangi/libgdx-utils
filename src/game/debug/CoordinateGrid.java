package game.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
	private final float cx, cy;
	private final float halfWidth;
	private final float halfHeight;

	private final ICameraProvider cameraProvider;

	public CoordinateGrid( float cx, float cy, float halfWidth, float halfHeight, ICameraProvider cameraProvider )
	{
		this.cx = cx;
		this.cy = cy;
		this.halfWidth = halfWidth;
		this.halfHeight = halfHeight;
		this.cameraProvider = cameraProvider;
	}

	Color backColor = new Color(0x3D75A00A);
	Color lineColor = new Color(0x5EB2F233);

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

		Camera camera = cameraProvider.getCamera();

		// TODO: dont be lazy, use sqrt
		float order;
		for( order = 2048f; order > 0.000001; order /= 2f )
		{
			if( Math.round(2*order / cameraProvider.zoom()) == 0 )
			{
				break;
			}
		}

		// lower left screen corner in world coordinates
		float screenMinX = camera.position.x - camera.viewportWidth / 2 * cameraProvider.zoom();
		float screenMinY = camera.position.y - camera.viewportHeight / 2 * cameraProvider.zoom();
		// higher right screen corner in world coordinates
		float screenMaxX = camera.position.x + camera.viewportWidth / 2 * cameraProvider.zoom();
		float screenMaxY = camera.position.y + camera.viewportHeight / 2 * cameraProvider.zoom();

		int steps = 8;
		shape.set(ShapeType.Line);
		shape.setColor(lineColor);

		for( int i = 1; i <= steps; i++ )
		{

			int magnitude = (int) Math.pow(2, i);
			float step = order * magnitude * 16;
			
			float rminx = Math.max(cx-halfWidth, screenMinX);
			float rmaxx = Math.min(cx+halfWidth, screenMaxX);
			float rminy = Math.max(cy-halfHeight, screenMinY);
			float rmaxy = Math.min(cy+halfHeight, screenMaxY);
			float minx = FastMath.toGrid(rminx, step, cx);
			float maxx = FastMath.toGrid(rmaxx, step, cx);
			float miny = FastMath.toGrid(rminy, step, cy);
			float maxy = FastMath.toGrid(rmaxy, step, cy);

			for( float x = minx; x <= maxx; x += step )
			{
				if( x >= rminx && x <= rmaxx )
					shape.line(x, rminy, x, rmaxy);
			}
			for( float y = miny; y <= maxy; y += step )
			{
				if( y >= rminy && y <= rmaxy )
				shape.line(rminx, y, rmaxx, y);
			}
		}

		shape.setColor(lineColor.r, lineColor.g, lineColor.b, 1f);
		shape.rect(cx-halfWidth, cy-halfHeight, 2 * halfWidth, 2 * halfHeight);

		shape.end();

	}

	@Override
	public boolean useWorldCoordinates() { return true; }

	@Override
	public String toDesc() { return "coordinate grid"; }

}
