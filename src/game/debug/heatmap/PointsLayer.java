package game.debug.heatmap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import lombok.AllArgsConstructor;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;


@AllArgsConstructor
public class PointsLayer implements HeatmapLayer
{

	Array <Vector2> points;
	float size;
	Color color;
	
	public void render(Graphics2D g2d, float scale)
	{
		g2d.setColor(color);
		for(Vector2 p : points)
		{
			Shape circleShape = new Ellipse2D.Double(p.x*scale, p.y*scale, size, size);
			g2d.fill(circleShape);
		}
	}
}
