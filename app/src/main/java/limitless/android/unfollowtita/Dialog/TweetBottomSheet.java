package limitless.android.unfollowtita.Dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.android.unfollowtita.Other.AdManager;
import limitless.android.unfollowtita.Other.Constant;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.BottomSheetTweetBinding;
import twitter4j.StatusUpdate;

/**
 * Send new tweets
 */
public class TweetBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetTweetBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;
    private UnfollowTiTa unfollowTiTa;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            binding.progressBar.setProgress(s.length());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetTweetBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Init vars
     */
    private void init() {
        bottomSheetBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        unfollowTiTa = new UnfollowTiTa(getContext());

        binding.buttonTweet.setOnClickListener(this);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        binding.editTextTweet.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_tweet) {
            String msg = binding.editTextTweet.getText().toString();
            if (msg.length() <= 0) {
                Utils.toast(getContext(), R.string.the_tweet_is_short);
                return;
            }
            if (msg.length() > 280) {
                Utils.toast(getContext(), getString(R.string.the_max_tweet_size_is_i, Constant.max_tweet_size));
                return;
            }
            binding.progressBarTweet.setVisibility(View.VISIBLE);
            binding.buttonTweet.setVisibility(View.INVISIBLE);
            StatusUpdate statusUpdate = new StatusUpdate(msg);
            new AdManager(getContext()).loadAndShowInterstitial();
            unfollowTiTa.updateStatus(statusUpdate, status -> {
                if (status == null)
                    Utils.toast(getContext(), R.string.failed_to_tweet);
                else
                    Utils.toast(getContext(), R.string.tweet_sent);
                binding.progressBarTweet.setVisibility(View.INVISIBLE);
                binding.buttonTweet.setVisibility(View.VISIBLE);
                new AdManager(getActivity()).loadAndShowInterstitial();
                dismiss();
            });
        }
    }
}
