package game.debug;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import game.systems.rendering.IRenderer;
import game.util.MeshDef;

public class MeshDebugOverlay implements IOverlay
{
	private MeshDef trs;

	public MeshDebugOverlay( MeshDef trs )
	{
		this.trs = trs;
	}

	@Override
	public void draw( IRenderer renderer )
	{
		ShapeRenderer shaper = renderer.shaper();
		shaper.begin(ShapeType.Line);
		shaper.setColor(1,0.5f,0.5f, 0.2f);
		for(int tidx = 0; tidx < trs.triangles.size; tidx += 3)
		{
			short idx1 = trs.triangles.get(tidx+0);
			short idx2 = trs.triangles.get(tidx+1);
			short idx3 = trs.triangles.get(tidx+2);

			float px1 = trs.points.get(2*idx1);
			float py1 = trs.points.get(2*idx1+1);
			float px2 = trs.points.get(2*idx2);
			float py2 = trs.points.get(2*idx2+1);
			float px3 = trs.points.get(2*idx3);
			float py3 = trs.points.get(2*idx3+1);
			shaper.line(px1, py1, px2, py2);
			shaper.line(px2, py2, px3, py3);
			shaper.line(px3, py3, px1, py1);

		}
		shaper.end();
	}

	@Override public boolean useWorldCoordinates() { return true; }

	@Override
	public String toDesc() { return "mesh debug"; }

}
