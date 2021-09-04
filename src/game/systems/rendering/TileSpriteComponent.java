package game.systems.rendering;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import game.resources.ResourceFactory;
import game.systems.IComponentDef;
import game.systems.tiles.ITile;
import game.world.Level;
import game.world.saves.EntityProps;
import game.world.saves.Savable;

public class TileSpriteComponent implements IRenderingComponent, Savable<TileSpriteDef>
{

	static ComponentMapper<TileSpriteComponent> MAPPER = ComponentMapper.getFor(TileSpriteComponent.class);


	String atlasName;
	TextureRegion region = new TextureRegion();

	//////////////////////////
	// derived state:
	int tx;
	int ty;
	float priority;
	float x,y;
	float dx, dy;

	float width;
	float height;


	////////////////////////////////
	// static props
	/**
	 * Size scale
	 */
	float sizeCoef = 1;

	float xOffset, yOffset, zOffset;

	////////////////////////////////
	// dynamic props
	Color color = new Color();
	//	float dy;

	private int [] cid = new int [] {};


	public transient TileSpritesRenderer renderer;


	public static TileSpriteComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	@Override
	public void reset()
	{

	}

	@Override
	public int[] cid() { return cid; }

	@Override
	public void init(Entity entity, IComponentDef<?> rendererDef, Level level)
	{
		TileSpriteDef def = (TileSpriteDef) rendererDef;

		atlasName = def.atlas.getName();

		TextureAtlas atlas = ResourceFactory.getTextureAtlas(def.atlas.getName());
		TextureRegion origRegion = atlas.findRegion(def.regionName);
		if( origRegion == null )
			throw new IllegalArgumentException("Cannot find region " + def.regionName + " in atlas " + def.atlas.getName());

		region.setRegion(origRegion);

		color.set(def.color);



		this.renderer = level.getEngine().getSystem(TileSpritesRenderer.class);

		this.xOffset = def.xOffset;
		this.yOffset = def.yOffset;
		this.zOffset = def.zOffset;
		this.sizeCoef = def.sizeCoef;

		scale(1);

	}

	@Override
	public void render(Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime)
	{
		// TODO Auto-generated method stub

	}

	public void scale(float size)
	{
		this.width = size; // TODO: this maybe not the best idea to hold copies of SpatialComponent props here
		float rw = region.getRegionWidth();
		float rh = region.getRegionHeight();

		this.height = rh / rw * width;

		dx = (0.5f - xOffset)*sizeCoef*width;
		dy = (0.5f - yOffset)*sizeCoef*height;

		if( renderer != null)
			renderer.entityUpdated(this);
	}

	public void setColor(Color color)
	{
		this.color.set(color);

		if( renderer != null)
			renderer.entityUpdated(this);
	}

	public void setRegion(TextureRegion region)
	{
		this.region.setRegion(region);;

		if( renderer != null)
			renderer.entityUpdated(this);
	}

	public void directRight()
	{
		if( this.region.isFlipX())
			return;
		this.region.flip(true, false);
		renderer.entityUpdated(this);
	}

	public void directLeft()
	{
		if( !this.region.isFlipX())
			return;
		this.region.flip(false, false);
		renderer.entityUpdated(this);
	}


	public void update(float x, float y, boolean inv, ITile tile)
	{
		this.x = x;
		this.y = y; // TODO: this maybe not the best idea to hold copies of SpatialComponent xy here
		this.tx = tile.getX();
		this.ty = tile.getY();
		region.flip(inv, false);

		this.priority = tile.getSpritePriority(x, y, zOffset);
	}

	public static final String PROP_COLOR = "color";

	@Override
	public void save(TileSpriteDef def, EntityProps props)
	{
		props.put(PROP_COLOR, color);
	}

	@Override
	public void load(TileSpriteDef def, EntityProps props)
	{
		color = props.get(PROP_COLOR, def.color);
	}

	@Override
	public Class<TileSpriteDef> getDefClass() { return TileSpriteDef.class; }


}
