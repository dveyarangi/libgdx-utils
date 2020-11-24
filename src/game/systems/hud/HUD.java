package game.systems.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import game.systems.control.GameInputProcessor;
import lombok.Getter;

public abstract class HUD
{
	@Getter protected Stage stage;
	
	
	
	protected Table canvas;
	
	@Getter private int screenWidth, screenHeight;

	protected GameInputProcessor input;

	public HUD()
	{
	}
	
	public void init(GameInputProcessor gameInputProcessor) 
	{
		this.stage = new Stage(new ScreenViewport());

		this.input = gameInputProcessor;
		
		this.canvas = new Table();
		
		canvas.setFillParent(true);
		stage.addActor(canvas);
	}

	
	public void render()
	{
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	
	public void resize(int width, int height)
	{
		this.screenWidth = width;
		this.screenHeight = height;

		stage.getViewport().update(width, height, true);
	}

	public InputProcessor getInputProcessor() { return stage; } 

	public void dispose() 
	{
		stage.dispose();
	}

	public void setDebug(boolean showDebug)
	{
		stage.setDebugAll(showDebug);
	}
}
