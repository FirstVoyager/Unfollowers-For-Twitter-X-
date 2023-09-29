package limitless.android.unfollowtita;

import android.app.Application;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Collections;

import limitless.android.unfollowtita.Other.Constant;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.Utils;

public class ApplicationLoader extends Application {

    private static BillingProcessor billingProcessor;

    @Override
    public void onCreate() {
        super.onCreate();
        SharePref sharePref = new SharePref(this);
        if (sharePref.get(SharePref.changeApiKey, true)){
            sharePref.clearAll();
            Utils.clearCookies();
            sharePref.put(SharePref.changeApiKey, false);
        }

        if (BuildConfig.DEBUG){
            RequestConfiguration rc = new RequestConfiguration
                    .Builder()
                    .setTestDeviceIds(Collections.singletonList("1959AA40684936177226CB92270"))
                    .build();
            MobileAds.setRequestConfiguration(rc);
        }
        MobileAds.initialize(this);

        billingProcessor = BillingProcessor.newBillingProcessor(getApplicationContext(), getString(R.string.license_key_google_play), new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {

            }

            @Override
            public void onPurchaseHistoryRestored() {

            }

            @Override
            public void onBillingError(int errorCode, Throwable error) {

            }

            @Override
            public void onBillingInitialized() {
                new SharePref(getApplicationContext()).put(SharePref.PRO_VERSION, isProVersion());
            }
        });
        billingProcessor.initialize();
    }

    /**
     * Check user is pro version or no
     * @return Pro version or no
     */
    public static boolean isProVersion() {
        return
                BuildConfig.DEBUG ||
                billingProcessor.isSubscribed(Constant.PRO_VERSION_PRODUCT_ID);
    }

}
