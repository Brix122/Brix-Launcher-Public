package com.kdt.mcgui;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import git.artdeell.mojo.R;
import git.artdeell.mojo.MojoApplication;
import git.artdeell.mojo.Tools;
import git.artdeell.mojo.extra.ExtraConstants;
import git.artdeell.mojo.extra.ExtraCore;
import git.artdeell.mojo.extra.ExtraListener;
import git.artdeell.mojo.fragments.InstanceEditorFragment;
import git.artdeell.mojo.fragments.ProfileTypeSelectFragment;
import git.artdeell.mojo.instances.DisplayInstance;
import git.artdeell.mojo.instances.Instances;
import git.artdeell.mojo.instances.InstanceAdapter;
import git.artdeell.mojo.instances.InstanceAdapterExtra;
import java.io.IOException;
import fr.spse.extended_view.ExtendedTextView;
/** Theme-aware BRIX centered version chooser; preserves existing instance selection actions. */
public class mcVersionSpinner extends ExtendedTextView {
 private static final int CREATE=0;
 private final InstanceAdapter adapter=new InstanceAdapter(new InstanceAdapterExtra[]{
    new InstanceAdapterExtra(CREATE,R.string.create_instance,ResourcesCompat.getDrawable(getResources(),R.drawable.brix_polish_add,null))});
 private Dialog dialog;
 private ListView list;
 private int selectedIndex;
 public mcVersionSpinner(@NonNull Context c){super(c);init();}
 public mcVersionSpinner(@NonNull Context c,@Nullable AttributeSet a){super(c,a);init();}
 public mcVersionSpinner(@NonNull Context c,@Nullable AttributeSet a,int d){super(c,a,d);init();}
 private int dp(int v){return (int)(getResources().getDisplayMetrics().density*v+.5f);}
 private void init(){
    setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
    setTextColor(ContextCompat.getColor(getContext(),R.color.brix_text));
    setGravity(Gravity.CENTER_VERTICAL);
    setPaddingRelative(dp(14),0,dp(14),0);
    setCompoundDrawablePadding(dp(13));
    adapter.setIconChangedListener(() -> post(() -> { adapter.notifyDataSetChanged(); setSelection(selectedIndex); }));
    addOnAttachStateChangeListener(new ExtraAttachListener());
    setSelection(0);
    setOnClickListener(v->showChooser());
 }
 public void setProfileSelection(int position){setSelection(position);Instances.setSelectedInstance((DisplayInstance)adapter.getItem(position));}
 public void setSelection(int position){
    if(position<0||position>=adapter.getCount())return;
    selectedIndex=position;
    adapter.setView(this,position,false);
    adapter.applySelectionIndex(position);
    if(list!=null)list.setSelection(position);
    setTextColor(ContextCompat.getColor(getContext(),R.color.brix_text));
 }
 public void openProfileEditor(FragmentActivity a){
    Object selected=adapter.getItem(selectedIndex);
    if(selected instanceof InstanceAdapterExtra)extra((InstanceAdapterExtra)selected);
    else Tools.swapFragment(a, InstanceEditorFragment.class,InstanceEditorFragment.TAG,null);
 }
 private void extra(InstanceAdapterExtra e){if(e.id==CREATE)Tools.swapFragment((FragmentActivity)getContext(),ProfileTypeSelectFragment.class,ProfileTypeSelectFragment.TAG,null);}
 public void reloadProfiles(){MojoApplication.sExecutorService.execute(()->{
    try {final Instances loaded=Instances.loadDisplay();Tools.runOnUiThread(()->{
        adapter.applyInstances(loaded);setSelection(loaded.selectedIndex);
    });}catch(IOException e){Tools.runOnUiThread(()->Tools.showError(getContext(),e));}
 });}
 private void showChooser(){
    if(dialog!=null && dialog.isShowing()){dialog.dismiss();return;}
    Context c=getContext();
    LinearLayout shell=new LinearLayout(c);shell.setOrientation(LinearLayout.VERTICAL);
    shell.setBackgroundResource(R.drawable.brix_polish_popup);shell.setPadding(dp(18),dp(15),dp(18),dp(17));
    TextView heading=new TextView(c);heading.setText("SELECT GAME VERSION");heading.setTextSize(19);
    heading.setTypeface(null,android.graphics.Typeface.BOLD);heading.setTextColor(ContextCompat.getColor(c,R.color.brix_text));shell.addView(heading);
    TextView hint=new TextView(c);hint.setText("Installed profiles and new instance setup");hint.setTextSize(12);
    hint.setTextColor(ContextCompat.getColor(c,R.color.brix_subtext));hint.setPadding(0,dp(3),0,dp(12));shell.addView(hint);
    list=new ListView(c);list.setDividerHeight(dp(7));list.setDivider(new ColorDrawable(Color.TRANSPARENT));
    list.setCacheColorHint(Color.TRANSPARENT);list.setVerticalScrollBarEnabled(true);
    list.setAdapter(adapter);shell.addView(list,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1));
    list.setOnItemClickListener((parent,view,pos,id)->{
        Object chosen=adapter.getItem(pos);
        dialog.dismiss();
        if(chosen instanceof DisplayInstance)setProfileSelection(pos);
        else if(chosen instanceof InstanceAdapterExtra)extra((InstanceAdapterExtra)chosen);
    });
    dialog=new Dialog(c);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(shell);dialog.setCanceledOnTouchOutside(true);dialog.show();
    Window window=dialog.getWindow();if(window!=null){
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp=window.getAttributes();lp.gravity=Gravity.CENTER;lp.dimAmount=.65f;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        int width=Math.min((int)(getResources().getDisplayMetrics().widthPixels*.83f),dp(760));
        int h=Math.min((int)(getResources().getDisplayMetrics().heightPixels*.78f),dp(410));
        window.setLayout(width,h);
    }
    list.setSelection(Math.max(0,selectedIndex));
 }
 class ExtraAttachListener implements OnAttachStateChangeListener,ExtraListener<Void>{
    @Override public void onViewAttachedToWindow(@NonNull View view){reloadProfiles();ExtraCore.addExtraListener(ExtraConstants.REFRESH_VERSION_SPINNER,this);}
    @Override public void onViewDetachedFromWindow(@NonNull View view){ExtraCore.removeExtraListenerFromValue(ExtraConstants.REFRESH_VERSION_SPINNER,this);if(dialog!=null)dialog.dismiss();}
    @Override public boolean onValueSet(String key,@NonNull Void value){post(mcVersionSpinner.this::reloadProfiles);ExtraCore.consumeValue(key);return false;}
 }
}
