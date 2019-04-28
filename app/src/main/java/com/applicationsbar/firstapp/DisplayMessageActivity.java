package com.applicationsbar.firstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static com.applicationsbar.firstapp.MainActivity.tcpClient;

public class DisplayMessageActivity extends AppCompatActivity {

    public static TCPClient tcpClient;

    ImageView myImage;
    byte[] gmessage;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        TCPClient.AsyncResponse ar=new TCPClient.AsyncResponse() {

            @Override
            public void processFinish(int messageType, byte[] message) {
                    if (messageType==1) {
                        String msgStr=new String(message);

                        TextView textView = findViewById(R.id.textView);

                        textView.append(System.getProperty("line.separator") + msgStr);
                    }

                    else {

                        myImage = (ImageView) findViewById(R.id.imageView1);
                        gmessage=message;
                        new Thread(new Runnable(){
                            public void run() {
                                try {
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                    options.inDither = false;
                                    bitmap =  BitmapFactory.decodeByteArray(gmessage,0,gmessage.length,options);
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            myImage.setImageBitmap(bitmap);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                       // Bitmap myBitmap = BitmapFactory.decodeByteArray(message,0,message.length);



                       // myImage.setImageBitmap(myBitmap);

                    }

            }
        };

        TCPClient.delegate=ar;




    }


    public void sendMessage(View view) {
        String message = ((EditText) findViewById(R.id.messageToSend)).getText().toString();
        TCPClient.message=message;
        ((EditText) findViewById(R.id.messageToSend)).setText("");
    }



    public void draw(View view) {

        Intent intent = new Intent(this, DrawActivity.class);
        startActivity(intent);


    }
}
