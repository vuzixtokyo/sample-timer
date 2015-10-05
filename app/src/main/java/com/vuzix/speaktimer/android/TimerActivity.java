package com.vuzix.speaktimer.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimerActivity extends AppCompatActivity {

    private static final int SEC = 1000;
    private static final int MIN = SEC * 60;
    private static final int HOUR = MIN * 60;

    private static final String KEY_HOUR = "key_hour";
    private static final String KEY_MIN = "key_min";
    private static final String KEY_SEC = "key_sec";

    static Intent getIntent(Context context, int hour, int min, int sec) {
        Intent intent = new Intent(context, TimerActivity.class);

        intent.putExtra(KEY_HOUR, hour);
        intent.putExtra(KEY_MIN, min);
        intent.putExtra(KEY_SEC, sec);

        return intent;
    }

    @Bind(R.id.pb_remain)
    ProgressBar mProgressBar;

    @Bind(R.id.tv_hour)
    TextView mHour;

    @Bind(R.id.tv_min)
    TextView mMin;

    @Bind(R.id.tv_sec)
    TextView mSec;

    private long mStartTime;
    private long mRemainTimeInMilliSeconds;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mHandler = new MyHandler(this);

        setContentView(R.layout.activity_timer);
        ButterKnife.bind(this);

        int hour = getIntent().getIntExtra(KEY_HOUR, 0);
        int min = getIntent().getIntExtra(KEY_MIN, 0);
        int sec = getIntent().getIntExtra(KEY_SEC, 0);

        mRemainTimeInMilliSeconds = (((hour * 60) + min * 60) + sec) * 1000;

        mProgressBar.setIndeterminate(false);
        mProgressBar.setMax((int) (mRemainTimeInMilliSeconds / 1000));

        showRemainTime(mRemainTimeInMilliSeconds);

        mStartTime = System.currentTimeMillis();

        mHandler.sendEmptyMessage(HANDLE_TICK);
    }

    private void showRemainTime(long remainTimeInMilliSeconds) {
        long hour = remainTimeInMilliSeconds / HOUR;
        long min = remainTimeInMilliSeconds % HOUR / MIN;
        long sec = remainTimeInMilliSeconds % MIN / SEC;

        mHour.setText(String.format(Locale.JAPAN, "%02d", hour));
        mMin.setText(String.format(Locale.JAPAN, "%02d", min));
        mSec.setText(String.format(Locale.JAPAN, "%02d", sec));

        mProgressBar.setProgress((int) (remainTimeInMilliSeconds / 1000));
    }

    private static final int HANDLE_TICK = 0x01;

    private static class MyHandler extends Handler {

        private final WeakReference<TimerActivity> activity;

        MyHandler(TimerActivity a) {
            activity = new WeakReference<>(a);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            TimerActivity timerActivity = activity.get();
            if (timerActivity == null) {
                return;
            }

            switch (msg.what) {
                case HANDLE_TICK:
                    long progress = System.currentTimeMillis() - timerActivity.mStartTime;
                    progress = timerActivity.mRemainTimeInMilliSeconds - progress;

                    if (progress >= 0) {
                        sendEmptyMessageDelayed(HANDLE_TICK, SEC);
                    } else {
                        progress = 0;
                    }

                    timerActivity.showRemainTime(progress);
                    break;
            }

        }
    }
}
