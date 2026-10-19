package com.kdt.mcgui;

import android.content.*;
import android.graphics.*;
import android.util.*;

import androidx.core.content.res.ResourcesCompat;

import git.artdeell.mojo.R;

public class MineButton extends androidx.appcompat.widget.AppCompatButton {
	
	public MineButton(Context ctx) {
		this(ctx, null);
	}
	
	public MineButton(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init();
	}

	public void init() {
		setTypeface(ResourcesCompat.getFont(getContext(), R.font.noto_sans_bold));
		setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.brix_v6_primary_button, null));
		setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen._13ssp));
        // BRIX_MINEBUTTON_VISIBILITY: visible red actions in both Light and Dark mode.
        setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.brix_final_primary, null));
        setTextColor(android.graphics.Color.WHITE);
        setAllCaps(false);
        setMinHeight((int)(getResources().getDisplayMetrics().density*46));
        setPadding((int)(12*getResources().getDisplayMetrics().density),0,(int)(12*getResources().getDisplayMetrics().density),0);

        setTextColor(android.graphics.Color.WHITE);
        setAllCaps(false);
        setElevation(getResources().getDisplayMetrics().density * 3f);
	}

}
