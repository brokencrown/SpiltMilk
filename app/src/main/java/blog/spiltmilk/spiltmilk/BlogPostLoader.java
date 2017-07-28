package blog.spiltmilk.spiltmilk;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.Spanned;

/**
 * Created by jeremy on 7/21/2017.
 */

public class BlogPostLoader extends AsyncTaskLoader<Spanned>{

    private String mBody;

    /**
     * Post Loader constructor
     * @param context
     * @param body
     */
    public BlogPostLoader(Context context, String body) {
        super(context);

        mBody = body;
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
    public Spanned loadInBackground() {
        if (mBody == null) {
            return null;
        }

        Spanned formattedBody = Utils.formatBody(mBody);

        return formattedBody;
    }
}
