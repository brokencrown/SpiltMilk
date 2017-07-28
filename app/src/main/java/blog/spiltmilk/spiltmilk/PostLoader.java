package blog.spiltmilk.spiltmilk;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.ArrayList;

/**
 * Created by jeremy on 7/14/2017.
 */

public class PostLoader extends AsyncTaskLoader<ArrayList<Post>> {

    private String mUrl;

    /**
     * Post Loader constructor
     * @param context
     * @param url
     */
    public PostLoader(Context context, String url) {
        super(context);

        mUrl = url;
    }

    /**
     * forceLord on start
     */
    @Override
    protected void onStartLoading() { forceLoad();}

    /**
     * Load the post results in the background if the URL is not null
     * @return
     */
    @Override
    public ArrayList<Post> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        ArrayList<Post> results = Utils.retrievePostData(mUrl);
        return results;
    }
}
