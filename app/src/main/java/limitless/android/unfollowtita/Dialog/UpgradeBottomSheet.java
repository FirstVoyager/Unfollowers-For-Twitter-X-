package limitless.android.unfollowtita.Dialog;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.android.unfollowtita.ApplicationLoader;
import limitless.android.unfollowtita.Other.Constant;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.BottomSheetUpgradeBinding;

/**
 * Upgrade to pro version bottom sheet
 */
public class UpgradeBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener, BillingProcessor.IBillingHandler {
    // TODO: 11/27/20 update license key and product id
    private BottomSheetUpgradeBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;
    private BillingProcessor bp;
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
        binding = BottomSheetUpgradeBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Init vars and set listeners
     */
    private void init() {
        bottomSheetBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        bp = BillingProcessor.newBillingProcessor(getContext(), getString(R.string.license_key_google_play), this);

        bp.initialize();
        binding.buttonPurchase.setOnClickListener(this);
        binding.buttonRestore.setOnClickListener(this);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_restore) {
            restorePurchase();
        }else if (v.getId() == R.id.button_purchase) {
            bp.subscribe(getActivity(), Constant.PRO_VERSION_PRODUCT_ID);
        }
    }

    /**
     * Restore Purchase
     */
    private void restorePurchase() {
        if (bp.loadOwnedPurchasesFromGoogle()) {
            onPurchaseHistoryRestored();
        }else {
            Utils.toast(getContext(), R.string.could_not_restore_purchase);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (! bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        new SharePref(getContext()).put(SharePref.PRO_VERSION, true);
        Utils.toast(getContext(), R.string.please_restart);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        if (ApplicationLoader.isProVersion()) {
            new SharePref(getContext()).put(SharePref.PRO_VERSION, true);
            Utils.toast(getContext(), R.string.restored_previous_purchase_please_restart);
        }else {
            Utils.toast(getContext(), R.string.not_purchase_found);
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Utils.log("Billing error code : " + errorCode);
    }

    @Override
    public void onBillingInitialized() {
        binding.buttonRestore.setEnabled(true);
        binding.buttonPurchase.setEnabled(true);
    }

}
