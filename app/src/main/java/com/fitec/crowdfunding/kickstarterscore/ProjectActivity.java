package com.fitec.crowdfunding.kickstarterscore;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;


public class ProjectActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Project>> {

    public static final String LOG_TAG = ProjectActivity.class.getName();
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int PROJECT_LOADER_ID = 1;
    /**
     * URL for project data from the web service
     */
    private static final String WEBSERVICE_REQUEST_URL = "http://fitec-filrouge-com.stackstaging.com/ProjetFilRouge/scripts/json_for_android.php";
    /**
     * Adapter for the list of projects
     */
    private ProjectAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * Progress bar
     */
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView projectListView = (ListView) findViewById(R.id.list);
        // set empty view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_text_view);
        projectListView.setEmptyView(mEmptyStateTextView);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // Create a new adapter that takes an empty list of projects as input
            mAdapter = new ProjectAdapter(this, new ArrayList<Project>());

            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            projectListView.setAdapter(mAdapter);

            // Set an item click listener on the ListView, which sends an intent to a web browser
            // to open a website with more information about the selected project.
            projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // Find the current project that was clicked on
                    Project currentProject = mAdapter.getItem(position);
                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri projectUri = Uri.parse(currentProject.getUrl());
                    // Create a new intent to view the project URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, projectUri);
                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            });

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(PROJECT_LOADER_ID, null, this);
        } else {
            // Set empty state text to display "No internet connection."
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            // Hide progress bar
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
            mProgressBar.setVisibility(GONE);
        }
    }

    @Override
    public Loader<List<Project>> onCreateLoader(int i, Bundle bundle) {
        return new ProjectLoader(this, WEBSERVICE_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Project>> loader, List<Project> projects) {
        // Clear the adapter of previous project data
        mAdapter.clear();
        // If there is a valid list of {@link Project}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (projects != null && !projects.isEmpty()) {
            mAdapter.addAll(projects);
        }
        // Set empty state text to display "No projects found."
        mEmptyStateTextView.setText(R.string.no_projects_found);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Project>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}