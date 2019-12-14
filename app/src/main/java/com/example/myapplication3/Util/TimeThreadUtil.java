package com.example.myapplication3.Util;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.myapplication3.TimeListener;

public class TimeThreadUtil extends Thread {
    private static final int CURRENTDATETIME = 1;
    private static TimeThreadUtil timeThreadUtil;
    private TimeListener listener;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            listener.onGetDateTime();
        }
    };



    public TimeThreadUtil(TimeListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();
        do {
            try {
                Thread.sleep(1000);
                Message msg = new Message();
                msg.what = CURRENTDATETIME;
                mhandler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
    }

}
