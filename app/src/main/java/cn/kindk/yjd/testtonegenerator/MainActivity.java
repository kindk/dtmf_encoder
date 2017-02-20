package cn.kindk.yjd.testtonegenerator;

import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Arrays;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.os.SystemClock.sleep;

public class MainActivity extends AppCompatActivity {
    static ToneGenerator generator;

    final static String TAG = "TestToneGenerator";
    final private int ssidMaxLength = 50;
    final private int pwdMaxLength = 50;
    final private int toneCodeMaxLength = 200; //////TODO
    final private int prefixLength = 5;
    final private byte prefixCode = 0;

    final private byte divideCode = 15;


    final private int playPeriod = 140;
    final private int playInterval = 150;

    private EditText ssidEditText;
    private EditText pwdEditText;
    byte[] ssid; //Android-TP-LINK_2.4GHz
    byte[] pwd;
    // toneCode = prefix + ssid + divide + pwd + suffix;
    byte[] toneCode;

    byte[] prefix; //000
    byte[] divide; //111
    byte[] suffix; //000

    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generator = new ToneGenerator(STREAM_MUSIC, 100);

        ssidEditText = (EditText) findViewById(R.id.ssidEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);

        ssid   = new byte[ssidMaxLength];
        pwd    = new byte[pwdMaxLength];
        toneCode = new byte[toneCodeMaxLength];

        prefix = new byte[prefixLength];

//        for (int i = 0; i < prefixLength; i++) {
//            prefix[i] = prefixCode;
//            divide[i] = prefixCode;
//            suffix[i] = prefixCode;
//        }
    }

    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < index; i++) {
                    generator.startTone(toneCode[i], playPeriod);
                    sleep(playInterval);
                }
            }
        }).start();
    }

    public void onPlayBtnClicked(View view) throws InterruptedException {
        ssid = ssidEditText.getText().toString().getBytes();
        pwd  = pwdEditText.getText().toString().getBytes();

        Log.i(TAG, "SSID: " + ssid.length + " " + Arrays.toString(ssid));
        Log.i(TAG, "PWD: " + pwd.length + " " + Arrays.toString(pwd));

        //int index;
        int i;
        byte h, l;

        for (i = 0;i < toneCode.length; i++) {
            toneCode[i] = 0;
        }

        index = 0;
        for (i = 0; i < prefixLength; i++) {
            toneCode[index++] = prefixCode;
        }

        for (i = 0; i < ssid.length; i++) {
            h = (byte)(ssid[i] / 15);
            l = (byte)(ssid[i] % 15);

            // Duplicate code.
            if (h == toneCode[index-1]) {
                toneCode[index++] = divideCode;
                toneCode[index++] = h;
            } else {
                toneCode[index++] = h;
            }

            if (l == toneCode[index-1]) {
                toneCode[index++] = divideCode;
                toneCode[index++] = l;
            } else {
                toneCode[index++] = l;
            }
        }

        //If last ssid code is 0.
        if (toneCode[index-1] == 0) {
            toneCode[index++] = divideCode;
        }


        for (i = 0; i < prefixLength; i++) {
            toneCode[index++] = prefixCode;
        }

        for (i = 0; i < pwd.length; i++) {
            h = (byte)(pwd[i] / 15);
            l = (byte)(pwd[i] % 15);

            // Duplicate code.
            if (h == toneCode[index-1]) {
                toneCode[index++] = divideCode;
                toneCode[index++] = h;
            } else {
                toneCode[index++] = h;
            }

            if (l == toneCode[index-1]) {
                toneCode[index++] = divideCode;
                toneCode[index++] = l;
            } else {
                toneCode[index++] = l;
            }
        }


        //If last ssid code is 0.
        if (toneCode[index-1] == 0) {
            toneCode[index++] = divideCode;
        }



        for (i = 0; i < prefixLength; i++) {
            toneCode[index++] = prefixCode;
        }

        toneCode[index] = 0;
        Log.i(TAG, "toneCode length: " + index);
        Log.i(TAG, Arrays.toString(toneCode));

        play();
    }
}
