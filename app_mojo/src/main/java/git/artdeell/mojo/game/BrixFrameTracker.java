package git.artdeell.mojo.game;

import android.os.SystemClock;

/** Counts frames actually published by Minecraft's TextureView SurfaceTexture. */
public final class BrixFrameTracker {
    private static final long[] FRAME_TIMES = new long[240];
    private static int index,filled;
    private static long last;
    private BrixFrameTracker(){}
    public static synchronized void onPresentedFrame(){
        long now=SystemClock.elapsedRealtimeNanos();
        FRAME_TIMES[index]=now;index=(index+1)%FRAME_TIMES.length;
        if(filled<FRAME_TIMES.length)filled++;
        last=now;
    }
    public static synchronized int recentFps(){
        long now=SystemClock.elapsedRealtimeNanos();
        if(last==0||now-last>2_000_000_000L)return -1;
        int frames=0;long earliest=now;
        for(int i=0;i<filled;i++){
            long sample=FRAME_TIMES[i];
            if(sample!=0&&now-sample<1_000_000_000L){frames++;earliest=Math.min(earliest,sample);}
        }
        if(frames<2)return -1;
        double seconds=(now-earliest)/1_000_000_000.0;
        return seconds>.15?Math.round((float)((frames-1)/seconds)):-1;
    }
    public static synchronized void clear(){index=0;filled=0;last=0;java.util.Arrays.fill(FRAME_TIMES,0L);}
}
