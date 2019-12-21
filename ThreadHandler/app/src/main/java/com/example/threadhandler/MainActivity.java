package com.example.threadhandler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.view_text);
        findViewById(R.id.view_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        newThread("Hello stranger...");
    }

//=================================================================================

    private void newThread(final String text) {
        Log.d("!!!", "UI thread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("!!!", "New thread" );
                setText(text);
            }
        }).start();
    }

    private void setText(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(text);
            }
        });
    }
}
