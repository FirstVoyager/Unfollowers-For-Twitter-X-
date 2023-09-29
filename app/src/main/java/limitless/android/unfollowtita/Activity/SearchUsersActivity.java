package limitless.android.unfollowtita.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import limitless.android.unfollowtita.Adapter.UserAdapter;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ActivitySearchUsersBinding;
import twitter4j.Query;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class SearchUsersActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySearchUsersBinding binding;
    private UserAdapter userAdapter;
    private boolean hasNext = true;
    private int page = 1;

    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        userAdapter = new UserAdapter(this, new ArrayList<>(), getSupportFragmentManager(), false, false, false, true, false, false, false, false, false);

        loadBannerAds(binding.adView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.recyclerView.setAdapter(userAdapter);
        binding.buttonSearch.setOnClickListener(this);
        userAdapter.setOnEndScrollListener(userModel -> {
            if (hasNext)
                getUser(page);
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_search){
            userAdapter.clearAll();
            getUser(page);
        }
    }

    private void getUser(int i) {
        Query query = new Query(binding.editText.getText().toString());
        query.setCount(100);
        binding.buttonSearch.setVisibility(View.INVISIBLE);
        binding.progress.progress.setVisibility(View.VISIBLE);
        unfollowTiTa.searchUser(
                query.getQuery(), i, users -> {
                    binding.buttonSearch.setVisibility(View.VISIBLE);
                    binding.progress.progress.setVisibility(View.INVISIBLE);
                    if (users == null || users.size() <= 0){
                        hasNext = false;
                        Utils.toast(SearchUsersActivity.this, R.string.cant_find_user);
                        return;
                    }else {
                        binding.buttonSearch.setVisibility(View.VISIBLE);
                        binding.progress.progress.setVisibility(View.INVISIBLE);
                        Utils.toast(SearchUsersActivity.this, R.string.error);
                    }
                    page = page + 1;
                    userAdapter.insertUsers(Utils.setUpUsers(users));
                }
        );
    }
}
