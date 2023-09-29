package limitless.android.unfollowtita.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import limitless.android.unfollowtita.Adapter.TweetAdapter;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ActivitySearchTweetsBinding;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;

public class SearchTweetsActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySearchTweetsBinding binding;
    private TweetAdapter tweetAdapter;
    private boolean hasNext = true;
    private Query nextQuery = null;

    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchTweetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        tweetAdapter = new TweetAdapter(this, null);

        loadBannerAds(binding.adView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.recyclerView.setAdapter(tweetAdapter);
        binding.buttonSearch.setOnClickListener(this);
        tweetAdapter.setOnEndListener(status -> {
            if (hasNext)
                loadData(nextQuery);
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_search){
            if (isReady()){
                tweetAdapter.clearAll();
                nextQuery = new Query(binding.editTextSearch.getText().toString() + " (from:" + binding.editTextUserId.getText().toString() + ")");
                nextQuery.setCount(100);
                loadData(nextQuery);
            }
        }

    }

    private void loadData(Query q) {
        binding.buttonSearch.setVisibility(View.INVISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);
        unfollowTiTa.searchTweets(q, result);
    }

    private Listener<QueryResult> result = new Listener<QueryResult>() {
        @Override
        public void data(QueryResult queryResult) {
            if (queryResult == null) {
                binding.buttonSearch.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
                Utils.toast(SearchTweetsActivity.this, R.string.error);
                return;
            }
            hasNext = queryResult.hasNext();
            if (hasNext)
                nextQuery = queryResult.nextQuery();
            binding.buttonSearch.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
            if (queryResult.getTweets() == null || queryResult.getTweets().size() <= 0){
                Utils.toast(SearchTweetsActivity.this, R.string.cant_find);
            }else {
                tweetAdapter.addData(queryResult.getTweets());
            }
        }
    };

    private boolean isReady() {
        if (Utils.isEmpty(binding.editTextUserId.getText().toString())){
            binding.editTextUserId.setError(getString(R.string.error));
            return false;
        }
        if (Utils.isEmpty(binding.editTextSearch.getText().toString())){
            binding.editTextSearch.setError(getString(R.string.error));
            return false;
        }
        return true;
    }
}
