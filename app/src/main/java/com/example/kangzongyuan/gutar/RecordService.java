package com.example.kangzongyuan.gutar;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import org.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by kangzongyuan on 2017-03-07.
 */
public class RecordService extends IntentService {
    /**
     * 一个构造函数是必须的，并且你必须调用父类的IntentService(String)以传入工作线程的名字.
     */
    public RecordService() {
        super("RecordService");
        Log.e("MainActivityOnCreat","start service");
    }
    /**
     * IntentService在默认的工作线程中调用这个方法<p>   *当这个方法返回后，IntentService停止服务，如果能停止的话．
     */
    @Override
    protected void onHandleIntent(Intent intent) {
            /*Record part*/
        int bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        double[] doubleFFT = new double[bufferSize];
        DoubleFFT_1D doubleFFT_1D = new DoubleFFT_1D(16384);

        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,bufferSize);
        short[] buffer = new short[bufferSize];
        audioRecord.startRecording();

        while(true) {
            int recordingBuffer = audioRecord.read(buffer, 0, bufferSize);
            Log.e("MainActivityOnCreat",String.valueOf(recordingBuffer));

            //从short变换到double
            for (int i = 0; i < 16384 && i < recordingBuffer; i++) {
                // 除以32768.0 得到-1.0到1.0之间的数字
                doubleFFT[i] = (double) buffer[i]/ 32768.0;
                //Log.e("MainActivityOnCreat",String.valueOf(doubleFFT[i]));
            }
            doubleFFT_1D.realForward(doubleFFT);
        }
            /*Record part end*/
    }
}