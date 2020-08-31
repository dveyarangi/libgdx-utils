package game.util;

public abstract class LoadableThread implements LoadableModule 
{
	
	Thread loadingThread = null;
	
	LoadingProgress loadingProgress = new LoadingProgress();

	@Override
	public LoadingProgress stepLoading(float f)
	{
		if(loadingThread != null)
		{
			return loadingProgress;
		}
		
		loadingThread = new Thread(()->loadInternal(loadingProgress));
		loadingThread.start();
		return loadingProgress;
	}
	
	protected void loadInternal(LoadingProgress loadingProgress)
	{
		load(loadingProgress);
		loadingProgress.setFinished(true);
	}

	@Override
	public void finishLoading() {};
	
	public abstract void load(LoadingProgress loadingProgress);
	

}
