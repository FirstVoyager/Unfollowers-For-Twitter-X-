package limitless.android.unfollowtita.Dialog;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.android.unfollowtita.Activity.UserListActivity;
import limitless.android.unfollowtita.Activity.SettingActivity;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Model.Account;
import limitless.android.unfollowtita.Model.Type;
import limitless.android.unfollowtita.Other.SQLiteUnfollow;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.BottomSheetMainMenuBinding;

/**
 * Menu bottom sheet
 */
public class MenuBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private Listener<Account> accountListener;
    private BottomSheetMainMenuBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;
    private SQLiteUnfollow sqLiteUnfollow;
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

    public MenuBottomSheet(Listener<Account> accountListener) {
        this.accountListener = accountListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetMainMenuBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Init vars
     */
    private void init() {
        sqLiteUnfollow = new SQLiteUnfollow(getContext());
        bottomSheetBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());

        setImages();
        // set listeners
        binding.linearLayoutBlackList.setOnClickListener(this);
        binding.linearLayoutWhiteList.setOnClickListener(this);
        binding.linearLayoutMainAccount.setOnClickListener(this);
        binding.linearLayoutBlocked.setOnClickListener(this);
        binding.linearLayoutMuted.setOnClickListener(this);
        binding.linearLayoutSetting.setOnClickListener(this);
        binding.linearLayoutPremium.setOnClickListener(this);
        binding.linearLayoutReview.setOnClickListener(this);
        binding.linearLayoutAbout.setOnClickListener(this);
        binding.linearLayoutShare.setOnClickListener(this);

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Account account = sqLiteUnfollow.getMainAccount();
        if (account == null)
            return;
        sqLiteUnfollow.setCurrentAccount(account.id);
        Utils.loadPhoto(getContext(), account.profileUrl, binding.imageViewAvatar, true);
        binding.textViewDetails.setText("@");
        binding.textViewDetails.append(account.screenName);
        binding.textViewName.setText(account.name);
        // set data sizes
        new LoadDataSize().execute();
    }

    /**
     * Load images from Drawable folder with Glide
     */
    private void setImages() {
        Utils.loadPhoto(getContext(), R.drawable.icon_main, binding.imageViewMiniIcon, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_block_512dp, binding.imageViewBlocked, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_mute_512dp, binding.imageViewMuted, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_white_list_512dp, binding.imageViewWhiteList, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_black_list_512dp, binding.imageViewBlackList, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_setting_512dp, binding.imageViewSetting, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_premium_512dp, binding.imageViewUpgrade, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_info_512dp, binding.imageViewAboutUs, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_share_512dp, binding.imageViewShareApp, false);
        Utils.loadPhoto(getContext(), R.drawable.ic_rating_512dp, binding.imageViewRateUs, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayout_blackList:
                Utils.startActivity(
                        getContext(),
                        new Intent(getActivity(), UserListActivity.class)
                                .putExtra(UserListActivity.USER_TYPE, Type.BLACK_LIST.name()));
                break;
            case R.id.linearLayout_whiteList:
                Utils.startActivity(
                        getContext(),
                        new Intent(getActivity(), UserListActivity.class)
                                .putExtra(UserListActivity.USER_TYPE, Type.WHITE_LIST.name()));
                break;
            case R.id.linearLayout_blocked:
                Utils.startActivity(
                        getContext(),
                        new Intent(getActivity(), UserListActivity.class)
                                .putExtra(UserListActivity.USER_TYPE, Type.BLOCKED.name()));
                break;
            case R.id.linearLayout_muted:
                Utils.startActivity(
                        getContext(),
                        new Intent(getActivity(), UserListActivity.class)
                                .putExtra(UserListActivity.USER_TYPE, Type.MUTED.name()));
                break;
            case R.id.linearLayout_setting:
                Utils.startActivity(getContext(), SettingActivity.class);
                break;
            case R.id.linearLayout_premium:
                if (getActivity() != null) {
                    new UpgradeBottomSheet().show(getActivity().getSupportFragmentManager(), null);
                }
                break;
            case R.id.linearLayout_about:
//                Utils.startActivity(getContext(), );
                break;
            case R.id.linearLayout_review:
                Utils.openUrl(getContext(), R.string.app_url);
                break;
            case R.id.linearLayout_share:
                Utils.shareText(getContext(), R.string.app_url);
                break;
            case R.id.linearLayout_mainAccount:
                if (getActivity() != null)
                    new AccountsBottomSheet(accountListener).show(getActivity().getSupportFragmentManager(), null);
                break;
        }
        dismiss();
    }

    /**
     * Load data in background
     */
    private class LoadDataSize extends AsyncTask<Void, Void, Integer[]> {

        @Override
        protected Integer[] doInBackground(Void... voids) {
            Integer[] integers = new Integer[4];
            integers[0] = sqLiteUnfollow.userSize(Type.BLOCKED);
            integers[1] = sqLiteUnfollow.userSize(Type.MUTED);
            integers[2] = sqLiteUnfollow.userSize(Type.WHITE_LIST);
            integers[3] = sqLiteUnfollow.userSize(Type.BLACK_LIST);
            return integers;
        }

        @Override
        protected void onPostExecute(Integer[] integers) {
            super.onPostExecute(integers);
            if (integers == null)
                return;
            if (integers[0] > 0)
                binding.textViewBlockedSize.setText(String.valueOf(integers[0]));
            if (integers[1] > 0)
                binding.textViewMutedSize.setText(String.valueOf(integers[1]));
            if (integers[2] > 0)
                binding.textViewWhiteListSize.setText(String.valueOf(integers[2]));
            if (integers[3] > 0)
                binding.textViewBlackListSize.setText(String.valueOf(integers[3]));
        }
    }

}
