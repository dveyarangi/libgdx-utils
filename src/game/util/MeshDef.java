package game.util;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public class MeshDef
{
	public FloatArray points;
	public ShortArray triangles;

	public MeshDef( FloatArray points, ShortArray triangles )
	{
		this.points = points;
		this.triangles = triangles;
	}
}
