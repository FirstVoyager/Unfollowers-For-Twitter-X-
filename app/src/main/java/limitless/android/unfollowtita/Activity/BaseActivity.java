package limitless.android.unfollowtita.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import limitless.android.unfollowtita.Other.AdManager;
import limitless.android.unfollowtita.Other.SQLiteUnfollow;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.Other.Utils;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;

/**
 * Manage something if need
 */
public abstract class BaseActivity extends AppCompatActivity {

    private AdManager adManager;
    public SharePref sharePref;
    public SQLiteUnfollow sqLiteUnfollow;
    public UnfollowTiTa unfollowTiTa;

    /**
     * @return Show ads in this activity or no
     */
    public abstract boolean showAds();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init vars
        sharePref = new SharePref(this);
        adManager = new AdManager(this);
        sqLiteUnfollow = new SQLiteUnfollow(this);
        unfollowTiTa = new UnfollowTiTa(this);

        try {
            sqLiteUnfollow.setCurrentAccount(sqLiteUnfollow.getMainAccount().id);
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Load banner ads
     * @param adView AdView Unit
     */
    public void loadBannerAds(AdView adView) {
        if (sharePref.get(SharePref.PRO_VERSION, false))
            adView.setVisibility(View.GONE);
        else
            AdManager.loadBanner(adView);
    }

    /**
     * Show interstitial ad if user not in pro version
     */
    public void showInterstitialAd() {
        if (! sharePref.get(SharePref.PRO_VERSION, false))
            adManager.loadAndShowInterstitial();
    }

}
