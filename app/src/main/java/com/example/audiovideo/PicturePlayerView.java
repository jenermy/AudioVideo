package com.example.audiovideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;

/**
 * @author wanlijun
 * @description
 * @time 2018/4/9 13:55
 */

public class PicturePlayerView extends TextureView implements TextureView.SurfaceTextureListener{
    private Paint mPaint;
    private int mPlayFrame; //当前播放的那一帧
    private int mFrameCount; //总帧数
    private long mDelayTime; //播放帧间隔
    private String[] mPaths;//图片的绝对地址集合
    private DrawThread drawThread;
    public PicturePlayerView(Context context){
        this(context,null);
    }
    public PicturePlayerView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public PicturePlayerView(Context context, AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init();
    }
    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setOpaque(false); //设置背景为透明
        setSurfaceTextureListener(this);
    }

    public void start(String[] paths,long duration){
        this.mPaths = paths;
        this.mFrameCount = paths.length;
        this.mDelayTime = duration / mFrameCount;
        this.mPlayFrame = 0;
        drawThread = new DrawThread();
        drawThread.start();
    }

    //从本地读取图片
//    private Bitmap readBitmap(String path) throws IOException{
//        return BitmapFactory.decodeFile(path);
//    }
    //从Assets读取图片
    private Bitmap readBitmap(String path) throws IOException{
//        return BitmapFactory.decodeStream(getResources().getAssets().open(path));
        return BitmapFactory.decodeStream(getClass().getResourceAsStream(path));
    }
    //将图片画到画布上
    private void drawBitmap(Bitmap bitmap){
        Canvas canvas = lockCanvas(new Rect(0,0,getWidth(),getHeight()));
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //清空画布
        Rect src = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        Rect dst = new Rect(0,0,getWidth(),bitmap.getHeight() * getHeight()/bitmap.getWidth());
        canvas.drawBitmap(bitmap,src,dst,mPaint);
        unlockCanvasAndPost(canvas);
    }
    //回收bitmap
    private void recycleBitmap(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
        }
    }

    class DrawThread extends  Thread{
        @Override
        public void run() {
            try {

                while (mPlayFrame < mFrameCount){
                    Bitmap bitmap = readBitmap(mPaths[mPlayFrame]);
                    drawBitmap(bitmap);
                    recycleBitmap(bitmap);
                    mPlayFrame++;
                    SystemClock.sleep(mDelayTime);
                }
            }catch (Exception e){
                Log.i("wanlijun",e.toString());
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }
}
