package limitless.android.unfollowtita.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import limitless.android.unfollowtita.Adapter.UserAdapter;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Model.UserModel;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ActivityCopyFollowersBinding;
import twitter4j.PagableResponseList;
import twitter4j.User;

public class CopyFollowersActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCopyFollowersBinding binding;
    private UserAdapter userAdapter;
    private long cursor = -1;
    private boolean hasNextUser = true, isFollowing = true;
    private Listener<UserModel> endListener = userModel -> loadData(cursor, isFollowing);


    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCopyFollowersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        userAdapter = new UserAdapter(this, null, getSupportFragmentManager(), false, false, false, true, false, false, false, false, false);

        loadBannerAds(binding.adView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userAdapter.setOnEndScrollListener(endListener);
        binding.recyclerView.setAdapter(userAdapter);
        binding.buttonFollowing.setOnClickListener(this);
        binding.buttonFollowers.setOnClickListener(this);
    }

    private void loadData(long l, boolean isF) {
        if (isF){
            binding.progressBarFollowing.setVisibility(View.VISIBLE);
            binding.progressBarFollowers.setVisibility(View.INVISIBLE);
            binding.buttonFollowers.setEnabled(false);
            binding.buttonFollowing.setEnabled(true);
            unfollowTiTa.getUserFollowing(binding.editText.getText().toString().trim(), l, listener);
        }else {
            binding.progressBarFollowers.setVisibility(View.VISIBLE);
            binding.buttonFollowers.setVisibility(View.INVISIBLE);
            binding.buttonFollowing.setEnabled(false);
            binding.buttonFollowers.setEnabled(true);
            unfollowTiTa.getUserFollowers(binding.editText.getText().toString().trim(), l, listener);
        }
    }

    private Listener<PagableResponseList<User>> listener = new Listener<PagableResponseList<User>>() {
        @Override
        public void data(PagableResponseList<User> users) {
            binding.progressBarFollowers.setVisibility(View.INVISIBLE);
            binding.progressBarFollowing.setVisibility(View.INVISIBLE);
            binding.buttonFollowing.setVisibility(View.VISIBLE);
            binding.buttonFollowers.setVisibility(View.VISIBLE);
            binding.buttonFollowing.setEnabled(true);
            binding.buttonFollowers.setEnabled(true);

            if (users != null){
                userAdapter.insertUsers(Utils.setUpUsers(users));
                hasNextUser = users.hasNext();
                if (hasNextUser){
                    cursor = users.getNextCursor();
                }
            }else {
                Utils.toast(CopyFollowersActivity.this, R.string.error);
            }

        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_following){
            if (isReady()){
                cursor = -1;
                isFollowing = true;
                userAdapter.clearAll();
                loadData(cursor, true);
            }else {
                binding.editText.setError("Error");
            }
        }else if (v.getId() == R.id.button_followers){
            if (isReady()){
                cursor = -1;
                isFollowing = false;
                userAdapter.clearAll();
                loadData(cursor, false);
            }else {
                binding.editText.setError(getString(R.string.error));
            }
        }
    }

    private boolean isReady() {
        return ! Utils.isEmpty(binding.editText.getText().toString());
    }
}
