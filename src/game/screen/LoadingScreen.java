package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import game.config.GraphicOptions;
import game.debug.Debug;
import game.util.LoadableModule;
import game.util.LoadingProgress;
import lombok.Getter;
import lombok.experimental.var;

/**
 * Creates and gradually loads game resources;
 *
 * transits control to targetScreen
 *
 * @author Fima
 */

public class LoadingScreen<G extends AbstractGame> extends AbstractScreen<G>
{

	/**
	 * Resource factory, loaded by this loading screen
	 */
	LoadableModule loadable;

	Sprite loadingSprite;
	static float loadingSpriteAngle = 0;
	
	
	/**
	 * Progress bar dims
	 */
	private int pbarminx, pbarminy, pbarlw, pbarlh;

	private float barProgress = 0;
	
	/** 
	 * Delay before moving to the next screen after loading is complete
	 */
	private float cullinTime;
	private float culloutTime;

	/**
	 * Target screen to show after loading is complete
	 */
	AbstractScreen<G> targetScreen;
	
	@Getter private GraphicOptions options = new GraphicOptions();
	
	FadeGameOverlay overlay = new FadeGameOverlay();
	
	FileHandle font = Gdx.files.internal("fonts/VisOpenSans.ttf");
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(font);
    BitmapFont fontLabel;

	public LoadingScreen( AbstractScreen<G> targetScreen )
	{
		this(targetScreen, targetScreen.getLoadable());
	}
	
	public LoadingScreen( AbstractScreen<G> targetScreen, LoadableModule loadable )
	{
		super(targetScreen.game);

		this.targetScreen = targetScreen;

		this.loadable = loadable;
		
		var param = new FreeTypeFontGenerator.FreeTypeFontParameter();
		param.size = 12;
		
		fontLabel = generator.generateFont(param);
		
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
		if( cullinTime < FadeGameOverlay.INTERVAL)
		{
			cullinTime += delta;
			overlay.updateOverlay(delta);
			overlay.drawOverlay(renderer);
		}
		
		if(loadable == null)
		{
			this.getGame().setScreen(targetScreen);
			return;
		}
		// load some data:
		LoadingProgress loadingProgress = loadable.stepLoading(0.02f);
		float progress = loadingProgress.getProgress();
		if( loadingProgress.getThr() != null )
			throw new RuntimeException(loadingProgress.getThr());
			
		if(progress == 1)
		{
			culloutTime += delta;
			if(culloutTime > FadeGameOverlay.OUTERVAL)
			{
				//this.dispose();
				this.setScreen(targetScreen);

				loadable.finishLoading();
				Debug.stopTiming("resource loading");
				this.getGame().setScreen(targetScreen);
				
				// TODO: maybe this helps:
				Runtime.getRuntime().gc();

				return;
			}
			else
			{
				overlay.outFade = true;
				overlay.updateOverlay(delta);
				overlay.drawOverlay(renderer);
			}
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		animateSprite(delta);
		
		animateBar(delta, progress);

		showMessage(loadingProgress);

	}


	private void animateSprite(float delta)
	{
		loadingSpriteAngle -= 50*delta;

		float w = loadingSprite.getWidth();
		float h = loadingSprite.getHeight();
		float cx = Gdx.graphics.getWidth()/2;
		float cy = Gdx.graphics.getHeight()/2;
		renderer.sprites().begin();
		renderer.sprites().draw( loadingSprite, cx-w/2, cy-h/2, w/2,h/2, w,h,1,1,
				loadingSpriteAngle);
		
		renderer.sprites().end();	
	}
	
	private void animateBar(float delta, float progress)
	{

		// smoothing a little:
		barProgress += (progress - barProgress)/3;
		
		renderer.shaper().begin( ShapeType.Line );
		renderer.shaper().setColor(1,1,1,1);
		renderer.shaper().rect( pbarminx, pbarminy, pbarlw, pbarlh );
		renderer.shaper().end();
		renderer.shaper().begin( ShapeType.Filled );
		renderer.shaper().rect( pbarminx, pbarminy, barProgress*pbarlw, pbarlh );
		renderer.shaper().end();	
	}

	private void showMessage(LoadingProgress progress)
	{
		if(progress.getMessage() == null)
			return;
		float cx = Gdx.graphics.getWidth()/2;
		float cy = Gdx.graphics.getHeight()/2;
		renderer.sprites().begin();

		fontLabel.setColor(1,1,1,1);
		fontLabel.draw(renderer.sprites(), progress.getMessage(), pbarminx, pbarminy + 2*pbarlh);
		
		renderer.sprites().end();	
	}

	@Override
	public void resize( int width, int height )
	{
		super.resize(width, height);
		float w = loadingSprite.getWidth();
		float h = loadingSprite.getHeight();
		
		this.pbarminx = width / 2 - width / 4;
    	int pbarmaxx = width / 2 + width / 4;
    	pbarlw = pbarmaxx - pbarminx;
    	this.pbarminy = (int) (height/2 - h/2 - 2*height / 100);
    	int pbarmaxy = (int)(height/2 - h/2- 3*height / 100);
    	//int maxy = 3*height / 100;
    	pbarlh = pbarmaxy - pbarminy;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
	}


}
