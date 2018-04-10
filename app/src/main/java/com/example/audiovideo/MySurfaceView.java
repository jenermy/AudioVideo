package com.example.audiovideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author wanlijun
 * @description
 * @time 2018/3/21 14:02
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private Bitmap icon;
    private DrawThread drawThread;
    public MySurfaceView(Context context){
        super(context);
        init();
    }
    public MySurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }
    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init();
    }
    private void init(){
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSLUCENT);
        drawThread = new DrawThread(holder);
    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        drawThread.setRun(false);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        icon = BitmapFactory.decodeResource(getResources(),R.drawable.qinsang);
        drawThread.setRun(true);
        drawThread.start();
    }

    public class DrawThread extends Thread{
        private SurfaceHolder mSurfaceHolder;
        private boolean isRun = false;
        public DrawThread(SurfaceHolder surfaceHolder){
            this.mSurfaceHolder = surfaceHolder;
        }
        public void setRun(boolean isRun){
            this.isRun = isRun;
        }
        @Override
        public void run() {
            int count = 0;
            while (isRun){
                Canvas canvas = null;
                synchronized (mSurfaceHolder){
                    try {
                        canvas = mSurfaceHolder.lockCanvas();
                        canvas.drawColor(Color.WHITE);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.BLACK);
                        canvas.drawText((count++)+"秒",100,50,paint);
                        Rect rect = new Rect(100,100,300,300);
//                        canvas.drawRect(rect,paint);
                        canvas.drawBitmap(icon,rect.left, rect.top,null);
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if(canvas != null){
                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }
    }
}
