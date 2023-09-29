package limitless.android.unfollowtita.Activity;

import androidx.annotation.NonNull;
import limitless.android.unfollowtita.Model.Words;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ActivityDateOfTweetsBinding;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DateOfTweetsActivity extends BaseActivity {

    private ActivityDateOfTweetsBinding binding;
    private int loadPages = 2;
    private Paging paging = new Paging(1, 200);
    private List<Status> statusList = new ArrayList<>();

    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDateOfTweetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        loadBannerAds(binding.adView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData(paging);
    }

    private void getData(Paging p) {
        binding.progress.progress.setVisibility(View.VISIBLE);
        unfollowTiTa.getUserTimeLine(p, statuses -> {
            binding.progress.progress.setVisibility(View.INVISIBLE);
            if (statuses != null){
                statusList.addAll(statuses);
                if (paging.getPage() <= loadPages){
                    paging = new Paging(paging.getPage() + 1, 200);
                    getData(paging);
                }else {
                    if (! setData()){
                        Utils.toast(DateOfTweetsActivity.this, getString(R.string.error_try_again));
                    }
                }
            }else {
                binding.progress.progress.setVisibility(View.INVISIBLE);
                Utils.toast(DateOfTweetsActivity.this, R.string.error);
            }
        });
    }

    private boolean setData() {
        binding.progress.progress.setVisibility(View.VISIBLE);
        if (statusList.size() <= 0){
            binding.progress.progress.setVisibility(View.INVISIBLE);
            return false;
        }
        Cartesian cartesian = AnyChart.column();
        cartesian.title("Hour of tweets via @UnfollowTiTa");
        cartesian.xAxis(0).title(getString(R.string.t_hour));
        cartesian.yAxis(0).title(getString(R.string.t_tweets_count));
        cartesian.animation(true);

        long[] longs = Utils.getTweetsDate(statusList);
        if (longs == null){
            binding.progress.progress.setVisibility(View.INVISIBLE);
            return false;
        }
        List<Integer> hours = new ArrayList<>();
        for (long l : longs) {
            hours.add(Utils.getHour(l));
        }
        Collections.sort(hours);
        List<Words> words = new ArrayList<>();
        for (int i = 0; i < hours.size(); i++) {
            String s = String.valueOf(hours.get(i).intValue());
            if (i == 0){
                words.add(new Words(s, 1));
                continue;
            }
            if (s.equals(words.get(words.size() - 1).word)){
                words.get(words.size() - 1).count += 1;
            }else {
                words.add(new Words(s, 1));
            }
        }

        List<DataEntry> entryList = new ArrayList<>();
        for (int i = 1; i < words.size(); i++) {
            entryList.add(new ValueDataEntry(words.get(i).word, words.get(i).count));
        }
        cartesian.data(entryList);
        binding.chartView.setChart(cartesian);
        binding.chartView.setZoomEnabled(true);
        binding.chartView.setOnRenderedListener(() -> {
            binding.progress.progress.setVisibility(View.INVISIBLE);
            showInterstitialAd();
        });
        return true;
    }
}
