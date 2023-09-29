package limitless.android.unfollowtita.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import limitless.android.unfollowtita.Model.Account;
import limitless.android.unfollowtita.Model.TopFriends;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Other.SQLiteUnfollow;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ActivityTopFriendsBinding;
import twitter4j.Paging;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TopFriendsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityTopFriendsBinding binding;
    private Twitter twitter;
    private Account account;

    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTopFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        twitter = unfollowTiTa.getTwitter();
        account = new SQLiteUnfollow(this).getMainAccount();

        loadBannerAds(binding.adView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.buttonCopy.setOnClickListener(this);
        binding.buttonTweet.setOnClickListener(this);
        if (account == null){
            finish();
            return;
        }
        loadData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_tweet){
            tweet();
        }else if (v.getId() == R.id.button_copy){
            Utils.copyText(this, binding.tvTopFriends.getText().toString());
            Utils.toast(this, R.string.copied);
        }
    }

    private void tweet() {
        if (Utils.isEmpty(binding.tvTopFriends.getText().toString()))
            return;
        unfollowTiTa.updateStatus(new StatusUpdate(binding.tvTopFriends.getText().toString()), status -> Utils.toast(TopFriendsActivity.this, R.string.check_your_profile));
    }

    private void loadData() {
        Utils.toast(TopFriendsActivity.this, R.string.start_loading);
        binding.cardViewTopFriends.setVisibility(View.INVISIBLE);
        binding.progress.progress.setVisibility(View.VISIBLE);
        new LoadData(new Listener<List<TopFriends>>() {
            @Override
            public void data(List<TopFriends> topFriends) {
                if (topFriends == null || topFriends.size() <= 0){
                    Utils.toast(TopFriendsActivity.this, R.string.cant_find_your_friends);
                    return;
                }
                StringBuilder builder = new StringBuilder();
                builder.append("my #topFriends via @unfollowTiTa");
                builder.append("\n\n");
                for (int i = 0; i < topFriends.size(); i++) {
                    if (i == 7)
                        break;
                    TopFriends m = topFriends.get(i);
                    if (m.count == 0)
                        break;
                    builder.append("#").append(i + 1).append(" @").append(m.screenName)
                            .append(" ").append(m.count).append(" mentions").append("\n");
                }
                binding.tvTopFriends.setText(builder.toString());
                binding.cardViewTopFriends.setVisibility(View.VISIBLE);
                binding.progress.progress.setVisibility(View.INVISIBLE);
                showInterstitialAd();
            }
        }).execute(account.id);
    }

    private class LoadData extends AsyncTask<Long, Void, List<TopFriends>>{

        private Paging paging;
        private Listener<List<TopFriends>> listener;

        public LoadData(Listener<List<TopFriends>> listener) {
            this.listener = listener;
            this.paging = new Paging(1, 200);
        }

        @Override
        protected List<TopFriends> doInBackground(Long... longs) {
            try {
                List<twitter4j.Status> statuses = new ArrayList<>();
                for (int i = 0; i < 5 ; i++) {
                    List<twitter4j.Status> s = null;
                    try {
                        s = twitter.getUserTimeline(paging);
                    } catch (TwitterException e) {
                        Utils.error(e);
                    }
                    if (s != null)
                        statuses.addAll(s);
                    paging = new Paging(paging.getPage() + 1, 200);
                }

                List<TopFriends> tm = new ArrayList<>();
                for (twitter4j.Status s : statuses) {
                    tm.add(UnfollowTiTa.getRealFriend(s));
                }
                try {
                    Collections.sort(tm, (o1, o2) -> {
                        return o1.screenName.compareTo(o2.screenName);
                    });
                }catch (Exception e){
                    Utils.error(e);
                }

                List<TopFriends> fm = new ArrayList<>();
                for (int i = 0; i < tm.size(); i++) {
                    if (i == 0){
                        fm.add(tm.get(0));
                        fm.get(0).count = 1;
                    }else {
                        if (fm.get(fm.size() - 1).screenName.equals(tm.get(i).screenName)){
                            fm.get(fm.size() - 1).count += 1;
                        }else {
                            fm.add(tm.get(i));
                        }
                    }
                }

                int n = 0;
                for (int i = 0; i < fm.size(); i++) {
                    if (fm.get(i).screenName.toLowerCase().trim().equals(sharePref.getString(SharePref.screenName, null).toLowerCase().trim())){
                        n = i;
                        break;
                    }
                }
                if (n != 0){
                    fm.remove(n);
                }

                Map<String, TopFriends> personHashMap = new HashMap<>();
                for (TopFriends m : fm) {
                    personHashMap.put(m.screenName, m);
                }

                List<TopFriends> tfm = new ArrayList<>(personHashMap.values());

                Collections.sort(tfm, (o1, o2) -> o1.count - o2.count);
                Collections.reverse(tfm);
                return tfm;
            } catch (Exception e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<TopFriends> topFriends) {
            super.onPostExecute(topFriends);
            listener.data(topFriends);
        }
    }

}
