package com.example.audiovideo;

/**
 * @author wanlijun
 * @description
 * @time 2018/4/9 16:28
 */

public class Scheduler {
    private long mDuration;
    private long mFrameCount;
    private double mDelayTime;
    public Scheduler(long duration,long framecount){
        if(framecount > duration){
            throw new RuntimeException("duration must be greater than framecount");
        }
        if(duration < 1){
            throw  new RuntimeException("duration must be greater than 0");
        }
        if(framecount < 2){
            throw  new RuntimeException("framecount must be greater than 1");
        }
        this.mDuration = duration;
        this.mFrameCount = framecount;
        this.mDelayTime = mDuration / (mFrameCount -1);
    }
}
