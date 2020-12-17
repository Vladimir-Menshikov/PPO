package com.example.ppo2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.media.AudioManager;
import android.media.SoundPool;

import java.time.Instant;
import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity
{

    private TextView tvTimer;
    private ListView lvModes;
    private DatabaseAdapter adapter;
    private ImageButton btnPause;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private TextView tvItemName;

    private CountDownTimer countDownTimer;
    private int id;
    private int prepare;
    private int work;
    private int chill;
    private int cycles;
    private int sets;
    private int setChill;
    private boolean isPause;
    private long timeLeft = 0;
    private long endTime;
    private int pos = 0;
    private int maxPos;
    private ArrayList<Pair<String,Integer>> modes;
    private SoundPool sp;
    private int soundIdBell;

    private boolean bound = false;
    private boolean needService;
    private ServiceConnection sConn;
    private Intent intent;
    MyService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        lvModes = (ListView) findViewById(R.id.lvModes);
        tvItemName = (TextView) findViewById(R.id.tvItemName);
        btnPause = (ImageButton) findViewById(R.id.btnPause);
        btnPrev = (ImageButton) findViewById(R.id.btnPrev);
        btnNext = (ImageButton) findViewById(R.id.btnNext);

        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundIdBell = sp.load(this, R.raw.bell, 1);

        isPause = false;
        btnPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isPause)
                {
                    if (pos < maxPos)
                    {
                        btnPause.setImageResource(android.R.drawable.ic_media_pause);
                        isPause = false;
                        timer();
                    }
                }
                else {
                    btnPause.setImageResource(android.R.drawable.ic_media_play);
                    isPause = true;
                    countDownTimer.cancel();
                }

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                next();
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (pos > 0)
                {
                    pos--;
                    countDownTimer.cancel();
                    startTimer();
                }
            }
        });

        adapter = new DatabaseAdapter(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            id = extras.getInt("id");
        }
        // если 0, то добавление
        if (id > 0)
        {
            // получаем элемент по id из бд
            adapter.open();
            Sequence sequence = adapter.getSequence(id);
            prepare = sequence.prepare;
            work = sequence.work;
            chill = sequence.chill;
            cycles = sequence.cycles;
            sets = sequence.sets;
            setChill = sequence.setChill;
            adapter.close();
        }

        modes = new ArrayList<Pair<String,Integer>>();
        modes.add(new Pair<String,Integer>(getString(R.string.prepare), prepare));
        for(int i = 0; i < sets; i++)
        {
            for(int j = 0; j < cycles; j++)
            {
                modes.add(new Pair<String,Integer>(getString(R.string.work), work));
                modes.add(new Pair<String,Integer>(getString(R.string.chill), chill));
            }
            modes.add(new Pair<String,Integer>(getString(R.string.setChill), setChill));
        }
        maxPos = 1 + sets * (cycles * 2 + 1);

        ModeAdapter modeAdapter = new ModeAdapter(this,
                R.layout.mode_item, modes);
        lvModes.setAdapter(modeAdapter);

        timeLeft = modes.get(pos).second * 1000;

        needService = true;
        intent = new Intent(this, MyService.class);
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int i = 0; i < maxPos; i++)
        {
            temp.add(modes.get(i).second);
        }
        intent.putExtra("modes", temp);
        sConn = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder)
            {
                myService = ((MyService.MyBinder) binder).getService();
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                bound = false;
            }
        };
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (bound)
        {
            pos = myService.getPos();
            timeLeft = myService.getTimeLeft();
            unbindService(sConn);
            stopService(intent);
            bound = false;
        }

        if (isPause)
        {
            btnPause.setImageResource(android.R.drawable.ic_media_play);
        }
        else
        {
            btnPause.setImageResource(android.R.drawable.ic_media_pause);
        }

        tvTimer.setText(String.valueOf((int)(timeLeft/1000)));
        tvItemName.setText(modes.get(pos).first);

        if (!isPause)
        {
            timer();
        }
        lvModes.setSelection(pos);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (needService && (!bound) && (!isPause))
        {
            intent.putExtra("pos", pos);
            intent.putExtra("timeLeft", timeLeft);
            startService(intent);
            bindService(intent, sConn, BIND_AUTO_CREATE);
            bound = true;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (bound)
        {
            unbindService(sConn);
            stopService(intent);
            bound = false;
        }
    }

    public void startTimer()
    {
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
        }
        tvItemName.setText(modes.get(pos).first);
        timeLeft = modes.get(pos).second * 1000;
        tvTimer.setText(String.valueOf((int)(timeLeft/1000)));
        if (!isPause) {
            timer();
        }
        lvModes.setSelection(pos);
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
            tvTimer.setText(R.string.finish);
            tvItemName.setText(R.string.finish);
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
        endTime = timeLeft + System.currentTimeMillis();
        countDownTimer = new CountDownTimer(timeLeft, 1000)
        {
            @Override
            public void onTick(long l)
            {
                timeLeft = l;
                tvTimer.setText(String.valueOf((int)(l/1000)));
            }

            @Override
            public void onFinish()
            {
                sp.play(soundIdBell, 1, 1, 0, 0, 1);
                next();
            }
        }.start();
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setTitle(R.string.finishTraining)
                .setMessage(R.string.finishTrainingSure)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // The user wants to leave - so dismiss the dialog and exit
                        countDownTimer.cancel();
                        needService = false;
                        finish();
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // The user is not sure, so you can exit or just stay
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPause", isPause);
        outState.putInt("pos", pos);
        outState.putLong("timeLeft", timeLeft);
        outState.putLong("endTime", endTime);
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        isPause = savedInstanceState.getBoolean("isPause");
        pos = savedInstanceState.getInt("pos");
        timeLeft = savedInstanceState.getLong("timeLeft");
        endTime = savedInstanceState.getLong("endTime");
        if (!isPause)
        {
            timeLeft = endTime - System.currentTimeMillis();
        }
    }
}