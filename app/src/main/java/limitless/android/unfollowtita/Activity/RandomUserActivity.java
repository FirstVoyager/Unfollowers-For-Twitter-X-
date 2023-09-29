package limitless.android.unfollowtita.Activity;

import limitless.android.unfollowtita.Dialog.UserBottomSheet;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Model.Type;
import limitless.android.unfollowtita.Model.UserModel;
import limitless.android.unfollowtita.Other.SQLiteUnfollow;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ActivityRandomUserBinding;
import twitter4j.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.util.List;
import java.util.Random;

/**
 * Select random user from #Following - #Followers - #Fans
 */
public class RandomUserActivity extends BaseActivity implements View.OnClickListener {

    private ActivityRandomUserBinding binding;
    private Listener<User> listener = new Listener<User>() {
        @Override
        public void data(User user) {
            binding.progress.progress.setVisibility(View.INVISIBLE);
            binding.linearLayoutFollowing.setEnabled(true);
            binding.linearLayoutFollowers.setEnabled(true);
            binding.linearLayoutFans.setEnabled(true);
            binding.linearLayoutMutual.setEnabled(true);

            if (user == null) {
                Utils.toast(RandomUserActivity.this, R.string.try_again);
                return;
            }
            UserBottomSheet sheet = new UserBottomSheet();
            Bundle bundle = new Bundle();
            bundle.putLong(SharePref.userId, user.getId());
            sheet.setArguments(bundle);
            sheet.show(getSupportFragmentManager(), null);
        }
    };

    @Override
    public boolean showAds() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRandomUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    /**
     * Init vars
     */
    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.linearLayoutFollowers.setOnClickListener(this);
        binding.linearLayoutFollowing.setOnClickListener(this);
        binding.linearLayoutFans.setOnClickListener(this);
        binding.linearLayoutMutual.setOnClickListener(this);
        Utils.loadPhoto(this, R.drawable.ic_followers_512dp, binding.imageViewFollowers, false);
        Utils.loadPhoto(this, R.drawable.ic_following_512dp, binding.imageViewFollowing, false);
        Utils.loadPhoto(this, R.drawable.ic_fans_512dp, binding.imageViewFans, false);
        Utils.loadPhoto(this, R.drawable.ic_mutual_friends_512dp, binding.imageViewMutual, false);
    }

    @Override
    public void onClick(View v) {
        binding.progress.progress.setVisibility(View.VISIBLE);
        binding.linearLayoutFollowing.setEnabled(false);
        binding.linearLayoutFollowers.setEnabled(false);
        binding.linearLayoutFans.setEnabled(false);
        binding.linearLayoutMutual.setEnabled(false);
        if (v.getId() == R.id.linearLayout_followers) {
            randomFromDataBase(0);
        }else if (v.getId() == R.id.linearLayout_following){
            randomFromDataBase(1);
        }else if (v.getId() == R.id.linearLayout_fans){
            unfollowTiTa.showUser(
                    Utils.randomItem(MainActivity.fans),
                    null,
                    listener);
        }else if (v.getId() == R.id.linearLayout_mutual){
            unfollowTiTa.showUser(
                    Utils.randomItem(MainActivity.mutual),
                    null,
                    listener);
        }
    }

    /**
     * Select random user from database
     * 1. Following
     * 2. Followers
     * @param n Is following or followers
     */
    private void randomFromDataBase(int n) {
        if (n == 0)
            new LoadData().execute(Type.FOLLOWERS);
        else if (n == 1)
            new LoadData().execute(Type.FOLLOWING);

    }

    private class LoadData extends AsyncTask<Type, Void, List<UserModel>> {

        @Override
        protected List<UserModel> doInBackground(Type... types) {
            return sqLiteUnfollow.getUsers(types[0]);
        }

        @Override
        protected void onPostExecute(List<UserModel> userModels) {
            super.onPostExecute(userModels);
            if (userModels == null || userModels.size() <= 0){
                binding.progress.progress.setVisibility(View.INVISIBLE);
                binding.linearLayoutFollowing.setEnabled(false);
                binding.linearLayoutFollowers.setEnabled(false);
                binding.linearLayoutFans.setEnabled(false);
                binding.linearLayoutMutual.setEnabled(false);
                Utils.toast(RandomUserActivity.this, R.string.failed);
                return;
            }
            UserModel um = userModels.get(new Random().nextInt(userModels.size()));
            unfollowTiTa.showUser(um.userId, null, listener);
        }
    }

}