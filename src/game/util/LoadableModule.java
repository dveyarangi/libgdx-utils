package game.util;

public interface LoadableModule {


	LoadingProgress stepLoading(float f);
	
	void finishLoading();

}
