package com.applicationsbar.firstapp;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
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
        void processFinish(String output);
    }

    public static AsyncResponse delegate = null;

    String response = "";
    public static Socket clientSocket;
    PrintStream os;
    DataInputStream is;
    public static String message="";
    HashMap<String, String> params;


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
           // is = new DataInputStream(clientSocket.getInputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os.println(getPostDataString(params));
            String responseLine;
            byte[] b=new  byte[1000];
            try {
                while (true) {
                    if (in.ready()) {
                        responseLine = in.readLine();
                        delegate.processFinish(responseLine);
                         if (responseLine.indexOf("*** Bye") != -1)
                                break;

                    }
                    if (message.length()>0){
                        os.println(message);
                        message="";
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

    protected void onPostExecute(Long result) {


        delegate.processFinish(response);

    }
}



