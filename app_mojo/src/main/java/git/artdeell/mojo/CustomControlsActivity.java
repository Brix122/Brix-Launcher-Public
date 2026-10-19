package git.artdeell.mojo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.JsonSyntaxException;

import git.artdeell.mojo.input.ControlData;
import git.artdeell.mojo.input.ControlDrawerData;
import git.artdeell.mojo.input.ControlJoystickData;
import git.artdeell.mojo.input.ControlLayout;
import git.artdeell.mojo.input.EditorExitable;
import git.artdeell.mojo.prefs.LauncherPreferences;
import git.artdeell.mojo.utils.CropperUtils;

import java.io.IOException;


public class CustomControlsActivity extends BaseActivity implements EditorExitable, CropperUtils.CropperReceiver {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerNavigationView;
	private ControlLayout mControlLayout;
	private CropperUtils.CropperReceiver mCropperReceiver;
	private ActivityResultLauncher<?> mCropperLauncher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCropperLauncher = CropperUtils.registerCropper(this, this);

		setContentView(R.layout.activity_custom_controls);

		mControlLayout = findViewById(R.id.customctrl_controllayout);
		mDrawerLayout = findViewById(R.id.customctrl_drawerlayout);
		mDrawerNavigationView = findViewById(R.id.customctrl_navigation_view);
		View mPullDrawerButton = findViewById(R.id.drawer_button);

		mPullDrawerButton.setOnClickListener(v -> mDrawerLayout.openDrawer(mDrawerNavigationView));
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // BRIX_DUAL_CTRL_SETUP: two functional drawer sides, identical safe editor actions.
        ListView brixLeft = findViewById(R.id.brix_ctrl_left_nav);
        brixLeft.setAdapter(new ArrayAdapter<>(this, R.layout.brix_final_menu_item, getResources().getStringArray(R.array.menu_customcontrol_customactivity)));
        findViewById(R.id.brix_ctrl_tools_toggle).setOnClickListener(v -> showBrixEditorMenu(true));
        findViewById(R.id.brix_ctrl_settings_toggle).setOnClickListener(v -> showBrixEditorMenu(false));
		mDrawerNavigationView.setAdapter(new ArrayAdapter<>(this, R.layout.brix_final_menu_item,getResources().getStringArray(R.array.menu_customcontrol_customactivity)));
		mDrawerNavigationView.setOnItemClickListener((parent, view, position, id) -> {
			switch(position) {
				case 0: mControlLayout.addControlButton(new ControlData("New")); break;
				case 1: mControlLayout.addDrawer(new ControlDrawerData()); break;
				case 2: mControlLayout.addJoystickButton(new ControlJoystickData()); break;
				case 3: mControlLayout.openLoadDialog(); break;
				case 4: mControlLayout.openSaveDialog(this); break;
				case 5: mControlLayout.openSetDefaultDialog(); break;
				case 6: // Saving the currently shown control
					try {
						Uri contentUri = DocumentsContract.buildDocumentUri(getString(R.string.storageProviderAuthorities), mControlLayout.saveToDirectory(mControlLayout.mLayoutFileName));

						Intent shareIntent = new Intent();
						shareIntent.setAction(Intent.ACTION_SEND);
						shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
						shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						shareIntent.setType("application/json");
						startActivity(shareIntent);

						Intent sendIntent = Intent.createChooser(shareIntent, mControlLayout.mLayoutFileName);
						startActivity(sendIntent);
					}catch (Exception e) {
						Tools.showError(this, e);
					}
					break;
			}
			mDrawerLayout.closeDrawers();
		});
        brixLeft.setOnItemClickListener((parent, view, position, id) -> {
            // Duplicate the right menu's supported operations with their real backing actions.
            switch(position) {
                case 0: mControlLayout.addControlButton(new ControlData("New")); break;
                case 1: mControlLayout.addDrawer(new ControlDrawerData()); break;
                case 2: mControlLayout.addJoystickButton(new ControlJoystickData()); break;
                case 3: mControlLayout.openLoadDialog(); break;
                case 4: mControlLayout.openSaveDialog(this); break;
                case 5: mControlLayout.openSetDefaultDialog(); break;
                case 6:
                    try {
                        Uri contentUri = DocumentsContract.buildDocumentUri(getString(R.string.storageProviderAuthorities), mControlLayout.saveToDirectory(mControlLayout.mLayoutFileName));
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.setType("application/json");
                        startActivity(Intent.createChooser(shareIntent, mControlLayout.mLayoutFileName));
                    } catch(Exception e) { Tools.showError(this, e); }
                    break;
            }
            mDrawerLayout.closeDrawers();
        });
		mControlLayout.setModifiable(true);
	}

    private void showBrixEditorMenu(boolean tools){
        if(tools){ showBrixModulesEditor(); return; }
        boolean dark=BrixTheme.dark(this);
        int background=dark?0xFF141820:0xFFF6F7FA;
        int buttonColor=dark?0xFF222630:0xFFFFFFFF;
        int border=dark?0xFF414858:0xFFD7DEE9;
        int textColor=dark?android.graphics.Color.WHITE:0xFF20252D;
        int smallColor=dark?0xFFADB6C9:0xFF657283;
        android.app.Dialog dialog=new android.app.Dialog(this);dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        android.widget.LinearLayout panel=new android.widget.LinearLayout(this);panel.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad=(int)(getResources().getDisplayMetrics().density*16);panel.setPadding(pad,pad,pad,pad);
        android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();bg.setColor(background);bg.setCornerRadius(pad*2);bg.setStroke(1,border);panel.setBackground(bg);
        android.widget.TextView heading=new android.widget.TextView(this);heading.setText(tools?"BRIX  /  CONTROL BUILDER":"BRIX  /  CONTROL SETTINGS");
        heading.setTextColor(0xFFED243A);heading.setTextSize(19);heading.setTypeface(null,1);panel.addView(heading);
        android.widget.TextView subtitle=new android.widget.TextView(this);subtitle.setText(tools?"Create controls with original game bindings":"Manage and export your custom controlmap");
        subtitle.setTextColor(smallColor);subtitle.setTextSize(12);subtitle.setPadding(0,4,0,pad);panel.addView(subtitle);
        android.widget.ScrollView scroll=new android.widget.ScrollView(this);android.widget.LinearLayout actions=new android.widget.LinearLayout(this);
        actions.setOrientation(android.widget.LinearLayout.VERTICAL);scroll.addView(actions);panel.addView(scroll,new android.widget.LinearLayout.LayoutParams(-1,0,1));
        String[] labels=tools ? new String[]{"Add touch button","Add button drawer","Add joystick","Load controlmap","Save controlmap"} :
            new String[]{"Select default controlmap","Save current controlmap","Export controlmap","Load controlmap","Add button"};
        int[] positions=tools?new int[]{0,1,2,3,4}:new int[]{5,4,6,3,0};
        for(int i=0;i<labels.length;i++){
            final int index=positions[i];android.widget.Button button=new android.widget.Button(this);
            button.setText(labels[i]);button.setAllCaps(false);button.setTextColor(textColor);button.setTextSize(14);
            android.graphics.drawable.GradientDrawable shape=new android.graphics.drawable.GradientDrawable();shape.setColor(buttonColor);shape.setCornerRadius(pad);
            shape.setStroke(1,border);button.setBackground(shape);
            android.widget.LinearLayout.LayoutParams params=new android.widget.LinearLayout.LayoutParams(-1,pad*3);params.bottomMargin=pad/2;
            actions.addView(button,params);
            button.setOnClickListener(v->{dialog.dismiss();mDrawerNavigationView.performItemClick(null,index,index);});
        }
        dialog.setContentView(panel);dialog.show();android.view.Window window=dialog.getWindow();
        if(window!=null){window.setBackgroundDrawableResource(android.R.color.transparent);window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            android.view.WindowManager.LayoutParams a=window.getAttributes();a.dimAmount=.50f;window.setAttributes(a);
            window.setLayout(Math.min((int)(getResources().getDisplayMetrics().widthPixels*.43f),pad*28),
                (int)(getResources().getDisplayMetrics().heightPixels*.84f));window.setGravity(tools?android.view.Gravity.START|android.view.Gravity.CENTER_VERTICAL:android.view.Gravity.END|android.view.Gravity.CENTER_VERTICAL);
        }
    }


    /** Left side configures visible LIVE game modules; right side edits touch controls. */
    private void showBrixModulesEditor(){
        boolean dark=BrixTheme.dark(this);int surface=dark?0xFF151820:0xFFF6F7FB;
        int row=dark?0xFF222631:0xFFFFFFFF;
        int fg=dark?android.graphics.Color.WHITE:0xFF18202B;
        int muted=dark?0xFFB5BECD:0xFF626A78;
        int edge=dark?0xFF424958:0xFFD5DCE7;
        float density=getResources().getDisplayMetrics().density;int pad=(int)(16*density);
        android.app.Dialog dialog=new android.app.Dialog(this);dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        android.widget.LinearLayout panel=new android.widget.LinearLayout(this);panel.setOrientation(android.widget.LinearLayout.VERTICAL);
        panel.setPadding(pad,pad,pad,pad);
        android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();
        bg.setColor(surface);bg.setCornerRadius(pad*2);bg.setStroke(1,edge);panel.setBackground(bg);
        android.widget.TextView title=new android.widget.TextView(this);title.setText("BRIX  /  GAME MODULES");title.setTextSize(19);title.setTypeface(null,1);
        title.setTextColor(0xFFEA203B);panel.addView(title);
        android.widget.TextView hint=new android.widget.TextView(this);
        hint.setText("Choose which of the 15 live modules appear in-game. The actual FPS counter comes from Minecraft's F3 screen; CPS counts BRIX touch clicks.");
        hint.setTextColor(muted);hint.setTextSize(12);hint.setPadding(0,pad/2,0,pad);panel.addView(hint);
        android.widget.ScrollView scroll=new android.widget.ScrollView(this);
        android.widget.LinearLayout list=new android.widget.LinearLayout(this);list.setOrientation(android.widget.LinearLayout.VERTICAL);
        scroll.addView(list);panel.addView(scroll,new android.widget.LinearLayout.LayoutParams(-1,0,1));
        for(int index=0;index<git.artdeell.mojo.game.BrixModuleRegistry.IDS.length;index++){
            final int i=index;android.widget.LinearLayout tile=new android.widget.LinearLayout(this);
            tile.setGravity(android.view.Gravity.CENTER_VERTICAL);tile.setPadding(pad,pad/2,pad,pad/2);
            android.graphics.drawable.GradientDrawable box=new android.graphics.drawable.GradientDrawable();box.setColor(row);
            box.setCornerRadius(pad);box.setStroke(1,edge);tile.setBackground(box);
            android.widget.LinearLayout labels=new android.widget.LinearLayout(this);labels.setOrientation(android.widget.LinearLayout.VERTICAL);
            android.widget.TextView name=new android.widget.TextView(this);name.setText(git.artdeell.mojo.game.BrixModuleRegistry.NAMES[i]);name.setTextSize(14);name.setTypeface(null,1);name.setTextColor(fg);labels.addView(name);
            android.widget.TextView sub=new android.widget.TextView(this);sub.setText(git.artdeell.mojo.game.BrixModuleRegistry.DESCRIPTIONS[i]);sub.setTextSize(10);sub.setTextColor(muted);labels.addView(sub);
            tile.addView(labels,new android.widget.LinearLayout.LayoutParams(0,-2,1));
            android.widget.Switch toggle=new android.widget.Switch(this);toggle.setChecked(git.artdeell.mojo.game.BrixModuleRegistry.enabled(this,i));
            toggle.setOnCheckedChangeListener((button,checked)->git.artdeell.mojo.game.BrixModuleRegistry.setEnabled(this,i,checked));
            tile.addView(toggle);tile.setOnClickListener(v->toggle.setChecked(!toggle.isChecked()));
            android.widget.LinearLayout.LayoutParams lp=new android.widget.LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=pad/3;list.addView(tile,lp);
        }
        dialog.setContentView(panel);dialog.show();android.view.Window win=dialog.getWindow();if(win!=null){win.setBackgroundDrawableResource(android.R.color.transparent);
            win.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);android.view.WindowManager.LayoutParams at=win.getAttributes();at.dimAmount=.47f;win.setAttributes(at);
            win.setLayout(Math.min((int)(getResources().getDisplayMetrics().widthPixels*.52f),pad*31),(int)(getResources().getDisplayMetrics().heightPixels*.90f));
            win.setGravity(android.view.Gravity.START|android.view.Gravity.CENTER_VERTICAL);
        }
    }

	@Override
	public void onAttachedToWindow() {
		mControlLayout.post(()->{
			try {
				mControlLayout.loadLayout(LauncherPreferences.PREF_DEFAULTCTRL_PATH);
			}catch (IOException | JsonSyntaxException e) {
				Tools.showError(this, e);
			}
		});
	}

	public void startCropping(CropperUtils.CropperReceiver cropperReceiver) {
		mCropperReceiver = cropperReceiver;
		CropperUtils.startCropper(mCropperLauncher);
	}

	@Override
	public void onBackPressed() {
		mControlLayout.askToExit(this);
	}

	@Override
	public void exitEditor() {
		super.onBackPressed();
	}

	@Override
	public float getAspectRatio() {
		if(mCropperReceiver != null) return mCropperReceiver.getAspectRatio();
		return 1f;
	}

	@Override
	public int getTargetMaxSide() {
		if(mCropperReceiver != null) return mCropperReceiver.getTargetMaxSide();
		return 128;
	}

	@Override
	public void onCropped(Bitmap contentBitmap) {
		if(mCropperReceiver != null) mCropperReceiver.onCropped(contentBitmap);
	}

	@Override
	public void onFailed(Exception exception) {
		if(mCropperReceiver != null) mCropperReceiver.onFailed(exception);
	}
}
