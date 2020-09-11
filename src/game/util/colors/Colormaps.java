package game.util.colors;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Colormaps 
{
	
	/**
	 * Cache of loaded colormap configurations
	 */
	private static Map <String, ColormapConf> confs = new HashMap <> ();
	
	public static ColormapConf getColormapConf(String filename)
	{
		ColormapConf conf = confs.get(filename);
		if( conf == null )
		{
			conf = read(filename);
			confs.put(filename, conf);	
		}
		
		return conf;
	}	
	
	/**
	 * 
	 * @param filename
	 * @param min
	 * @param max
	 * @return
	 */
	public static Colormap getColormap(String filename, float min, float max)
	{
		ColormapConf conf = getColormapConf(filename);
		
		return new Colormap(min, max, conf);
	}
	
	private static ColormapConf read( String filename )
	{
		try (FileReader reader = new FileReader( new File( filename )) )
		{
			return read( reader );
		} 
		catch( IOException e ){
			throw new IllegalArgumentException("Failed to read " + filename, e);
		}
	}
	
	private static ColormapConf read( Reader reader ) throws IOException
	{
		GsonBuilder builder = new GsonBuilder();
		buildGson(builder);
		Gson gson = builder.create();
		try
		{
			return gson.fromJson( reader, ColormapConf.class );
		}
		catch( JsonParseException e )
		{
			throw new IOException( e );
		}
	}
	
	public static ColormapConf readJson( String colormapJson ) throws IOException
	{
		GsonBuilder builder = new GsonBuilder();
		buildGson(builder);
		Gson gson = builder.create();
		try
		{
			return gson.fromJson( colormapJson, ColormapConf.class );
		}
		catch( JsonParseException e )
		{
			throw new IOException( e );
		}
	}
	
	private static GsonBuilder buildGson( GsonBuilder builder)
	{
		return builder
				// converts class name to Class objects:
				.registerTypeAdapter(Color.class, new LibGDXColorDeserializer());
		// converts property set references to PropertySet objects:
		//.registerTypeAdapter(Map.class, new PropertySetDeserializer())
	}
	
	public static class LibGDXColorDeserializer implements JsonDeserializer <Color>, JsonSerializer<Color>
	{

		@Override
		public Color deserialize( JsonElement element, Type arg1, JsonDeserializationContext arg2 ) throws JsonParseException
		{
			Color color;
			if( element instanceof JsonObject)
			{
				JsonObject colorObject = (JsonObject) element;
				float r = colorObject.get("r").getAsFloat();
				float g = colorObject.get("g").getAsFloat();
				float b = colorObject.get("b").getAsFloat();
				float a = colorObject.get("a").getAsFloat();
				color = new Color(r, g, b, a);
			}
			else 
			{
				long hex = Long.parseLong( element.getAsString(), 16 );
				color = new Color( (int)hex );
			}
			return color;
		}

		@Override
		public JsonElement serialize( Color src, Type typeOfSrc, JsonSerializationContext context )
		{
			String colorstr = src.toString();
			return new JsonPrimitive( colorstr );
		}

	}

}
