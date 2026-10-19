package git.artdeell.mojo.prefs.screens;


import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import git.artdeell.mojo.LauncherActivity;
import git.artdeell.mojo.R;
import git.artdeell.mojo.prefs.LauncherPreferences;

/**
 * Preference for the main screen, any sub-screen should inherit this class for consistent behavior,
 * overriding only onCreatePreferences
 */
public class LauncherPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    protected Runnable mVisibilityUpdater = () -> {};

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.setBackgroundColor(getResources().getColor(R.color.background_app));
        super.onViewCreated(view, savedInstanceState);
        // BRIX_PREMIUM_SETTINGS_ROWS — view styling only; preference actions retained.
        androidx.recyclerview.widget.RecyclerView list = getListView();
        // BRIX_SETTINGS_CONTRAST_V8: restore clear icons + readable preference titles/summaries.
        list.addOnChildAttachStateChangeListener(new androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener() {
            @Override public void onChildViewAttachedToWindow(@NonNull View child) {
                int fg=getResources().getColor(R.color.brix_text), sub=getResources().getColor(R.color.brix_subtext);
                android.widget.TextView heading=child.findViewById(android.R.id.title);
                android.widget.TextView summary=child.findViewById(android.R.id.summary);
                if(heading!=null)heading.setTextColor(fg);
                if(summary!=null)summary.setTextColor(sub);
                android.widget.ImageView icon=child.findViewById(android.R.id.icon);
                if(icon!=null && icon.getDrawable()!=null){
                    androidx.core.graphics.drawable.DrawableCompat.setTint(icon.getDrawable().mutate(),getResources().getColor(R.color.brix_accent));
                    icon.setAlpha(1f);
                }
            }
            @Override public void onChildViewDetachedFromWindow(@NonNull View child) {}
        });
        int pad = (int)(12 * getResources().getDisplayMetrics().density);
        list.setPadding(pad, pad, pad, pad);
        list.setClipToPadding(false);
        list.setBackgroundColor(getResources().getColor(R.color.brix_page));
        list.addItemDecoration(new androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            final android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
            @Override public void onDraw(android.graphics.Canvas canvas, androidx.recyclerview.widget.RecyclerView parent, androidx.recyclerview.widget.RecyclerView.State state) {
                paint.setColor(getResources().getColor(R.color.brix_tile));
                for (int k=0;k<parent.getChildCount();k++) {
                    View child = parent.getChildAt(k);
                    canvas.drawRoundRect(child.getLeft()+2, child.getTop()+3, child.getRight()-2, child.getBottom()-3, 14, 14, paint);
                }
            }
        });

    }

    @Override
    public void onCreatePreferences(Bundle b, String str) {
        mVisibilityUpdater = this::updateVisibility;
        addPreferencesFromResource(R.xml.pref_main);
        addBrixQuickAccess();
        setupNotificationRequestPreference();
    }

    /** Fully functional shortcuts layered over the existing Java/video/control preferences. */
    private void addBrixQuickAccess(){
        androidx.preference.PreferenceCategory group=new androidx.preference.PreferenceCategory(requireContext());
        group.setTitle("BRIX  •  USEFUL TOOLS");
        getPreferenceScreen().addPreference(group);
        addBrixShortcut(group,"Skin Studio","Import a Java 64×64/legacy skin and rotate it",()->
            startActivity(new android.content.Intent(requireContext(),git.artdeell.mojo.BrixSkinActivity.class)));
        addBrixShortcut(group,"FPS / Useful Mods","Browse performance mods and compatible loaders",()->
            startActivity(new android.content.Intent(requireContext(),git.artdeell.mojo.BrixModsActivity.class)));
        addBrixShortcut(group,"Game modules & controls","Configure 15 in-game tools and edit button maps",()->
            startActivity(new android.content.Intent(requireContext(),git.artdeell.mojo.CustomControlsActivity.class)));
        addBrixShortcut(group,"Switch Light / Dark","Changes launcher UI, dialogs and module panels",()->
            git.artdeell.mojo.BrixTheme.toggle(requireActivity()));
        addBrixShortcut(group,"Share diagnostic log","Useful for crashes, renderer and downloads",()->
            git.artdeell.mojo.Tools.shareLog(requireContext()));
        addBrixShortcut(group,"Credits & licenses","BrixTools design credits and upstream project notices",()->
            startActivity(new android.content.Intent(requireContext(),git.artdeell.mojo.BrixCreditsActivity.class)));
    }
    private void addBrixShortcut(androidx.preference.PreferenceCategory group,String title,String description,Runnable action){
        androidx.preference.Preference pref=new androidx.preference.Preference(requireContext());
        pref.setTitle(title);pref.setSummary(description);pref.setSelectable(true);
        int icon=R.drawable.brix_glass_ic_settings;
        if(title.contains("Skin")) icon=R.drawable.brix_glass_ic_skin;
        else if(title.contains("Mods")) icon=R.drawable.brix_glass_ic_mods;
        else if(title.contains("modules")) icon=R.drawable.brix_glass_ic_controls;
        else if(title.contains("Light")) icon=R.drawable.brix_theme_icon;
        else if(title.contains("log")) icon=R.drawable.brix_glass_ic_logs;
        else if(title.contains("Credits")) icon=R.drawable.brix_glass_ic_info;
        pref.setIcon(icon);
        pref.setOnPreferenceClickListener(p->{action.run();return true;});group.addPreference(pref);
    }

    private void updateVisibility(){
        requirePreference("notification_permission_request").setVisible(!getLauncherActivity().checkForPermission(33, Manifest.permission.POST_NOTIFICATIONS));
    }

    private void setupNotificationRequestPreference() {
        Preference mRequestNotificationPermissionPreference = requirePreference("notification_permission_request");
        Activity activity = getActivity();
        if(activity instanceof LauncherActivity) {
            mRequestNotificationPermissionPreference.setOnPreferenceClickListener(preference -> {
                ((LauncherActivity) activity).askForPermission(33, Manifest.permission.POST_NOTIFICATIONS);
                return true;
            });
        }else{
            mRequestNotificationPermissionPreference.setVisible(false);
        }
        updateVisibility();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        if(sharedPreferences != null) sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mVisibilityUpdater.run();
    }

    @Override
    public void onPause() {
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        if(sharedPreferences != null) sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        LauncherPreferences.loadPreferences(getContext());
    }

    protected Preference requirePreference(CharSequence key) {
        Preference preference = findPreference(key);
        if(preference != null) return preference;
        throw new IllegalStateException("Preference "+key+" is null");
    }
    @SuppressWarnings("unchecked")
    protected <T extends Preference> T requirePreference(CharSequence key, Class<T> preferenceClass) {
        Preference preference = requirePreference(key);
        if(preferenceClass.isInstance(preference)) return (T)preference;
        throw new IllegalStateException("Preference "+key+" is not an instance of "+preferenceClass.getSimpleName());
    }
    protected LauncherActivity getLauncherActivity(){
        return ((LauncherActivity) getActivity());
    }
}
