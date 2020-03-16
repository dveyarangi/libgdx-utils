package game.debug;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import game.systems.faction.FactionComponent;
import game.systems.rendering.IRenderer;
import game.systems.rendering.IRenderingContext;
import game.systems.rendering.RendererDef;
import game.systems.rendering.ShapeRenderingComponent;
import game.systems.spatial.ISpatialComponent;
import game.world.Level;

/**
 * This is debug rendering method for unit params.
 *
 * @author Fima
 */
public class UnitSymbol extends ShapeRenderingComponent
{

	@Override
	public void init( Entity entity, RendererDef def, Level level )
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void render( Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime )
	{
		ShapeRenderer shaper = renderer.shaper();
		ISpatialComponent spatial = ISpatialComponent.get(entity);
		FactionComponent faction = FactionComponent.get(entity);
		// TargetingComponent faction = FactionComponent.get( entity );

		shaper.set(ShapeType.Filled);
		Color color = faction.color();
		shaper.setColor(color.r, color.g, color.b, 0.5f);
		shaper.circle(spatial.x(), spatial.y(), spatial.r());

		/*
		 * if( target != null) { shape.begin(ShapeType.Line); shape.line(
		 * body.getAnchor().x, body.getAnchor().y,
		 * target.getArea().getAnchor().x, target.getArea().getAnchor().y );
		 * shape.end(); shape.begin(ShapeType.Line); shape.circle(
		 * target.getArea().cx(), target.getArea().cy(), target.getArea().rx());
		 * shape.end();
		 *
		 * }
		 */

		return;
	}
	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}
	@Override
	public int [] cid() { return Debug.PROJECTED_SHAPER_ID;	}


}
