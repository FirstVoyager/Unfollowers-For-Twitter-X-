package limitless.android.unfollowtita.Activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import limitless.android.unfollowtita.Adapter.TweetAdapter;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.R;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;
import twitter4j.User;


public class FirstTweetActivity extends BaseActivity implements View.OnClickListener {

    private MaterialButton btnGet;
    private TextInputEditText etID;
    private View vProgressBar;
    private RecyclerView recyclerView;
    private TweetAdapter tweetAdapter;
    private boolean hasNextTweet = true;
    private Query nextQuery = null;

    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_tweet);
        init();
    }

    private void init() {
        unfollowTiTa = new UnfollowTiTa(this);
        recyclerView = findViewById(R.id.recyclerView);
        tweetAdapter = new TweetAdapter(this, new ArrayList<>());
        btnGet = findViewById(R.id.button_getFirstTweet);
        vProgressBar = findViewById(R.id.progressBar);
        etID = findViewById(R.id.editText);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(tweetAdapter);
        btnGet.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_getFirstTweet){
            if (isReady()){
                btnGet.setVisibility(View.INVISIBLE);
                vProgressBar.setVisibility(View.VISIBLE);
                tweetAdapter.clearAll();
                unfollowTiTa.showUser(
                        0,
                        etID.getText().toString(),
                        user -> {
                            if (user != null){
                                nextQuery = new Query("(from:" + user.getScreenName() + ") until:" + Utils.dateForSearch(user.getCreatedAt().getTime() + 1000 * 365 * 24 * 60 * 60) + " since:" + Utils.dateForSearch(user.getCreatedAt().getTime()));
                                nextQuery.setCount(200);

                                loadData(nextQuery);
                            }else {
                                Utils.toast(FirstTweetActivity.this, R.string.error);
                            }
                        }
                );
            }else{
                etID.setError("Error");
            }
        }
    }

    private void loadData(Query query) {
        btnGet.setVisibility(View.INVISIBLE);
        vProgressBar.setVisibility(View.VISIBLE);
        unfollowTiTa.searchTweets(
                query, resulat
        );
    }

    private Listener<QueryResult> resulat = new Listener<QueryResult>() {
        @Override
        public void data(QueryResult queryResult) {
            if (queryResult == null){
                btnGet.setVisibility(View.VISIBLE);
                vProgressBar.setVisibility(View.INVISIBLE);
                Utils.toast(FirstTweetActivity.this, R.string.error);
                return;
            }
            btnGet.setVisibility(View.VISIBLE);
            vProgressBar.setVisibility(View.INVISIBLE);
            hasNextTweet = queryResult.hasNext();
            if (hasNextTweet){
                nextQuery = queryResult.nextQuery();
            }
            if (queryResult.getTweets() != null){
                if (queryResult.getTweets().size() <= 0){
                    Utils.toast(FirstTweetActivity.this, "Can't find");
                }else {
                    tweetAdapter.addData(queryResult.getTweets());
                }
            }

        }
    };

    private boolean isReady() {
        return ! Utils.isEmpty(etID.getText().toString());
    }
}
