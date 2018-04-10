package com.example.audiovideo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class VedioActivity extends AppCompatActivity implements SurfaceHolder.Callback{

    private SurfaceView liveStreamSv;
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private OrientationEventListener orientationEventListener;
    private ImageView pictureIv;
    private ImageView vedioIv;
    private ImageView playIv;
    private boolean isPlaying = false;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private TextureView textureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio);
        liveStreamSv = (SurfaceView)findViewById(R.id.liveStreamSv);
        surfaceHolder = liveStreamSv.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        pictureIv = (ImageView)findViewById(R.id.pictureIv);
        vedioIv = (ImageView)findViewById(R.id.vedioIv);
        playIv = (ImageView)findViewById(R.id.playIv);
        pictureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                    }
                }, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {

                    }
                }, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] bytes, Camera camera) {
                        //当cameraPic目录不存在时，不能用 File fileDir = new File(Environment.getExternalStorageDirectory()+"/cameraPic/my.jpg");
                        //file.createNewFile()会提示No such file or directory
                        //当cameraPic目录存在时，就不存在这样的问题了
                        File fileDir = new File(Environment.getExternalStorageDirectory()+"/cameraPic/");
                        if(!fileDir.exists()){
                            fileDir.mkdirs();
                        }
                        File file = new File(fileDir,"my.jpg");
                        if(file.exists()){
                            file.delete();
                        }
                        try {
                            file.createNewFile();
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(bytes);
                            fos.close();
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.i("wanlijun",e.toString());
                        }
                        mCamera.startPreview();
                        try {
                            ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                            Log.i("wanlijun",exifInterface.getAttribute(ExifInterface.TAG_MAKE));
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.i("wanlijun",e.toString());
                        }
                    }
                });
            }
        });
        vedioIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording){
                    isRecording = false;
                    mediaRecorder.setOnErrorListener(null);
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    mediaRecorder.release();
                    mediaRecorder = null;
                }else{
                    if(mediaRecorder == null){
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                            @Override
                            public void onError(MediaRecorder mediaRecorder, int i, int i1) {
                                if(mediaRecorder != null){
                                    mediaRecorder.reset();
                                }
                                Log.i("wanlijun",i +":"+i1);
                            }
                        });
                    }
                    if (mCamera != null) {
                        mCamera.setDisplayOrientation(90);
                        mCamera.unlock();
                        mediaRecorder.setCamera(mCamera);
                    }
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
                    mediaRecorder.setVideoSize(640,480);
                    mediaRecorder.setVideoFrameRate(30);
                    mediaRecorder.setVideoEncodingBitRate(3*1024*1024);
                    mediaRecorder.setOrientationHint(90);
                    mediaRecorder.setMaxDuration(30 * 1000);
                    mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                    File file = new File(Environment.getExternalStorageDirectory()+"/cameraRec/");
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    Log.i("wanlijun","getPath:"+file.getPath());
                    mediaRecorder.setOutputFile(file.getPath()+ System.currentTimeMillis()+".mp4");
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        isRecording = true;
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.i("wanlijun",e.toString());
                    }
                }
            }
        });
        playIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    isPlaying = false;

                }else{

                }
            }
        });
//        orientationEventListener = new OrientationEventListener(VedioActivity.this, SensorManager.SENSOR_DELAY_NORMAL) {
//            @Override
//            public void onOrientationChanged(int i) {
//                Log.i("wanlijun","角度："+i);
//                if(i == ORIENTATION_UNKNOWN){
//                    return;
//                }
//                if(i<10 || i>350){
//                  i = 0;
//                }else if(i>80 && i<100){
//                    i = 90;
//                }else if(i>170 && i<190){
//                    i = 180;
//                }else if(i>260 && i<280){
//                    i = 270;
//                }
//            }
//        };
//        if(orientationEventListener.canDetectOrientation()){
//            orientationEventListener.enable();
//        }else{
//            Toast.makeText(VedioActivity.this,"can not detect orientation",Toast.LENGTH_SHORT).show();
//        }
    }
    private void initCamera(){
        int cameraNum = Camera.getNumberOfCameras();
        try {
            if(mCamera == null && cameraNum > 0){
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH_SPEED_HIGH);
//                Camera.Parameters parameters = mCamera.getParameters();
//                parameters.setPreviewSize(profile.videoFrameWidth,profile.videoFrameHeight);
//                mCamera.setParameters(parameters);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.setDisplayOrientation(90);
                Camera.Size size = mCamera.getParameters().getPreviewSize();
                byte[] buffer = new byte[(size.width * size.height * ImageFormat.getBitsPerPixel(ImageFormat.NV21))/8];
                mCamera.addCallbackBuffer(buffer);
                mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, Camera camera) {
                        //先addCallbackBuffer，然后再处理帧数据，否则会降低帧率
                        camera.addCallbackBuffer(bytes);
                        try {
                            Camera.Size size = camera.getParameters().getPreviewSize();
                            YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21,size.width,size.height,null);
                            Bitmap bitmap = null;
                            if(yuvImage != null){
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                yuvImage.compressToJpeg(new Rect(0,0,size.width,size.height),80,bos);
                                bitmap = BitmapFactory.decodeByteArray(bos.toByteArray(),0,bos.size());
                                bitmap = addWaterMark(bitmap);
                                bitmap = rotateBitmap(bitmap);
                                bos.close();
                            }
                            pictureIv.setImageBitmap(bitmap);
//                            Canvas canvas = vedioSv.getHolder().lockCanvas();
//                            canvas.drawBitmap(bitmap,0,0,new Paint());
//                            vedioSv.getHolder().unlockCanvasAndPost(canvas);
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.i("wanlijun",e.toString());
                        }
                    }
                });
                mCamera.startPreview();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean b, Camera camera) {
//                if(b){
//                    takePic();
//                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(orientationEventListener != null && orientationEventListener.canDetectOrientation()){
            orientationEventListener.disable();
        }
    }

    private void takePic(){
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                //当cameraPic目录不存在时，不能用 File fileDir = new File(Environment.getExternalStorageDirectory()+"/cameraPic/my.jpg");
                //file.createNewFile()会提示No such file or directory
                //当cameraPic目录存在时，就不存在这样的问题了
                File fileDir = new File(Environment.getExternalStorageDirectory()+"/cameraPic/");
                if(!fileDir.exists()){
                    fileDir.mkdirs();
                }
                File file = new File(fileDir,"my.jpg");
                if(file.exists()){
                    file.delete();
                }
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.close();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("wanlijun",e.toString());
                }
                mCamera.startPreview();
                try {
                    ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                    Log.i("wanlijun",exifInterface.getAttribute(ExifInterface.TAG_MAKE));
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("wanlijun",e.toString());
                }
            }
        });
    }

    //添加水印
    private Bitmap addWaterMark(Bitmap src){
        Bitmap newb = Bitmap.createBitmap(src.getWidth(),src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newb);
        canvas.drawBitmap(src,0,0,new Paint());
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        String text = "万丽君";
        canvas.drawText(text,10,10,new Paint());
        return newb;
    }
    //将图片旋转
    private Bitmap rotateBitmap(Bitmap src){
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(src,0,0,src.getWidth(),src.getHeight(),matrix,true);
        return bitmap;
    }

}
