package com.applicationsbar.firstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import static com.applicationsbar.firstapp.MainActivity.tcpClient;

public class DisplayMessageActivity extends AppCompatActivity {

    public static TCPClient tcpClient;

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
            public void processFinish(String response) {
                    TextView textView = findViewById(R.id.textView);

                    textView.append(System.getProperty("line.separator")+response);

            }
        };

        TCPClient.delegate=ar;
    }

    public void sendMessage(View view) {
        String message = ((EditText) findViewById(R.id.messageToSend)).getText().toString();
        TCPClient.message=message;
    }
}
