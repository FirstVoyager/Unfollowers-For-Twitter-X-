package limitless.android.unfollowtita.Model;

public class ModelAccInformation {
    public String title;
    public String data;
    public Action action;

    public enum Action {
        NONE,
        COPY,
        SHARE
    }

}
