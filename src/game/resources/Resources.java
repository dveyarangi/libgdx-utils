package game.resources;

import game.resources.ResourceFactory.Atlas;
import game.resources.ResourceFactory.Font;
import game.resources.ResourceFactory.Shader;
import game.resources.ResourceFactory.Sound;
import game.resources.ResourceFactory.Texture;

/**
 * TODO: remove unused assets
 */
public class Resources
{

	// @Skin public static final String MAIN_MENU_SKIN = "skins/menuskin.json";

	@Font
	public static final String FONT_DIGITS_60 = "fonts/atarian_digits_60.fnt";
	@Font
	public static final String FONT_62 = "fonts/as_distance_bold_60_2.fnt";

	@Texture
	public static final String LOTUS = "images//lotus.png";

	@Texture
	public static final String MENU_BACKGROUND = "images//menu//background01.png";
	@Texture
	public static final String MENU_PLAY_ARROW = "images//menu//play_arrow.png";
	@Texture
	public static final String MENU_ACHIEVEMENTS_BUTTON = "images/menu/ic_play_games_badge_achievements_white.png";
	@Texture
	public static final String MENU_LEADERBOARD_BUTTON = "images/menu/ic_play_games_badge_leaderboards_white.png";

	@Texture
	public static String RESTART_BUTTON_IMAGE = "images/menu/colorbutts/button_restart.png";
	// @Texture public static String OPTIONS_BUTTON_IMAGE =
	// "images/menu/colorbutts/button_options.png";
	@Texture
	public static String SOUND_BUTTON_IMAGE = "images/menu/colorbutts/button_sound.png";
	@Texture
	public static String UNSOUND_BUTTON_IMAGE = "images/menu/colorbutts/button_unsound.png";
	// @Texture public static String PAUSE_BUTTON_IMAGE =
	// "images/menu/colorbutts/button_pause.png";
	// @Texture public static String BACK_BUTTON_IMAGE =
	// "images/menu/colorbutts/button_back.png";
	@Texture
	public static String EXIT_BUTTON_IMAGE = "images/menu/colorbutts/button_exit.png";

	// @Texture public static final String DIALOG_9P =
	// "images/menu/dialog02.9.png";
	// @Texture public static final String DIALOG_TRIANGLE =
	// "images/menu/dialog_triangle.png";
	// @Texture public static final String DIALOG_BACKGROUND =
	// "images/menu/dialog_background.png";

	@Texture
	public static String DRAGON_WINGS = "images/dragon/dragon_wings.png";
	// @Texture public static String DRAGON_WINGS_MASK =
	// "images/dragon/dragon_wings_mask.png";
	@Texture
	public static String DRAGON_HEAD_RED = "images/dragon/dragon_head_red.png";
	@Texture
	public static String DRAGON_HEAD_ORANGE = "images/dragon/dragon_head_orange.png";
	// @Texture public static String DRAGON_HEAD_ORANGE_MASK =
	// "images/dragon/dragon_head_orange_mask.png";
	@Texture
	public static String DRAGON_HEAD_BLACK = "images/dragon/dragon_head_black.png";
	@Texture
	public static String DRAGON_SEGMENT_LIGHT = "images/dragon/dragon_segment_light.png";

	@Texture
	public static String OBSTACLE_MASK = "images/obstacles/obstacle01.png";
	@Texture
	public static String OBSTACLE_NORMAL = "images/obstacles/obstacle01.png";
	@Texture
	public static String OBSTACLE_HARD = "images/obstacles/obstacle01.png";
	@Texture
	public static String OBSTACLE_SOFT = "images/obstacles/obstacle01.png";

	// @Texture public static String ORNAMENT_LINE =
	// "images/tunnel/ornament_line_03.png";
	@Texture
	public static String TUNNEL_WALL = "images/tunnel/tunnel10.png";
	@Atlas
	public static final String ORNAMENT_ATLAS = "images/tunnel/ornament.atlas";

	@Texture
	public static String DOT = "images/dot_64x64.png";

	@Sound
	public static final String COLLISION_SOUND_01 = "sound/collision_01.ogg";
	@Sound
	public static final String COLLISION_SOUND_02 = "sound/collision_02.ogg";
	@Sound
	public static final String COLLISION_SOUND_03 = "sound/collision_03.ogg";
	@Sound
	public static final String COLLISION_SOUND_04 = "sound/collision_04.ogg";
	@Sound
	public static final String COLLISION_SOUND_05 = "sound/collision_05.ogg";
	@Sound
	public static final String COLLISION_SOUND_06 = "sound/collision_06.ogg";
	@Sound
	public static final String COLLISION_SOUND_07 = "sound/collision_07.ogg";
	@Sound
	public static final String CONSUMPTION_SOUND_01 = "sound/consumption_01.ogg";
	@Sound
	public static final String CONSUMPTION_SOUND_02 = "sound/consumption_02.ogg";
	@Sound
	public static final String CONSUMPTION_SOUND_03 = "sound/consumption_03.ogg";
	@Sound
	public static final String CONSUMPTION_SOUND_04 = "sound/consumption_04.ogg";
	@Sound
	public static final String CONSUMPTION_SOUND_05 = "sound/consumption_05.ogg";
	@Sound
	public static final String CONSUMPTION_SOUND_06 = "sound/consumption_06.ogg";
	@Sound
	public static final String CONSUMPTION_SOUND_07 = "sound/consumption_07.ogg";

	/*
	 * @Shader( fragment = "shaders//coloring_frag.glsl", vertex =
	 * "shaders//coloring_vertex.glsl" ) public static final String
	 * COLORING_SHADER = "colorshade";
	 */
	@Shader(
			fragment = "shaders//bicoloring_frag.glsl",
			vertex = "shaders//bicoloring_vertex.glsl" )
	public static final String BICOLORING_SHADER = "bicolorshade";

	/*
	 * @Shader( fragment = "shaders//colormesh_frag.glsl", vertex =
	 * "shaders//colormesh_vertex.glsl" ) public static final String
	 * COLORMESH_SHADER = "colormesh";
	 */

	@Shader(
			fragment = "shaders//distance_field_frag.glsl",
			vertex = "shaders//distance_field_vertex.glsl" )
	public static final String DISTANCE_FIELD_SHADER = "distance_field";

	/*
	 * @Shader( fragment = "shaders//alpha_frag.glsl", vertex =
	 * "shaders//alpha_vertex.glsl" ) public static final String ALPHA_SHADER =
	 * "aplha";
	 */

}
