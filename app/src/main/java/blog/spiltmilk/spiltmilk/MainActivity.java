package blog.spiltmilk.spiltmilk;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Post>> {

    private static final String QUERY_URL = "http://www.spiltmilk.blog/blog?format=json";
    private ProgressBar mProgressBar;
    private TextView mEmptyState;
    private static final int POST_LOADER_ID = 1;
    private PostAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Use {@link ConnectivityManager} to retrieve connection status
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        /**
         * Test for to ensure internet connection before attempting HTTP request
         * Display {@link ProgressBar} if loading slowly
         * Display EmptyView with text if no connection detected
         */
        if (activeNetwork != null && activeNetwork.isConnected()) {
            getLoaderManager().initLoader(POST_LOADER_ID, null, this);
        } else {

            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
            mProgressBar.setVisibility(View.GONE);

            mEmptyState = (TextView) findViewById(R.id.empty_state);
            mEmptyState.setText(R.string.no_connection);
        }

        ListView postListView = (ListView) findViewById(R.id.list);

        // Assign {@link EmptyView} to {@link TextView}
        mEmptyState = (TextView) findViewById(R.id.empty_state);
        postListView.setEmptyView(mEmptyState);

        mAdapter = new PostAdapter(this, new ArrayList<Post>());

        postListView.setAdapter(mAdapter);

        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent blogPostIntent = new Intent(getApplicationContext(), BlogPostActivity.class);
                blogPostIntent.putExtra("post", mAdapter.getItem(position));
                startActivity(blogPostIntent);
            }
        });
    }

    @Override
    public Loader<ArrayList<Post>> onCreateLoader(int id, Bundle args) {
        return new PostLoader(this, QUERY_URL);
    }

    /**
     * Hide {@link ProgressBar}, clear adapter and add new data is the data is not null
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<Post>> loader, ArrayList<Post> data) {
        mAdapter.clear();

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Post>> loader) {
        mAdapter.clear();
    }
}
