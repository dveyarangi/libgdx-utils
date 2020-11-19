package game.resources;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * <p>Allows serializing and deserializing members typed by java interface.
 *
 * <p>This adapter expects {@link #TYPE_ATTRIB} field in such deserialized
 * object to specify the actual class of the member.
 * Consequenty, the {@link #TYPE_ATTRIB} field will be present in serialization output.
 * @author Fima
 *
 * @param <E>
 */

public class JsonInterfaceAdapter <E> implements JsonDeserializer <E>, JsonSerializer<E>
{
	public static final String TYPE_ATTRIB = "class";

	private ClassLoader classloader;

	public JsonInterfaceAdapter()
	{
		this( JsonInterfaceAdapter.class.getClassLoader() );
	}

	public JsonInterfaceAdapter( ClassLoader classloader )
	{
		this.classloader = classloader;
	}

	@Override
	public E deserialize( JsonElement json, Type typeOfT, JsonDeserializationContext context ) throws JsonParseException
	{
		JsonObject object = json.getAsJsonObject();

		// get real type from the json fields:
		String className = object.get(TYPE_ATTRIB).getAsString();
	    Class<?> klass = null;
	    try {
	    	klass = classloader.loadClass(className);
	    } catch (ClassNotFoundException e) {
	    	throw new IllegalArgumentException("Could not load class " + className );
	    }

    	return context.deserialize( json, klass );
	}

	@Override
	public JsonElement serialize( E src, Type typeOfSrc, JsonSerializationContext context )
	{
		JsonElement element = context.serialize( src );
		JsonObject jso = (JsonObject) element;
		//TODO : Changed to solve inner classes naming problem
//		jso.addProperty( TYPE_ATTRIB, src.getClass().getCanonicalName() );
		jso.addProperty( TYPE_ATTRIB, src.getClass().getName() );
		return element;
	}

}
