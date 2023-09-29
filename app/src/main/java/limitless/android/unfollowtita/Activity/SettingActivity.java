package limitless.android.unfollowtita.Activity;

import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.CompoundButton;

import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.R;

public class SettingActivity extends BaseActivity {

    private SwitchCompat scBlocked, scMuted, scHeader;
    private SharePref sharePref;

    private SwitchCompat.OnCheckedChangeListener checkListenerBlocked = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sharePref.put(SharePref.settingLoadBlocked, isChecked);
        }
    };
    private SwitchCompat.OnCheckedChangeListener checkedListenerMuted = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sharePref.put(SharePref.settingLoadMuted, isChecked);
        }
    };
    private SwitchCompat.OnCheckedChangeListener checkedChangeListenerHeader = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sharePref.put(SharePref.settingLoadHeader, isChecked);
        }
    };

    @Override
    public boolean showAds() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.setting);
        sharePref = new SharePref(this);
        scBlocked = findViewById(R.id.switch_blocked);
        scMuted = findViewById(R.id.switch_muted);
        scHeader = findViewById(R.id.switch_header);

        scBlocked.setChecked(sharePref.get(SharePref.settingLoadBlocked, true));
        scMuted.setChecked(sharePref.get(SharePref.settingLoadMuted, true));
        scHeader.setChecked(sharePref.get(SharePref.settingLoadHeader, true));
        scBlocked.setOnCheckedChangeListener(checkListenerBlocked);
        scMuted.setOnCheckedChangeListener(checkedListenerMuted);
        scHeader.setOnCheckedChangeListener(checkedChangeListenerHeader);
    }
}
