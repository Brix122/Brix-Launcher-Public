package com.kdt.mcgui;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import git.artdeell.mojo.R;

import git.artdeell.mojo.progresskeeper.ProgressKeeper;
import git.artdeell.mojo.progresskeeper.ProgressListener;
import git.artdeell.mojo.progresskeeper.TaskCountListener;

import java.util.ArrayList;


/** Class staring at specific values and automatically show something if the progress is present
 * Since progress is posted in a specific way, The packing/unpacking is handheld by the class
 *
 * This class relies on ExtraCore for its behavior.
 */
public class ProgressLayout extends ConstraintLayout implements View.OnClickListener, TaskCountListener{
    public static final String UNPACK_RUNTIME = "unpack_runtime";
    public static final String DOWNLOAD_GAME = "download_minecraft";
    public static final String DOWNLOAD_VERSION_LIST = "download_verlist";
    public static final String AUTHENTICATE = "authenticate";
    public static final String INSTALL_MODPACK = "install_modpack";
    public static final String EXTRACT_COMPONENTS = "extract_components";
    public static final String EXTRACT_SINGLE_FILES = "extract_single_files";
    public static final String INSTANCE_INSTALL = "instance_install";
    public static final String DATA_MIGRATION = "data_migration";

    public ProgressLayout(@NonNull Context context) {
        super(context);
        init();
    }
    public ProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public ProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private final ArrayList<LayoutProgressListener> mMap = new ArrayList<>();
    private LinearLayout mLinearLayout;
    private TextView mTaskNumberDisplayer;
    private ImageView mFlipArrow;



    public void observe(String progressKey){
        mMap.add(new LayoutProgressListener(progressKey));
    }

    public void cleanUpObservers() {
        for(LayoutProgressListener progressListener : mMap) {
            ProgressKeeper.removeListener(progressListener.progressKey, progressListener);
        }
    }

    public boolean hasProcesses(){
        return ProgressKeeper.getTaskCount() > 0;
    }


    private void init(){
        inflate(getContext(), R.layout.view_progress, this);
        mLinearLayout = findViewById(R.id.progress_linear_layout);
        mTaskNumberDisplayer = findViewById(R.id.progress_textview);
        mFlipArrow = findViewById(R.id.progress_flip_arrow);
        // BRIX_PROGRESS_CONTRAST: same readable palette for Light and Dark mode.
        mTaskNumberDisplayer.setTextColor(getResources().getColor(R.color.brix_text));
        mFlipArrow.setColorFilter(getResources().getColor(R.color.brix_accent));
        setBackgroundColor(getResources().getColor(R.color.brix_panel));
        // BRIX_DOWNLOAD_PAUSE_BUTTONS: applied only when an actual file Downloader is active.
        android.widget.LinearLayout actions=new android.widget.LinearLayout(getContext());
        actions.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        actions.setGravity(android.view.Gravity.END|android.view.Gravity.CENTER_VERTICAL);
        android.widget.Button pause=new android.widget.Button(getContext());
        pause.setText("Ⅱ Pause");pause.setAllCaps(false);pause.setTextColor(getResources().getColor(R.color.brix_text));
        pause.setBackgroundResource(R.drawable.brix_final_tile);
        android.widget.Button stop=new android.widget.Button(getContext());
        stop.setText("■ Stop");stop.setAllCaps(false);stop.setTextColor(android.graphics.Color.WHITE);
        stop.setBackgroundResource(R.drawable.brix_final_primary);
        android.widget.Button remove=new android.widget.Button(getContext());
        remove.setText("× Remove");remove.setAllCaps(false);remove.setTextColor(getResources().getColor(R.color.brix_text));
        remove.setBackgroundResource(R.drawable.brix_final_tile);
        int px=(int)(getResources().getDisplayMetrics().density*32);
        pause.setTextSize(10);stop.setTextSize(10);remove.setTextSize(10);
        actions.addView(pause,new android.widget.LinearLayout.LayoutParams(0,px,1));
        actions.addView(stop,new android.widget.LinearLayout.LayoutParams(0,px,1));
        actions.addView(remove,new android.widget.LinearLayout.LayoutParams(0,px,1));
        mLinearLayout.addView(actions,0);
        pause.setOnClickListener(v->{
            if(git.artdeell.mojo.downloader.Downloader.togglePause(DOWNLOAD_GAME))
                pause.setText(git.artdeell.mojo.downloader.Downloader.isPaused(DOWNLOAD_GAME)?"▶ Resume":"⏸ Pause");
            else android.widget.Toast.makeText(getContext(),"No active file transfer to pause",android.widget.Toast.LENGTH_SHORT).show();
        });
        stop.setOnClickListener(v->{
            if(!git.artdeell.mojo.downloader.Downloader.stopDownload(DOWNLOAD_GAME))
                android.widget.Toast.makeText(getContext(),"This task does not support stopping here",android.widget.Toast.LENGTH_LONG).show();
        });
        remove.setOnClickListener(v->new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Remove download?")
            .setMessage("Stop this transfer? Installed Minecraft files and worlds will not be deleted. Partially downloaded files may be reused on retry.")
            .setPositiveButton("Stop transfer",(d,w)->git.artdeell.mojo.downloader.Downloader.stopDownload(DOWNLOAD_GAME))
            .setNegativeButton("Keep",null).show());
        actions.setVisibility(android.view.View.GONE);
        java.lang.Runnable sync=new java.lang.Runnable(){
            @Override public void run(){
                boolean active=git.artdeell.mojo.downloader.Downloader.isActive(DOWNLOAD_GAME);
                actions.setVisibility(active?android.view.View.VISIBLE:android.view.View.GONE);
                // Start polling before the actual downloader is created and stop after progress clears.
                if(git.artdeell.mojo.progresskeeper.ProgressKeeper.hasProgressKey(DOWNLOAD_GAME))
                    actions.postDelayed(this,450);
            }
        };
        actions.setTag(sync);
        // BRIX_GLASS_PROGRESS: compact summary bar by default; expand only on user tap.
        mLinearLayout.setVisibility(GONE);
        mFlipArrow.setRotation(0);
        setOnClickListener(this);
    }


    /** Update the progress bar content */
    public static void setProgress(String progressKey, int progress){
        ProgressKeeper.submitProgress(progressKey, progress, -1, (Object)null);
    }

    /** Update the text and progress content */
    public static void setProgress(String progressKey, int progress, @StringRes int resource, Object... message){
        ProgressKeeper.submitProgress(progressKey, progress, resource, message);
    }

    /** Update the text and progress content */
    public static void setProgress(String progressKey, int progress, String message){
        setProgress(progressKey,progress, -1, message);
    }

    /** Update the text and progress content */
    public static void clearProgress(String progressKey){
        setProgress(progressKey, -1, -1);
    }

    @Override
    public void onClick(View v) {
        mLinearLayout.setVisibility(mLinearLayout.getVisibility() == GONE ? VISIBLE : GONE);
        mFlipArrow.setRotation(mLinearLayout.getVisibility() == GONE? 0 : 180);
    }

    @Override
    public boolean onUpdateTaskCount(int tc) {
        post(()->{
            if(tc > 0) {
                mTaskNumberDisplayer.setText(getContext().getString(R.string.progresslayout_tasks_in_progress, tc));
                setVisibility(VISIBLE);
            }else
                setVisibility(GONE);
        });
        return false;
    }

    class LayoutProgressListener implements ProgressListener {
        final String progressKey;
        final TextProgressBar textView;
        final LinearLayout.LayoutParams params;
        boolean isAttached = false;
        public LayoutProgressListener(String progressKey) {
            this.progressKey = progressKey;
            textView = new TextProgressBar(getContext());
            textView.setTextPadding(getContext().getResources().getDimensionPixelOffset(R.dimen._6sdp));
            params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen._20sdp));
            params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen._6sdp);
            ProgressKeeper.addListener(progressKey, this);
        }
        @Override
        public void onProgressStarted() {
            post(()-> {
                Log.i("ProgressLayout", "onProgressStarted");
                if(!isAttached) mLinearLayout.addView(textView, params);
                if(DOWNLOAD_GAME.equals(progressKey)) {
                    View row=mLinearLayout.getChildAt(0);
                    if(row!=null && row.getTag() instanceof Runnable) row.post((Runnable)row.getTag());
                }
                isAttached = true;
            });
        }

        @Override
        public void onProgressUpdated(int progress, int resid, Object... va) {
            post(()-> {
                // BRIX_REAL_PROGRESS: percentage is passed through the actual download task.
                textView.setProgress(progress);
                if(resid != -1) textView.setText(getContext().getString(resid, va));
                else if(va.length > 0 && va[0] != null)textView.setText((String)va[0]);
                else textView.setText("");
                if (progress >= 0 && progress <= 100) {
                    mTaskNumberDisplayer.setText(progress + "%  •  " + (resid != -1 ? getContext().getString(resid, va) : (va.length > 0 && va[0] != null ? va[0].toString() : "Working...")));
                    textView.setText(progress + "%  •  " + (resid != -1 ? getContext().getString(resid, va) : (va.length > 0 && va[0] != null ? va[0].toString() : "Working...")));
                }
            });
        }

        @Override
        public void onProgressEnded() {
            post(()-> {
                mLinearLayout.removeView(textView);
                isAttached = false;
            });
        }
    }
}
