package com.example.asynctask;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String RETAINING_FRAGMENT_TAG = "MainActivity.RETAINING_FRAGMENT_TAG";
    private static final String TASK_UUID_TAG = "MainActivity.TASK_UUID_TAG";
    private TextView mTextView;
    private Button mButton;
    private ProgressBar mProgressBar;
    //private RetainingFragment mRetainingFragment;
    private StorageFragment mStorageFragment;
    private final CalculationTask.Listener mListener = new CalculationTask.Listener() {
        @Override
        public void onProgressChanged(int progress) {
            mProgressBar.setProgress(progress);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onFinished(int result) {
            updateUi(false);
            mTextView.setText(String.format("Result: %d", result));
        }
    };

    private CalculationTask mTask;
    private String mTaskKey;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.view_progress_bar);
        mTextView = findViewById(R.id.view_result);
        mButton = findViewById(R.id.view_button);
        findViewById(R.id.view_button).setOnClickListener(this);

        if (savedInstanceState == null) {
            mTaskKey = UUID.randomUUID().toString();
            //mRetainingFragment = new RetainingFragment();
            mStorageFragment = StorageFragment.getInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(mStorageFragment, RETAINING_FRAGMENT_TAG).commit();
//            getSupportFragmentManager().beginTransaction()
//                    .add(mRetainingFragment, RETAINING_FRAGMENT_TAG).commit();
        } else {
            mTaskKey = savedInstanceState.getString(TASK_UUID_TAG);
            mStorageFragment = (StorageFragment) getSupportFragmentManager()
                    .findFragmentByTag(RETAINING_FRAGMENT_TAG);
//            mRetainingFragment = (RetainingFragment) getSupportFragmentManager()
//                    .findFragmentByTag(RETAINING_FRAGMENT_TAG);
        }
        mTask = (CalculationTask) mStorageFragment.get(mTaskKey);
//        mTask = (CalculationTask) mRetainingFragment.get(mTaskKey);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTask != null) {
            mStorageFragment.put(mTaskKey, mTask);
            //mRetainingFragment.put(mTaskKey, mTask);
            mTask.setListener(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTask != null) {
            if (mTask.getStatus() != AsyncTask.Status.FINISHED) {
                updateUi(true);
                mProgressBar.setProgress(mTask.getProgress());
                mTask.setListener(mListener);
            } else {
                updateUi(false);
                try {
                    mTextView.setText(String.format("Result: %d", mTask.get()));
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            updateUi(false);
            mTextView.setText("Result N/A");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TASK_UUID_TAG, mTaskKey);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            mStorageFragment.remove(mTaskKey);
            //mRetainingFragment.remove(mTaskKey);
        }
    }

    @Override
    public void onClick(View v) {
        mTask = new CalculationTask();
        mTask.setListener(mListener);
        mProgressBar.setProgress(0);
        mTask.execute();
        updateUi(true);
    }

    private void updateUi(boolean isInProgress) {
        if (isInProgress) {
            mProgressBar.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.GONE);
            mTextView.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mButton.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    private static class CalculationTask extends AsyncTask<Void, Integer, Integer> {

        private Listener mListener;
        private int mProgress;

        @Override
        protected Integer doInBackground(Void... voids) {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                publishProgress(i);
            }
            return 43;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgress = values[0];
            if (mListener != null) {
                mListener.onProgressChanged(mProgress);
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (mListener != null) {
                mListener.onFinished(result);
            }
        }

        public void setListener(Listener listener) {
            mListener = listener;
        }

        public int getProgress() {
            return mProgress;
        }

        public interface Listener {
            void onProgressChanged(int progress);
            void onFinished(int result);
        }
    }
}
