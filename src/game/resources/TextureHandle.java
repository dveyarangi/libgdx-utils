package game.resources;

import lombok.ToString;

@ToString
public class TextureHandle implements Comparable <TextureHandle>
{
	int priority;
	String textureName;

	public TextureHandle( String textureFile, int priority )
	{
		this.textureName = textureFile;
		this.priority = priority;
	}

	@Override
	public int compareTo( TextureHandle that )
	{
		return this.priority - that .priority;
	}

	public int getPriority()
	{
		return priority;
	}


	public String getTextureName()
	{
		return textureName;
	}

	@Override
	public int hashCode() { return textureName.hashCode(); }

	@Override
	public boolean equals(Object object)
	{
		if(object == null) return false;
		if(object == this) return true;
		if(!(object instanceof TextureHandle)) return false;

		TextureHandle th = (TextureHandle) object;
		return th.textureName.equals(this.textureName);
	}


}
