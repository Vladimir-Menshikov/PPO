package com.example.ppo2;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyService extends Service
{

    MyBinder binder = new MyBinder();

    private CountDownTimer countDownTimer;

    private long timeLeft;
    private int pos;
    private int maxPos;
    private ArrayList<Integer> modes;
    private SoundPool sp;
    private int soundIdBell;

    public void onCreate()
    {
        super.onCreate();
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundIdBell = sp.load(this, R.raw.bell, 1);
    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        modes = intent.getIntegerArrayListExtra("modes");
        maxPos = modes.size();
        timeLeft = intent.getLongExtra("timeLeft", 0);
        pos = intent.getIntExtra("pos", 0);
        timer();
        return super.onStartCommand(intent, flags, startId);
    }


    public IBinder onBind(Intent arg0)
    {
        return binder;
    }

    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    public void onDestroy()
    {
        super.onDestroy();
        countDownTimer.cancel();
    }

    public void startTimer()
    {
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
        }
        timeLeft = modes.get(pos) * 1000;
        timer();
    }

    public void next()
    {

        if (pos == maxPos - 1)
        {
            pos++;
            if (countDownTimer != null)
            {
                countDownTimer.cancel();
            }
        }
        else if (pos == maxPos)
        {

        }
        else {
            pos++;
            startTimer();
        }
    }

    public void timer()
    {
        countDownTimer = new CountDownTimer(timeLeft, 1000)
        {
            @Override
            public void onTick(long l)
            {
                timeLeft = l;
            }

            @Override
            public void onFinish()
            {
                sp.play(soundIdBell, 1, 1, 0, 0, 1);
                next();
            }
        }.start();
    }

    public int getPos()
    {
        return pos;
    }

    public long getTimeLeft()
    {
        return timeLeft;
    }


    class MyBinder extends Binder
    {
        MyService getService()
        {
            return MyService.this;
        }
    }
}
