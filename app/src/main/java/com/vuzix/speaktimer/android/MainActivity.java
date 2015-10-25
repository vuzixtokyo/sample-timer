package com.vuzix.speaktimer.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind({R.id.tv_hour, R.id.tv_min, R.id.tv_sec})
    List<TextView> timeArray;

    @Bind(R.id.tv_hour)
    TextView mHour;

    @Bind(R.id.tv_min)
    TextView mMin;

    @Bind(R.id.tv_sec)
    TextView mSec;

    private int mFocusedIndex = 0;
    private TextView mFocusedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        for (TextView tv : timeArray) {
            tv.setTextSize(getResources().getDimensionPixelSize(R.dimen.time_textsize_normal));
        }
        setFocus(mFocusedIndex);
    }

    private boolean setFocus(int index) {
        if (index < 0) {
            return false;
        }
        if (index > timeArray.size() - 1) {
            return false;
        }

        if (mFocusedTextView != null) {
            mFocusedTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.time_textsize_normal));
        }
        mFocusedTextView = timeArray.get(index);
        mFocusedTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.time_textsize_large));

        mFocusedIndex = index;

        return true;
    }

    private boolean setNextFocus() {
        return setFocus(mFocusedIndex + 1);
    }

    private boolean setPrevFocus() {
        return setFocus(mFocusedIndex - 1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:   // 前
                countUp(1);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:    // 後
                countDown(1);
                break;
            case KeyEvent.KEYCODE_ENTER: {
                boolean result = setNextFocus();
                if (!result) {
                    startTimer();
                }
                break;
            }
            case KeyEvent.KEYCODE_BACK: {
                boolean result = setPrevFocus();
                if (result) {
                    return true;
                }
                break;
            }
            case KeyEvent.KEYCODE_MENU:
                LicenseDialog.create(this).show();
                break;
            default:
                Log.d(TAG, "unknown keycode = " + keyCode);
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void startTimer() {
        int hour = Integer.parseInt(mHour.getText().toString());
        int min = Integer.parseInt(mMin.getText().toString());
        int sec = Integer.parseInt(mSec.getText().toString());

        Intent intent = TimerActivity.getIntent(this, hour, min, sec);
        startActivity(intent);
    }

    private void countUp(int count) {
        int value = Integer.parseInt(mFocusedTextView.getText().toString());
        value += count;
        if (mFocusedTextView != mHour || value < 60) {
            mFocusedTextView.setText(String.format(Locale.JAPAN, "%02d", value));
        }
    }

    private void countDown(int count) {
        int value = Integer.parseInt(mFocusedTextView.getText().toString());
        value -= count;
        if (value >= 0) {
            mFocusedTextView.setText(String.format(Locale.JAPAN, "%02d", value));
        }
    }

}
