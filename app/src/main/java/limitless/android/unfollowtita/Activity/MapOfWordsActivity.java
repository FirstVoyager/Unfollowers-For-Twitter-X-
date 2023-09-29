package limitless.android.unfollowtita.Activity;

import limitless.android.unfollowtita.Model.Words;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ActivityMapOfWordsBinding;
import twitter4j.Paging;
import twitter4j.Status;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.TagCloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MapOfWordsActivity extends BaseActivity {

    private ActivityMapOfWordsBinding binding;
    private Paging paging = new Paging(1, 200);
    private List<Status> statusList = new ArrayList<>();
    private int loadCount = 4;

    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapOfWordsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        unfollowTiTa = new UnfollowTiTa(this);

        showInterstitialAd();
        loadBannerAds(binding.adView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.chartView.addFont("test", "file:///android_asset/b_fonts.ttf");
        binding.chartView.setOnRenderedListener(() -> binding.progress.progress.setVisibility(View.INVISIBLE));
        binding.chartView.setProgressBar(binding.progress.progress);
        getData(paging);
    }

    private void getData(Paging page) {
        binding.progress.progress.setVisibility(View.VISIBLE);
        unfollowTiTa.getUserTimeLine(page, statuses -> {
            if (statuses != null){
                statusList.addAll(statuses);
                if (paging.getPage() <= loadCount){
                    paging = new Paging(page.getPage() + 1, 200);
                    getData(paging);
                }else {
                    new GetWords().execute();
                }
            }
        });
    }

    private TagCloud getWords() {
        if (statusList.size() <= 0)
            return null;
        List<String> words = Utils.splitWordsFromTweets(statusList);
        if (words == null)
            return null;
        for (int i = 0; i < words.size(); i++)
            words.get(i).trim();
        Collections.sort(words);
        List<Words> newList = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if (i == 0){
                newList.add(new Words(words.get(i), 1));
                continue;
            }
            if (words.get(i).equals(newList.get(newList.size() - 1).word))
                newList.get(newList.size() - 1).count += 1;
            else
                newList.add(new Words(words.get(i), 1));
        }
        HashMap<String, Words> map = new HashMap<>();
        for (Words mw : newList) {
            if (mw.word == null || mw.word.length() <= 2 || mw.word.startsWith("https://") || mw.word.startsWith("http://"))
                continue;
            map.put(mw.word, mw);
        }
        List<Words> wordsList = new ArrayList<>(map.values());
        Collections.sort(wordsList, (o1, o2) -> o1.count - o2.count);
        Collections.reverse(wordsList);
        for (Words mw : wordsList)
            Log.i("msg_word", mw.word + " : " + mw.count);
        TagCloud tagCloud = AnyChart.tagCloud();
        tagCloud.title("Word map via (@unfollowTiTa)");
//        OrdinalColor ordinalColor = OrdinalColor.instantiate();
//        ordinalColor.colors(new String[]{
//                "#9C27B0", "#E91E63", "#607D8B", "#f44336",
//                "#616161", "#FF8F00", "#2E7D32", "#33691E",
//                "#651FFF", "#9FA8DA", "#212121", "#C51162",
//                "#2962FF", "#FFD600", "#FFC400", "#303F9F",
//                "#84FFFF", "#FFFF8D", "#FFA726", "#FFAB91",
//                "#e57373", "#b71c1c", "6A1B9A#", "#8E24AA",
//                "#4527A0", "#8BC34A", "#FFEB3B", "#B3E5FC",
//                "#2196F3", "#FF5722", "#673AB7", "#9E9E9E",
//        });
        List<DataEntry> data = new ArrayList<>();
        for (Words mw : wordsList) {
            data.add(new CategoryValueDataEntry(mw.word, "Words", mw.count * 1000));
        }
        tagCloud.data(data);
        return tagCloud;
    }

    private class GetWords extends AsyncTask<Void, Void, TagCloud>{

        @Override
        protected TagCloud doInBackground(Void... voids) {
            return getWords();
        }

        @Override
        protected void onPostExecute(TagCloud tagCloud) {
            super.onPostExecute(tagCloud);
            if (tagCloud == null){
                Utils.toast(MapOfWordsActivity.this, getString(R.string.t_cant_load_tweets_try_again));
            }else {
                binding.chartView.setChart(tagCloud);
            }
        }
    }

}
