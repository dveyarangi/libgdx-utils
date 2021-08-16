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
import com.badlogic.gdx.assets.loaders.PixmapLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.gson.JsonDeserializer;

import game.debug.Debug;
import game.systems.EntityDef;
import game.util.Angles;
import game.util.LoadableModule;
import game.util.LoadingProgress;
import game.util.colors.Colormaps;

/**
 * This factory looks for resource annotations on constants in provided resource
 * list class and provides method to load them.
 *
 * @author Fima
 *
 */
public class ResourceFactory implements LoadableModule
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
		boolean useMipMap() default true;
		int priority() default 1;
	}

	/**
	 * Annotation to mark textures:
	 *
	 * <p>
	 * example: @Texture public static String PLAY_BUTTON_IMAGE =
	 * "images/menu/button_play.png";
	 */
	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Pixmap {
		int priority() default 1;
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
		int priority() default 1;
	}

	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Region {
		int priority() default 1;
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
	private final Map<String, Animation<TextureRegion>> animationCache = new HashMap<>();

	private final Map<Object, NamedTextureRegion> regionCache = new HashMap<>();

	private final Map<String, Region> regionList = new HashMap<>();

	private PriorityQueue <TextureHandle> textures = new PriorityQueue <> ();

	//private List <ResourceTypeFactory> customFactories = new ArrayList <> ();

	// /////////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO factory singleton
	private static ResourceFactory factory;


	private JsonLoader jsonLoader;


	private LoadingProgress progress = new LoadingProgress();

	// /////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loads the resources, specified by annotated fields in the provided class.
	 * After this call factory
	 *
	 * @param resourceSetType
	 *            should contain static variables, annotated with resource type
	 *            annotations
	 */
	public static ResourceFactory init( Class<?> resourceSetType)
	{
		return init(resourceSetType, new HashMap <>());
	}
	public static ResourceFactory init( Class<?> resourceSetType, Map <Class<?>, JsonDeserializer<?>> customJsonDeserializers)
	{

		if( factory != null )
		{
			// throw new
			// IllegalStateException("Resource factory is already initialized.");
			factory.unload();
		}

		factory = new ResourceFactory(resourceSetType);

		// register colors deserializer

		customJsonDeserializers.put(Color.class, new Colormaps.LibGDXColorDeserializer());
		customJsonDeserializers.put(Class.class, new JsonClassAdapter());


		// factory.manager.getLogger().setLevel(Logger.INFO);;
		// add resources to assets manager:
		factory.resolver = new InternalFileHandleResolver();

		factory.jsonLoader = new JsonLoader(factory, factory.resolver, customJsonDeserializers);

		factory.loadConfigurations();

		factory.loadTextures(resourceSetType);

		factory.loadAtlases();
		// factory.loadResources(Animation.class,
		// com.badlogic.gdx.graphics.g2d.Animation.class );
		factory.loadResources(Font.class, com.badlogic.gdx.graphics.g2d.BitmapFont.class);

		factory.loadResources(Skin.class, com.badlogic.gdx.scenes.scene2d.ui.Skin.class);

		factory.loadResources(Sound.class, com.badlogic.gdx.audio.Sound.class);

		factory.loadShaders();

		factory.loadRegions();

		//factory.loadCustomResources();


		return factory;
	}

	/**
	 * Register a custom resource factory
	 * @param customFactory
	 */
	/*public void registerCustomResource(ResourceTypeFactory <?,?> customFactory)
	{
		customFactories.add(customFactory);
	}

	private void loadCustomResources()
	{

		for(ResourceTypeFactory <Object, AssetLoaderParameters<Object>> rtFactory : customFactories)
		{

			Class <?> annoClass = rtFactory.getAnnotation();
			manager.setLoader(rtFactory.getResourceType(), rtFactory.createLoader(resolver));
			try
			{
				for( Field field : resourceSetType.getDeclaredFields() )
				{
					Annotation[] annos = field.getDeclaredAnnotations();
					for( Annotation anno : annos )
					{
						if( anno.annotationType().equals(annoClass) )
							manager.load((String) field.get(null), rtFactory.getResourceType());
					}
				}
			}
			catch( IllegalArgumentException e ) { e.printStackTrace(); }
			catch( IllegalAccessException e ) { e.printStackTrace(); }
		}
	}*/


	/**
	 * Load some resources
	 *
	 * @param seconds
	 *            execution time restriction
	 * @return progress in range 0-1
	 */
	@Override
	public LoadingProgress stepLoading( float seconds )
	{
		boolean isFinished = factory.manager.update((int) ( 1000 * seconds ));

		progress.update(factory.manager.getProgress(), "Loading assets...");
		progress.setFinished(isFinished);

		return progress;
	}

	@Override
	public void finishLoading()
	{

		factory.manager.finishLoading();

		mapEntityDefs(); // collect all loaded entityDefGroups to allow access to list of groups
		// this is needed for level saving/loading
		// factory.sound.init(factory);
	}

	public static ResourceFactory getFactory()
	{
		return factory;
	}

	public ResourceFactory( Class <?> resourceSetType )
	{
		this.resourceSetType = resourceSetType;
	}

	public void unload()
	{
		manager.dispose();
		factory = null;
	}

	private void loadResources( Class<?> annotationType, Class<?> resourceType )
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
		}
		catch( IllegalArgumentException e ) { e.printStackTrace(); }
		catch( IllegalAccessException e ) { e.printStackTrace(); }
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

						loadTexture(textureFile, texxanno.useMipMap(), texxanno.priority());
					}
				}
			}
		} catch( IllegalArgumentException e ){e.printStackTrace();
		} catch( IllegalAccessException e ){e.printStackTrace();}
	}
	void loadTexture(String textureFile, boolean useMipMap, int priority)
	{
		TextureLoader.TextureParameter p = new TextureLoader.TextureParameter();
		p.genMipMaps = useMipMap;

		TextureHandle textureHandle = new TextureHandle(textureFile, priority);

		if(!textures.contains( textureHandle))
		{
			textures.offer( textureHandle );
			manager.load( textureFile, com.badlogic.gdx.graphics.Texture.class, p);
		}
	}

	public void loadPixmaps()
	{
		try
		{
			for( Field field : resourceSetType.getDeclaredFields() )
			{
				Annotation[] annos = field.getDeclaredAnnotations();
				for( Annotation anno : annos )
				{
					if( anno instanceof Pixmap )
					{
						//Pixmap pixanno = (Pixmap) anno;
						String file = (String) field.get(null);

						PixmapLoader.PixmapParameter p = new PixmapLoader.PixmapParameter();
						manager.load( file, com.badlogic.gdx.graphics.Pixmap.class, p);
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
						String atlasName = (String) field.get(null);
						loadAtlas(atlasName, atlanno.priority());
					}
				}
			}
		} catch( IllegalArgumentException e ) { e.printStackTrace();
		} catch( IllegalAccessException e ) { e.printStackTrace(); }
	}

	void loadAtlas(String atlasName, int proirity)
	{
		manager.load( atlasName, TextureAtlas.class );
		String textureFile = atlasName.substring(0, atlasName.length()-6) + ".png";
		TextureHandle textureHandle = new TextureHandle( textureFile, proirity );
		if(!textures.contains( textureHandle))
			textures.offer( textureHandle );
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

		manager.setLoader(Configuration.class, jsonLoader);

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

						String filename = (String) field.get(null);
						Configuration.Parameter param = new Configuration.Parameter(cfganno.type());
						//FileHandle configFile = resolver.resolve(filename);

						//jsonLoader.getDependencies(filename, configFile, param);

						manager.load(filename,
								Configuration.class,
								param
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

	public static com.badlogic.gdx.graphics.Texture getTexture( String textureName )
	{
		try {
			com.badlogic.gdx.graphics.Texture texture = factory.manager.get(textureName);
			return texture;
		}
		catch(GdxRuntimeException e)
		{
			Debug.log(e.getMessage() +"; check definitions in " + factory.resourceSetType.getName());
			throw new IllegalArgumentException(e);
		}
	}

	public static com.badlogic.gdx.graphics.Pixmap getPixmap( String pixmap )
	{
		try {
			return factory.manager.get(pixmap);
		}
		catch(GdxRuntimeException e)
		{
			Debug.log(e.getMessage() +"; check definitions in " + factory.resourceSetType.getName());
			throw new IllegalArgumentException(e);
		}
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
	public Animation <TextureRegion> getAnimation( String atlasName )
	{
		Animation <TextureRegion> animation = animationCache.get(atlasName);
		if( animation == null )
		{
			com.badlogic.gdx.graphics.g2d.TextureAtlas atlas = getTextureAtlas(atlasName);
			animation = new Animation<>(DEFAULT_FRAME_DURATION, atlas.getRegions());
			animationCache.put(atlasName, animation);
		}
		return animation;
	}

	// public SoundProvider getSoundProvider() { return sound; }

	public static NamedTextureRegion getTextureRegion( String textureName )
	{
		NamedTextureRegion region;
		if( factory.regionList.containsKey(textureName) )
		{
			Region regianno = factory.regionList.get(textureName);
			TextureAtlas atlas = factory.manager.get(regianno.atlas());
			region = new NamedTextureRegion(new TextureRegionName(textureName, null),
					atlas.findRegion(regianno.name()));
		}
		else
		{
			region = factory.regionCache.get(textureName);
			if( region == null )
			{
				region = new NamedTextureRegion(new TextureRegionName(textureName, null),
						new TextureRegion(getTexture(textureName)));
			}
		}

		factory.regionCache.put(textureName, region);

		return region;
	}

	public static NamedTextureRegion getTextureRegion( TextureRegionName regionName )
	{
		NamedTextureRegion result = factory.regionCache.get(regionName);
		if( result != null)
			return result;
		TextureAtlas atlas = factory.manager.get(regionName.atlasName);
		TextureRegion region = atlas.findRegion(regionName.regionName);
		result = new NamedTextureRegion(regionName, region);
		factory.regionCache.put(regionName, result);
		return result;
	}

	private Map <String, EntityDefGroup> entityDefGroups = new HashMap <> ();
	public static <E extends EntityDef> Map <String,E> getEntityDefs(Class<?> type)
	{
		Map <String,E> entityDefs = new HashMap <> ();
		for(EntityDefGroup group : factory.entityDefGroups.values())
		{
			for(EntityDef entityDef: group.getEntities())
			{
				if( ! (entityDef.getClass().isAssignableFrom(type) ) )
					continue;

				E def = (E)entityDef;
				if( def.getName() == null)
					throw new IllegalArgumentException("Nameless entity def in group " + group.getName());
				entityDefs.put(def.getType(), def);
			}
		}
		return entityDefs;
	}

	private void mapEntityDefs()
	{
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

						String filename = (String) field.get(null);

						if(cfganno.type().isAssignableFrom(EntityDefGroup.class))
						{
							EntityDefGroup [] groups = this.getConfiguration(filename);
							for(EntityDefGroup group : groups)
							{
								if( group.getName() == null)
									throw new IllegalArgumentException("Missing name for EntityDef group in " + filename);

								entityDefGroups.put(group.getName(), group);
								for(EntityDef def : group.getEntities())
									def.setType(group.getName() + "/" + def.getName());
							}
						}

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

		loadConfigurations();
	}

	@SuppressWarnings("unchecked")
	public static <O> O getResource(String name)
	{
		try {
			return (O) factory.manager.get(name);
		}
		catch(GdxRuntimeException e)
		{
			throw new GdxRuntimeException(e.getMessage() + "\n"
					+ "Check whether it is defined in " + factory.resourceSetType.getCanonicalName() + " class.", e);
		}
	}

}
