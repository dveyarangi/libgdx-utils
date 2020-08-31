package game.util;

import game.debug.Debug;
import lombok.Getter;
import lombok.Setter;

public class LoadingProgress 
{
	@Getter protected float progress;
	@Getter private String message;
	@Setter @Getter private boolean isFinished;
	int stackDepth = 3;
	
	public void update(float progress, String message)
	{
		update(stackDepth, progress, message);
	}
	
	protected void update(int stackDepth, float progress, String message)
	{
		this.progress = progress;
		if( message != null && !message.equals(this.message)&& !message.trim().isEmpty())
			Debug.log(stackDepth, "STEP", "Loading step: %s (%s%%)", message, progress*100);
		this.message = message;

	}
	
	protected class LoadingSubprogress extends LoadingProgress
	{
		float start, end;
		
		
		public LoadingSubprogress(float start, float end, int stackDepth)
		{
			this.start = start;
			this.end = end;
			this.stackDepth = stackDepth;
		}
		
		public void update(int stackDepth, float progress, String message)
		{
			this.progress = start + progress * (end-start);
			LoadingProgress.this.update(stackDepth, this.progress, message);
		}
	}

	public LoadingProgress subprogress(float percent)
	{
		
		return new LoadingSubprogress(progress, progress+percent, this.stackDepth+1);
	}
}
