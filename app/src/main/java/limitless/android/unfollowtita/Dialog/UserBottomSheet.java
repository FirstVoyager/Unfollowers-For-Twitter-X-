package limitless.android.unfollowtita.Dialog;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import limitless.android.unfollowtita.Adapter.TweetAdapter;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.DialogUserBinding;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;

/**
 * User timeLine bottom sheet
 */
public class UserBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private DialogUserBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;
    private Paging paging;
    private TweetAdapter tweetAdapter;
    private Listener<User> userListener = new Listener<User>() {
        @Override
        public void data(User user) {
            binding.progress.progress.setVisibility(View.INVISIBLE);
            if (user != null){
                thisUser = user;
                Utils.loadPhoto(getContext(), user.getBiggerProfileImageURL(), binding.imageViewAvatar, true);
                Utils.loadPhoto(getContext(), user.getProfileBannerURL(), binding.imageViewHeader, false);
                binding.textViewName.setText(user.getName());
                binding.textViewScreen.setText("@" + user.getScreenName());
                binding.textViewBio.setText(user.getDescription());
                binding.textViewFollowing.setText("Following : " + user.getFriendsCount());
                binding.textViewFollowers.setText("Followers : " + user.getFollowersCount());
            }else {
                Utils.toast(getContext(), R.string.error_try_again);
                dismiss();
            }
        }
    };
    private Listener<Status> endScrollListener = new Listener<Status>() {
        @Override
        public void data(Status status) {
            paging = new Paging(paging.getPage() + 1, paging.getCount());
            getTweets();
        }
    };
    public static final int addBlock = 0, addMute = 1, addFollow = 2, addUnfollow = 3;
    private UnfollowTiTa unfollowTiTa;
    private User thisUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogUserBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void init() {
        unfollowTiTa = new UnfollowTiTa(getContext());
        bottomSheetBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        paging = new Paging(1, 200);
        tweetAdapter = new TweetAdapter(getContext(), null);

        tweetAdapter.setOnEndListener(endScrollListener);
        binding.recyclerView.setAdapter(tweetAdapter);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        getTweets();
        unfollowTiTa.showUser(
                getArguments().getLong(SharePref.userId, 0),
                null,
                userListener);
    }

    private void getTweets() {
        unfollowTiTa.getTweets(paging, getArguments().getLong(SharePref.userId) , new Listener<ResponseList<Status>>() {
            @Override
            public void data(ResponseList<Status> statuses) {
                tweetAdapter.addData(statuses);
            }
        });
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN){
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == android.R.id.home){
            dismiss();
        }
    }
}
