package com.example.audiovideo;

import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.security.Policy;

/**
 * 用小米5c测试的，前后置摄像头暂不能同时打开，
 * SurfaceHolderCallbackBack是后置摄像头的回调
 * SurfaceHolderCallbackFront是前置摄像头的回调
 * 同时开启前后摄像头时，只有后置摄像头能工作
 */
public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private Button switchCameraBtn;
    private SurfaceView captureSV;
    private SurfaceView smallPreviewSv;
    private MediaRecorder mediaRecorder;
    //视频采集分辨率
    int width = 320;
    int height = 240;
    private Camera cameraBack;
    private Camera cameraFront;
    private Camera mCamera;
    private int cameraPosition = 0;//1代表前置摄像头，0代表后置摄像头
    private int displayOrientation = 90;//相机预览方向，默认是横屏的，旋转90度为竖屏
    SurfaceHolder surfaceHolderBack;
    SurfaceHolder surfaceHolderFront;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        switchCameraBtn = (Button)findViewById(R.id.switchCameraBtn);
        switchCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换前后摄像头
                changeCamera();
            }
        });
        //大预览 后置摄像头
        captureSV = (SurfaceView)findViewById(R.id.captureSV);
        //小预览 前置摄像头
        smallPreviewSv = (SurfaceView)findViewById(R.id.smallPreviewSv);
        surfaceHolderBack = captureSV.getHolder();
//        surfaceHolderBack.addCallback(new SurfaceHolderCallbackBack());
        surfaceHolderBack.addCallback(this);
        surfaceHolderBack.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolderFront = smallPreviewSv.getHolder();
//        surfaceHolderFront.addCallback(new SurfaceHolderCallbackFront());
        surfaceHolderFront.addCallback(this);
        surfaceHolderFront.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initCamera(){
        try {
            Camera.Parameters parameters = cameraBack.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            cameraBack.setDisplayOrientation(90);
            cameraBack.setParameters(parameters);
            cameraBack.startPreview();
            cameraBack.cancelAutoFocus();
//            mediaRecorder = new MediaRecorder();
//            camera.unlock();
//            mediaRecorder.setCamera(camera);
//            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//            mediaRecorder.setVideoSize(width,height);
//            mediaRecorder.setVideoFrameRate(30);
//            mediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
//            mediaRecorder.setOrientationHint(90);
//            mediaRecorder.setMaxDuration(30 * 1000);
//            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        }catch (Exception e){
            e.printStackTrace();
            Log.i("wanlijun",e.toString());
        }
    }

    //打开摄像头
    private Camera openCamera(int cameraId){
        int cameraNum = Camera.getNumberOfCameras();
        Log.i("wanlijun","相机数量："+cameraNum);
        if(cameraNum > 0 && cameraId < cameraNum){
            try {
                mCamera = Camera.open(cameraId);
                if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    mCamera.setPreviewDisplay(surfaceHolderFront);
                }else{
                    mCamera.setPreviewDisplay(surfaceHolderBack);
                }
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                if(CameraActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                    parameters.setRotation(90);
                    parameters.set("orientation","portrait");
                    mCamera.setDisplayOrientation(90);
                }else{
                    parameters.set("orientation","landscape");
                    parameters.setRotation(0);
                    mCamera.setDisplayOrientation(0);
                }
                mCamera.setParameters(parameters);
                mCamera.startPreview();
                return  mCamera;
            }catch (Exception e){
                e.printStackTrace();
                Log.i("wanlijun",e.toString());
//                    cameraBack.release();
                return null;
            }
        }else{
            Toast.makeText(CameraActivity.this,"手机没有检测到摄像头",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //切换前后摄像头
    private void changeCamera(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
        }
        if(cameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK){
            openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
            cameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else{
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
        openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    class SurfaceHolderCallbackBack implements SurfaceHolder.Callback{
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            int cameraNum = Camera.getNumberOfCameras();
            Log.i("wanlijun","相机数量："+cameraNum);
            if(cameraNum > 0){
                try {
                    cameraBack = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    cameraBack.setPreviewDisplay(surfaceHolder);
                    Camera.Parameters parametersBack = cameraBack.getParameters();
                    if(CameraActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                        parametersBack.setRotation(90);
                        parametersBack.set("orientation","portrait");
                        cameraBack.setDisplayOrientation(90);
                    }else{
                        parametersBack.set("orientation","landscape");
                        parametersBack.setRotation(0);
                        cameraBack.setDisplayOrientation(0);
                    }
                    cameraBack.setParameters(parametersBack);
                    cameraBack.startPreview();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("wanlijun",e.toString());
//                    cameraBack.release();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
           cameraBack.autoFocus(new Camera.AutoFocusCallback() {
               @Override
               public void onAutoFocus(boolean b, Camera camera) {
                   initCamera();
                   camera.cancelAutoFocus();
               }
           });
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }
    class SurfaceHolderCallbackFront implements SurfaceHolder.Callback{
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            int cameraNum = Camera.getNumberOfCameras();
            if(cameraNum == 2) {
                try {
                    cameraFront = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    cameraFront.setPreviewDisplay(surfaceHolder);
                    Camera.Parameters parameters = cameraFront.getParameters();
                    if (CameraActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        parameters.set("orientation", "landscape");
                        parameters.setRotation(0);
                        cameraFront.setDisplayOrientation(0);
                    } else {
                        parameters.set("orientation", "portrait");
                        parameters.setRotation(90);
                        cameraFront.setDisplayOrientation(90);
                    }
                    cameraFront.setParameters(parameters);
                    cameraFront.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("wanlijun", e.toString());
//                cameraFront.release();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            if(cameraFront != null) {
                cameraFront.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        Camera.Parameters parameters = cameraFront.getParameters();
                        parameters.setPictureFormat(PixelFormat.JPEG);
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        cameraFront.setDisplayOrientation(90);
                        cameraFront.setParameters(parameters);
                        cameraFront.startPreview();
                        cameraFront.cancelAutoFocus();
                        camera.cancelAutoFocus();
                    }
                });
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    }
}
