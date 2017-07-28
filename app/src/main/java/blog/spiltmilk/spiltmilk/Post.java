package blog.spiltmilk.spiltmilk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jeremy on 7/14/2017.
 */

public class Post implements Parcelable {

    private String mImage;
    private String mBody;
    private String mTitle;
    private long mTimeInMilliseconds;

    public Post(String image, String body, String title, long date) {
        mImage = image;
        mBody = body;
        mTitle = title;
        mTimeInMilliseconds = date;
    }

    /**
     * Constructor accepting {@link Parcel}
     * @param in
     */
    private Post(Parcel in) {
        mBody = in.readString();
        mTitle = in.readString();
        mTimeInMilliseconds = in.readLong();
    }

    public String getImage() {
        return mImage;
    }

    public String getBody() {
        return mBody;
    }

    public String getTitle() {
        return mTitle;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Create {@link Parcel} from Post
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mBody);
        dest.writeString(this.mTitle);
        dest.writeLong(mTimeInMilliseconds);
    }

    /**
     * Create Post from {@link Parcel}
     */
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
