package com.kdt.mcgui;

import android.content.*;
import android.util.*;
import android.graphics.*;
import android.widget.EditText;

public class MineEditText extends androidx.appcompat.widget.AppCompatEditText {
	public MineEditText(Context ctx) {
		super(ctx);
		init();
	}

	public MineEditText(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init();
	}

	public void init() {
		setBackgroundResource(git.artdeell.mojo.R.drawable.brix_polish_field);
        setTextColor(getResources().getColor(git.artdeell.mojo.R.color.brix_text));
        setHintTextColor(getResources().getColor(git.artdeell.mojo.R.color.brix_subtext));
        setSingleLine(true);
		setPadding(5, 5, 5, 5);
	}
}
