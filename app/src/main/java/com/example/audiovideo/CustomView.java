package com.example.audiovideo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author wanlijun
 * @description
 * @time 2018/3/21 16:38
 */

public class CustomView extends View {
    private String mTitle;
    private int mTextColor;
    private int mTextSize;
    private Bitmap image;
    private int imageScale;
    private Rect rect;
    private Rect mTextBound;
    private Paint mPaint;
    private int mHeight;
    private int mWidth;
    public CustomView(Context context){
        this(context,null);
    }
    public CustomView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context,attrs,defStyleAttr);
    }
    private void init(Context context,AttributeSet attrs,int defStyleAttr){
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.MySurfaceView,defStyleAttr,0);
        mTitle = typedArray.getString(R.styleable.MySurfaceView_text);
        mTextColor = typedArray.getColor(R.styleable.MySurfaceView_textColor,0xffe09f);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.MySurfaceView_textSize,16);
        image = BitmapFactory.decodeResource(context.getResources(),typedArray.getResourceId(R.styleable.MySurfaceView_background,0));
        imageScale = typedArray.getInt(R.styleable.MySurfaceView_imageScale,0);
        typedArray.recycle();
        rect = new Rect();
        mTextBound = new Rect();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mTitle,0,mTitle.length(),mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY){
            mWidth = specSize;
        }else{
            int desireImage = getPaddingLeft() + getPaddingRight() + image.getWidth();
            int desireText = getPaddingLeft() + getPaddingRight() + mTextBound.width();
            if(specMode == MeasureSpec.AT_MOST){
                int desire = Math.max(desireImage,desireText);
                mWidth = Math.min(desire,specSize);
            }
        }
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY){
            mHeight = specSize;
        }else{
            int desire = getPaddingTop() + getPaddingBottom() + image.getHeight() + mTextBound.height();
            if(specMode == MeasureSpec.AT_MOST){
                mHeight = Math.min(desire,specSize);
            }
        }
        setMeasuredDimension(mWidth,mHeight);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.MAGENTA);
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
        rect.left = getPaddingLeft();
        rect.top = getPaddingTop();
        rect.right = mWidth -getPaddingRight();
        rect.bottom = mHeight - getPaddingBottom();
        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        if(mTextBound.width() > mWidth){
            TextPaint textPaint = new TextPaint(mPaint);
            String msg = TextUtils.ellipsize(mTitle,textPaint,(float) (mWidth-getPaddingLeft()-getPaddingRight()), TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg,getPaddingLeft(),mHeight - getPaddingBottom(),mPaint);

        }else {
            canvas.drawText(mTitle,mWidth/2 - mTextBound.width()*1.0f/2,mHeight-getPaddingBottom(),mPaint);
        }
        rect.bottom = mTextBound.height();
        if(imageScale == 0){
            canvas.drawBitmap(image,null,rect,mPaint);
        }else{
            // 计算居中的矩形范围
            rect.left = mWidth / 2 - image.getWidth() / 2;
            rect.right = mWidth / 2 + image.getWidth() / 2;
            rect.top = (mHeight - mTextBound.height()) / 2 - image.getHeight()
                    / 2;
            rect.bottom = (mHeight - mTextBound.height()) / 2
                    + image.getHeight() / 2;
            canvas.drawBitmap(image,null,rect,mPaint);
        }
    }

}
