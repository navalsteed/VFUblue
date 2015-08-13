package com.example.mohamed.blue11;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Wifi extends Activity {

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton,mRunButton;
    WifiManager wifi;
    ArrayAdapter<String> mConversationList;
    private StringBuffer mOutStringBuffer;
    private String IP = "194.47.44.105";
    private int PORT =8089;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    Handler handler ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wifi_chat);
        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        mConversationView = (ListView) findViewById(R.id.in);
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mSendButton = (Button) findViewById(R.id.button_send);
        mRunButton = (Button) findViewById(R.id.button_run);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        byte [] messageBytes = (byte [])msg.obj;
                        String message = new String(messageBytes,0,msg.arg1);
                        mConversationList.add("Server :"+ message);
                        break;
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!wifi.isWifiEnabled()){
            promptToEnableWifi();
//            try {
//                wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        new Thread(new ConnectingThread()).start();
        setUpChat();
    }

    class CheckingThread implements Runnable{

        @Override
        public void run() {
            while (true){
                if(wifi.isWifiEnabled()){
                    try {
                        Thread.sleep(2000);
                        notify();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    protected void promptToEnableWifi() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a wifi connection to use this application. Enable Wi-Fi ?.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Enable Wifi",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                wifi.setWifiEnabled(true);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Wifi.this.finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setUpChat() {
        mConversationList = new ArrayAdapter<String>(getApplication(),R.layout.message);
        mConversationView.setAdapter(mConversationList);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    TextView textView = (TextView) findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });

        mOutStringBuffer = new StringBuffer("");

        mRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplication(),DataViewerActivity.class);
                startActivity(i);
            }
        });
    }

    class ConnectingThread implements Runnable{
        @Override
        public void run() {
            try {
                socket = new Socket(IP,PORT);
                if(socket != null){
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte [] buffer = new byte[1024];
            int bytes;
            String reading;
            while (in != null){
                try {
                    bytes = in.read(buffer);
                    if(bytes > 0){
                        for(int i =0; i<buffer.length;i++){
                            Constants.buffer2[i] = buffer[i];
                        }
                        reading = new String(buffer,0,bytes);
                        System.out.println(reading);
                        handler.obtainMessage(1,bytes,-1,buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessage(String message) {
        if(message.length() > 0){
            byte [] messageBytes = message.getBytes();
            // send to the outputstream
            if(out != null){
                try {
                    out.write(messageBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mOutStringBuffer.setLength(0);
            mOutEditText.setText("");
        }
    }
}
