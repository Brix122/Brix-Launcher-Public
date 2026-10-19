package git.artdeell.mojo.fragments;

import static git.artdeell.mojo.Tools.openPath;
import static git.artdeell.mojo.Tools.shareLog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.kdt.mcgui.mcVersionSpinner;

import git.artdeell.mojo.CreditsActivity;
import git.artdeell.mojo.CustomControlsActivity;
import git.artdeell.mojo.R;
import git.artdeell.mojo.Tools;
import git.artdeell.mojo.contracts.OpenDocumentWithExtension;
import git.artdeell.mojo.extra.ExtraConstants;
import git.artdeell.mojo.extra.ExtraCore;
import git.artdeell.mojo.instances.Instance;
import git.artdeell.mojo.instances.Instances;
import git.artdeell.mojo.progresskeeper.ProgressKeeper;
import git.artdeell.mojo.utils.FileUtils;

import java.io.File;

public class MainMenuFragment extends Fragment {
    public static final String TAG = "MainMenuFragment";

    private mcVersionSpinner mVersionSpinner;

    private final ActivityResultLauncher<Object> mModInstallerLauncher =
            registerForActivityResult(new OpenDocumentWithExtension("jar"), (data) -> {
                if (data != null) Tools.launchModInstaller(requireContext(), data);
            });

    public MainMenuFragment() {
        super(R.layout.fragment_launcher);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View mNewsButton = view.findViewById(R.id.news_button);
        View mDiscordButton = view.findViewById(R.id.social_media_button);
        View mCustomControlButton = view.findViewById(R.id.custom_control_button);
        // BRIX_MODS_CLICK: live mod browser, plus the EXISTING in-app modpack installer.
        View brixMods = view.findViewById(R.id.brix_mods_button);
        if (brixMods != null) brixMods.setOnClickListener(v ->
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
              .setTitle("FPS / Useful Mods")
              .setItems(new String[]{"Browse FPS / Utility / All mods (Modrinth)","Browse & install compatible Modpacks"},(dlg,which)->{
                  if(which==0) startActivity(new Intent(requireContext(),git.artdeell.mojo.BrixModsActivity.class));
                  else Tools.swapFragment(requireActivity(),SearchModFragment.class,SearchModFragment.TAG,null);
              }).show());
        View mInstallJarButton = view.findViewById(R.id.install_jar_button);
        View mShareLogsButton = view.findViewById(R.id.share_logs_button);
        View mOpenDirectoryButton = view.findViewById(R.id.open_files_button);
        View mCreditsButton = view.findViewById(R.id.credits_button);

        ImageButton mEditProfileButton = view.findViewById(R.id.edit_profile_button);
        Button mPlayButton = view.findViewById(R.id.play_button);
        mVersionSpinner = view.findViewById(R.id.mc_version_spinner);
        // BRIX_HERO_MOTION: a subtle reveal, no continuous GPU animation.
        View banner=view.findViewById(R.id.brix_hero_image);
        if(banner!=null){banner.setAlpha(.3f);banner.setTranslationX(12f*getResources().getDisplayMetrics().density);
            banner.animate().alpha(1f).translationX(0f).setDuration(620).start();}
        View heroAction=view.findViewById(R.id.brix_hero_button);
        if(heroAction!=null)heroAction.setOnClickListener(v->mVersionSpinner.performClick());

        // BRIX_SKIN_STUDIO: preview imported PNG and open the actual skin editor.
        View skinButton=view.findViewById(R.id.brix_skin_edit_button);
        git.artdeell.mojo.BrixSkinView skinPreview=view.findViewById(R.id.brix_skin_preview);
        if(skinButton!=null) skinButton.setOnClickListener(v -> startActivity(new Intent(requireContext(),git.artdeell.mojo.BrixSkinActivity.class)));
        if(skinPreview!=null){


            File skinPath=new File(requireContext().getFilesDir(),"brix-skins/preview.png");
            if(skinPath.exists())skinPreview.setSkin(android.graphics.BitmapFactory.decodeFile(skinPath.getAbsolutePath()));
        }
        mNewsButton.setOnClickListener(v -> Tools.openURL(requireActivity(), Tools.URL_HOME));
        mDiscordButton.setOnClickListener(v -> Tools.openURL(requireActivity(), getString(R.string.social_media_invite)));
        mCustomControlButton.setOnClickListener(v -> startActivity(new Intent(requireContext(), CustomControlsActivity.class)));
        mInstallJarButton.setOnClickListener(v -> runInstallerWithConfirmation());
        mEditProfileButton.setOnClickListener(v -> mVersionSpinner.openProfileEditor(requireActivity()));
        mPlayButton.setOnClickListener(v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));
        mShareLogsButton.setOnClickListener(v -> shareLog(requireContext()));
        mOpenDirectoryButton.setOnClickListener(v -> openGameDirectory(v.getContext()));
        // BRIX_STAGGERED_HOME — visual motion only, all listeners remain original.
        View[] actionCards = {mNewsButton, mDiscordButton, mCustomControlButton, mInstallJarButton, mShareLogsButton, mOpenDirectoryButton, mPlayButton};
        for(int index=0; index<actionCards.length; index++) {
            View card = actionCards[index];
            card.setAlpha(0f);
            card.setTranslationY(card.getResources().getDisplayMetrics().density * 12f);
            card.animate().alpha(1f).translationY(0f).setStartDelay(index*45L).setDuration(250).start();
        }

        // BRIX_CREDITS_ENTRY — opens a genuine full-page credits view.
        View credits = view.findViewById(R.id.brix_credits_button);
        if(credits != null) credits.setOnClickListener(v -> startActivity(new Intent(requireContext(), git.artdeell.mojo.BrixCreditsActivity.class)));


        if (mCreditsButton != null) {
            mCreditsButton.setOnClickListener(v -> startActivity(new Intent(requireContext(), CreditsActivity.class)));
            mCreditsButton.setOnLongClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("BRIX Credits")
                        .setMessage("Made by :- BrixTools\nOfficial Source: https://brix4earn.com")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            });
        }

        mNewsButton.setOnLongClickListener(v -> {
            Tools.swapFragment(requireActivity(), GamepadMapperFragment.class, GamepadMapperFragment.TAG, null);
            return true;
        });
    }

    private void openGameDirectory(Context context) {
        Instance instance = Instances.loadSelectedInstance();
        if (instance == null) {
            Toast.makeText(context, R.string.no_instance, Toast.LENGTH_LONG).show();
            return;
        }
        File gameDirectory = instance.getGameDirectory();
        if (FileUtils.ensureDirectorySilently(gameDirectory)) {
            openPath(context, gameDirectory, false);
        } else {
            Toast.makeText(context, R.string.gamedir_open_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ExtraCore.setValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, true);
        // Refresh preview after returning from Skin Studio.
        View body=getView();if(body!=null){
            git.artdeell.mojo.BrixSkinView model=body.findViewById(R.id.brix_skin_preview);
            File f=new File(requireContext().getFilesDir(),"brix-skins/preview.png");
            if(model!=null && f.isFile()){
                android.graphics.BitmapFactory.Options options=new android.graphics.BitmapFactory.Options();
                options.inScaled=false;
                android.graphics.Bitmap image=android.graphics.BitmapFactory.decodeFile(f.getAbsolutePath(),options);
                if(image!=null)model.setSkin(image);
            }
        }

    }

    private void runInstallerWithConfirmation() {
        if (ProgressKeeper.getTaskCount() == 0) {
            mModInstallerLauncher.launch(null);
        } else {
            Toast.makeText(requireContext(), R.string.tasks_ongoing, Toast.LENGTH_LONG).show();
        }
    }
}