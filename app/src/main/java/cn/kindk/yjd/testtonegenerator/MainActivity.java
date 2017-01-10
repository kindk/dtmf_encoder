package cn.kindk.yjd.testtonegenerator;

import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.os.SystemClock.sleep;

public class MainActivity extends AppCompatActivity {
    static ToneGenerator generator;

    final static String TAG = "TestToneGenerator";
    final private int playPeriod = 100;
    final private int playInterval = 100;

    private EditText ssidEditText;
    private EditText pwdEditText;
    char[] ssid;
    char[] pwd;
    char[] prefix;
    char[] divide;
    char[] suffix;
    char   key = 0;
    char   lastKey = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generator = new ToneGenerator(STREAM_MUSIC, 100);

        ssidEditText = (EditText) findViewById(R.id.ssidEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);

        prefix = new char[4];
        divide = new char[3];
        suffix = new char[3];
        ssid   = new char[30];
        pwd    = new char[30];





//        byte    a = -128;       //  8  -128     -   127
//        short   b = 65;         //  16 -32768   -   32767
//        char    c = 65;         //  16  0       -   65535, unicode.
//        int     d = 1434;       //  32  -xxxx   -   xxxx-1
//        long    e = 1434;       //  64
//        float   f = -5.265f;
//        double  g = -5.265;
//        boolean h = false;
//
//        String str = "jdal";
//        byte[] as = str.getBytes();
//
//        Log.w(TAG, new String(as));
        //new String()

//        Log.w(TAG, "" + c);     //打印出来的不是整数, 而是对应的unicode编码: A
//        Log.w(TAG, "" + b);     //打印出来的是整数, 65
    }

    public void onPlayBtnClicked(View view) throws InterruptedException {
        Log.w(TAG, "Btn clicked!");
        Log.w(TAG, ssidEditText.getText().toString());

        ssid = ssidEditText.getText().toString().toCharArray();
        pwd = pwdEditText.getText().toString().toCharArray();

//        Log.w(TAG, "ssid: " + ssid[0]);
//        Log.w(TAG, "s
//                        if (lastKey == '0') {
//
//                        }
//
//
//                        if (key == '0' && (resIdx >= 2)) {
//                            if
//                        }
//sid: " + (byte)ssid[0]);


        prefix[0] = 10;
        prefix[1] = 10;
        prefix[2] = 3;
        prefix[3] = 10;

        divide[0] = 10;  //*
        divide[1] = 11;  //#
        divide[2] = 10;

        suffix[0] = 11;
        suffix[1] = 8;
        suffix[2] = 11;

///////////test
//        prefix[0] = 12;
//        prefix[1] = 10;
//        prefix[2] = 11;
//        prefix[3] = 13;
//
//        divide[0] = 14;  //*
//        divide[1] = 15;  //#
//        divide[2] = 0;
//
//        suffix[0] = 11;
//        suffix[1] = 8;
//        suffix[2] = 11;
//

        send();

        //generator.startTone(ToneGenerator.TONE_DTMF_9, 100);
    }

    public void send() throws InterruptedException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < prefix.length; i++) {
                    Log.w(TAG, "prefix" + (byte)(prefix[i]));
                    generator.startTone(prefix[i], playPeriod);
                    sleep(playInterval);
                }

                for (int i = 0; i < ssid.length; i++) {
                    byte tmp = (byte)ssid[i];

                    Log.w(TAG, "ssid : " + tmp);
                    if (tmp == 0x33 || tmp == 0x44 || tmp == 0x55 || tmp == 0x66 ||
                            tmp == 0x77) {
                        generator.startTone(8, playPeriod);
                        sleep(playInterval);
                    } else {
                        generator.startTone(tmp / 16, playPeriod);
                        sleep(playInterval);
                    }

                    generator.startTone(tmp % 16, playPeriod);
                    sleep(playInterval);

///////////     Android-TP-LINK_2.4GHz      goodandroid

//                    if (tmp >= '0' && tmp <= '9') {
//                        generator.startTone(3, playPeriod);
//                        sleep(playInterval);
//                        generator.startTone(tmp-'0', playPeriod);
//                        sleep(playInterval);
//                    }
//pwd
//                    if (tmp >= 'A' && tmp <= 'Z') {
//
//                    }

//                    key = (char)(ssid[i] - '0');
//
//                    Log.w(TAG, "ssid" + (ssid[i]-'0'));
//
//                    generator.startTone(key, playPeriod);
//                    sleep(playInterval);
                }

                for (int i = 0; i < divide.length; i++) {
                    Log.w(TAG, "divide" + (byte)(divide[i]));
                    generator.startTone(divide[i], playPeriod);
                    sleep(playInterval);
                }

//                for (int i = 0; i < pwd.length; i++) {
//                    Log.w(TAG, "pwd" + (pwd[i]-'0'));
//                    generator.startTone(pwd[i] - '0', playPeriod);
//                    sleep(playInterval);
//                }

                for (int i = 0; i < pwd.length; i++) {
                    byte tmp = (byte) pwd[i];

                    if (tmp == 0x33 || tmp == 0x44 || tmp == 0x55 || tmp == 0x66 ||
                            tmp == 0x77) {
                        generator.startTone(8, playPeriod);
                        sleep(playInterval);
                    } else {
                        generator.startTone(tmp / 16, playPeriod);
                        sleep(playInterval);
                    }

                    generator.startTone(tmp % 16, playPeriod);
                    sleep(playInterval);
                }

                for (int i = 0; i < suffix.length; i++) {
                    Log.w(TAG, "suffix" + (byte)(suffix[i]));
                    generator.startTone(suffix[i], playPeriod);
                    sleep(playInterval);
                }
            }
        }).start();

     //   generator.startTone(ToneGenerator.TONE_DTMF_9, 100);
    }
}
