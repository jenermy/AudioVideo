package com.example.audiovideo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 视频数据由RTP传输（传输层）
 * 视频质量由RTCP控制（传输层）
 * 播放暂停快进回放由RTSP提供（应用层）
 * 播放流程：获取流->解码->播放
 * 录制播放流程：录制音视频->剪辑->编码->上传服务器->别人播放
 * 直播流程：录制音视频->编码->流媒体传输到服务器->流媒体传输到其它客户端->解码->播放
 * 录制音视频：AudioRecord/MediaRecord
 * 视频剪辑：mp4parser或ffmpeg
 * 音视频编码：aac&h264
 * 上传大文件，网络框架，进度监听，断点续传
 * 流媒体传输：传输协议
 * 音视频解码：aac&h264
 * 渲染播放：MediaPlayer
 */
public class MainActivity extends AppCompatActivity {
    private Button imageViewBtn;
    private Button surfaceViewBtn;
    private Button customViewBtn;
    private Button audioRecordBtn;
    private Button vedioBtn;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private Button audioTrackBtn;
    private RecordTask recordTask;
    private PlayTask playTask;
    private Button stopBtn;
    private File wavFile;
    private Button cameraBtn;
    private Button recordVedioBtn;
    private Button animationBtn;
    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewBtn = (Button)findViewById(R.id.imageViewBtn);
        surfaceViewBtn = (Button)findViewById(R.id.surfaceViewBtn);
        customViewBtn = (Button)findViewById(R.id.customViewBtn);
        audioRecordBtn = (Button)findViewById(R.id.audioRecordBtn);
        audioTrackBtn = (Button)findViewById(R.id.audioTrackBtn);
        stopBtn = (Button)findViewById(R.id.stopBtn);
        cameraBtn = (Button)findViewById(R.id.cameraBtn);
        vedioBtn = (Button)findViewById(R.id.vedioBtn);
        recordVedioBtn = (Button)findViewById(R.id.recordVedioBtn);
        animationBtn = (Button)findViewById(R.id.animationBtn);
//        if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,permissions,101);
//        }
        ActivityCompat.requestPermissions(MainActivity.this,permissions,101);
        imageViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ImageViewActivity.class));
            }
        });
        surfaceViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SurfaceViewActivity.class));
            }
        });
        customViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CustomViewActivity.class));
            }
        });
        audioRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordTask = new RecordTask();
                recordTask.execute();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRecording = false;
                isPlaying = false;
            }
        });
        audioTrackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               playTask = new PlayTask();
               playTask.execute();
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CameraActivity.class));
            }
        });
        vedioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,30);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
                startActivityForResult(intent,101);
            }
        });
        recordVedioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,VedioActivity.class));
            }
        });
        animationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AnimationActivity.class));
            }
        });
    }
    private void play(){
        isPlaying = true;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/reverseme.pcm");
        int bufferSize = AudioTrack.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT);
        short[] music = new short[bufferSize];
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    44100,
                    AudioFormat.CHANNEL_IN_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
            while (isPlaying && dataInputStream.available() > 0){
                int i = 0;
                while(dataInputStream.available() > 0 && i<music.length){
                    music[i] = dataInputStream.readShort();
                    i++;
                }
                audioTrack.write(music,0,music.length);
            }
            audioTrack.stop();
            inputStream.close();
            bufferedInputStream.close();
            dataInputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //生成PCM文件以及WAV文件，PCM是原始为音频数据，一般的播放器识别不了，转化成WAV格式的文件后就可以正常播放
    //WAV文件就是比PCM文件多了个头文件
    private void record(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss", Locale.CHINA);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/reverseme.pcm");
        wavFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "wav");
        File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + sdf.format(new Date())+".wav");
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            OutputStream outputStream = new FileOutputStream(file);
            DataOutputStream wavdos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(wavFile)));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
            //AudioFormat.CHANNEL_IN_MONO 单通道
            //AudioFormat.CHANNEL_IN_STEREO 双通道
            int bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,44100,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSize);
            //WAV文件的头文件
            writeWavFileHeader(wavdos,bufferSize,44100,audioRecord.getChannelCount());
            short[] buffer = new short[bufferSize];
            audioRecord.startRecording();
            isRecording = true;
            while (isRecording){
                int readResult = audioRecord.read(buffer,0,bufferSize);
                for(int i=0;i<readResult;i++){
                    //PCM文件和WAV文件分别写入音频数据流
                    dataOutputStream.writeShort(buffer[i]);
                    wavdos.writeShort(buffer[i]);
                }
            }
            audioRecord.stop();
            outputStream.close();
            bufferedOutputStream.close();
            dataOutputStream.close();
            wavdos.close();
            //生成临时的.wav文件
            RandomAccessFile raf = new RandomAccessFile(tempFile,"rw");
            byte[] header = generateWavFileHeader(file.length(), 44100, audioRecord.getChannelCount());
            raf.seek(0);
            raf.write(header);
            raf.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    /**
     * @param out            wav音频文件流
     * @param totalAudioLen  不包括header的音频数据总长度
     * @param longSampleRate 采样率,也就是录制时使用的频率
     * @param channels       audioRecord的频道数量
     * @throws IOException 写文件错误
     */
    private void writeWavFileHeader(OutputStream out, long totalAudioLen, long longSampleRate,
                                    int channels) throws IOException {
        byte[] header = generateWavFileHeader(totalAudioLen, longSampleRate, channels);
        out.write(header, 0, header.length);
    }
    /**
     * 任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，
     * wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk，
     * FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的
     *
     * @param pcmAudioByteCount 不包括header的音频数据总长度
     * @param longSampleRate    采样率,也就是录制时使用的频率
     * @param channels          audioRecord的频道数量
     */
    private byte[] generateWavFileHeader(long pcmAudioByteCount, long longSampleRate, int channels) {
        long totalDataLen = pcmAudioByteCount + 36; // 不包含前8个字节的WAV文件总长度
        long byteRate = longSampleRate * 2 * channels;
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (2 * channels);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (pcmAudioByteCount & 0xff);
        header[41] = (byte) ((pcmAudioByteCount >> 8) & 0xff);
        header[42] = (byte) ((pcmAudioByteCount >> 16) & 0xff);
        header[43] = (byte) ((pcmAudioByteCount >> 24) & 0xff);
        return header;
    }

    class RecordTask extends AsyncTask<Void,Integer,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            record();
            return null;
        }
    }
    class PlayTask extends AsyncTask<Void,Integer,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            play();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 101){
            Log.i("wanlijun",data.getData().toString());
        }
    }
}
