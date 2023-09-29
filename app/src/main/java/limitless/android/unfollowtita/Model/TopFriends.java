package limitless.android.unfollowtita.Model;


import twitter4j.User;

public class TopFriends {
    public User user;
    public long id;
    public String screenName;
    public int count;
    public int degree;

    public int getCount(){
        return count;
    }
}
