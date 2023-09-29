package limitless.android.unfollowtita.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.android.unfollowtita.Activity.LoginActivity;
import limitless.android.unfollowtita.Adapter.AccountAdapter;
import limitless.android.unfollowtita.ApplicationLoader;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Model.Account;
import limitless.android.unfollowtita.Other.SQLiteUnfollow;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.BottomSheetAccountBinding;

/**
 * Show accounts
 */
public class AccountsBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    public static String LOGIN_ACCOUNT = "loginAccount";
    private static int loginCode = 201;

    private BottomSheetAccountBinding binding;
    private SQLiteUnfollow sqlite;
    private AccountAdapter accountAdapter;
    private Listener<Account> listener;
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

    public AccountsBottomSheet(@NonNull Listener<Account> listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAccountBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void init() {
        sqlite = new SQLiteUnfollow(getContext(), 0);
        accountAdapter = new AccountAdapter(getContext(), sqlite.getAccounts(), accountListener);
        bottomSheetBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        binding.buttonAdd.setOnClickListener(this);
        binding.recyclerView.setAdapter(accountAdapter);
        binding.textViewGetRealFollowers.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView_getRealFollowers){
            new UnfollowTiTa(getContext()).followUser("unfollowTiTa", user -> Utils.toast(getContext(), R.string.thank_you));
        }else if (v.getId() == R.id.button_add){
            if (new SharePref(getContext()).get(SharePref.PRO_VERSION, false)) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivityForResult(intent, loginCode);
            }else {
                new UpgradeBottomSheet().show(getActivity().getSupportFragmentManager(), null);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == loginCode){
            if (resultCode == Activity.RESULT_CANCELED || data == null){
                Utils.toast(getContext(), R.string.login_failed);
                dismiss();
            }else if (resultCode == Activity.RESULT_OK){
                Account account = data.getParcelableExtra(LOGIN_ACCOUNT);
                if (account == null)
                    return;
                if (sqlite.checkAccount(account.id)){
                    Utils.toast(getContext(), R.string.account_exist);
                }else {
                    sqlite.insertAccount(account);
                    sqlite.setMainAccount(account.id);
                    if (listener != null){
                        listener.data(account);
                    }
                    dismiss();
                }
            }
        }
    }

    private Listener<Account> accountListener = new Listener<Account>() {
        @Override
        public void data(Account account) {
            sqlite.setMainAccount(account.id);
            listener.data(account);
            dismiss();
        }
    };

}


