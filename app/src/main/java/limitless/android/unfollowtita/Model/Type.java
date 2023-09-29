package limitless.android.unfollowtita.Model;

/**
 * Type of users
 */
public enum Type {

    FOLLOWING("following"),
    FOLLOWERS("followers"),
    FANS("fans"),
    NOT_FOLLOWING_BACK("not_following_back"),
    BLOCKED("blocked"),
    MUTUAL_FRIENDS("mutual_friends"),
    MUTED("muted"),
    WHITE_LIST("white_list"),
    BLACK_LIST("black_list");

    private String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
