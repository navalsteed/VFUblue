package com.example.mohamed.blue11;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mohamed on 2015-02-17.
 */
public class DataViewerActivity extends Activity {
    private TextView mChannel1;
    private TextView mChannel2;
    private TextView mChannel3;
    private TextView mChannel4;
    private TextView mChannel5;
    private TextView mChannel6;
    private TextView mChannel7;
    private TextView mChannel8;
    private Button mGraph;

    //private BluetoothChatService mChatService = null;
    Thread updateChannels;
    Handler handler;
    String ch1;
    String ch2;
    String ch3;
    String ch4;
    String ch5;
    String ch6;
    String ch7;
    String ch8;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_viewer_layout);

        mGraph = (Button)findViewById(R.id.btnGraph);
        mGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Intent i = new Intent(getApplicationContext(),DynamicXYPlotActivity.class);
             //   startActivity(i);
            }
        });

        mChannel1 = (TextView) findViewById(R.id.mChannel1TextView);
        mChannel2 = (TextView) findViewById(R.id.mChannel2TextView);
        mChannel3 = (TextView) findViewById(R.id.mChannel3TextView);
        mChannel4 = (TextView) findViewById(R.id.mChannel4TextView);
        mChannel5 = (TextView) findViewById(R.id.mChannel5TextView);
        mChannel6 = (TextView) findViewById(R.id.mChannel6TextView);
        mChannel7 = (TextView) findViewById(R.id.mChannel7TextView);
        mChannel8 = (TextView) findViewById(R.id.mChannel8TextView);

        if (isRunning(getApplication())==true){
            updateChannels = new Thread(new UpdateThread());
            updateChannels.start();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    ch1 = msg.getData().getString("channel1");
                    mChannel1.setText(ch1);
                    ch2 = msg.getData().getString("channel2");
                    mChannel2.setText(ch2);

                }
            };
        }}


    class UpdateThread implements Runnable{

        @Override
        public void run() {
            while (isRunning(getApplication())){
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                ch1 = new String(Constants.buffer2).substring(0,2);
                ch2 = new String(Constants.buffer2).substring(2,4);
                bundle.putString("channel1",ch1);
                bundle.putString("channel2",ch2);
                message.setData(bundle);
                handler.sendMessage(message);

            }
        }
    }

    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //   List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        // List<ActivityManager.RunningTaskInfo> processInfos  = activityManager.getRunningAppProcesses();

        // List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunning(Integer.MAX_VALUE);


        final Set<String> activePackages = new HashSet<String>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
                return true;
            }
//        for (ActivityManager.RunningTaskInfo task : ) {
//            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
//                return true;
       }

            return false;


//    public void getValue(){
//
//        if(Constants.buffer2 != null){
//            byte [] buffer = Constants.buffer2 ;
//            ch1 = new String(buffer).substring(0,2);
//            ch2 = new String(buffer).substring(2,4);
//
//
//        }
//    }
//
//    public void setvalue() {
//        mChannel1.setText(ch1);
//        mChannel2.setText(ch2);
////        mChannel1.setText(ch3);
////        mChannel2.setText(ch4);
////        mChannel1.setText(ch5);
////        mChannel2.setText(ch6);
////        mChannel1.setText(ch7);
////        mChannel2.setText(ch8);
   }


}
