package blog.spiltmilk.spiltmilk;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.LoaderManager;

/**
 * Created by jeremy on 7/19/2017.
 */

public class BlogPostActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Spanned> {

    private ProgressBar mProgressBar;
    private TextView mEmptyState;
    private static final int BLOG_POST_LOADER_ID = 2;
    private Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_post_activity);

        ConnectivityManager cm = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Use {@link ConnectivityManager} to retrieve connection status
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        mEmptyState = (TextView) findViewById(R.id.empty_state);


        Intent postIntent = getIntent();
        mPost = postIntent.getParcelableExtra("post");

        /**
         * Test for to ensure internet connection before attempting HTTP request
         * Display {@link ProgressBar} if loading slowly
         * Display EmptyView with text if no connection detected
         */
        if (activeNetwork != null && activeNetwork.isConnected()) {
            getLoaderManager().initLoader(BLOG_POST_LOADER_ID, null, this);
        } else {

            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
            mProgressBar.setVisibility(View.GONE);

            mEmptyState.setText(R.string.no_connection);
        }
    }


    @Override
    public android.content.Loader<Spanned> onCreateLoader(int id, Bundle args) {
        return new BlogPostLoader(this, mPost.getBody());
    }

    @Override
    public void onLoadFinished(android.content.Loader<Spanned> loader, Spanned data) {

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        if (data != null && data.length() > 0) {
            TextView bodyTextView = (TextView) findViewById(R.id.post_container);
            bodyTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            bodyTextView.setText(data);

            TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
            titleTextView.setText(mPost.getTitle());

            TextView dateTextView = (TextView) findViewById(R.id.date_text_view);
            dateTextView.setText(Utils.formatDate(mPost.getTimeInMilliseconds()));
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Spanned> loader) {
        mPost = null;
    }
}
