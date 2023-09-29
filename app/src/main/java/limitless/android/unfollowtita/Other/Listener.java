package limitless.android.unfollowtita.Other;

/**
 * Global listener for any thing
 * @param <T> Your custom data
 */
public interface Listener<T> {

    /**
     * Your main data
     * @param t
     */
    void data(T t);

    /**
     * Show error
     * @param e
     */
//    void error(Exception e);

}
