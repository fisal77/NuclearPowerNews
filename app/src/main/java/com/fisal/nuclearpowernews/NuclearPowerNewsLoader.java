package com.fisal.nuclearpowernews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by fisal on 13/01/2018.
 */

public class NuclearPowerNewsLoader extends AsyncTaskLoader<List<NuclearPower>> {

    private static final String LOG_TAG = NuclearPowerNewsLoader.class.getName();

    private String mUrl;

    public NuclearPowerNewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * Background thread.
     */
    @Override
    public List<NuclearPower> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of Nuclear Power news.
        List<NuclearPower> nuclearPowers = QueryTools.fetchNuclearPowerData(mUrl);
        return nuclearPowers;
    }
}
