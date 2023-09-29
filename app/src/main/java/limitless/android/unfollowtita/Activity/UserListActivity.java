package limitless.android.unfollowtita.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import limitless.android.unfollowtita.Adapter.UserAdapter;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Model.Type;
import limitless.android.unfollowtita.Model.UserModel;
import limitless.android.unfollowtita.Other.Constant;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ActivityWhiteListBinding;
import twitter4j.PagableResponseList;
import twitter4j.ResponseList;
import twitter4j.User;

/**
 * Show user lists
 *  1. Muted users
 *  2. Blocked users
 *  3. White list
 *  4. Black list
 */
public class UserListActivity extends BaseActivity  {

    /**
     * Type of user want to show
     */
    public static final String USER_TYPE = "user_type";
    /**
     * Current type
     */
    private Type currentType;
    /**
     * Last item for muted
     */
    private int lastItem = 0;
    /**
     * Users for muted
     */
    private List<UserModel> users;
    private ActivityWhiteListBinding binding;
    private UserAdapter userAdapter;
    private boolean hasNextUsers = true;
    private long cursor = -1;
    private Listener<PagableResponseList<User>> listener = new Listener<PagableResponseList<User>>() {
        @Override
        public void data(PagableResponseList<User> users) {
            binding.progress.progress.setVisibility(View.INVISIBLE);
            if (users != null && users.size() > 0){
                userAdapter.insertUsers(Utils.setUpUsers(users));
                hasNextUsers = users.hasNext();
                if (hasNextUsers){
                    cursor = users.getNextCursor();
                }
            }else {
                hasNextUsers = false;
                if (userAdapter.getUserSize() <= 0) {
                    binding.linearLayoutEmpty.setVisibility(View.VISIBLE);
                }
            }
        }
    };
    /**
     * For muted
     */
    private Listener<ResponseList<User>> userListener = new Listener<ResponseList<User>>() {
        @Override
        public void data(ResponseList<User> users) {
            binding.progress.progress.setVisibility(View.INVISIBLE);
            if (users != null && users.size() > 0){
                userAdapter.insertUsers(Utils.setUpUsers(users));
            }else {
                if (userAdapter.getUserSize() <= 0) {
                    binding.linearLayoutEmpty.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWhiteListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    /**
     * Init vars
     */
    private void init() {
        currentType = Type.valueOf(getIntent().getStringExtra(USER_TYPE));

        loadBannerAds(binding.adView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switch (currentType) {
            case FOLLOWING:
                userAdapter = new UserAdapter(this, null, getSupportFragmentManager(), false, false, false, false, false, false, true, false, false);
                setTitle(R.string.following);
                break;
            case FOLLOWERS:
                userAdapter = new UserAdapter(this, null, getSupportFragmentManager(), false, true, false, false, false, false,  false, false, false);
                setTitle(R.string.followers);
                break;
            case FANS:
                userAdapter = new UserAdapter(this, null, getSupportFragmentManager(), false, false, false, true, false, false, false, false, false);
                setTitle(R.string.fans);
                new LoadUsersDataBase().execute();
                break;
            case NOT_FOLLOWING_BACK:
                userAdapter = new UserAdapter(this, new ArrayList<>(), getSupportFragmentManager(), false, false, false, false, false, true, false, false, false);
                setTitle(R.string.not_following_back);
                new LoadUsersDataBase().execute();
                break;
            case MUTUAL_FRIENDS:
                userAdapter = new UserAdapter(this, null, getSupportFragmentManager(), false, false, false, false, true, false, false, false, false);
                setTitle(R.string.mutual_friends);
                new LoadUsersDataBase().execute();
                break;
            case BLOCKED:
                userAdapter = new UserAdapter(this, new ArrayList<>(), getSupportFragmentManager(), true, false, false, false, false, false, false, false, false);
                setTitle(R.string.blocked);
                break;
            case MUTED:
                userAdapter = new UserAdapter(this, null, getSupportFragmentManager(), false, false, true, false, false, false, false, false, false);
                setTitle(R.string.muted);
                break;
            case WHITE_LIST:
                userAdapter = new UserAdapter(this, new ArrayList<>(), getSupportFragmentManager(), false, false, false, false, false, false, false, true, false);
                setTitle(R.string.white_list);
                new LoadUsersDataBase().execute();
                break;
            case BLACK_LIST:
                setTitle(R.string.black_list);
                userAdapter = new UserAdapter(this, null, getSupportFragmentManager(), false, false, false, false, false, false, false, false, true);
                new LoadUsersDataBase().execute();
                break;
        }
        binding.recyclerView.setAdapter(userAdapter);
        getData();
        userAdapter.setOnEndScrollListener(userModel -> getData());
    }

    private void getData() {
        binding.progress.progress.setVisibility(View.VISIBLE);
        switch (currentType) {
            case FOLLOWING:
                if (hasNextUsers)
                    unfollowTiTa.getFollowing(null, cursor, listener);
                else
                    binding.progress.progress.setVisibility(View.INVISIBLE);
                break;
            case FOLLOWERS:
                if (hasNextUsers)
                    unfollowTiTa.getFollowers(null, cursor, listener);
                else
                    binding.progress.progress.setVisibility(View.INVISIBLE);
                break;
            case BLOCKED:
                if (hasNextUsers)
                    unfollowTiTa.getBlockedUser(cursor, listener);
                else
                    binding.progress.progress.setVisibility(View.INVISIBLE);
                break;
            case MUTED:
                if (hasNextUsers)
                    unfollowTiTa.getMutedUsers(cursor, listener);
                else
                    binding.progress.progress.setVisibility(View.INVISIBLE);
                break;
            case MUTUAL_FRIENDS:
            case FANS:
            case NOT_FOLLOWING_BACK:
            case WHITE_LIST:
            case BLACK_LIST:
                binding.progress.progress.setVisibility(View.INVISIBLE);
                if (users == null || users.size() <= 0) {
                    binding.linearLayoutEmpty.setVisibility(View.VISIBLE);
                    return;
                }else {
                    binding.linearLayoutEmpty.setVisibility(View.INVISIBLE);
                }
                if (lastItem >= users.size())
                    return;
                int from = lastItem;
                int to = Constant.userLoadCount + lastItem;
                if (to > users.size())
                    to = users.size();
                long[] ids = Utils.usersToIds(users.subList(from, to));
                lastItem = to;
                binding.progress.progress.setVisibility(View.VISIBLE);
                unfollowTiTa.lookupUsers(ids, userListener);
                break;
        }
    }

    private class LoadUsersDataBase extends AsyncTask<Void, Void, List<UserModel>> {

        @Override
        protected List<UserModel> doInBackground(Void... voids) {
            if (currentType == Type.FANS) {
                return Utils.setUpUsers(MainActivity.fans);
            }else if (currentType == Type.NOT_FOLLOWING_BACK) {
                return Utils.setUpUsers(MainActivity.notBack);
            }else if (currentType == Type.MUTUAL_FRIENDS){
                return Utils.setUpUsers(MainActivity.mutual);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<UserModel> userModels) {
            super.onPostExecute(userModels);
            users = userModels;
            getData();
        }
    }

}
