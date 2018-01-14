package com.fisal.nuclearpowernews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NuclearPowerNewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NuclearPower>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = NuclearPowerNewsActivity.class.getName();

    private static final String NUCLEAR_NEWS_URL =
            "https://content.guardianapis.com/search";

    private static final int NUCLEAR_LOADER_ID = 1;

    private NuclearPowerAdapter mAdapter;

    private TextView mEmptyTxtView;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST MSG: Nuclear power news activity onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuclear_power_news);

        // Create new adapter and set it to the {@link ListView}
        ListView nuclearListView = (ListView) findViewById(R.id.list);
        mAdapter = new NuclearPowerAdapter(this, new ArrayList<NuclearPower>());
        nuclearListView.setAdapter(mAdapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

        // Set TextView that is displayed when the list is empty
        mEmptyTxtView = (TextView) findViewById(R.id.empty_view);
        nuclearListView.setEmptyView(mEmptyTxtView);

        // Set ProgressBar that is displayed when the list is downloading.
        mProgressBar = (ProgressBar) findViewById(R.id.loading_bar);

        // Get the URL web link for each title in list view and make click listener.
        nuclearListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                NuclearPower currentNuclearPowerNews = mAdapter.getItem(position);
                Uri nuclearPowerNewsUri = Uri.parse(currentNuclearPowerNews.getWebUrl());
                Intent webLinkIntent = new Intent(Intent.ACTION_VIEW, nuclearPowerNewsUri);
                startActivity(webLinkIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connState = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo netInfo = connState.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (netInfo != null && netInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.i(LOG_TAG, "TEST: Calling initLoader() ...");
            loaderManager.initLoader(NUCLEAR_LOADER_ID, null, this);

        } else {
            // Otherwise, display error
            // First, hide loading progress bar so error message will be visible
            View loadingProgressBar = findViewById(R.id.loading_bar);
            loadingProgressBar.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyTxtView.setText(R.string.no_internet_connection);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_order_by_key)) ||
                key.equals(getString(R.string.settings_section_key))){
            // Clear the ListView as a new query will be kicked off
            mAdapter.clear();
            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyTxtView.setVisibility(View.GONE);
            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.loading_bar);
            loadingIndicator.setVisibility(View.VISIBLE);
            // Restart the loader to requery the Nuclear Power news as the query settings have been updated
            getLoaderManager().restartLoader(NUCLEAR_LOADER_ID, null, this);
        }
    }

    @Override
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<NuclearPower>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: onCreateLoader() called ...");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String section = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(NUCLEAR_NEWS_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `q=nuclear`
        uriBuilder.appendQueryParameter("q", "nuclear");
        if (!((section) == getString(R.string.settings_section_default))) {
            uriBuilder.appendQueryParameter("section", section);
        }
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("from-date", "2017-01-01");
        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("order-by", orderBy);

        // Return the completed uri https://content.guardianapis.com/search?q=nuclear&section=environment&show-tags=contributor&from-date=2017-01-01&api-key=test&order-by=oldest
        return new NuclearPowerNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NuclearPower>> loader, List<NuclearPower> nuclearPowers) {
        // Hide the loading indicator (Progress bar) when onLoadFinished() is called.
        mProgressBar.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyTxtView.setText(R.string.no_nuclearPowerNews);

        Log.i(LOG_TAG, "TEST: onLoadFinished() called ...");

        // Clear the adapter of previous Nuclear Power news data
        mAdapter.clear();

        // If there is a valid list of {@link nuclearPower}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (nuclearPowers != null && !nuclearPowers.isEmpty()) {
            mAdapter.addAll(nuclearPowers);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NuclearPower>> loader) {

        Log.i(LOG_TAG, "TEST: onLoaderReset() called ...");

        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
