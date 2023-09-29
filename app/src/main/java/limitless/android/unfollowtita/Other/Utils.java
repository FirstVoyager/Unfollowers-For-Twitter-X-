package limitless.android.unfollowtita.Other;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import limitless.android.unfollowtita.BuildConfig;
import limitless.android.unfollowtita.Dialog.UserBottomSheet;
import limitless.android.unfollowtita.Model.UserModel;
import twitter4j.Status;
import twitter4j.User;

public class Utils {

    public static void error(Exception e){
        if (e != null && BuildConfig.DEBUG){
            e.printStackTrace();
        }
    }

    public static void startActivity(Context context, Intent intent){
        if (context == null || intent == null)
            return;
        try {
            context.startActivity(intent);
        }catch (Exception e){
            error(e);
        }
    }

    public static void startActivity(Context context, Class<?> aClass){
        if (context == null || aClass == null)
            return;
        try {
            startActivity(context, new Intent(context, aClass));
        }catch (Exception e){
            error(e);
        }
    }

    public static void shareText(Context context, String s){
        Intent intent = new Intent();
        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, s);
        startActivity(context, intent);
    }

    public static void shareText(Context context, @StringRes int id){
        if (context != null)
            shareText(context, context.getString(id));
    }

    public static void openUrl(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(context, intent);
    }

    public static void openUrl(Context context, @StringRes int id){
        if (context == null)
            return;
        openUrl(context, context.getString(id));
    }

    public static long[] removeFollowersFromFollowing(Map<Long, Long> mapA, Map<Long, Long> mapB){
        Set<Long> hashSet = new HashSet<>(mapB.keySet());
        hashSet.removeAll(new HashSet<>(mapA.keySet()));
        List<Long> longList = new ArrayList<>(hashSet);
        long[] longs = new long[hashSet.size()];
        for (int i = 0; i < longList.size(); i++) {
            longs[i] = longList.get(i);
        }
        return longs;
    }

    public static long[] mutualFollowers(Map<Long, Long> friend, Map<Long, Long> followers) {
        Set<Long> hashSet = new HashSet<>(friend.keySet());
        hashSet.retainAll(followers.keySet());
        List<Long> longList = new ArrayList<>(hashSet);
        long[] longs = new long[longList.size()];
        for (int i = 0; i < longList.size(); i++) {
            longs[i] = longList.get(i);
        }
        return longs;
    }

    public static void toast(@Nullable Context context, String message) {
        if (context == null || message == null)
            return;
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toast(@Nullable Context context, @StringRes int resID){
        if (context == null)
            return;
        toast(context, context.getString(resID));
    }

    public static void openEmail(Context context, String email, String s) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.fromParts("mailto", email, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "From Unfollow TiTa");
        intent.putExtra(Intent.EXTRA_TEXT, s);
        startActivity(context, intent);
    }

    public static boolean isEmpty(@Nullable String s) {
        if (s != null){
            return s.isEmpty();
        }else{
            return true;
        }
    }

    public static void copyText(Context context, String s) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("label", s);
            clipboardManager.setPrimaryClip(clipData);
        }catch (Exception e){
            error(e);
        }
    }

    public static String date(long time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            Date date = new Date(time);
            return simpleDateFormat.format(date);
        }catch (Exception e){
            error(e);
            return "0";
        }
    }

    public static String dateForSearch(long time){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date(time);
            return simpleDateFormat.format(date).replace("/", "-");
        }catch (Exception e){
            error(e);
            return "0";
        }
    }

    public static List<UserModel> setUpUsers(@Nullable List<User> users) {
        if (users == null || users.size() <= 0)
            return null;
        List<UserModel> userModelList = new ArrayList<>();
        for (User u : users) {
            UserModel mu = new UserModel();
            mu.user = u;
            userModelList.add(mu);
        }
        return userModelList;
    }

    public static String profileUrl(String screenName) {
        return "https://twitter.com/" + screenName;
    }

    public static void startActivityForResault(Activity activity, Class<?> aClass, int reqeustCode) {
        if (aClass == null || activity == null)
            return;
        try {
            Intent intent = new Intent(activity, aClass);
            activity.startActivityForResult(intent, reqeustCode);
        }catch (Exception e){
            error(e);
        }
    }

    public static List<String> splitWordsFromTweets(List<Status> statusList) {
        if (statusList == null || statusList.size() <= 0){
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Status s : statusList) {
            if (! s.isRetweeted())
                sb.append(s.getText()).append(" ");
        }
        String[] strings = sb
                .toString()
                .replace(".", " ")
                .replace("/", " ")
                .replace("\\", " ")
                .split(" ");
        return new ArrayList<>(Arrays.asList(strings));
    }

    public static long[] getTweetsDate(List<Status> statusList) {
        if (statusList == null || statusList.size() <= 0)
            return null;
        long[] longs = new long[statusList.size()];
        for (int i = 0; i < statusList.size(); i++) {
            longs[i] = statusList.get(i).getCreatedAt().getTime();
        }
        return longs;
    }

    public static int getHour(long aLong) {
        try {
            return Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(aLong));
        }catch (Exception e) {
            error(e);
            return -1;
        }
    }

    public static void showDialog(DialogFragment dialog, FragmentManager fm) {
        if (dialog == null || fm == null)
            return;
        try {
            dialog.show(fm, null);
        }catch (Exception e){
            error(e);
        }
    }

    public static void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
    }

    /**
     * Load photo from url
     * @param context
     * @param data Image url
     * @param iv Target to set image
     * @param circle Convert image to circle or no
     */
    public static void loadPhoto(Context context, String data, ImageView iv, boolean circle) {
        if (context == null || data == null || iv == null)
            return;
        if (circle){
            Glide.with(context).load(data).apply(RequestOptions.circleCropTransform()).into(iv);
        }else {
            Glide.with(context).load(data).into(iv);
        }
    }

    /**
     * Load image from drawable folder
     * @param context
     * @param res Image resource id
     * @param iv Target to set image
     * @param circle Convert image to circle or no
     */
    public static void loadPhoto(Context context, @DrawableRes int res, ImageView iv, boolean circle) {
        if (context == null || iv == null)
            return;
        if (circle)
            Glide.with(context).load(res).apply(RequestOptions.circleCropTransform()).into(iv);
        else
            Glide.with(context).load(res).into(iv);
    }

    public static long[] usersToIds(@Nullable List<UserModel> users) {
        if (users == null || users.size() <= 0)
            return new long[0];
        long[] ids = new long[users.size()];
        for (int i = 0; i < users.size(); i++) {
            ids[i] = users.get(i).userId;
        }
        return ids;
    }

    public static void showUserDialog(long id, FragmentManager fm) {
        if (fm == null)
            return;
        UserBottomSheet userBottomSheet = new UserBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putLong(SharePref.userId, id);
        userBottomSheet.setArguments(bundle);
        showDialog(userBottomSheet, fm);
    }

    public static void log(String log) {
        if (BuildConfig.DEBUG && log != null)
            Log.i("twitter_log", log);
    }

    /**
     * Convert to user ids to user list
     * @param ids User ids
     * @return
     */
    public static List<UserModel> setUpUsers(long[] ids) {
        if (ids == null || ids.length <= 0)
            return null;
        List<UserModel> list = new ArrayList<>();
        for (long id : ids) {
            UserModel um = new UserModel();
            um.userId = id;
            list.add(um);
        }
        return list;
    }

    /**
     * Select random id from ids
     * @param ids User ids
     * @return random id
     */
    public static long randomItem(long[] ids) {
        if (ids == null || ids.length <= 0) {
            return 0;
        }
        return ids[new Random().nextInt(ids.length)];
    }
}
