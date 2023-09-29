package limitless.android.unfollowtita.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {

    public long id;
    public String name;
    public String screenName;
    public String bio;
    public String profileUrl;
    public String headerUrl;
    public String accessToken;
    public String accessSecret;
    public String consumerToken;
    public String consumerSecret;
    public boolean isMain;
    public boolean followingLoaded = false;
    public boolean followersLoaded = false;
    public boolean blockedLoaded = false;
    public boolean mutedLoaded = false;

    public Account() {

    }

    public Account(
            long id, String name, String screenName,
            String bio, String profileUrl, String headerUrl,
            String accessToken, String accessSecret, String consumerToken,
            String consumerSecret, boolean isMain, boolean followingLoaded,
            boolean followersLoaded, boolean blockedLoaded, boolean mutedLoaded) {
        this.id = id;
        this.name = name;
        this.screenName = screenName;
        this.bio = bio;
        this.profileUrl = profileUrl;
        this.headerUrl = headerUrl;
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
        this.consumerToken = consumerToken;
        this.consumerSecret = consumerSecret;
        this.isMain = isMain;
        this.followingLoaded = followingLoaded;
        this.followersLoaded = followersLoaded;
        this.blockedLoaded = blockedLoaded;
        this.mutedLoaded = mutedLoaded;
    }

    protected Account(Parcel in) {
        id = in.readLong();
        name = in.readString();
        screenName = in.readString();
        bio = in.readString();
        profileUrl = in.readString();
        headerUrl = in.readString();
        accessToken = in.readString();
        accessSecret = in.readString();
        consumerToken = in.readString();
        consumerSecret = in.readString();
        isMain = in.readByte() != 0;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(screenName);
        dest.writeString(bio);
        dest.writeString(profileUrl);
        dest.writeString(headerUrl);
        dest.writeString(accessToken);
        dest.writeString(accessSecret);
        dest.writeString(consumerToken);
        dest.writeString(consumerSecret);
        dest.writeByte((byte) (isMain ? 1 : 0));
    }
}
