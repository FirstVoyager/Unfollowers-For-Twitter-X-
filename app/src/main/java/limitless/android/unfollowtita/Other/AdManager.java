package limitless.android.unfollowtita.Other;

import android.content.Context;
import android.view.View;

import com.anychart.charts.Pert;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;

import androidx.annotation.NonNull;
import limitless.android.unfollowtita.ApplicationLoader;
import limitless.android.unfollowtita.R;

/**
 * Manage application ads
 */
public class AdManager {

    private SharePref sharePref;
    /**
     * Interstitial ad is ready to show or no
     */
    private static boolean interstitialIsReady = false;
    /**
     * Interstitial ad global
     */
    private static InterstitialAd interstitialAd;
    /**
     * Listener for ad if it loaded or no
     */
    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            interstitialIsReady = true;
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            interstitialIsReady = false;
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            super.onAdFailedToLoad(loadAdError);
            interstitialIsReady = false;
        }
    };

    /**
     * @param context Cant be null
     */
    public AdManager(@NonNull Context context) {
        sharePref = new SharePref(context);
        interstitialAd = new InterstitialAd(context);

        interstitialAd.setAdUnitId(context.getString(R.string.ad_unit_id_interstitial));
        interstitialAd.setAdListener(adListener);
    }

    /**
     * Create ad request
     * @return Ad request
     */
    private static AdRequest adRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Load banner
     * @param adView Your ad view
     */
    public static void loadBanner(AdView adView) {
        if (adView == null)
            return;
        adView.loadAd(adRequest());
    }

    /**
     * Load interstitial ad if user not in pro version
     */
    public static void loadInterstitial() {
        interstitialAd.loadAd(adRequest());
    }

    /**
     * Show interstitial if user not in pro version
     */
    public static void showInterstitial() {
        if (interstitialAd.isLoaded())
            interstitialAd.show();
    }


    /**
     * Load interstitial ad and then show
     */
    public void loadAndShowInterstitial() {
        if (sharePref.get(SharePref.PRO_VERSION, false))
            return;
        interstitialAd.loadAd(adRequest());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }
        });
    }

}
