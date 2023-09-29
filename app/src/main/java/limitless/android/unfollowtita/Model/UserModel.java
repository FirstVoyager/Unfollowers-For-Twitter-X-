package limitless.android.unfollowtita.Model;

public class UserModel {

    public int id;
    public long userId;
    public long accountId;
    public twitter4j.User user;
    public String type;
    public boolean isProgressing = false;

    public UserModel() {

    }

    public UserModel(int id, long userId, long accountId, twitter4j.User user, String type) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.user = user;
        this.type = type;
    }
}
