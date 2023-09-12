package com.example.kamalnayan.qunto;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

public class MyTask extends AsyncTask<Void, Void, Void> {
   private ProgressBar progress;
    public MyTask(ProgressBar progress) {
        this.progress = progress;
    }

    public void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    public void onPostExecute(Void unused) {
        progress.setVisibility(View.GONE);
    }
}