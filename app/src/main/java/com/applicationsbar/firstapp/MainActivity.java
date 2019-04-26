package com.applicationsbar.firstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    MainActivity appConetxt=this;
    public static TCPClient tcpClient;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void checkLogin(View view) {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        String loginMessage="";


        try {
            loginMessage="password="+URLEncoder.encode(password, "UTF-8")+"&username="+URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tcpClient = new TCPClient(new TCPClient.AsyncResponse() {

            @Override
            public void processFinish(int messageType, byte[] message) {
                String response=new String(message);
                System.out.println(response);
                if (response.contains("Welcome")){
                    intent = new Intent(appConetxt, DisplayMessageActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, response);
                    startActivity(intent);
                }

            }
        },  loginMessage);
        tcpClient.execute();

    }
}
