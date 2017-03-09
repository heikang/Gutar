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
    }

    /**
     * IntentService在默认的工作线程中调用这个方法<p>   *当这个方法返回后，IntentService停止服务，如果能停止的话．
     */
    @Override
    protected void onHandleIntent(Intent intent) {
            /*Record part*/
        boolean fuller = true;
        double amplitude;
        int index;
        int SampleRate = 44100;
        final int bufferSize = AudioRecord.getMinBufferSize(SampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        final int fftsize = bufferSize * 4;
        double[] doubleFFT = new double[fftsize];
        DoubleFFT_1D doubleFFT_1D = new DoubleFFT_1D(bufferSize); // 8192

        final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        final short[] buffer = new short[bufferSize];
        audioRecord.startRecording();

        while (true) {
            int counter = 0;
            int n = 0;
            for (int i = 0; i < fftsize; i++) {
                // 除以32768.0 得到-1.0到1.0之间的数字
                doubleFFT[i] = 0;
            }
            while (true) {
                int recordingBuffer = audioRecord.read(buffer, 0, bufferSize);

                //FFT
                for (int i = 0; i < recordingBuffer; i++) {
                    // 除以32768.0 得到-1.0到1.0之间的数字
                    if (counter % 54 == 0) {
                        doubleFFT[n] = (double) buffer[i] / 32768.0;
                        n++;
                    }
                    counter++;
                    if (n == fftsize / 8)
                        break;
                }
                if (n == fftsize / 8)
                    break;
            }
            doubleFFT_1D.realForward(doubleFFT);

            //Calculation Freq
            amplitude = 0;
            index = 0;
            for (int i = 0; i < fftsize; i++) {
                if (amplitude < Math.abs(doubleFFT[i])) {
                    amplitude = Math.abs(doubleFFT[i]);
                    index = i;
                }
            }
            Log.e("Freq", "*" + Double.toString(((double) index * 1638.4 / fftsize)-1.2)+"HZ");
            //compare with standard freq
            //update GUI
        }
            /*Record part end*/
    }
}