package limitless.android.unfollowtita.Model;

/**
 * Actions of you can do on users
 */
public enum UserAction {

    FOLLOW("FOLLOW"),
    UNFOLLOW("UNFOLLOW"),
    BLOCK("BLOCK"),
    MUTE("MUTE"),
    ADD_TO_BLACK_LIST("ADD_TO_BLACK_LIST"),
    ADD_TO_WHITE_LIST("ADD_TO_WHITE_LIST");

    UserAction(String action) {
        this.action = action;
    }

    private String action;

    public String getAction() {
        return action;
    }
}
