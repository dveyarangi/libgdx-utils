package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import game.debug.Debug;
import game.resources.ResourceFactory;

/**
 * Creates and gradually loads game resources;
 *
 * transits control to targetScreen
 *
 * @author Fima
 */

public class LoadingScreen<G extends AbstractGame> extends AbstractScreen<G>
{

	AbstractScreen<?> targetScreen;

	ResourceFactory factory;

	Sprite loadingSprite;
	float loadingSpriteAngle = 0;
	
	
	private int minx, miny, lw, lh;

	private float culloutTime;

	private float barProgress = 0;

	public LoadingScreen( AbstractScreen<G> targetScreen, Class<?> resourcesClass )
	{
		super(targetScreen.game);

		this.targetScreen = targetScreen;

		factory = targetScreen.game.resourceFactory = ResourceFactory.init(resourcesClass);

		loadingSprite = new Sprite(new Texture("images//lotus.png"));
		loadingSprite.setSize(Gdx.graphics.getHeight() / 4,Gdx.graphics.getHeight() / 4);
	}

	@Override
	public void show()
	{
		super.show();

		Debug.startTiming("resource loading");
	}

	@Override
	public void render( float delta )
	{
		//super.render(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float progress = factory.stepLoading(0.02f);
		if(progress == 1)
		{
			culloutTime += delta;
			if(culloutTime > 0.2)
			{
				//this.dispose();
				this.setScreen(targetScreen);

				factory.finishLoading();
				Debug.stopTiming("resource loading");
				this.getGame().setScreen(targetScreen);
				return;
			}
		}
		

		loadingSpriteAngle -= 50*delta;

		float w = loadingSprite.getWidth();
		float h = loadingSprite.getHeight();
		float cx = Gdx.graphics.getWidth()/2;
		float cy = Gdx.graphics.getHeight()/2;
		renderer.batch.begin();
		renderer.batch.draw( loadingSprite, cx-w/2, cy-h/2, w/2,h/2, w,h,1,1,
				loadingSpriteAngle);

		renderer.batch.end();

		// smoothing a little:
		barProgress += (progress - barProgress)/3;
		
		renderer.shaper.begin( ShapeType.Line );
		renderer.shaper.rect( minx, miny, lw, lh );
		renderer.shaper.end();
		renderer.shaper.begin( ShapeType.Filled );
		renderer.shaper.rect( minx, miny, barProgress*lw, lh );
		renderer.shaper.end();


	}

	@Override
	public void resize( int width, int height )
	{
		super.resize(width, height);
		float w = loadingSprite.getWidth();
		float h = loadingSprite.getHeight();
		
		this.minx = width / 2 - width / 4;
    	int maxx = width / 2 + width / 4;
    	lw = maxx - minx;
    	this.miny = (int) (height/2 - h/2 - 2*height / 100);
    	int maxy = (int)(height/2 - h/2- 3*height / 100);
    	//int maxy = 3*height / 100;
    	lh = maxy - miny;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
	}
}
