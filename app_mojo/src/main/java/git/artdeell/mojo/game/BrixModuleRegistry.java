package git.artdeell.mojo.game;

import android.content.Context;
import android.content.SharedPreferences;

/** The BRIX modules are real game key actions or launcher touch functions, not injected mods. */
public final class BrixModuleRegistry {
    public static final String[] IDS={"debug_fps","touch_cps","screenshot","camera","clean_hud","inventory","chat","pause","virtual_mouse","touch_controls","hitboxes","chunk_borders","advanced_tooltips","game_logs","edit_controls"};
    public static final String[] NAMES={"Live FPS Counter","Live touch CPS HUD","Minecraft screenshot (F2)","Camera view (F5)","Clean HUD (F1)","Inventory (E)","Game chat (T)","Pause menu (Esc)","Virtual mouse","Touch controls on/off","Hitboxes (F3+B)","Chunk borders (F3+G)","Advanced tooltips (F3+H)","Game console / logs","Edit touch controls"};
    public static final String[] DESCRIPTIONS={"Frames actually presented by the Minecraft TextureView; N/A on other surfaces","CPS counts BRIX primary-button taps, not external mouse clicks","Saved by Minecraft in screenshots folder","Cycles Minecraft perspective","Hide/show Minecraft HUD","Opens inventory if this keybind is unchanged","Opens game chat","Closes UI or opens Minecraft pause menu","Existing virtual mouse control","Hide/show the configured touch buttons","Available in supported Minecraft Java versions","Shows chunk borders in supported versions","Toggles advanced tooltips where supported","Shows the running game's console","Opens the existing BRIX touch editor"};
    private BrixModuleRegistry(){}
    public static boolean enabled(Context c,int index){return c.getSharedPreferences("brix_modules",Context.MODE_PRIVATE).getBoolean(IDS[index],true);}
    public static void setEnabled(Context c,int index,boolean enabled){c.getSharedPreferences("brix_modules",Context.MODE_PRIVATE).edit().putBoolean(IDS[index],enabled).apply();}
}
