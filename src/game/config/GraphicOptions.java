package game.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class GraphicOptions
{
/*	public int resX = 1024;
	public int resY = 786;
	
	public GraphicOptions(int resX, int resY)
	{
		this.resX = resX;
		this.resY = resY;
	}*/
	
	
	public int lightsFBOWidth = 512;
	public int lightsFBOHeight = 512;
	public int lightsBlurSize = 3;
	public int lightsFullRay = 180;
	
	public int spriteBatchSize = 2000;
}
