package blog.spiltmilk.spiltmilk;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import static com.bumptech.glide.request.RequestOptions.centerCropTransform;
import java.util.ArrayList;

/**
 * Created by jeremy on 7/14/2017.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    private Context mContext;

    public PostAdapter(Activity context, ArrayList<Post> posts) {
        super(context, 0, posts);

        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post currentPost = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        ImageView postImageView = (ImageView) convertView.findViewById(R.id.list_item_image);
        Glide.with(mContext).load(currentPost.getImage()).apply(centerCropTransform()).into(postImageView);

        TextView dateView = (TextView) convertView.findViewById(R.id.list_item_date);
        dateView.setText(Utils.formatDate(currentPost.getTimeInMilliseconds()));

        TextView descriptionView = (TextView) convertView.findViewById(R.id.list_item_description);
        descriptionView.setText(extractDescription(currentPost.getBody()));

        TextView titleView = (TextView) convertView.findViewById(R.id.list_item_title);
        titleView.setText(currentPost.getTitle());

        return convertView;
    }

    /**
     * Extract description from first paragraph of {@link Post} body
     * @param body
     * @return
     */
    private static String extractDescription(String body) {

        Document doc = Jsoup.parse(body);
        Element p = doc.select("p").first();

        String description = p.text();

        return description;
    }

}
