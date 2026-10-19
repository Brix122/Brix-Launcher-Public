package git.artdeell.mojo;
import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;
public final class BrixTheme {
  private BrixTheme() {}
  public static boolean dark(Context c){return c.getSharedPreferences("brix_user_theme",Context.MODE_PRIVATE).getBoolean("dark",false);}
  public static void init(Context c){int wanted=dark(c)?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO;
    if(AppCompatDelegate.getDefaultNightMode()!=wanted)AppCompatDelegate.setDefaultNightMode(wanted);}
  public static void toggle(Activity a){boolean value=!dark(a);
    a.getSharedPreferences("brix_user_theme",Context.MODE_PRIVATE).edit().putBoolean("dark",value).apply();
    AppCompatDelegate.setDefaultNightMode(value?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
  }
}
