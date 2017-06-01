package com.fitec.crowdfunding.kickstarterscore;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

import static com.fitec.crowdfunding.kickstarterscore.ProjectActivity.LOG_TAG;

public class ProjectLoader extends AsyncTaskLoader<List<Project>> {

    String mUrl;

    public ProjectLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Project> loadInBackground() {
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (mUrl == null) {
            return null;
        }
        List<Project> result = QueryUtils.fetchProjectData(mUrl);
        Log.d(LOG_TAG, "Fetched " + result.size() + " projects");
        return result;
    }
}