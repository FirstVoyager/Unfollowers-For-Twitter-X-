package limitless.android.unfollowtita.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;

import android.content.IntentFilter;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import limitless.android.unfollowtita.Dialog.AccountsBottomSheet;
import limitless.android.unfollowtita.Dialog.ToolsBottomSheet;
import limitless.android.unfollowtita.Dialog.MenuBottomSheet;
import limitless.android.unfollowtita.Dialog.TweetBottomSheet;
import limitless.android.unfollowtita.Model.Account;
import limitless.android.unfollowtita.Model.Type;
import limitless.android.unfollowtita.Model.UserAction;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.Model.UserModel;
import limitless.android.unfollowtita.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private Account account;
    private int loginCode = 2001;
    private boolean followingHasNext = true, followersHasNext = true, mutedHasNext = true;
    private long followingCursor = -1, followersCursor = -1, mutedCursor = -1;
    private boolean isLoading = true;
    public static long[] fans, notBack, mutual;
    public static List<UserModel> following, followers;
    public static final String CHANGE_DATA_ACTION = "CHANGE_DATA_ACTION";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && ! intent.getAction().equals(CHANGE_DATA_ACTION))
                return;
            if (! intent.hasExtra(Intent.EXTRA_TEXT))
                return;
            UserAction action = UserAction.valueOf(intent.getStringExtra(Intent.EXTRA_TEXT));
            long userId = intent.getLongExtra(SharePref.userId, 0);
            switch (action) {
                case FOLLOW:
                    following.add(new UserModel(0, userId, account.id, null, null));
                    break;
                case UNFOLLOW:
                    int index = -1;
                    for (int i = 0; i < following.size(); i++) {
                        if (following.get(i).userId == userId) {
                            index = i;
                            break;
                        }
                    }
                    if (index >= 0)
                        following.remove(index);
                    break;
                case BLOCK:
                    // remove from following
                    int index_a = -1;
                    for (int i = 0; i < following.size(); i++) {
                        if (following.get(i).userId == userId) {
                            index_a = i;
                            break;
                        }
                    }
                    if (index_a >= 0)
                        following.remove(index_a);
                    // remove from followers
                    index_a = -1;
                    for (int i = 0; i < followers.size(); i++) {
                        if (followers.get(i).userId == userId) {
                            index_a = i;
                            break;
                        }
                    }
                    if (index_a >= 0)
                        followers.remove(index_a);
                    break;
            }
            setMainDataSize();
        }
    };

    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            if (isLoading)
                Utils.toast(this, R.string.is_loading);
            else
                getData();
        }else if (item.getItemId() == R.id.menu_logOut) {
            logout();
        }else if (item.getItemId() == R.id.menu_followUs) {
            unfollowTiTa.followUser("unfollowTita", user -> {
                if (user != null)
                    Utils.toast(this, R.string.thank_you);
            });
        }else if (item.getItemId() == R.id.menu_exit) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == loginCode){
            if (resultCode == RESULT_OK){
                Account account = data.getParcelableExtra(AccountsBottomSheet.LOGIN_ACCOUNT);
                if (account != null){
                    setup();
                }else {
                    Utils.toast(this, R.string.error);
                    finish();
                }
            }else {
                Toast.makeText(this, R.string.t_login_failed, Toast.LENGTH_LONG).show();
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_tweet:
                new TweetBottomSheet().show(getSupportFragmentManager(), null);
                break;
            case R.id.linearLayout_magicTools:
                new ToolsBottomSheet().show(getSupportFragmentManager(), null);
                break;
            case R.id.linearLayout_following:
                Utils.startActivity(
                        this,
                        new Intent(this, UserListActivity.class)
                                .putExtra(UserListActivity.USER_TYPE, Type.FOLLOWING.name())
                );
                break;
            case R.id.linearLayout_followers:
                Utils.startActivity(
                        this,
                        new Intent(this, UserListActivity.class)
                                .putExtra(UserListActivity.USER_TYPE, Type.FOLLOWERS.name())
                );
                break;
            case R.id.linearLayout_notBack:
                Utils.startActivity(
                        this,
                        new Intent(this, UserListActivity.class)
                                .putExtra(UserListActivity.USER_TYPE, Type.NOT_FOLLOWING_BACK.name())
                );
                break;
            case R.id.linearLayout_fans:
                Utils.startActivity(
                        this,
                        new Intent(this, UserListActivity.class)
                                .putExtra(UserListActivity.USER_TYPE, Type.FANS.name())
                );
                break;
            case R.id.linearLayout_mutual:
                Utils.startActivity(
                        this,
                        new Intent(this, UserListActivity.class)
                                .putExtra(UserListActivity.USER_TYPE, Type.MUTUAL_FRIENDS.name())
                );
                break;
        }
    }

    /**
     * Init vars
     */
    private void init() {
        loadBannerAds(binding.adView);
        setImages();
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> {
            new MenuBottomSheet(account1 -> setup()).show(getSupportFragmentManager(), null);
        });
        binding.fabTweet.setOnClickListener(this);
        binding.linearLayoutMagicTools.setOnClickListener(this);
        binding.linearLayoutFollowing.setOnClickListener(this);
        binding.linearLayoutFollowers.setOnClickListener(this);
        binding.linearLayoutNotBack.setOnClickListener(this);
        binding.linearLayoutFans.setOnClickListener(this);
        binding.linearLayoutMutual.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CHANGE_DATA_ACTION);
        registerReceiver(broadcastReceiver, filter);

        if (sqLiteUnfollow.accountCount() <= 0){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, loginCode);
        }else {
            setup();
        }
    }

    /**
     * Load image from Drawable folder with Glide
     */
    private void setImages() {
        Utils.loadPhoto(this, R.drawable.ic_tools_512dp, binding.imageViewMagic, false);
        Utils.loadPhoto(this, R.drawable.ic_following_512dp, binding.imageViewFollowing, false);
        Utils.loadPhoto(this, R.drawable.ic_followers_512dp, binding.imageViewFollowers, false);
        Utils.loadPhoto(this, R.drawable.ic_not_following_back_512dp, binding.imageViewNotBack, false);
        Utils.loadPhoto(this, R.drawable.ic_fans_512dp, binding.imageViewFan, false);
        Utils.loadPhoto(this, R.drawable.ic_mute_512dp, binding.imageViewMutual, false);
    }

    private void setup() {
        unfollowTiTa = new UnfollowTiTa(this);
        account = sqLiteUnfollow.getMainAccount();

        sqLiteUnfollow.setAccountId(account.id);
        showInterstitialAd();
        showUser();
        if (account.followersLoaded && account.followingLoaded) {
            hideProgress();
        }else {
            getData();
        }
    }

    private void showUser() {
        unfollowTiTa.showUser(
                account.id,
                null,
                user -> {
                    if (user == null)
                        return;
                    account.name = user.getName();
                    account.bio = user.getDescription();
                    account.screenName = user.getScreenName();
                    account.profileUrl = user.getBiggerProfileImageURL();
                    account.headerUrl= user.getProfileBannerURL();
                    sqLiteUnfollow.updateAccount(account);

                    binding.textViewFollowing.setText(String.valueOf(user.getFriendsCount()));
                    binding.textViewFollowers.setText(String.valueOf(user.getFollowersCount()));
                    hideProgress();
                }
        );
    }

    private void hideProgress() {
        if (account == null)
            return;
        if (account.followingLoaded && account.followersLoaded){
            sqLiteUnfollow.getUsers(Type.FOLLOWING, userModels -> {
                following = userModels;
                setMainDataSize();
            });
            sqLiteUnfollow.getUsers(Type.FOLLOWERS, userModels -> {
                followers = userModels;
                setMainDataSize();
            });
        }
    }

    /**
     * Get muted users ids
     * @param cursor Next page id
     */
    private void getMuted(long cursor) {
        // Get muted
        unfollowTiTa.getMutedIds(cursor, iDs -> {
            if (iDs != null){
                mutedHasNext = iDs.hasNext();
                sqLiteUnfollow.insertUsers(iDs.getIDs(), Type.MUTED, null);
                if (mutedHasNext){
                    mutedCursor = iDs.getNextCursor();
                    getMuted(mutedCursor);
                }else {
                    account.mutedLoaded = true;
                    sqLiteUnfollow.updateAccount(account);
                }
            }
        });
    }

    /**
     * Get blocked ids
     */
    private void getBlocked() {
        // Get blocked
        unfollowTiTa.getAllBlockedIds(iDs -> {
            if (iDs != null){
                sqLiteUnfollow.insertUsers(iDs.getIDs(), Type.BLOCKED, null);
                account.blockedLoaded = true;
                sqLiteUnfollow.updateAccount(account);
            }
        });
    }

    private void getFollowers(long cursor) {
        unfollowTiTa.getFollowersIDs(cursor, iDs -> {
            if (iDs != null){
                followersHasNext = iDs.hasNext();
                sqLiteUnfollow.insertUsers(iDs.getIDs(), Type.FOLLOWERS, null);
                if (followersHasNext){
                    followersCursor = iDs.getNextCursor();
                    getFollowers(followersCursor);
                }else {
                    account.followersLoaded = true;
                    sqLiteUnfollow.updateAccount(account);
                    hideProgress();
                }
            }
        });
    }

    private void getFollowing(long cursor) {
        unfollowTiTa.getFollowingIDs(cursor, iDs -> {
            if (iDs != null){
                followingHasNext = iDs.hasNext();
                sqLiteUnfollow.insertUsers(iDs.getIDs(), Type.FOLLOWING, null);
                if (followingHasNext){
                    followingCursor = iDs.getNextCursor();
                    getFollowing(followingCursor);
                }else {
                    account.followingLoaded = true;
                    sqLiteUnfollow.updateAccount(account);
                    hideProgress();
                }
            }

        });
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_exit_to_app_black_24dp);
        builder.setTitle(R.string.logout);
        builder.setMessage("Do you want to logout (" + sqLiteUnfollow.getMainAccount().screenName + ")?");
        builder.setPositiveButton("Logout", (dialog, which) -> {
            Utils.clearCookies();
            Utils.toast(MainActivity.this, "Logout");
            sqLiteUnfollow.deleteAccount(sqLiteUnfollow.getMainAccount().id);
            if (sqLiteUnfollow.accountCount() > 0){
                sqLiteUnfollow.setMainAccount(sqLiteUnfollow.getAccounts().get(0).id);
                setup();
            }else {
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void getData() {
        isLoading = true;
        account.followingLoaded = false;
        account.followersLoaded = false;
        account.blockedLoaded = false;
        account.mutedLoaded = false;
        sqLiteUnfollow.updateAccount(account);

        binding.progress.progress.setVisibility(View.VISIBLE);
        binding.textViewFollowing.setText(R.string.stars);
        binding.textViewFollowers.setText(R.string.stars);
        binding.textViewNotBack.setText(R.string.stars);
        binding.textViewFans.setText(R.string.stars);
        binding.textViewMutual.setText(R.string.stars);
        // delete data
        sqLiteUnfollow.deleteUsers(Type.FOLLOWERS);
        sqLiteUnfollow.deleteUsers(Type.FOLLOWING);
        sqLiteUnfollow.deleteUsers(Type.NOT_FOLLOWING_BACK);
        sqLiteUnfollow.deleteUsers(Type.FANS);
        sqLiteUnfollow.deleteUsers(Type.MUTUAL_FRIENDS);
        sqLiteUnfollow.deleteUsers(Type.MUTED);
        sqLiteUnfollow.deleteUsers(Type.BLOCKED);

        getMuted(mutedCursor);
        getBlocked();
        getFollowing(followingCursor);
        getFollowers(followersCursor);
    }

    private void setMainDataSize() {
        if (following == null || followers == null)
            return;
        isLoading = false;

        binding.textViewFollowing.setText(String.valueOf(following.size()));
        Map<Long, Long> friend = new HashMap();
        for (UserModel um: following) {
            friend.put(um.userId, um.userId);
        }

        binding.textViewFollowers.setText(String.valueOf(followers.size()));
        Map<Long, Long> follower = new HashMap();
        for (UserModel um : followers) {
            follower.put(um.userId, um.userId);
        }

        fans = Utils.removeFollowersFromFollowing(friend, follower);
        binding.textViewFans.setText(String.valueOf(fans.length));

        notBack = Utils.removeFollowersFromFollowing(follower, friend);
        binding.textViewNotBack.setText(String.valueOf(notBack.length));

        mutual = Utils.mutualFollowers(friend, follower);
        binding.textViewMutual.setText(String.valueOf(mutual.length));

        binding.progress.progress.setVisibility(View.INVISIBLE);
    }

}
