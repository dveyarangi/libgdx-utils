package game.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.TimeUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import game.debug.Debug.OverlayBinding;
import game.systems.control.GameInputProcessor;
import game.systems.rendering.IRenderer;
import game.world.Level;

public class DebugInfoOverlay implements IOverlay
{
	private GameInputProcessor inputController;

	long prevTime = TimeUtils.millis();
	long sinceSampling = 0;
	float averageFPS;


	private IntMap<OverlayBinding> debugOverlays;

	private static float alpha = 0.95f;

	VisWindow debugWindow;
	VisLabel fpsLabel;
	VisLabel entitiesLabel;
	VisLabel cameraPosLabel;

	IdentityMap <String, VisLabel> timerLabels = new IdentityMap <> ();

	public DebugInfoOverlay(GameInputProcessor inputController, IntMap<OverlayBinding> debugOverlays)
	{
		this.inputController = inputController;

		averageFPS = Gdx.graphics.getFramesPerSecond();

		this.debugOverlays = new IntMap <> (debugOverlays);

		this.debugWindow = new VisWindow("Debug");
		inputController.getUi().getStage().addActor(debugWindow);

		debugWindow.row().align(Align.left);
		debugWindow.add("FPS:");
		fpsLabel = new VisLabel();
		debugWindow.add(fpsLabel);

		if(Debug.debug.level != null)
		{
			debugWindow.row().align(Align.left);
			debugWindow.add("Entities:");
			entitiesLabel = new VisLabel();
			debugWindow.add(entitiesLabel);
		}

		debugWindow.row().align(Align.left);
		debugWindow.add("Camera:");
		cameraPosLabel = new VisLabel();
		debugWindow.add(cameraPosLabel);

		debugWindow.row().align(Align.left).colspan(2);
		debugWindow.add("- CALL COUNT ------------------------------");
		for(String debugTag : Debug.timers.keys())
		{
			debugWindow.row().align(Align.left);
			debugWindow.add(debugTag + ":");
			VisLabel timerLabel = new VisLabel();
			timerLabels.put(debugTag, timerLabel);
			debugWindow.add(timerLabel);
		}

		debugWindow.row().align(Align.left).colspan(2);
		debugWindow.add("-------------------------------------------");
		debugWindow.row().align(Align.left);
		debugWindow.add("Overlays: ");

		int idx = 0;
		for( Entry<OverlayBinding> entry : debugOverlays.entries() )
		{
			if( entry.value.overlay == this )
				continue;

			if( idx % 2 == 0)
				debugWindow.row().align(Align.left);
			idx ++;
			final OverlayBinding binding = entry.value;
			VisTextButton button = new VisTextButton(binding.name + " (" +Keys.toString(binding.keyCode) + ")", "toggle");
			button.setProgrammaticChangeEvents(false);
			button.addListener(new ChangeListener()	{
				@Override
				public void changed(ChangeEvent event, Actor actor)
				{
					binding.toggle();
					//button.setChecked(binding.isOn());
				}
			});
			binding.addToggleListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor)
				{
					button.setChecked(binding.isOn());

				}
			});

			debugWindow.add(button).width(150);
		}

		debugWindow.pack();
		debugWindow.setPosition(5, Gdx.graphics.getHeight()-100);
		debugWindow.setVisible(false);

	}

	@Override
	public void onShow()
	{
		//inputController.getUi().getStage().addActor(debugWindow);
		//debugWindow.setPosition(5, Gdx.graphics.getHeight()-100);
		debugWindow.setVisible(true);
	}
	@Override
	public void onHide()
	{
		debugWindow.setVisible(false);
		//debugWindow.addAction(Actions.removeActor());

	}

	private static final String CAMERA_POS_FMT = "%3.2f, %3.2f, z%2.2f";
	private static final String FLOAT_FMT = "%5.2f";

	@Override
	public void draw(IRenderer renderer)
	{
		long delta = TimeUtils.timeSinceMillis(prevTime);

		Level level = Debug.debug.level;

		averageFPS = averageFPS * alpha +  Gdx.graphics.getFramesPerSecond() * (1 - alpha);
		sinceSampling += delta;

		if( sinceSampling >= 1000)
		{
			sinceSampling = 0;


			fpsLabel.setText(""+(int)averageFPS);

			if(Debug.debug.level != null)
			{
				entitiesLabel.setText(""+level.getEngine().getEntities().size());
			}


			Vector3 cameraPos = level.getModules().getCameraProvider().position();
			float cameraZoom = level.getModules().getCameraProvider().zoom();
			String cameraInfo = String.format(CAMERA_POS_FMT,cameraPos.x, cameraPos.y, cameraZoom);

			cameraPosLabel.setText(cameraInfo);



			for(String debugTag : timerLabels.keys())
			{
				InvokationTimer timer = Debug.timers.get(debugTag);
				VisLabel timerLabel = timerLabels.get(debugTag);
				timerLabel.setText(String.format(FLOAT_FMT,  timer.getAverage()));
			}

		}

		debugWindow.pack();

	}

	@Override
	public boolean useWorldCoordinates() { return false; }


}
