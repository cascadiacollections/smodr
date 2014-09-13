package com.kevintcoughlin.smodr.jobs;

import android.content.ContentValues;

import com.kevintcoughlin.smodr.SmodrApplication;
import com.kevintcoughlin.smodr.data.database.table.EpisodesTable;
import com.kevintcoughlin.smodr.data.model.Episodes;
import com.kevintcoughlin.smodr.data.provider.SmodrProvider;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

public class UpdateEpisodeJob extends Job {
    public static final int PRIORITY = 1;
    public static final int UPDATE_INTERVAL = 5000;
    private int mId = 0;
    private int mPosition = 0;
    private int mDuration = 0;

    public UpdateEpisodeJob(int id, int position, int duration) {
        super(new Params(PRIORITY));
        this.mId = id;
        this.mPosition = position;
        this.mDuration = duration;
    }

    @Override
    public void onAdded() {
        // Update UI
    }

    @Override
    public void onRun() throws Throwable {
        ContentValues values = new ContentValues();
        values.put(EpisodesTable._ID, mId);
        values.put(EpisodesTable.POSITION, mPosition);
        values.put(EpisodesTable.DURATION, mDuration);

        SmodrApplication.getInstance().getApplicationContext().getContentResolver().update(
                SmodrProvider.EPISODES_CONTENT_URI,
                values,
                EpisodesTable._ID + " = " + mId,
                null
        );
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }

    @Override
    protected void onCancel() {

    }
}
