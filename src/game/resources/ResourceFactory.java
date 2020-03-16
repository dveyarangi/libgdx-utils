package game.resources;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import game.util.Angles;

/**
 * This factory looks for resource annotations on constants in provided resource
 * list class and provides method to load them.
 *
 * @author Fima
 *
 */
public class ResourceFactory
{
	// /////////////////////////////////////////////////////////////////////////////////////////////////////
	// RESOURCE ANNOTATIONS:


	/**
	 * Annotation to mark textures:
	 *
	 * <p>
	 * example: @Texture public static String PLAY_BUTTON_IMAGE =
	 * "images/menu/button_play.png";
	 */
	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Texture {
		boolean useMipMap() default false;
		int priority() default 0;
	}

	/**
	 * Annotation to mark texture atlases:
	 *
	 * <p>
	 * example: @Texture public static String PLAY_BUTTON_IMAGE =
	 * "images/menu/button_play.png";
	 */
	// @Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME) public
	// static @interface Animation { }

	/**
	 * Annotation to mark skins
	 *
	 * <p>
	 * example: @Skin public static final String MAIN_MENU_SKIN =
	 * "skins/menuskin.json";
	 */
	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Skin {
		int priority() default 0;
	}

	/**
	 * Annotation to mark fonts
	 *
	 * <p>
	 * example: @Font public static final String FONT_32 =
	 * "fonts/as_bold_32.fnt";
	 */
	@Target( ElementType.FIELD )@Retention( RetentionPolicy.RUNTIME ) public static @interface Font {
		int priority() default 0;
	}

	/**
	 * Annotation to mark shaders.
	 *
	 * <p>
	 * example:
	 *
	 * @Shader( fragment = "shaders//coloring_frag.glsl", vertex =
	 *          "shaders//coloring_vertex.glsl" ) public static final String
	 *          COLORING_SHADER = "colorshade";
	 *
	 */
	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Shader {
		String fragment();

		String vertex();
	}

	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Atlas {
		int priority() default 0;
	}

	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Region {
		int priority() default 0;
		String atlas();
		String name();
	}

	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Sound {
		int priority() default 0;
	}
	
	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Cfg {
		Class <?>type();
		int priority() default 0;
	}


	// /////////////////////////////////////////////////////////////////////////////////////////////////////
	// FACTORY MEMBERS:
	/**
	 * Asset manager
	 */
	private AssetManager manager = new AssetManager();

	/**
	 * Resources list type
	 */
	private Class<?> resourceSetType;

	private FileHandleResolver resolver;

	/**
	 * Loaded textures by name (filename, actually)
	 */
	private final Map<String, Animation> animationCache = new HashMap<String, Animation>();

	private final Map<String, TextureRegion> regionCache = new HashMap<String, TextureRegion>();

	private final Map<String, Region> regionList = new HashMap<String, Region>();

	private PriorityQueue <TextureHandle> textures = new PriorityQueue <TextureHandle> ();

	// /////////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO factory singleton
	private static ResourceFactory factory;

	// /////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads the resources, specified by annotated fields in the provided class.
	 * After this call factory
	 *
	 * @param resourceSetType
	 *            should contain static variables, annotated with resource type
	 *            annotations
	 */
	public static ResourceFactory init( Class<?> resourceSetType )
	{

		if( factory != null )
		{
			// throw new
			// IllegalStateException("Resource factory is already initialized.");
			factory.unload();
		}

		factory = new ResourceFactory(resourceSetType);

		// factory.manager.getLogger().setLevel(Logger.INFO);;
		// add resources to assets manager:
		factory.resolver = new InternalFileHandleResolver();

		factory.loadTextures(resourceSetType);

		factory.loadAtlases();
		// factory.loadResources(Animation.class,
		// com.badlogic.gdx.graphics.g2d.Animation.class );
		factory.loadResources(Font.class, com.badlogic.gdx.graphics.g2d.BitmapFont.class);

		factory.loadResources(Skin.class, com.badlogic.gdx.scenes.scene2d.ui.Skin.class);

		factory.loadResources(Sound.class, com.badlogic.gdx.audio.Sound.class);

		factory.loadShaders();

		factory.loadRegions();
		
		factory.loadConfigurations();

		// load colorfiles:
		// TODO: to separate loader
		// Colors.init();

		return factory;
	}


	/**
	 * Load some resources
	 *
	 * @param seconds
	 *            execution time restriction
	 * @return progress in range 0-1
	 */
	public float stepLoading( float seconds )
	{
		boolean isFinished = factory.manager.update((int) ( 1000 * seconds ));

		return (float) ( isFinished ? 1.0 : factory.manager.getProgress() );
	}

	public void finishLoading()
	{
		factory.manager.finishLoading();
		;

		// factory.sound.init(factory);

	}

	public static ResourceFactory getFactory()
	{
		return factory;
	}

	public ResourceFactory( Class resourceSetType )
	{
		this.resourceSetType = resourceSetType;
	}

	public void unload()
	{
		manager.dispose();
		factory = null;
	}

	@SuppressWarnings( "unchecked" )
	private void loadResources( Class annotationType, Class resourceType )
	{
		try
		{
			for( Field field : resourceSetType.getDeclaredFields() )
			{
				Annotation[] annos = field.getDeclaredAnnotations();
				for( Annotation anno : annos )
				{
					if( anno.annotationType().equals(annotationType) )
						manager.load((String) field.get(null), resourceType);
				}
			}
		} catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
	}
	
	public void loadTextures(Class<?> resourceSetType)
	{
		try
		{
			for( Field field : resourceSetType.getDeclaredFields() )
			{
				Annotation[] annos = field.getDeclaredAnnotations();
				for( Annotation anno : annos )
				{
					if( anno instanceof Texture )
					{
						Texture texxanno = (Texture) anno;
						String textureFile = (String) field.get(null);
						
						TextureLoader.TextureParameter p = new TextureLoader.TextureParameter();
						p.genMipMaps = texxanno.useMipMap();
						
						manager.load( textureFile, com.badlogic.gdx.graphics.Texture.class, p);

						TextureHandle textureHandle = new TextureHandle(textureFile, texxanno.priority());

						if(!textures.contains( textureHandle))
							textures.offer( textureHandle );
					}
				}
			}
		} catch( IllegalArgumentException e ){e.printStackTrace();
		} catch( IllegalAccessException e ){e.printStackTrace();}
	}
	private void loadAtlases()
	{
		try
		{
			for( Field field : resourceSetType.getDeclaredFields() )
			{
				Annotation[] annos = field.getDeclaredAnnotations();
				for( Annotation anno : annos )
				{
					if( anno instanceof Atlas )
					{
						Atlas atlanno = (Atlas) anno;
						String atlasFile = (String) field.get(null);
						manager.load( atlasFile, TextureAtlas.class );

						String textureFile = atlasFile.substring(0, atlasFile.length()-6) + ".png";
						TextureHandle textureHandle = new TextureHandle( textureFile, atlanno.priority() );
						if(!textures.contains( textureHandle))
							textures.offer( textureHandle );
					}
				}
			}
		} catch( IllegalArgumentException e ) { e.printStackTrace();
		} catch( IllegalAccessException e ) { e.printStackTrace(); }
	}

	private void loadShaders()
	{

		manager.setLoader(ShaderProgram.class, new ShaderLoader(resolver));

		try
		{
			for( Field field : resourceSetType.getDeclaredFields() )
			{
				Annotation[] annos = field.getDeclaredAnnotations();
				for( Annotation anno : annos )
				{
					if( anno instanceof Shader )
					{
						Shader shaderanno = (Shader) anno;
						manager.load(
								(String) field.get(null),
								ShaderProgram.class,
								new ShaderParameters(shaderanno.vertex(), shaderanno.fragment())
								);
					}
				}
			}
		} catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}

	}
	
	private void loadConfigurations()
	{

		manager.setLoader(Configuration.class, new JsonLoader(resolver));

		try
		{
			for( Field field : resourceSetType.getDeclaredFields() )
			{
				Annotation[] annos = field.getDeclaredAnnotations();
				for( Annotation anno : annos )
				{
					if( anno instanceof Cfg )
					{
						Cfg cfganno = (Cfg) anno;
						manager.load(
								(String) field.get(null),
								Configuration.class,
								new Configuration.Parameter(cfganno.type())
								);
					}
				}
			}
		} catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}

	}

	private void loadRegions()
	{
		try
		{
			for( Field field : resourceSetType.getDeclaredFields() )
			{
				Annotation[] annos = field.getDeclaredAnnotations();
				for( Annotation anno : annos )
				{
					if( anno instanceof Region )
					{
						Region regianno = (Region) anno;

						String atlasName = regianno.atlas();
						manager.load(atlasName, TextureAtlas.class);
						regionList.put((String) field.get(null), regianno);

						String textureFile = atlasName.substring(0, atlasName.length()-6) + ".png";
						TextureHandle textureHandle = new TextureHandle( textureFile, regianno.priority() );
						if(!textures.contains( textureHandle))
							textures.offer( textureHandle );
					}
				}
			}
		} catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		} catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
	}

	public static BitmapFont getFont( String fontPath )
	{
		return factory.manager.get(fontPath);
	}

	public static ShaderProgram getShader( String id )
	{
		return factory.manager.get(id);
	}
	
	public static <T> T getConfiguration( String id )
	{
		return ((Configuration)factory.manager.get(id)).getObject();
	}

	public static com.badlogic.gdx.graphics.Texture getTexture( String texture )
	{
		return factory.manager.get(texture);
	}

	public static com.badlogic.gdx.scenes.scene2d.ui.Skin getSkin( String skin )
	{
		return factory.manager.get(skin);
	}

	public static com.badlogic.gdx.graphics.g2d.TextureAtlas getTextureAtlas( String atlas )
	{
		return factory.manager.get(atlas);
	}

	public static com.badlogic.gdx.audio.Sound getSound( String sound )
	{
		return factory.manager.get(sound);
	}

	public static Sprite createCenteredSprite( String textureName, float size )
	{
		Sprite sprite;
		float halfsize = size / 2;
		// Vector2 position = game.getPlayerBody().getPosition();
		com.badlogic.gdx.graphics.Texture texture = ResourceFactory.getTexture(textureName);
		sprite = new Sprite(texture);
		sprite.setOrigin(halfsize, halfsize);
		sprite.setRotation(0 * Angles.TO_DEG);
		sprite.setScale(size / texture.getWidth(), size / texture.getHeight());

		return sprite;
	}

	public static BitmapFont loadFont( String path )
	{
		return new BitmapFont(new FileHandle(path));
	}

	public PriorityQueue <TextureHandle> getTextures()
	{
		return textures;
	}

	private static final float DEFAULT_FRAME_DURATION = 1;

	/*
	 * public Animation getAnimation(TextureAtlas atlas) { new
	 * Animation(DEFAULT_FRAME_DURATION, atlas.getRegions()); return null; }
	 */

	/*
	 * private Animation createAnimation(final AnimationHandle handle) {
	 * TextureAtlas atlas = manager.get( handle.getAtlas().getPath(),
	 * TextureAtlas.class );
	 *
	 * int size = atlas.getRegions().size; TextureRegion[] frames = new
	 * TextureRegion[size];
	 *
	 * for(int fidx = 0; fidx < size; fidx ++) { frames[fidx] =
	 * atlas.findRegion( handle.getRegionName() + "." +
	 * ANIMA_NUMBERING.format(fidx) ); if(frames[fidx] == null) throw new
	 * IllegalArgumentException( "Region array " + handle.getRegionName() +
	 * " was not found in atlas " + handle ); }
	 *
	 * Animation animation = new Animation( 0.05f, frames ); return animation; }
	 */
	/*
	 * public Animation getAnimation( final AnimationHandle handle ) {
	 *
	 * }
	 */
	public Animation getAnimation( String atlasName )
	{
		Animation animation = animationCache.get(atlasName);
		if( animation == null )
		{
			com.badlogic.gdx.graphics.g2d.TextureAtlas atlas = getTextureAtlas(atlasName);
			animation = new Animation(DEFAULT_FRAME_DURATION, atlas.getRegions());
			animationCache.put(atlasName, animation);
		}
		return animation;
	}

	// public SoundProvider getSoundProvider() { return sound; }

	public com.badlogic.gdx.graphics.g2d.TextureRegion getTextureRegion( String name )
	{
		TextureRegion region;
		if( regionList.containsKey(name) )
		{
			Region regianno = regionList.get(name);
			TextureAtlas atlas = manager.get(regianno.atlas());
			region = atlas.findRegion(regianno.name());
		}
		else
		{
			region = regionCache.get(name);
			if( region == null )
			{
				region = new TextureRegion(getTexture(name));
				regionCache.put(name, region);
			}
		}

		return region;

	}

}
