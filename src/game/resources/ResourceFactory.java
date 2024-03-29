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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.google.gson.JsonDeserializer;

import game.debug.Debug;
import game.systems.EntityDef;
import game.util.LoadableModule;
import game.util.LoadingProgress;
import game.util.colors.Colormaps;
import lombok.Getter;

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

	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Strings {
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
	private Class<? extends ResourceSet> resourceSetType;

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

	@Getter private ResourceSet resourceSet;


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
	public static ResourceFactory init( Class<? extends ResourceSet> resourceSetType)
	{
		return init(resourceSetType, new HashMap <>());
	}
	public static ResourceFactory init( Class<? extends ResourceSet> resourceSetType, Map <Class<?>, JsonDeserializer<?>> customJsonDeserializers)
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
		
		for(var entry : factory.resourceSet.getCfgEnums().entrySet())
			customJsonDeserializers.put(entry.getKey(), new JsonEnumDeserializer(entry.getValue()));

		// factory.manager.getLogger().setLevel(Logger.INFO);;
		// add resources to assets manager:
		factory.resolver = new InternalFileHandleResolver();

		factory.jsonLoader = new JsonLoader(factory, factory.resolver, customJsonDeserializers);

		factory.loadConfigurations();

		factory.loadTextures(resourceSetType);

		factory.loadAtlases();
		// factory.loadResources(Animation.class,
		// com.badlogic.gdx.graphics.g2d.Animation.class );
		factory.loadGenericResources(Font.class, com.badlogic.gdx.graphics.g2d.BitmapFont.class);

		factory.loadGenericResources(Skin.class, com.badlogic.gdx.scenes.scene2d.ui.Skin.class);

		factory.loadGenericResources(Sound.class, com.badlogic.gdx.audio.Sound.class);

		factory.loadShaders();

		factory.loadRegions();
		
		factory.loadI18NBundles();

		//factory.loadCustomResources();


		return factory;
	}

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

	public ResourceFactory( Class <? extends ResourceSet> resourceSetType )
	{
		this.resourceSetType = resourceSetType;
		try
		{
			resourceSet = resourceSetType.newInstance();
		} catch (InstantiationException | IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void unload()
	{
		manager.dispose();
		factory = null;
	}
	
	private static interface LoaderMethod <A extends Annotation> {
		public void load(String resourceId, A paramAnno);
	}
	
	private <A extends Annotation> void loadResources( Class<?> annotationType, LoaderMethod<A> method )
	{
		try
		{
			for( Field field : resourceSetType.getDeclaredFields() )
			{
				Annotation[] annos = field.getDeclaredAnnotations();
				for( Annotation anno : annos )
				{
					if( anno.annotationType().equals(annotationType) )
					{
						String resourceId = (String) field.get(null);
						method.load(resourceId, (A) anno);
					}
				}
			}
		}
		catch( IllegalArgumentException e ) { e.printStackTrace(); }
		catch( IllegalAccessException e ) { e.printStackTrace(); }
		
	}

	private void loadGenericResources( Class<?> annotationType, Class<?> resourceType )
	{
		loadResources(annotationType, (resId, anno) ->
			manager.load(resId, resourceType));
	}

	public void loadTextures(Class<?> resourceSetType)
	{
		this.<Texture>loadResources(Texture.class, 
				(resId, anno) -> {
					loadTexture(resId, anno.useMipMap(), anno.priority());
				}
		);
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

		this.<Pixmap>loadResources(Pixmap.class, 
				(resId, anno) -> {
					PixmapLoader.PixmapParameter p = new PixmapLoader.PixmapParameter();
					manager.load( resId, com.badlogic.gdx.graphics.Pixmap.class, p);
				}
		);
	}

	private void loadAtlases()
	{
		this.<Atlas>loadResources(Atlas.class,
				(resId, anno) -> {
					loadAtlas(resId, anno.priority());
				}
		);
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
		
		this.<Shader>loadResources(Shader.class,
				(resId, anno) -> {
					manager.load(
							resId,
							ShaderProgram.class,
							new ShaderParameters(anno.vertex(), anno.fragment())
							);
				}
		);
	}

	private void loadConfigurations()
	{

		manager.setLoader(Configuration.class, jsonLoader);
		this.<Cfg>loadResources(Cfg.class,
				(resId, anno) -> {
					Configuration.Parameter param = new Configuration.Parameter(anno.type());

					manager.load(resId, Configuration.class, param );
				}
		);
	}

	private void loadRegions()
	{
		this.<Region>loadResources(Region.class,
				(resId, anno) -> {
					String atlasName = anno.atlas();
				manager.load(atlasName, TextureAtlas.class);
				regionList.put(resId, anno);

				String textureFile = atlasName.substring(0, atlasName.length()-6) + ".png";
				TextureHandle textureHandle = new TextureHandle( textureFile, anno.priority() );
				if(!textures.contains( textureHandle))
					textures.offer( textureHandle );
			}
		);

	}
	
	private void loadI18NBundles()
	{
		this.<Strings>loadResources(Strings.class,
				(resId, anno) -> {
					manager.load(resId, I18NBundle.class);
				}
		);
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
	
	public static I18NBundle getStrings(String id)
	{
		return factory.manager.get(id);
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

	public static BitmapFont loadFont( String path )
	{
		return new BitmapFont(new FileHandle(path));
	}

	public PriorityQueue <TextureHandle> getTextures()
	{
		return textures;
	}

	private static final float DEFAULT_FRAME_DURATION = 1;


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
	public static <E extends EntityDef> E getEntityDef(String path)
	{
		for(EntityDefGroup group : factory.entityDefGroups.values())
		{
			for(EntityDef entityDef: group.getEntities())
			{
				if(entityDef.getPath().equals(path))
					return (E)entityDef;
			}
		}
		return null;
	}
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
				entityDefs.put(def.getPath(), def);
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
							EntityDefGroup [] groups = ResourceFactory.getConfiguration(filename);
							for(EntityDefGroup group : groups)
							{
								if( group.getName() == null)
									throw new IllegalArgumentException("Missing name for EntityDef group in " + filename);

								// merge if group already loaded (from another file)
								if( entityDefGroups.containsKey(group.getName()))
									entityDefGroups.get(group.getName()).getEntities()
									.addAll(group.getEntities());
								else
									entityDefGroups.put(group.getName(), group);

								for(EntityDef def : group.getEntities())
									def.setPath(group.getName() + "/" + def.getName());
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
