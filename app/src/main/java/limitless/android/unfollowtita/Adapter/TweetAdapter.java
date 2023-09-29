package limitless.android.unfollowtita.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ItemTweetBinding;
import twitter4j.Status;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder> {

    private Context context;
    private List<Status> statuses;
    private Listener<Status> endListener;

    public TweetAdapter(Context context, List<Status> statuses) {
        this.context = context;
        this.statuses = statuses;
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TweetViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
        holder.bindView(statuses.get(position));
    }

    @Override
    public int getItemCount() {
        try {
            return statuses.size();
        }catch (Exception e){
            Utils.error(e);
            return 0;
        }
    }

    public void addData(@Nullable List<Status> statuses) {
        if (statuses == null || statuses.size() <= 0)
            return;
        if (this.statuses == null)
            this.statuses = new ArrayList<>();
        this.statuses.addAll(statuses);
        notifyDataSetChanged();
    }

    public void clearAll() {
        statuses = new ArrayList<>();
        statuses.clear();
        notifyDataSetChanged();
    }

    public void setOnEndListener(Listener<Status> onEndListener) {
        this.endListener = onEndListener;
    }

    class TweetViewHolder extends RecyclerView.ViewHolder {
        private ItemTweetBinding binding;
        public TweetViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
        }

        void bindView(Status s){
            if (s.isRetweet()){
                setUpData(s.getRetweetedStatus());
            }else if (s.getText().startsWith("@")){
                setUpData(s);
            }else {
                setUpData(s);
            }

            if (getAdapterPosition() == statuses.size() - 1){
                if (endListener != null)
                    endListener.data(statuses.get(getAdapterPosition()));
            }
        }

        private void setUpData(Status s) {
            Utils.loadPhoto(context, s.getUser().getBiggerProfileImageURL(), binding.imageViewAvatar, true);
            binding.textViewName.setText(s.getUser().getName());
            binding.textViewScreen.setText("@");
            binding.textViewScreen.append(s.getUser().getScreenName());
            binding.textViewDate.setText(Utils.date(s.getCreatedAt().getTime()));
            binding.textViewMain.setText(s.getText());
            binding.textViewReTweet.setText(String.valueOf(s.getRetweetCount()));
            binding.textViewFavorite.setText(String.valueOf(s.getFavoriteCount()));
            binding.textViewComment.setText(String.valueOf(s.getUserMentionEntities().length));
            binding.imageViewMain.setVisibility(View.GONE);
            if (s.getMediaEntities().length > 0){
                binding.imageViewMain.setVisibility(View.VISIBLE);
                Utils.loadPhoto(context, s.getMediaEntities()[0].getMediaURL(), binding.imageViewMain, false);
            }else {
                binding.imageViewMain.setVisibility(View.GONE);
            }
            if (s.isFavorited())
                binding.imageViewFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
        }
    }

    private void seeOnTwitter(int i) {
        try {
            Utils.openUrl(context, UnfollowTiTa.tweetUrl(statuses.get(i).getUser().getScreenName(), statuses.get(i).getId()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void share(int n) {
        try {
            Utils.shareText(context, UnfollowTiTa.tweetUrl(statuses.get(n).getUser().getScreenName(), statuses.get(n).getId()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
