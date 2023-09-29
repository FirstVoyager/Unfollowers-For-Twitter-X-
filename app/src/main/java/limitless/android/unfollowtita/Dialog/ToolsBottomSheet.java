package limitless.android.unfollowtita.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.android.unfollowtita.Activity.CopyFollowersActivity;
import limitless.android.unfollowtita.Activity.DateOfTweetsActivity;
import limitless.android.unfollowtita.Activity.RandomUserActivity;
import limitless.android.unfollowtita.Activity.SearchTweetsActivity;
import limitless.android.unfollowtita.Activity.SearchUsersActivity;
import limitless.android.unfollowtita.Activity.TopFriendsActivity;
import limitless.android.unfollowtita.ApplicationLoader;
import limitless.android.unfollowtita.Model.Account;
import limitless.android.unfollowtita.Model.TopFriends;
import limitless.android.unfollowtita.Other.SQLiteUnfollow;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.BottomSheetMagicToolsBinding;

/**
 * Bottom sheet for magic tools
 */
public class ToolsBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetMagicToolsBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetMagicToolsBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Init vars
     */
    private void init() {
        bottomSheetBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());

        setImages();
        binding.linearLayoutDateOfTweet.setOnClickListener(this);
        binding.linearLayoutTopFriends.setOnClickListener(this);
        binding.linearLayoutSearchUser.setOnClickListener(this);
        binding.linearLayoutCopyFollowers.setOnClickListener(this);
        binding.linearLayoutSearchTweet.setOnClickListener(this);
        binding.linearLayoutRandomUser.setOnClickListener(this);
        binding.linearLayoutTimeLine.setOnClickListener(this);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * Load images from Drawable folder with Glide
     */
    private void setImages() {
        Utils.loadPhoto(getContext(), R.drawable.ic_date_512dp, binding.imageViewDateOfTweet, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_top_friends_512dp, binding.imageViewTopFriends, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_search_user_512dp, binding.imageViewSearchUser, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_copy_512dp, binding.imageViewCopyFollowers, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_search_tweet_512dp, binding.imageViewSearchTweet, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_random_512dp, binding.imageViewRandomUser, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_timeline_512dp, binding.imageViewTimeLine, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linearLayout_dateOfTweet:
                Utils.startActivity(getContext(), DateOfTweetsActivity.class);
                break;
            case R.id.linearLayout_topFriends:
                Utils.startActivity(getContext(), TopFriendsActivity.class);
                break;
            case R.id.linearLayout_searchUser:
                Utils.startActivity(getContext(), SearchUsersActivity.class);
                break;
            case R.id.linearLayout_copyFollowers:
                Utils.startActivity(getContext(), CopyFollowersActivity.class);
                break;
            case R.id.linearLayout_searchTweet:
                Utils.startActivity(getContext(), SearchTweetsActivity.class);
                break;
            case R.id.linearLayout_randomUser:
                Utils.startActivity(getContext(), RandomUserActivity.class);
                break;
            case R.id.linearLayout_timeLine:
                try {
                    Bundle bundle = new Bundle();
                    bundle.putLong(SharePref.userId, new SQLiteUnfollow(getContext()).getMainAccount().id);
                    UserBottomSheet sheet = new UserBottomSheet();
                    sheet.setArguments(bundle);
                    sheet.show(getActivity().getSupportFragmentManager(), null);
                } catch (Exception e) {
                    Utils.error(e);
                }
                break;
        }
        dismiss();
    }
}
