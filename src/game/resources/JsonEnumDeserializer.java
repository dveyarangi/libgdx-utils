package game.resources;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JsonEnumDeserializer <T extends Enum<T>> implements JsonDeserializer <T>
{
	Class <T> enumClass;
	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		return Enum.valueOf(enumClass, json.getAsString());
	}
}