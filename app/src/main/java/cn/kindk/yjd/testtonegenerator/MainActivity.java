package cn.kindk.yjd.testtonegenerator;

import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

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


    final private int playPeriod = 170;
    final private int playInterval = 180;

    private EditText ssidEditText;
    private EditText pwdEditText;
    private TextView pm2_5TextView;
    private TextView pm10TextView;
    byte[] ssid; //Android-TP-LINK_2.4GHz
    byte[] pwd;
    // toneCode = prefix + ssid + divide + pwd + suffix;
    byte[] toneCode;

    byte[] prefix; //000
    byte[] divide; //111
    byte[] suffix; //000

    int index;

    Thread socketThread;
    Thread readThread;
    PrintWriter out;
    BufferedReader br;
    Socket socket;
    byte[] buffer = new byte[100];
    InputStream is;

    int PM2_5;
    int PM10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generator = new ToneGenerator(STREAM_MUSIC, 100);

        ssidEditText = (EditText) findViewById(R.id.ssidEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);
        pm2_5TextView = (TextView) findViewById(R.id.PM2_5TextView);
        pm10TextView = (TextView) findViewById(R.id.PM10TextView);


        ssid   = new byte[ssidMaxLength];
        pwd    = new byte[pwdMaxLength];
        toneCode = new byte[toneCodeMaxLength];

        prefix = new byte[prefixLength];


        socketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("10.192.241.142", 6000);
                    out = new PrintWriter(
                            new BufferedWriter(new OutputStreamWriter(
                                    socket.getOutputStream())), true);
                    is = socket.getInputStream();
//                    br = new BufferedReader(new InputStreamReader(
//                            socket.getInputStream()));
                    sleep(3000);
                    readThread.start();
                    if (socket.isConnected())
                        Log.i(TAG, "socket is connect");
                    while (socket.isConnected()) {
                        Log.i(TAG, "Send GET");
                        out.println("GET");
                        sleep(5000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        socketThread.start();

        readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int temp;
                while (socket.isConnected()) {
                    //Log.i(TAG, "read thread");
                    try {
                        while ((temp = is.read(buffer)) != -1) {
                            PM2_5 = (buffer[4] - '0') * 10 + (buffer[5] - '0');
                            PM10 = (buffer[6] - '0') * 10 + (buffer[7] - '0');
                            Log.i(TAG, "Recv PM sensor data: " + PM2_5 + ' ' + PM10);
                            System.out.println(new String(buffer, 0, temp));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pm2_5TextView.setText(""+PM2_5);
                                    pm10TextView.setText("" + PM10);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


//        for (int i = 0; i < prefixLength; i++) {
//            prefix[i] = prefixCode;
//            divide[i] = prefixCode;
//            suffix[i] = prefixCode;
//        }
    }

    public void onConnectBtnClicked(View view) {
        int a = 27, b = 35;
        //String message = "SET:" + Integer.toHexString(a)+Integer.toHexString(b);
        String message = "GET:" + a+b;
        Log.i(TAG, message);
        out.println(message);
    }

    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "start Play");
                for (int i = 0; i < index; i++) {
                    generator.startTone(toneCode[i], playPeriod);
                    sleep(playInterval);
                }
                Log.i(TAG, "Play over");
            }
        }).start();
    }

    public void onPlayBtnClicked(View view) throws InterruptedException {
        ssid = ssidEditText.getText().toString().getBytes();
        pwd  = pwdEditText.getText().toString().getBytes();

        Log.i(TAG, "1SSID: " + ssid.length + " " + Arrays.toString(ssid));
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
