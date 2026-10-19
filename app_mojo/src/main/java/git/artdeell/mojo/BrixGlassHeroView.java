package git.artdeell.mojo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/** Subtle continuous parallax for BRIX's bundled abstract banner; pauses off screen. */
public final class BrixGlassHeroView extends View {
    private Bitmap artwork;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private boolean active;
    public BrixGlassHeroView(Context context) { super(context); init(); }
    public BrixGlassHeroView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    private void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        artwork = BitmapFactory.decodeResource(getResources(), R.drawable.brix_castle_banner, options);
    }
    @Override protected void onAttachedToWindow() { super.onAttachedToWindow(); active = true; postInvalidateOnAnimation(); }
    @Override protected void onDetachedFromWindow() { active = false; super.onDetachedFromWindow(); }
    @Override protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && isAttachedToWindow()) postInvalidateOnAnimation();
    }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        canvas.drawColor(0xFF18151B);
        if (artwork != null && !artwork.isRecycled()) {
            double phase = (SystemClock.uptimeMillis() % 24000L) * (Math.PI * 2.0 / 24000.0);
            float zoom = 1.10f + 0.025f * (float)Math.sin(phase);
            float shift = 0.016f * (float)Math.sin(phase);
            float aw = artwork.getWidth(), ah = artwork.getHeight();
            float srcRatio = (float)w / h;
            float cropW = aw / zoom, cropH = ah / zoom;
            if (cropW / cropH > srcRatio) cropW = cropH * srcRatio;
            else cropH = cropW / srcRatio;
            float left = (aw - cropW) * (0.5f + shift);
            float top = (ah - cropH) * 0.5f;
            left = Math.max(0, Math.min(left, aw - cropW));
            top = Math.max(0, Math.min(top, ah - cropH));
            paint.setShader(null); paint.setColor(0xFFFFFFFF);
            canvas.drawBitmap(artwork, new Rect((int)left,(int)top,(int)(left+cropW),(int)(top+cropH)), new RectF(0,0,w,h), paint);
        }
        paint.setShader(new LinearGradient(0,0,w,0,
                new int[]{0xF2101521,0xA6212029,0x08000000},
                new float[]{0f,.46f,1f},Shader.TileMode.CLAMP));
        canvas.drawRect(0,0,w,h,paint); paint.setShader(null);
        if (active && getVisibility() == VISIBLE && getWindowVisibility() == VISIBLE) postInvalidateDelayed(32);
    }
}
