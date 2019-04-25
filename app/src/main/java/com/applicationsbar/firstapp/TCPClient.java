package com.applicationsbar.firstapp;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class TCPClient extends AsyncTask<URL, Integer, Long> {




    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(int messageType, byte[] message);
    }

    public static AsyncResponse delegate = null;

    String response = "";
    public static Socket clientSocket;
    PrintStream os;
    InputStream is;
    public static String message="";
    HashMap<String, String> params;

    public static byte[] ba=null;


    TCPClient(AsyncResponse delegate,  HashMap<String, String> pparams) {
        params = pparams;

        this.delegate = delegate;
    }

    TCPClient(String message) {
        this.message = message;

    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder feedback = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                feedback.append("&");

            feedback.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            feedback.append("=");
            feedback.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return feedback.toString();
    }

    public void getData() throws IOException {


        int portNumber = 2222;
        // The default host.
        String host = "10.0.2.2";
        try {
            clientSocket = new Socket(host, portNumber);
            os = new PrintStream(clientSocket.getOutputStream());
            is =clientSocket.getInputStream();
            os.println(getPostDataString(params));
            String responseLine;
            byte[] b=new  byte[1000];
            try {
                while (true) {
                    if (is.available()>0) {
                        int messageType = is.read();
                        int messageLength = 0;
                        for (int i=0; i<4; i++) {
                            int j = is.read();
                            messageLength=messageLength*256+j;
                        }
                        byte[] message= new byte[messageLength];
                        for (int i=0; i<messageLength; i++){
                            message[i]= (byte)is.read();
                        }

                        delegate.processFinish(messageType,message);

                        /*if (responseLine.indexOf("*** Bye") != -1)
                                break;*/

                    }

                    if (message.length()>0){
                        sendStringMessage(message);
                        message="";
                    }

                    if (ba!=null) {
                        sendByteArrayMessage(ba);
                        //os.write(ba);
                        ba=null;
                    }
                }
               // closed = true;
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Long doInBackground(URL... params) {
        try {

             getData();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // This counts how many bytes were downloaded
        final byte[] result = response.getBytes();
        Long numOfBytes = Long.valueOf(result.length);
        return numOfBytes;
    }
    @Override
    protected void onPostExecute(Long result) {


       // delegate.processFinish(response);

    }

    private void sendStringMessage(String s) {

        byte[] ba=s.getBytes();
        try {

            byte currentByte=1;
            os.write(currentByte);
            int len=ba.length;

            for (int i=3; i>=0; i--){
                currentByte= (byte)((len>>(i*8))% 256);
                os.write(currentByte);
            }

            os.write(ba);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendByteArrayMessage(byte[] ba) {


        try {

            byte currentByte=2;
            os.write(currentByte);
            int len=ba.length;

            for (int i=3; i>=0; i--){
                currentByte= (byte)((len>>(i*8))% 256);
                os.write(currentByte);
            }

            os.write(ba);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}



