package game.util.loading;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.yaml.snakeyaml.Yaml;

public class UnityAtlasLoader {
	public static void main(String [] args) throws IOException
	{
		Yaml yaml = new Yaml();
		File inputFile = new File("D:\\Dev\\games_workspace\\retreat\\Assets\\Resources\\Images\\Tiles\\vegetation.png.meta");
		InputStream inputStream = new FileInputStream(inputFile);
		
		String imageName = inputFile.getName().substring(0, inputFile.getName().length()-5);
		String dataName = imageName.substring(0, imageName.length()-4);
		
		BufferedImage atlasImage = ImageIO.read(new File(inputFile.getParentFile(), imageName) );
		int atlasWidth = atlasImage.getWidth();
		int atlasHeight = atlasImage.getHeight();

		
		FileWriter writer = new FileWriter(dataName+ ".atlas");
		Map<String, Object> map = yaml.load(inputStream);
		
		
		Map <String, Object> textureImporter = (Map<String, Object>) map.get("TextureImporter");
		Map <String, Object> spriteSheet = (Map<String, Object>) textureImporter.get("spriteSheet");
		List<Object> sprites = (List<Object>) spriteSheet.get("sprites");

		writer.write(imageName+"\n");
		writer.write("format: RGBA8888"+"\n");
		writer.write("filter: Nearest,Nearest"+"\n");
		writer.write("repeat: none"+"\n");

		
		for(Object spriteObj : sprites)
		{
			Map <String, Object> sprite = (Map<String, Object>) spriteObj;
			
			String spriteName = (String) sprite.get("name");
			
			
			Map <String, Object> rect = (Map<String, Object>)sprite.get("rect");
			
			int xoffset = (int) rect.get("x");
			int yoffset = (int) rect.get("y");
			int width = (int) rect.get("width");
			int height = (int) rect.get("height");
			yoffset = atlasHeight - yoffset - height;
			
			Map <String, Object> pivot = (Map<String, Object>)sprite.get("pivot");
			
			
			int xorigin = (int) (width*((Number)pivot.get("x")).floatValue());
			int yorigin = (int) (height*((Number)pivot.get("y")).floatValue());
			
			writer.write(spriteName+"\n");
			writer.write("  rotate: false"+"\n");
			writer.write("  xy: "+ xoffset + ", " + yoffset + "\n");
			writer.write("  size: "+ width + ", " + height + "\n");
			writer.write("  orig: "+ width + ", " + width + "\n");
			writer.write("  offset: "+ 0 + ", " + 0 + "\n");
			writer.write("  index : "+ -1 + "\n");

			
		}
		
		
		writer.flush();
		writer.close();
		
		System.out.println(sprites);
	}
}
